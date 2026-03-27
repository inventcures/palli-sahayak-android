package com.pallisahayak.core.fhir

import com.pallisahayak.core.common.result.Result
import com.pallisahayak.core.network.api.PalliSahayakApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FhirExporter @Inject constructor(
    private val apiService: PalliSahayakApiService,
) {
    suspend fun exportPatientBundle(patientId: String): Result<Map<String, Any?>> {
        return try {
            val response = apiService.exportFhir(patientId)
            Result.Success(response.bundle)
        } catch (e: Exception) {
            Result.Error(e, "FHIR export failed: ${e.message}")
        }
    }
}
