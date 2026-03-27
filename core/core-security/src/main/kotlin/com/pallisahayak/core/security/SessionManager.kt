package com.pallisahayak.core.security

import com.pallisahayak.core.common.constants.AppConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val pinManager: PinManager,
) {
    @Volatile
    private var lastActivityTimestamp = System.currentTimeMillis()

    fun isSessionValid(): Boolean =
        (System.currentTimeMillis() - lastActivityTimestamp) < AppConstants.SESSION_TIMEOUT_MS

    fun recordActivity() {
        lastActivityTimestamp = System.currentTimeMillis()
    }

    fun isAuthenticated(): Boolean =
        pinManager.isPinSet() && tokenManager.hasTokens() && isSessionValid()

    fun requiresOnboarding(): Boolean = !pinManager.isPinSet()

    fun requiresPinUnlock(): Boolean = pinManager.isPinSet() && !isSessionValid()

    fun logout() {
        tokenManager.clearTokens()
        lastActivityTimestamp = 0L
    }
}
