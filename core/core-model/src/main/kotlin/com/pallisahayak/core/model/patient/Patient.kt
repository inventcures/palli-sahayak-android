package com.pallisahayak.core.model.patient

enum class SeverityLevel(val value: Int) {
    NONE(0), MILD(1), MODERATE(2), SEVERE(3), VERY_SEVERE(4);

    companion object {
        fun fromValue(value: Int): SeverityLevel =
            entries.firstOrNull { it.value == value } ?: NONE
    }
}

enum class DataSourceType(val value: String) {
    VOICE_CALL("voice_call"),
    APP("app"),
    CAREGIVER_REPORT("caregiver_report"),
    CLINICAL_ENTRY("clinical_entry"),
    PATIENT_REPORTED("patient_reported"),
    FHIR_IMPORT("fhir_import");

    companion object {
        fun fromValue(value: String): DataSourceType =
            entries.firstOrNull { it.value == value } ?: APP
    }
}

enum class ObservationCategory(val value: String) {
    SYMPTOM("symptom"),
    MEDICATION("medication"),
    VITAL_SIGN("vital_sign"),
    FUNCTIONAL_STATUS("functional_status"),
    EMOTIONAL("emotional");

    companion object {
        fun fromValue(value: String): ObservationCategory =
            entries.firstOrNull { it.value == value } ?: SYMPTOM
    }
}

data class Patient(
    val patientId: String,
    val name: String,
    val primaryCondition: String? = null,
    val conditionStage: String? = null,
    val careLocation: String? = null,
    val assignedUserId: String? = null,
)

data class Observation(
    val observationId: String,
    val patientId: String,
    val timestamp: Long,
    val sourceType: DataSourceType,
    val reportedBy: String,
    val category: ObservationCategory,
    val entityName: String,
    val severity: SeverityLevel? = null,
    val valueText: String? = null,
    val location: String? = null,
    val duration: String? = null,
)
