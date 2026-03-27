package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "interaction_logs",
    indices = [Index("userId"), Index("sessionId"), Index("timestamp"), Index("syncStatus")],
)
data class InteractionLogEntity(
    @PrimaryKey val logId: String,
    val userId: String,
    val sessionId: String,
    val eventType: String,
    val eventData: String? = null,
    val timestamp: Long,
    val durationMs: Long? = null,
    val language: String,
    val siteId: String,
    val isOffline: Boolean = false,
    val syncStatus: String = "pending",
)
