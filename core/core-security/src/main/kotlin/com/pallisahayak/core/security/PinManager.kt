package com.pallisahayak.core.security

import android.content.SharedPreferences
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences,
) {
    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_FAILED_ATTEMPTS = "failed_pin_attempts"
        private const val ITERATIONS = 100_000
        private const val KEY_LENGTH = 256
        private const val MAX_ATTEMPTS = 5
    }

    fun setPin(pin: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = hashPin(pin, salt)
        encryptedPrefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt.toHex())
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = encryptedPrefs.getString(KEY_PIN_HASH, null) ?: return false
        val saltHex = encryptedPrefs.getString(KEY_PIN_SALT, null) ?: return false
        val failedAttempts = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0)

        if (failedAttempts >= MAX_ATTEMPTS) return false

        val salt = saltHex.fromHex()
        val computedHash = hashPin(pin, salt)

        return if (computedHash == storedHash) {
            encryptedPrefs.edit().putInt(KEY_FAILED_ATTEMPTS, 0).apply()
            true
        } else {
            encryptedPrefs.edit().putInt(KEY_FAILED_ATTEMPTS, failedAttempts + 1).apply()
            false
        }
    }

    fun isPinSet(): Boolean = encryptedPrefs.getString(KEY_PIN_HASH, null) != null

    fun isLockedOut(): Boolean = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0) >= MAX_ATTEMPTS

    fun resetLockout() {
        encryptedPrefs.edit().putInt(KEY_FAILED_ATTEMPTS, 0).apply()
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded.toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun String.fromHex(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
