package com.pallisahayak.feature.patient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.data.database.dao.CareTeamMemberDao
import com.pallisahayak.core.data.database.dao.ObservationDao
import com.pallisahayak.core.data.database.dao.PatientDao
import com.pallisahayak.core.data.database.entity.ObservationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val patientDao: PatientDao,
    private val observationDao: ObservationDao,
    private val careTeamDao: CareTeamMemberDao,
) : ViewModel() {

    private val patientId: String = savedStateHandle["patientId"] ?: ""

    val patient = patientDao.getPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val observations = observationDao.getObservationsByPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val careTeam = careTeamDao.getMembersByPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun recordObservation(
        category: String,
        entityName: String,
        severity: Int?,
        valueText: String?,
    ) {
        viewModelScope.launch {
            val observation = ObservationEntity(
                observationId = UUID.randomUUID().toString(),
                patientId = patientId,
                timestamp = System.currentTimeMillis(),
                sourceType = "app",
                reportedBy = "asha_worker",
                category = category,
                entityName = entityName,
                severity = severity,
                valueText = valueText,
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending",
            )
            observationDao.insert(observation)
        }
    }
}
