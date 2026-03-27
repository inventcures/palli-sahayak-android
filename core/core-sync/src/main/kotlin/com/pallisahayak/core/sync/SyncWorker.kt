package com.pallisahayak.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pallisahayak.core.data.database.dao.*
import com.pallisahayak.core.network.api.PalliSahayakApiService
import com.pallisahayak.core.network.dto.SyncPushRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val apiService: PalliSahayakApiService,
    private val observationDao: ObservationDao,
    private val patientDao: PatientDao,
    private val reminderDao: MedicationReminderDao,
    private val interactionLogDao: InteractionLogDao,
    private val vignetteResponseDao: VignetteResponseDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            pushPendingData()
            pullServerChanges()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun pushPendingData() {
        val pendingObservations = observationDao.getPendingSync()
        val pendingReminders = reminderDao.getPendingSync()
        val pendingLogs = interactionLogDao.getPendingSync()
        val pendingVignettes = vignetteResponseDao.getPendingSync()

        if (pendingObservations.isEmpty() && pendingReminders.isEmpty() &&
            pendingLogs.isEmpty() && pendingVignettes.isEmpty()) return

        val response = apiService.syncPush(
            SyncPushRequest(
                observations = pendingObservations.map { mapOf(
                    "observation_id" to it.observationId,
                    "patient_id" to it.patientId,
                    "timestamp" to it.timestamp,
                    "category" to it.category,
                    "entity_name" to it.entityName,
                    "severity" to it.severity,
                    "value_text" to it.valueText,
                    "source_type" to it.sourceType,
                ) },
                medication_reminders = pendingReminders.map { mapOf(
                    "reminder_id" to it.reminderId,
                    "patient_id" to it.patientId,
                    "medication_name" to it.medicationName,
                    "dosage" to it.dosage,
                    "scheduled_time" to it.scheduledTime,
                    "call_status" to it.callStatus,
                ) },
                interaction_logs = pendingLogs.map { mapOf(
                    "log_id" to it.logId,
                    "event_type" to it.eventType,
                    "timestamp" to it.timestamp,
                    "duration_ms" to it.durationMs,
                    "language" to it.language,
                    "site_id" to it.siteId,
                ) },
                vignette_responses = pendingVignettes.map { mapOf(
                    "response_id" to it.responseId,
                    "vignette_id" to it.vignetteId,
                    "with_tool" to it.withTool,
                    "started_at" to it.startedAt,
                    "completed_at" to it.completedAt,
                ) },
                device_timestamp = System.currentTimeMillis().toDouble() / 1000,
            )
        )

        if (response.accepted > 0) {
            observationDao.updateSyncStatus(pendingObservations.map { it.observationId }, "synced")
            reminderDao.updateSyncStatus(pendingReminders.map { it.reminderId }, "synced")
            interactionLogDao.updateSyncStatus(pendingLogs.map { it.logId }, "synced")
            vignetteResponseDao.updateSyncStatus(pendingVignettes.map { it.responseId }, "synced")
        }
    }

    private suspend fun pullServerChanges() {
        val lastSync = patientDao.getLastSyncTimestamp() ?: 0L
        val response = apiService.syncPull(lastSync.toDouble() / 1000)

        // Server data would be mapped to entities and upserted here
        // Implementation depends on the exact response format from the server
    }
}
