package com.pallisahayak.core.fhir

import com.pallisahayak.core.model.patient.SeverityLevel

object SnomedCodes {

    val SYMPTOM_CODES = mapOf(
        "pain" to "22253000",
        "nausea" to "422587007",
        "vomiting" to "422400008",
        "breathlessness" to "267036007",
        "dyspnea" to "267036007",
        "anxiety" to "48694002",
        "depression" to "35489007",
        "constipation" to "14760008",
        "insomnia" to "193462001",
        "fatigue" to "84229001",
        "appetite_loss" to "79890006",
        "confusion" to "40917007",
        "cough" to "49727002",
        "diarrhea" to "62315008",
        "edema" to "267038008",
        "fever" to "386661006",
        "itching" to "418290006",
        "mouth_sores" to "26284000",
        "hiccups" to "65958008",
        "delirium" to "2776000",
    )

    val SEVERITY_CODES = mapOf(
        SeverityLevel.NONE to "260413007",
        SeverityLevel.MILD to "255604002",
        SeverityLevel.MODERATE to "6736007",
        SeverityLevel.SEVERE to "24484000",
        SeverityLevel.VERY_SEVERE to "442452003",
    )

    val MEDICATION_CODES = mapOf(
        "morphine" to "373529000",
        "paracetamol" to "387517004",
        "ibuprofen" to "387207008",
        "ondansetron" to "372487007",
        "metoclopramide" to "372776000",
        "lactulose" to "386943002",
        "gabapentin" to "386845007",
        "haloperidol" to "386837002",
        "dexamethasone" to "372584003",
        "lorazepam" to "386842001",
    )

    const val SNOMED_SYSTEM = "http://snomed.info/sct"
    const val LOINC_SYSTEM = "http://loinc.org"
    const val ABDM_SYSTEM = "https://healthid.abdm.gov.in"
}
