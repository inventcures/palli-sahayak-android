package com.pallisahayak.feature.patient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.data.database.dao.CareTeamMemberDao
import com.pallisahayak.core.data.database.dao.ObservationDao
import com.pallisahayak.core.data.database.dao.PatientDao
import com.pallisahayak.core.data.database.entity.ObservationEntity
import com.pallisahayak.core.network.api.PalliSahayakApiService
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
    private val apiService: PalliSahayakApiService,
) : ViewModel() {

    private val patientId: String = savedStateHandle["patientId"] ?: ""

    val patient = patientDao.getPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val observations = observationDao.getObservationsByPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val careTeam = careTeamDao.getMembersByPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _insights = MutableStateFlow<List<ConsolidatedInsight>>(emptyList())
    val insights: StateFlow<List<ConsolidatedInsight>> = _insights

    private val _insightsLoading = MutableStateFlow(false)
    val insightsLoading: StateFlow<Boolean> = _insightsLoading

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            _insightsLoading.value = true
            try {
                val response = apiService.getPatientInsights(patientId)
                _insights.value = (response["insights"] as? List<*>)?.mapNotNull { item ->
                    val map = item as? Map<*, *> ?: return@mapNotNull null
                    ConsolidatedInsight(
                        id = map["id"] as? String ?: "",
                        insightText = map["insight_text"] as? String ?: "",
                        insightType = map["insight_type"] as? String ?: "trend",
                        sourceMemoryIds = (map["source_memory_ids"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        createdAt = (map["created_at"] as? Number)?.toLong() ?: 0L,
                    )
                } ?: emptyList()
            } catch (_: Exception) {
                _insights.value = emptyList()
            }
            _insightsLoading.value = false
        }
    }

    fun queryPatientMemory(question: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.queryPatientMemory(
                    patientId,
                    mapOf("question" to question),
                )
                val answer = response["answer"] as? String ?: "No insights available"
                onResult(answer)
            } catch (_: Exception) {
                onResult("Unable to query patient memory (offline)")
            }
        }
    }

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

data class ConsolidatedInsight(
    val id: String,
    val insightText: String,
    val insightType: String,
    val sourceMemoryIds: List<String>,
    val createdAt: Long,
)
