package com.pallisahayak.feature.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.data.database.dao.MedicationReminderDao
import com.pallisahayak.core.data.database.entity.MedicationReminderEntity
import com.pallisahayak.core.network.api.PalliSahayakApiService
import com.pallisahayak.core.network.dto.CreateReminderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val reminderDao: MedicationReminderDao,
    private val apiService: PalliSahayakApiService,
) : ViewModel() {

    val activeReminders = reminderDao.getAllActiveReminders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _createState = MutableStateFlow<CreateState>(CreateState.Idle)
    val createState: StateFlow<CreateState> = _createState

    fun createReminder(
        patientId: String,
        medicationName: String,
        dosage: String,
        scheduledTime: Long,
        language: String,
    ) {
        viewModelScope.launch {
            _createState.value = CreateState.Loading

            val reminderId = UUID.randomUUID().toString()
            val entity = MedicationReminderEntity(
                reminderId = reminderId,
                patientId = patientId,
                medicationName = medicationName,
                dosage = dosage,
                scheduledTime = scheduledTime,
                language = language,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                syncStatus = "pending",
            )

            reminderDao.upsert(entity)

            try {
                apiService.createReminder(
                    CreateReminderRequest(
                        patient_id = patientId,
                        medication_name = medicationName,
                        dosage = dosage,
                        scheduled_time = scheduledTime.toDouble() / 1000,
                        language = language,
                    )
                )
                reminderDao.updateSyncStatus(listOf(reminderId), "synced")
            } catch (_: Exception) {
                // Will sync later via WorkManager
            }

            _createState.value = CreateState.Success
        }
    }

    fun resetCreateState() {
        _createState.value = CreateState.Idle
    }

    sealed class CreateState {
        data object Idle : CreateState()
        data object Loading : CreateState()
        data object Success : CreateState()
        data class Error(val message: String) : CreateState()
    }
}
