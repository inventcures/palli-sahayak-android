package com.pallisahayak.core.fhir

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbhaIdValidator @Inject constructor() {

    fun validate(abhaId: String): Boolean {
        val cleaned = abhaId.replace("-", "").replace(" ", "")
        return cleaned.length == 14 && cleaned.all { it.isDigit() }
    }

    fun format(abhaId: String): String {
        val cleaned = abhaId.replace("-", "").replace(" ", "")
        if (cleaned.length != 14) return abhaId
        return "${cleaned.substring(0, 2)}-${cleaned.substring(2, 6)}-${cleaned.substring(6, 10)}-${cleaned.substring(10, 14)}"
    }
}
