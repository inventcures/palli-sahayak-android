package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.QueryCacheEntity

@Dao
interface QueryCacheDao {
    @Query("SELECT * FROM query_cache WHERE queryHash = :hash AND language = :language AND expiresAt > :now")
    suspend fun getByHash(hash: String, language: String, now: Long = System.currentTimeMillis()): QueryCacheEntity?

    @Query("SELECT * FROM query_cache WHERE language = :language AND expiresAt > :now ORDER BY accessCount DESC")
    suspend fun getAllValid(language: String, now: Long = System.currentTimeMillis()): List<QueryCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: QueryCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(caches: List<QueryCacheEntity>)

    @Query("UPDATE query_cache SET accessCount = accessCount + 1 WHERE queryHash = :hash")
    suspend fun incrementAccessCount(hash: String)

    @Query("DELETE FROM query_cache WHERE expiresAt < :now")
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())
}
