package com.pallisahayak.core.model.evaluation

data class SusScore(
    val submissionId: String,
    val userId: String,
    val scores: List<Int>,
    val susScore: Float,
    val siteId: String,
    val language: String,
    val completedAt: Long,
)

data class VignetteAssignment(
    val vignetteId: String,
    val withTool: Boolean,
    val order: Int,
)

data class VignetteResponse(
    val responseId: String,
    val userId: String,
    val vignetteId: String,
    val withTool: Boolean,
    val responseText: String? = null,
    val startedAt: Long,
    val completedAt: Long,
    val durationMs: Long,
)

data class InteractionEvent(
    val logId: String,
    val userId: String,
    val sessionId: String,
    val eventType: String,
    val eventData: String? = null,
    val timestamp: Long,
    val durationMs: Long? = null,
    val language: String,
    val siteId: String,
    val isOffline: Boolean = false,
)
