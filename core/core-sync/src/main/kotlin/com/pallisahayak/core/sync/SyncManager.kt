package com.pallisahayak.core.sync

import androidx.work.*
import com.pallisahayak.core.common.constants.AppConstants
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
) {
    companion object {
        private const val PERIODIC_SYNC = "periodic_sync"
        private const val IMMEDIATE_SYNC = "immediate_sync"
        private const val CACHE_SYNC = "cache_bundle_sync"
    }

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            AppConstants.SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES,
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest,
        )
    }

    fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            IMMEDIATE_SYNC,
            ExistingWorkPolicy.REPLACE,
            syncRequest,
        )
    }

    fun scheduleCacheBundleRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val cacheRequest = PeriodicWorkRequestBuilder<CacheBundleSyncWorker>(
            24, TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            CACHE_SYNC,
            ExistingPeriodicWorkPolicy.KEEP,
            cacheRequest,
        )
    }

    fun cancelAll() {
        workManager.cancelUniqueWork(PERIODIC_SYNC)
        workManager.cancelUniqueWork(CACHE_SYNC)
    }
}
