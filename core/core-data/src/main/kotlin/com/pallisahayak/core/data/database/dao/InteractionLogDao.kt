package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.InteractionLogEntity

@Dao
interface InteractionLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: InteractionLogEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(logs: List<InteractionLogEntity>)

    @Query("SELECT * FROM interaction_logs WHERE syncStatus = 'pending' ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getPendingSync(limit: Int = 50): List<InteractionLogEntity>

    @Query("UPDATE interaction_logs SET syncStatus = :status WHERE logId IN (:ids)")
    suspend fun updateSyncStatus(ids: List<String>, status: String)

    @Query("SELECT * FROM interaction_logs WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp ASC")
    suspend fun getLogsInRange(startDate: Long, endDate: Long): List<InteractionLogEntity>

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE userId = :userId AND eventType = :eventType AND timestamp > :since")
    suspend fun countEvents(userId: String, eventType: String, since: Long): Int
}
