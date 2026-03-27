package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val patientId: String,
    val name: String,
    val primaryCondition: String? = null,
    val conditionStage: String? = null,
    val careLocation: String? = null,
    val assignedUserId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncAt: Long = 0L,
    val syncStatus: String = "pending",
)
