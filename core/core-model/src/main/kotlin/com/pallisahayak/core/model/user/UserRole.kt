package com.pallisahayak.core.model.user

enum class UserRole(val value: String) {
    ASHA_WORKER("asha_worker"),
    CAREGIVER("caregiver"),
    PATIENT("patient");

    companion object {
        fun fromValue(value: String): UserRole =
            entries.firstOrNull { it.value == value } ?: PATIENT
    }
}

enum class CommunicationStyle(val value: String) {
    SIMPLE("simple"),
    DETAILED("detailed"),
    CLINICAL("clinical"),
    EMPATHETIC("empathetic");

    companion object {
        fun fromValue(value: String): CommunicationStyle =
            entries.firstOrNull { it.value == value } ?: SIMPLE
    }
}

data class UserProfile(
    val userId: String,
    val name: String,
    val phoneHash: String,
    val role: UserRole,
    val language: String,
    val communicationStyle: CommunicationStyle = CommunicationStyle.SIMPLE,
    val voiceSpeed: String = "normal",
    val siteId: String,
    val abhaId: String? = null,
    val digitalLiteracyScore: Int? = null,
)
