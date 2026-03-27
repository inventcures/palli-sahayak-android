package com.pallisahayak.feature.vignette

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.data.database.dao.VignetteResponseDao
import com.pallisahayak.core.data.database.entity.VignetteResponseEntity
import com.pallisahayak.core.evaluation.InteractionLogger
import com.pallisahayak.core.evaluation.TimeMotionTracker
import com.pallisahayak.core.network.api.PalliSahayakApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VignetteViewModel @Inject constructor(
    private val apiService: PalliSahayakApiService,
    private val vignetteDao: VignetteResponseDao,
    private val interactionLogger: InteractionLogger,
    private val timeMotionTracker: TimeMotionTracker,
) : ViewModel() {

    private val _assignments = MutableStateFlow<List<VignetteAssignment>>(emptyList())
    val assignments: StateFlow<List<VignetteAssignment>> = _assignments

    private val _activeVignette = MutableStateFlow<ActiveVignette?>(null)
    val activeVignette: StateFlow<ActiveVignette?> = _activeVignette

    init {
        loadAssignments()
    }

    private fun loadAssignments() {
        viewModelScope.launch {
            try {
                val response = apiService.getVignettes()
                _assignments.value = response.vignettes.map { map ->
                    VignetteAssignment(
                        vignetteId = map["vignette_id"] as? String ?: "",
                        title = map["title"] as? String ?: "",
                        scenario = map["scenario"] as? String ?: "",
                        withTool = map["with_tool"] as? Boolean ?: false,
                        order = (map["order"] as? Number)?.toInt() ?: 0,
                    )
                }
            } catch (_: Exception) {
                // Will load from cache or retry on next sync
            }
        }
    }

    fun startVignette(assignment: VignetteAssignment) {
        val event = timeMotionTracker.startEvent("vignette_${assignment.vignetteId}")
        _activeVignette.value = ActiveVignette(
            assignment = assignment,
            startedAt = System.currentTimeMillis(),
            timedEvent = event,
        )
        viewModelScope.launch {
            interactionLogger.log("vignette_started", eventData = assignment.vignetteId)
        }
    }

    fun submitResponse(responseText: String) {
        val active = _activeVignette.value ?: return
        val completedAt = System.currentTimeMillis()

        viewModelScope.launch {
            timeMotionTracker.endEvent(active.timedEvent)

            val entity = VignetteResponseEntity(
                responseId = UUID.randomUUID().toString(),
                userId = "",
                vignetteId = active.assignment.vignetteId,
                vignetteTitle = active.assignment.title,
                withTool = active.assignment.withTool,
                responseText = responseText,
                startedAt = active.startedAt,
                completedAt = completedAt,
                durationMs = completedAt - active.startedAt,
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending",
            )
            vignetteDao.upsert(entity)

            interactionLogger.log(
                "vignette_completed",
                eventData = active.assignment.vignetteId,
                durationMs = entity.durationMs,
            )

            _activeVignette.value = null
        }
    }
}

data class VignetteAssignment(
    val vignetteId: String,
    val title: String,
    val scenario: String,
    val withTool: Boolean,
    val order: Int,
)

data class ActiveVignette(
    val assignment: VignetteAssignment,
    val startedAt: Long,
    val timedEvent: TimeMotionTracker.TimedEvent,
)
