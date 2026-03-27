package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_reminders",
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("patientId"), Index("scheduledTime"), Index("syncStatus")],
)
data class MedicationReminderEntity(
    @PrimaryKey val reminderId: String,
    val patientId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String? = null,
    val scheduledTime: Long,
    val language: String,
    val callStatus: String = "scheduled",
    val callAttempts: Int = 0,
    val patientConfirmed: Boolean = false,
    val confirmationMethod: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncAt: Long = 0L,
    val syncStatus: String = "pending",
)
