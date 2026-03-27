package com.pallisahayak.core.voice

import android.util.Base64
import com.pallisahayak.core.common.result.Result
import com.pallisahayak.core.network.api.PalliSahayakApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerVoiceEngine @Inject constructor(
    private val apiService: PalliSahayakApiService,
    private val languageMapper: LanguageMapper,
) : VoiceEngine {

    override suspend fun speechToText(audio: ByteArray, language: String): Result<String> {
        return try {
            val bcp47 = languageMapper.toBcp47(language)
            val audioPart = MultipartBody.Part.createFormData(
                "audio",
                "recording.wav",
                audio.toRequestBody("audio/wav".toMediaType()),
            )
            val response = apiService.voiceQuery(audioPart, bcp47)
            Result.Success(response.transcript)
        } catch (e: Exception) {
            Result.Error(e, "STT failed: ${e.message}")
        }
    }

    override suspend fun textToSpeech(text: String, language: String): Result<ByteArray> {
        return try {
            val bcp47 = languageMapper.toBcp47(language)
            val audioPart = MultipartBody.Part.createFormData(
                "audio",
                "silence.wav",
                ByteArray(0).toRequestBody("audio/wav".toMediaType()),
            )
            val response = apiService.voiceQuery(audioPart, bcp47)
            val audioBytes = response.audio_base64?.let {
                Base64.decode(it, Base64.DEFAULT)
            }
            if (audioBytes != null) {
                Result.Success(audioBytes)
            } else {
                Result.Error(Exception("No audio in response"))
            }
        } catch (e: Exception) {
            Result.Error(e, "TTS failed: ${e.message}")
        }
    }

    override suspend fun voiceQuery(audio: ByteArray, language: String): Result<VoiceQueryResult> {
        return try {
            val bcp47 = languageMapper.toBcp47(language)
            val audioPart = MultipartBody.Part.createFormData(
                "audio",
                "recording.wav",
                audio.toRequestBody("audio/wav".toMediaType()),
            )
            val response = apiService.voiceQuery(audioPart, bcp47)
            val audioBytes = response.audio_base64?.let {
                Base64.decode(it, Base64.DEFAULT)
            }
            Result.Success(
                VoiceQueryResult(
                    transcript = response.transcript,
                    answer = response.answer,
                    evidenceLevel = response.evidence_level,
                    emergencyLevel = response.emergency_level,
                    confidence = response.confidence.toFloat(),
                    sources = response.sources,
                    audioResponse = audioBytes,
                )
            )
        } catch (e: Exception) {
            Result.Error(e, "Voice query failed: ${e.message}")
        }
    }

    override fun isAvailable(): Boolean = true
}
