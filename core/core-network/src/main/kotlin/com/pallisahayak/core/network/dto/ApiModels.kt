package com.pallisahayak.core.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HealthResponse(val status: String, val api_version: String)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val name: String,
    val phone_hash: String,
    val role: String,
    val language: String,
    val site_id: String,
    val pin: String,
    val abha_id: String? = null,
    val digital_literacy_score: Int? = null,
)

@JsonClass(generateAdapter = true)
data class LoginRequest(val user_id: String, val pin: String)

@JsonClass(generateAdapter = true)
data class AuthResponse(val user_id: String, val token: String, val expires_at: Double, val refresh_token: String)

@JsonClass(generateAdapter = true)
data class QueryRequest(val query: String, val language: String = "en-IN", val include_context: Boolean = true, val patient_id: String? = null)

@JsonClass(generateAdapter = true)
data class QueryResponse(
    val answer: String,
    val sources: List<Map<String, Any?>> = emptyList(),
    val evidence_level: String = "C",
    val emergency_level: String = "none",
    val confidence: Double = 0.0,
    val validation_status: String = "passed",
    val disclaimer: String? = null,
)

@JsonClass(generateAdapter = true)
data class VoiceQueryResponse(
    val transcript: String,
    val answer: String,
    val sources: List<Map<String, Any?>> = emptyList(),
    val evidence_level: String = "C",
    val emergency_level: String = "none",
    val confidence: Double = 0.0,
    val audio_base64: String? = null,
)

@JsonClass(generateAdapter = true)
data class SyncPushRequest(
    val observations: List<Map<String, Any?>> = emptyList(),
    val medication_reminders: List<Map<String, Any?>> = emptyList(),
    val interaction_logs: List<Map<String, Any?>> = emptyList(),
    val vignette_responses: List<Map<String, Any?>> = emptyList(),
    val device_timestamp: Double,
)

@JsonClass(generateAdapter = true)
data class SyncPushResponse(val accepted: Int, val rejected: Int, val conflicts: List<Map<String, Any?>> = emptyList())

@JsonClass(generateAdapter = true)
data class SyncPullResponse(
    val patients: List<Map<String, Any?>> = emptyList(),
    val observations: List<Map<String, Any?>> = emptyList(),
    val care_team_members: List<Map<String, Any?>> = emptyList(),
    val medication_reminders: List<Map<String, Any?>> = emptyList(),
    val query_cache_updates: List<Map<String, Any?>> = emptyList(),
    val vignette_assignments: List<Map<String, Any?>> = emptyList(),
    val server_timestamp: Double = 0.0,
)

@JsonClass(generateAdapter = true)
data class PatientsResponse(val patients: List<Map<String, Any?>> = emptyList())

@JsonClass(generateAdapter = true)
data class PatientDetailResponse(
    val patient: Map<String, Any?>,
    val observations: List<Map<String, Any?>> = emptyList(),
    val care_team: List<Map<String, Any?>> = emptyList(),
    val medication_reminders: List<Map<String, Any?>> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class CreateObservationRequest(
    val patient_id: String,
    val category: String,
    val entity_name: String,
    val value: String? = null,
    val value_text: String? = null,
    val severity: Int? = null,
    val location: String? = null,
    val duration: String? = null,
    val timestamp: Double? = null,
)

@JsonClass(generateAdapter = true)
data class ObservationResponse(val observation_id: String, val status: String)

@JsonClass(generateAdapter = true)
data class CreateReminderRequest(
    val patient_id: String,
    val medication_name: String,
    val dosage: String,
    val scheduled_time: Double,
    val language: String,
    val frequency: String? = null,
)

@JsonClass(generateAdapter = true)
data class ReminderResponse(val reminder_id: String, val status: String)

@JsonClass(generateAdapter = true)
data class RemindersResponse(val reminders: List<Map<String, Any?>> = emptyList())

@JsonClass(generateAdapter = true)
data class AdherenceResponse(val total: Int, val confirmed: Int, val missed: Int, val rate: Double)

@JsonClass(generateAdapter = true)
data class CareTeamResponse(val members: List<Map<String, Any?>> = emptyList())

@JsonClass(generateAdapter = true)
data class SusSubmitRequest(val scores: List<Int>, val site_id: String, val language: String, val completed_at: Double)

@JsonClass(generateAdapter = true)
data class SusSubmitResponse(val submission_id: String, val sus_score: Double, val status: String)

@JsonClass(generateAdapter = true)
data class VignettesResponse(val vignettes: List<Map<String, Any?>> = emptyList())

@JsonClass(generateAdapter = true)
data class VignetteSubmitRequest(
    val vignette_id: String,
    val with_tool: Boolean,
    val response_text: String? = null,
    val started_at: Double,
    val completed_at: Double,
    val metadata: Map<String, Any?>? = null,
)

@JsonClass(generateAdapter = true)
data class VignetteSubmitResponse(val submission_id: String, val status: String)

@JsonClass(generateAdapter = true)
data class InteractionLogBatchRequest(val logs: List<Map<String, Any?>>, val device_id: String, val batch_timestamp: Double)

@JsonClass(generateAdapter = true)
data class InteractionLogBatchResponse(val accepted: Int, val status: String)

@JsonClass(generateAdapter = true)
data class CacheBundleResponse(
    val version: String,
    val language: String,
    val generated_at: Double,
    val queries: List<Map<String, Any?>> = emptyList(),
    val treatments: List<Map<String, Any?>> = emptyList(),
    val emergency_keywords: Map<String, List<String>> = emptyMap(),
    val evidence_badge_metadata: Map<String, Map<String, String>> = emptyMap(),
)

@JsonClass(generateAdapter = true)
data class FhirExportResponse(val bundle: Map<String, Any?> = emptyMap())
