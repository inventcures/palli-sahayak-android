package com.pallisahayak.core.model.query

enum class EvidenceLevel(val label: String, val recommendation: String) {
    A("Strong Evidence", "Based on WHO guidelines or meta-analyses"),
    B("Good Evidence", "Based on clinical studies"),
    C("Moderate Evidence", "Based on expert consensus"),
    D("Limited Evidence", "Based on case reports — consult physician"),
    E("Insufficient Evidence", "Please consult your physician");

    companion object {
        fun fromString(value: String): EvidenceLevel =
            entries.firstOrNull { it.name == value.uppercase() } ?: C
    }
}

enum class EmergencyLevel {
    NONE, LOW, HIGH, CRITICAL
}

data class Source(
    val document: String,
    val page: Int? = null,
    val relevanceScore: Float = 0f,
    val snippet: String? = null,
)

data class QueryResult(
    val answer: String,
    val sources: List<Source> = emptyList(),
    val evidenceLevel: EvidenceLevel = EvidenceLevel.C,
    val emergencyLevel: EmergencyLevel = EmergencyLevel.NONE,
    val confidence: Float = 0f,
    val language: String = "en-IN",
    val validationStatus: String = "passed",
    val disclaimer: String? = null,
    val audioBase64: String? = null,
    val transcript: String? = null,
)
