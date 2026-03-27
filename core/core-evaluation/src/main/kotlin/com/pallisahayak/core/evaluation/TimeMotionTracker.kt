package com.pallisahayak.core.evaluation

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeMotionTracker @Inject constructor(
    private val interactionLogger: InteractionLogger,
) {
    private val activeEvents = ConcurrentHashMap<String, TimedEvent>()

    data class TimedEvent(
        val eventName: String,
        val startTime: Long = System.currentTimeMillis(),
        var endTime: Long? = null,
    ) {
        val durationMs: Long? get() = endTime?.let { it - startTime }
    }

    fun startEvent(eventName: String): TimedEvent {
        val event = TimedEvent(eventName)
        activeEvents[eventName] = event
        return event
    }

    suspend fun endEvent(event: TimedEvent) {
        event.endTime = System.currentTimeMillis()
        activeEvents.remove(event.eventName)
        interactionLogger.log(
            eventType = "time_motion_${event.eventName}",
            durationMs = event.durationMs,
        )
    }

    suspend fun endEvent(eventName: String) {
        activeEvents.remove(eventName)?.let { endEvent(it) }
    }

    fun getActiveEvents(): Map<String, TimedEvent> = activeEvents.toMap()
}
