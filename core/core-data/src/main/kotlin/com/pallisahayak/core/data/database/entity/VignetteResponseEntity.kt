package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vignette_responses",
    indices = [Index("userId"), Index("syncStatus")],
)
data class VignetteResponseEntity(
    @PrimaryKey val responseId: String,
    val userId: String,
    val vignetteId: String,
    val vignetteTitle: String = "",
    val withTool: Boolean,
    val responseText: String? = null,
    val responseAudioPath: String? = null,
    val startedAt: Long,
    val completedAt: Long,
    val durationMs: Long,
    val metadata: String? = null,
    val createdAt: Long,
    val syncStatus: String = "pending",
)
