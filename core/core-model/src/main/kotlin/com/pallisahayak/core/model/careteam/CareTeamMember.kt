package com.pallisahayak.core.model.careteam

enum class CareTeamRole(val value: String) {
    DOCTOR("doctor"),
    NURSE("nurse"),
    ASHA_WORKER("asha_worker"),
    CAREGIVER("caregiver"),
    VOLUNTEER("volunteer"),
    SOCIAL_WORKER("social_worker");

    companion object {
        fun fromValue(value: String): CareTeamRole =
            entries.firstOrNull { it.value == value } ?: CAREGIVER
    }
}

data class CareTeamMember(
    val memberId: String,
    val patientId: String,
    val name: String,
    val role: CareTeamRole,
    val organization: String? = null,
    val phoneNumber: String? = null,
    val primaryContact: Boolean = false,
)
