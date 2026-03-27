package com.pallisahayak.core.network.api

import com.pallisahayak.core.network.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PalliSahayakApiService {

    @GET("api/mobile/v1/health")
    suspend fun health(): HealthResponse

    @POST("api/mobile/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/mobile/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/mobile/v1/auth/refresh")
    suspend fun refreshToken(): AuthResponse

    @POST("api/mobile/v1/query")
    suspend fun query(@Body request: QueryRequest): QueryResponse

    @Multipart
    @POST("api/mobile/v1/query/voice")
    suspend fun voiceQuery(
        @Part audio: MultipartBody.Part,
        @Query("language") language: String,
    ): VoiceQueryResponse

    @POST("api/mobile/v1/sync/push")
    suspend fun syncPush(@Body request: SyncPushRequest): SyncPushResponse

    @POST("api/mobile/v1/sync/pull")
    suspend fun syncPull(@Query("last_sync_at") lastSyncAt: Double): SyncPullResponse

    @GET("api/mobile/v1/patients")
    suspend fun getPatients(): PatientsResponse

    @GET("api/mobile/v1/patient/{patientId}")
    suspend fun getPatient(@Path("patientId") patientId: String): PatientDetailResponse

    @POST("api/mobile/v1/observation")
    suspend fun createObservation(@Body request: CreateObservationRequest): ObservationResponse

    @GET("api/mobile/v1/medication/reminders")
    suspend fun getReminders(@Query("user_id") userId: String? = null): RemindersResponse

    @POST("api/mobile/v1/medication/reminder")
    suspend fun createReminder(@Body request: CreateReminderRequest): ReminderResponse

    @GET("api/mobile/v1/medication/adherence/{patientId}")
    suspend fun getAdherence(@Path("patientId") patientId: String): AdherenceResponse

    @GET("api/mobile/v1/careteam/{patientId}")
    suspend fun getCareTeam(@Path("patientId") patientId: String): CareTeamResponse

    @POST("api/mobile/v1/evaluation/sus")
    suspend fun submitSus(@Body request: SusSubmitRequest): SusSubmitResponse

    @GET("api/mobile/v1/evaluation/vignettes")
    suspend fun getVignettes(): VignettesResponse

    @POST("api/mobile/v1/evaluation/vignette")
    suspend fun submitVignette(@Body request: VignetteSubmitRequest): VignetteSubmitResponse

    @POST("api/mobile/v1/evaluation/logs")
    suspend fun submitInteractionLogs(@Body request: InteractionLogBatchRequest): InteractionLogBatchResponse

    @GET("api/mobile/v1/cache/bundle")
    suspend fun getCacheBundle(@Query("language") language: String): CacheBundleResponse

    @GET("api/mobile/v1/fhir/export/{patientId}")
    suspend fun exportFhir(@Path("patientId") patientId: String): FhirExportResponse

    @GET("api/mobile/v1/patient/{patientId}/insights")
    suspend fun getPatientInsights(@Path("patientId") patientId: String): Map<String, Any?>

    @POST("api/mobile/v1/patient/{patientId}/query-memory")
    suspend fun queryPatientMemory(
        @Path("patientId") patientId: String,
        @Body request: Map<String, String>,
    ): Map<String, Any?>

    @POST("api/mobile/v1/feedback")
    suspend fun submitFeedback(@Body feedback: Map<String, Any?>): Map<String, String>
}
