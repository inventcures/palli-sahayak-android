package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.ObservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {
    @Query("SELECT * FROM observations WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getObservationsByPatient(patientId: String): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations WHERE patientId = :patientId AND category = :category ORDER BY timestamp DESC")
    fun getObservationsByCategory(patientId: String, category: String): Flow<List<ObservationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(observation: ObservationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(observations: List<ObservationEntity>)

    @Query("SELECT * FROM observations WHERE syncStatus = 'pending'")
    suspend fun getPendingSync(): List<ObservationEntity>

    @Query("UPDATE observations SET syncStatus = :status WHERE observationId IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: String)
}
