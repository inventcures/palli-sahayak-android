package com.pallisahayak.core.evaluation

import com.pallisahayak.core.data.database.dao.InteractionLogDao
import com.pallisahayak.core.data.database.entity.InteractionLogEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InteractionLogger @Inject constructor(
    private val logDao: InteractionLogDao,
) {
    private var currentSessionId: String = UUID.randomUUID().toString()
    private var userId: String = ""
    private var language: String = "en"
    private var siteId: String = "all"

    fun initSession(userId: String, language: String, siteId: String) {
        this.currentSessionId = UUID.randomUUID().toString()
        this.userId = userId
        this.language = language
        this.siteId = siteId
    }

    suspend fun log(
        eventType: String,
        eventData: String? = null,
        durationMs: Long? = null,
        isOffline: Boolean = false,
    ) {
        val entity = InteractionLogEntity(
            logId = UUID.randomUUID().toString(),
            userId = userId,
            sessionId = currentSessionId,
            eventType = eventType,
            eventData = eventData,
            timestamp = System.currentTimeMillis(),
            durationMs = durationMs,
            language = language,
            siteId = siteId,
            isOffline = isOffline,
            syncStatus = "pending",
        )
        logDao.insert(entity)
    }

    suspend fun logSessionStart() = log("session_start")
    suspend fun logSessionEnd() = log("session_end")
    suspend fun logQueryStart() = log("query_voice_start")
    suspend fun logQueryComplete(durationMs: Long) = log("query_response_received", durationMs = durationMs)
    suspend fun logEmergencyDetected(language: String) = log("emergency_detected", eventData = language)
    suspend fun logOfflineCacheHit() = log("query_offline_cache_hit", isOffline = true)
    suspend fun logOfflineCacheMiss() = log("query_offline_cache_miss", isOffline = true)
    suspend fun logScreenView(screen: String) = log("screen_view", eventData = screen)
}

object InteractionEventTypes {
    const val SESSION_START = "session_start"
    const val SESSION_END = "session_end"
    const val QUERY_VOICE_START = "query_voice_start"
    const val QUERY_VOICE_RECORDING_COMPLETE = "query_voice_recording_complete"
    const val QUERY_STT_COMPLETE = "query_stt_complete"
    const val QUERY_SUBMITTED = "query_submitted"
    const val QUERY_RESPONSE_RECEIVED = "query_response_received"
    const val QUERY_TTS_PLAYED = "query_tts_played"
    const val QUERY_OFFLINE_CACHE_HIT = "query_offline_cache_hit"
    const val QUERY_OFFLINE_CACHE_MISS = "query_offline_cache_miss"
    const val EMERGENCY_DETECTED = "emergency_detected"
    const val EMERGENCY_CALL_INITIATED = "emergency_call_initiated"
    const val EVIDENCE_BADGE_SHOWN = "evidence_badge_shown"
    const val LANGUAGE_DETECTED = "language_detected"
    const val OBSERVATION_CREATED = "observation_created"
    const val MEDICATION_REMINDER_CREATED = "medication_reminder_created"
    const val SUS_COMPLETED = "sus_completed"
    const val VIGNETTE_STARTED = "vignette_started"
    const val VIGNETTE_COMPLETED = "vignette_completed"
    const val SYNC_COMPLETED = "sync_completed"
    const val WENT_OFFLINE = "went_offline"
    const val CAME_ONLINE = "came_online"
}
