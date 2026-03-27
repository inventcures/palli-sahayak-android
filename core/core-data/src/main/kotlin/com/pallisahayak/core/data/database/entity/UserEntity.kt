package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val phoneHash: String,
    val role: String,
    val language: String,
    val communicationStyle: String = "simple",
    val voiceSpeed: String = "normal",
    val siteId: String,
    val abhaId: String? = null,
    val digitalLiteracyScore: Int? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncAt: Long = 0L,
    val syncStatus: String = "pending",
)
