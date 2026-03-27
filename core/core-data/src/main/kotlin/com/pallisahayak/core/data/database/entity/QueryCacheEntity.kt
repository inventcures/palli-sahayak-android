package com.pallisahayak.core.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "query_cache",
    indices = [Index("language"), Index("expiresAt")],
)
data class QueryCacheEntity(
    @PrimaryKey val queryHash: String,
    val queryText: String,
    val language: String,
    val responseText: String,
    val responseJson: String,
    val evidenceLevel: String,
    val sources: String,
    val cachedAt: Long,
    val expiresAt: Long,
    val accessCount: Int = 0,
)
