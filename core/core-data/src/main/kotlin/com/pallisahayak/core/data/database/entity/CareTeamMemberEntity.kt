package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "care_team_members",
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = ["patientId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("patientId")],
)
data class CareTeamMemberEntity(
    @PrimaryKey val memberId: String,
    val patientId: String,
    val name: String,
    val role: String,
    val organization: String? = null,
    val phoneNumber: String? = null,
    val primaryContact: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncAt: Long = 0L,
    val syncStatus: String = "pending",
)
