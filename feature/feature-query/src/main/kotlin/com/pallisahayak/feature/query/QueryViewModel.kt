package com.pallisahayak.feature.query

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.common.result.Result
import com.pallisahayak.core.data.database.dao.QueryCacheDao
import com.pallisahayak.core.data.network.NetworkMonitor
import com.pallisahayak.core.model.query.EmergencyLevel
import com.pallisahayak.core.model.query.EvidenceLevel
import com.pallisahayak.core.model.query.QueryResult
import com.pallisahayak.core.voice.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class QueryViewModel @Inject constructor(
    @Named("server") private val serverVoice: VoiceEngine,
    @Named("onDevice") private val onDeviceVoice: VoiceEngine,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val emergencyDetector: EmergencyKeywordDetector,
    private val queryCacheDao: QueryCacheDao,
    private val networkMonitor: NetworkMonitor,
    private val languageMapper: LanguageMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(QueryUiState())
    val state: StateFlow<QueryUiState> = _state

    val amplitude: StateFlow<Float> = audioRecorder.amplitude
    val recordingState: StateFlow<AudioRecorder.RecordingState> = audioRecorder.recordingState
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private var currentLanguage = "en"

    fun setLanguage(language: String) {
        currentLanguage = language
    }

    fun startRecording() {
        viewModelScope.launch {
            _state.update { it.copy(phase = QueryPhase.RECORDING, error = null) }
            try {
                val audioData = audioRecorder.record()
                processAudio(audioData)
            } catch (e: Exception) {
                _state.update { it.copy(phase = QueryPhase.IDLE, error = "Recording failed: ${e.message}") }
            }
        }
    }

    fun stopRecording() {
        audioRecorder.stopRecording()
    }

    private suspend fun processAudio(audioData: ByteArray) {
        _state.update { it.copy(phase = QueryPhase.PROCESSING) }

        val online = isOnline.value

        if (online) {
            when (val result = serverVoice.voiceQuery(audioData, currentLanguage)) {
                is Result.Success -> {
                    val vqr = result.data
                    val emergency = emergencyDetector.detect(vqr.transcript, currentLanguage)

                    _state.update {
                        it.copy(
                            phase = QueryPhase.RESULT,
                            queryResult = QueryResult(
                                answer = vqr.answer,
                                evidenceLevel = EvidenceLevel.fromString(vqr.evidenceLevel),
                                emergencyLevel = emergency,
                                confidence = vqr.confidence,
                                transcript = vqr.transcript,
                            ),
                            isEmergency = emergency == EmergencyLevel.CRITICAL,
                        )
                    }

                    vqr.audioResponse?.let { audioPlayer.play(it) }
                }
                is Result.Error -> {
                    _state.update { it.copy(phase = QueryPhase.IDLE, error = result.message ?: "Query failed") }
                }
                is Result.Loading -> {}
            }
        } else {
            when (val sttResult = onDeviceVoice.speechToText(audioData, currentLanguage)) {
                is Result.Success -> {
                    val transcript = sttResult.data
                    val emergency = emergencyDetector.detect(transcript, currentLanguage)

                    if (emergency == EmergencyLevel.CRITICAL) {
                        _state.update {
                            it.copy(
                                phase = QueryPhase.RESULT,
                                isEmergency = true,
                                queryResult = QueryResult(
                                    answer = emergencyDetector.getEmergencyMessage(currentLanguage),
                                    emergencyLevel = EmergencyLevel.CRITICAL,
                                    transcript = transcript,
                                ),
                            )
                        }
                        return
                    }

                    val hash = sha256(transcript.lowercase().trim())
                    val cached = queryCacheDao.getByHash(hash, languageMapper.toBcp47(currentLanguage))
                    if (cached != null) {
                        queryCacheDao.incrementAccessCount(hash)
                        _state.update {
                            it.copy(
                                phase = QueryPhase.RESULT,
                                queryResult = QueryResult(
                                    answer = cached.responseText,
                                    evidenceLevel = EvidenceLevel.fromString(cached.evidenceLevel),
                                    transcript = transcript,
                                ),
                                isOfflineResult = true,
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                phase = QueryPhase.RESULT,
                                queryResult = QueryResult(
                                    answer = "This question requires a network connection. Please try again when online.",
                                    transcript = transcript,
                                ),
                                isOfflineResult = true,
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(phase = QueryPhase.IDLE, error = "Speech recognition failed") }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearResult() {
        _state.update { QueryUiState() }
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}

data class QueryUiState(
    val phase: QueryPhase = QueryPhase.IDLE,
    val queryResult: QueryResult? = null,
    val isEmergency: Boolean = false,
    val isOfflineResult: Boolean = false,
    val error: String? = null,
)

enum class QueryPhase { IDLE, RECORDING, PROCESSING, RESULT }
