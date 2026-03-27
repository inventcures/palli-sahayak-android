package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY updatedAt DESC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE assignedUserId = :userId ORDER BY updatedAt DESC")
    fun getPatientsByUser(userId: String): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE patientId = :patientId")
    fun getPatient(patientId: String): Flow<PatientEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(patient: PatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(patients: List<PatientEntity>)

    @Query("SELECT * FROM patients WHERE syncStatus = 'pending'")
    suspend fun getPendingSync(): List<PatientEntity>

    @Query("UPDATE patients SET syncStatus = :status, lastSyncAt = :syncAt WHERE patientId IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: String, syncAt: Long = System.currentTimeMillis())

    @Query("SELECT MAX(lastSyncAt) FROM patients")
    suspend fun getLastSyncTimestamp(): Long?
}
