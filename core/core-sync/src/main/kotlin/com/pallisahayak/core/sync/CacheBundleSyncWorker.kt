package com.pallisahayak.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pallisahayak.core.common.constants.AppConstants
import com.pallisahayak.core.data.database.dao.QueryCacheDao
import com.pallisahayak.core.data.database.entity.QueryCacheEntity
import com.pallisahayak.core.network.api.PalliSahayakApiService
import com.pallisahayak.core.security.TokenManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CacheBundleSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val apiService: PalliSahayakApiService,
    private val queryCacheDao: QueryCacheDao,
    private val tokenManager: TokenManager,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val language = inputData.getString("language") ?: "en-IN"
            val response = apiService.getCacheBundle(language)

            val now = System.currentTimeMillis()
            val ttl = AppConstants.CACHE_TTL_DAYS * 24 * 60 * 60 * 1000L

            val cacheEntities = response.queries.mapNotNull { queryMap ->
                val hash = queryMap["query_hash"] as? String ?: return@mapNotNull null
                val text = queryMap["query_text"] as? String ?: return@mapNotNull null
                val responseText = queryMap["response_text"] as? String ?: return@mapNotNull null
                val evidenceLevel = queryMap["evidence_level"] as? String ?: "C"

                QueryCacheEntity(
                    queryHash = hash,
                    queryText = text,
                    language = language,
                    responseText = responseText,
                    responseJson = "{}",
                    evidenceLevel = evidenceLevel,
                    sources = "[]",
                    cachedAt = now,
                    expiresAt = now + ttl,
                    accessCount = 0,
                )
            }

            queryCacheDao.deleteExpired()
            queryCacheDao.upsertAll(cacheEntities)

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
