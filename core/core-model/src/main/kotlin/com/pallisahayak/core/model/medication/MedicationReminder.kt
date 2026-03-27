package com.pallisahayak.core.model.medication

enum class ReminderStatus(val value: String) {
    SCHEDULED("scheduled"),
    PENDING("pending"),
    CALLING("calling"),
    CONNECTED("connected"),
    COMPLETED("completed"),
    CONFIRMED("confirmed"),
    MISSED("missed"),
    FAILED("failed"),
    RETRYING("retrying");

    companion object {
        fun fromValue(value: String): ReminderStatus =
            entries.firstOrNull { it.value == value } ?: SCHEDULED
    }
}

data class MedicationReminder(
    val reminderId: String,
    val patientId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String? = null,
    val scheduledTime: Long,
    val language: String,
    val status: ReminderStatus = ReminderStatus.SCHEDULED,
    val patientConfirmed: Boolean = false,
    val isActive: Boolean = true,
)

data class AdherenceStats(
    val totalReminders: Int,
    val confirmed: Int,
    val missed: Int,
    val confirmationRate: Float,
    val periodDays: Int = 7,
)
