package com.pallisahayak.core.voice

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageMapper @Inject constructor() {

    private val codeMap = mapOf(
        "ta" to "ta-IN",
        "te" to "te-IN",
        "kn" to "kn-IN",
        "ml" to "ml-IN",
        "bn" to "bn-IN",
        "as" to "as-IN",
        "hi" to "hi-IN",
        "en" to "en-IN",
        "tu" to "kn-IN",
        "ta-IN" to "ta-IN",
        "te-IN" to "te-IN",
        "kn-IN" to "kn-IN",
        "ml-IN" to "ml-IN",
        "bn-IN" to "bn-IN",
        "as-IN" to "as-IN",
        "hi-IN" to "hi-IN",
        "en-IN" to "en-IN",
    )

    private val voiceMap = mapOf(
        "hi-IN" to "meera",
        "ta-IN" to "meera",
        "bn-IN" to "meera",
        "kn-IN" to "meera",
        "en-IN" to "meera",
        "ml-IN" to "meera",
        "te-IN" to "meera",
        "as-IN" to "meera",
    )

    private val displayNames = mapOf(
        "ta" to "தமிழ்",
        "te" to "తెలుగు",
        "kn" to "ಕನ್ನಡ",
        "ml" to "മലയാളം",
        "bn" to "বাংলা",
        "as" to "অসমীয়া",
        "hi" to "हिन्दी",
        "en" to "English",
        "tu" to "ತುಳು",
    )

    fun toBcp47(shortCode: String): String = codeMap[shortCode] ?: "en-IN"

    fun toShortCode(bcp47: String): String {
        val short = bcp47.substringBefore("-")
        return if (codeMap.containsKey(short)) short else "en"
    }

    fun getVoice(bcp47: String): String = voiceMap[bcp47] ?: "meera"

    fun toLocale(code: String): Locale {
        val bcp47 = toBcp47(code)
        val parts = bcp47.split("-")
        return if (parts.size >= 2) Locale(parts[0], parts[1]) else Locale(parts[0])
    }

    fun getDisplayName(shortCode: String): String = displayNames[shortCode] ?: shortCode

    fun getSupportedLanguages(): List<Pair<String, String>> =
        displayNames.toList()

    fun hasTtsSupport(shortCode: String): Boolean =
        shortCode !in setOf("as", "tu")

    fun getTtsFallback(shortCode: String): String =
        if (hasTtsSupport(shortCode)) shortCode else "hi"
}
