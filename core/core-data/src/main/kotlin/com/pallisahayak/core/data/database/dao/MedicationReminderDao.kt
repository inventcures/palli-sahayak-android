package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.MedicationReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationReminderDao {
    @Query("SELECT * FROM medication_reminders WHERE patientId = :patientId AND isActive = 1 ORDER BY scheduledTime ASC")
    fun getActiveReminders(patientId: String): Flow<List<MedicationReminderEntity>>

    @Query("SELECT * FROM medication_reminders WHERE isActive = 1 ORDER BY scheduledTime ASC")
    fun getAllActiveReminders(): Flow<List<MedicationReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: MedicationReminderEntity)

    @Query("SELECT * FROM medication_reminders WHERE syncStatus = 'pending'")
    suspend fun getPendingSync(): List<MedicationReminderEntity>

    @Query("UPDATE medication_reminders SET syncStatus = :status WHERE reminderId IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: String)

    @Query("SELECT COUNT(*) FROM medication_reminders WHERE patientId = :patientId AND patientConfirmed = 1 AND scheduledTime > :since")
    suspend fun getConfirmedCount(patientId: String, since: Long): Int

    @Query("SELECT COUNT(*) FROM medication_reminders WHERE patientId = :patientId AND scheduledTime > :since")
    suspend fun getTotalCount(patientId: String, since: Long): Int
}
