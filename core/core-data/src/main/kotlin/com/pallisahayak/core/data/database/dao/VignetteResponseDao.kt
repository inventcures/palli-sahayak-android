package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.VignetteResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VignetteResponseDao {
    @Query("SELECT * FROM vignette_responses WHERE userId = :userId ORDER BY createdAt DESC")
    fun getResponsesByUser(userId: String): Flow<List<VignetteResponseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(response: VignetteResponseEntity)

    @Query("SELECT * FROM vignette_responses WHERE syncStatus = 'pending'")
    suspend fun getPendingSync(): List<VignetteResponseEntity>

    @Query("UPDATE vignette_responses SET syncStatus = :status WHERE responseId IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: String)

    @Query("SELECT COUNT(*) FROM vignette_responses WHERE userId = :userId")
    suspend fun getCompletedCount(userId: String): Int
}
