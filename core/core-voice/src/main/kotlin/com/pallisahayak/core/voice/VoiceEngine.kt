package com.pallisahayak.core.voice

import com.pallisahayak.core.common.result.Result
import kotlinx.coroutines.flow.Flow

interface VoiceEngine {
    suspend fun speechToText(audio: ByteArray, language: String): Result<String>
    suspend fun textToSpeech(text: String, language: String): Result<ByteArray>
    suspend fun voiceQuery(audio: ByteArray, language: String): Result<VoiceQueryResult>
    fun isAvailable(): Boolean
}

data class VoiceQueryResult(
    val transcript: String,
    val answer: String,
    val evidenceLevel: String,
    val emergencyLevel: String,
    val confidence: Float,
    val sources: List<Map<String, Any?>> = emptyList(),
    val audioResponse: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VoiceQueryResult) return false
        return transcript == other.transcript && answer == other.answer
    }

    override fun hashCode(): Int = transcript.hashCode() * 31 + answer.hashCode()
}

sealed class VoiceStreamEvent {
    data class TranscriptUpdate(val text: String, val isFinal: Boolean) : VoiceStreamEvent()
    data class ResponseText(val text: String) : VoiceStreamEvent()
    data class ResponseAudio(val audioData: ByteArray) : VoiceStreamEvent() {
        override fun equals(other: Any?) = this === other
        override fun hashCode() = audioData.contentHashCode()
    }
    data class Error(val message: String) : VoiceStreamEvent()
    data object Complete : VoiceStreamEvent()
}
