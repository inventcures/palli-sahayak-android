package com.pallisahayak.core.voice

import com.pallisahayak.core.model.query.EmergencyLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyKeywordDetector @Inject constructor() {

    private val emergencyPatterns = mapOf(
        "en" to listOf(
            "bleeding", "unconscious", "not breathing", "chest pain",
            "seizure", "suicide", "severe pain", "choking", "heart attack",
            "stroke", "can't breathe", "unresponsive",
        ),
        "hi" to listOf(
            "खून बह रहा", "बेहोश", "सांस नहीं", "छाती में दर्द",
            "दौरा", "तेज दर्द", "बहुत दर्द", "खून", "मर रहा",
        ),
        "ta" to listOf(
            "இரத்தப்போக்கு", "மயக்கம்", "மூச்சு விடவில்லை",
            "நெஞ்சு வலி", "வலிப்பு", "கடும் வலி",
        ),
        "bn" to listOf(
            "রক্তপাত", "অচেতন", "শ্বাস নেই", "বুকে ব্যথা",
            "খিঁচুনি", "তীব্র ব্যথা", "মারা যাচ্ছে",
        ),
        "kn" to listOf(
            "ರಕ್ತಸ್ರಾವ", "ಪ್ರಜ್ಞೆ ತಪ್ಪಿದ", "ಉಸಿರಾಟ ಇಲ್ಲ",
            "ಎದೆ ನೋವು", "ತೀವ್ರ ನೋವು",
        ),
        "ml" to listOf(
            "രക്തസ്രാവം", "ബോധം ഇല്ല", "ശ്വാസം ഇല്ല",
            "നെഞ്ചുവേദന", "കഠിന വേദന",
        ),
        "te" to listOf(
            "రక్తస్రావం", "స్పృహ లేదు", "ఊపిరి ఆడటం లేదు",
            "ఛాతీ నొప్పి", "తీవ్రమైన నొప్పి",
        ),
        "as" to listOf(
            "ৰক্তক্ষৰণ", "অচেতন", "উশাহ নাই",
            "বুকুৰ বিষ", "তীব্ৰ বিষ",
        ),
    )

    fun detect(transcript: String, language: String): EmergencyLevel {
        val normalized = transcript.lowercase().trim()
        val shortCode = if (language.contains("-")) language.substringBefore("-") else language

        val patterns = emergencyPatterns[shortCode] ?: emergencyPatterns["en"]!!
        val hasMatch = patterns.any { normalized.contains(it.lowercase()) }

        if (!hasMatch && shortCode != "en") {
            val englishPatterns = emergencyPatterns["en"]!!
            val hasEnglishMatch = englishPatterns.any { normalized.contains(it.lowercase()) }
            if (hasEnglishMatch) return EmergencyLevel.CRITICAL
        }

        return if (hasMatch) EmergencyLevel.CRITICAL else EmergencyLevel.NONE
    }

    fun getEmergencyNumber(): String = "108"

    fun getEmergencyMessage(language: String): String {
        val shortCode = if (language.contains("-")) language.substringBefore("-") else language
        return when (shortCode) {
            "hi" -> "आपातकालीन स्थिति का पता चला। कृपया तुरंत 108 पर कॉल करें।"
            "ta" -> "அவசரநிலை கண்டறியப்பட்டது. உடனடியாக 108 ஐ அழைக்கவும்."
            "bn" -> "জরুরি অবস্থা সনাক্ত হয়েছে। অনুগ্রহ করে অবিলম্বে 108 এ কল করুন।"
            "kn" -> "ತುರ್ತು ಪರಿಸ್ಥಿತಿ ಪತ್ತೆಯಾಗಿದೆ. ದಯವಿಟ್ಟು ತಕ್ಷಣ 108 ಗೆ ಕರೆ ಮಾಡಿ."
            "ml" -> "അടിയന്തര സാഹചര്യം കണ്ടെത്തി. ദയവായി ഉടൻ 108 ൽ വിളിക്കുക."
            "te" -> "అత్యవసర పరిస్థితి గుర్తించబడింది. దయచేసి వెంటనే 108 కు కాల్ చేయండి."
            "as" -> "জৰুৰীকালীন পৰিস্থিতি ধৰা পৰিছে। অনুগ্ৰহ কৰি তৎক্ষণাত 108 ত কল কৰক।"
            else -> "Emergency detected. Please call 108 immediately."
        }
    }
}
