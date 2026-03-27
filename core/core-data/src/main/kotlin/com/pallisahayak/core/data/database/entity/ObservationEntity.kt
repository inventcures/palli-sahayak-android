package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "observations",
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index("patientId"),
        Index("timestamp"),
        Index("syncStatus"),
        Index("category"),
    ],
)
data class ObservationEntity(
    @PrimaryKey val observationId: String,
    val patientId: String,
    val timestamp: Long,
    val sourceType: String,
    val reportedBy: String,
    val category: String,
    val entityName: String,
    val value: String? = null,
    val valueText: String? = null,
    val severity: Int? = null,
    val location: String? = null,
    val duration: String? = null,
    val metadata: String? = null,
    val createdAt: Long,
    val syncStatus: String = "pending",
)
