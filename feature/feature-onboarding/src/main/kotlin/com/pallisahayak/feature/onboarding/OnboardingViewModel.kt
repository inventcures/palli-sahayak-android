package com.pallisahayak.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.model.user.UserRole
import com.pallisahayak.core.network.api.PalliSahayakApiService
import com.pallisahayak.core.network.dto.RegisterRequest
import com.pallisahayak.core.network.interceptor.AuthInterceptor
import com.pallisahayak.core.security.PinManager
import com.pallisahayak.core.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val pinManager: PinManager,
    private val tokenManager: TokenManager,
    private val authInterceptor: AuthInterceptor,
    private val apiService: PalliSahayakApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state

    fun selectLanguage(code: String) {
        _state.update { it.copy(selectedLanguage = code) }
    }

    fun acceptConsent() {
        _state.update { it.copy(consentAccepted = true) }
    }

    fun selectRole(role: UserRole) {
        _state.update { it.copy(selectedRole = role) }
    }

    fun setName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun setPhone(phone: String) {
        _state.update { it.copy(phone = phone) }
    }

    fun setAbhaId(abhaId: String) {
        _state.update { it.copy(abhaId = abhaId.ifBlank { null }) }
    }

    fun setupPin(pin: String) {
        viewModelScope.launch {
            _state.update { it.copy(isRegistering = true, error = null) }

            pinManager.setPin(pin)
            val userId = UUID.randomUUID().toString()
            val phoneHash = hashPhone(_state.value.phone)

            try {
                val response = apiService.register(
                    RegisterRequest(
                        name = _state.value.name,
                        phone_hash = phoneHash,
                        role = _state.value.selectedRole.value,
                        language = _state.value.selectedLanguage,
                        site_id = _state.value.siteId,
                        pin = pin,
                        abha_id = _state.value.abhaId,
                    )
                )
                tokenManager.saveTokens(
                    accessToken = response.token,
                    refreshToken = response.refresh_token,
                    expiresAt = response.expires_at.toLong(),
                    userId = response.user_id,
                )
                authInterceptor.setToken(response.token)
                _state.update { it.copy(isRegistering = false, registrationComplete = true) }
            } catch (e: Exception) {
                tokenManager.saveTokens(
                    accessToken = "offline-token",
                    refreshToken = "offline-refresh",
                    expiresAt = System.currentTimeMillis() / 1000 + 86400 * 30,
                    userId = userId,
                )
                _state.update { it.copy(isRegistering = false, registrationComplete = true) }
            }
        }
    }

    private fun hashPhone(phone: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(phone.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}

data class OnboardingState(
    val selectedLanguage: String = "en",
    val consentAccepted: Boolean = false,
    val selectedRole: UserRole = UserRole.ASHA_WORKER,
    val name: String = "",
    val phone: String = "",
    val abhaId: String? = null,
    val siteId: String = "all",
    val isRegistering: Boolean = false,
    val registrationComplete: Boolean = false,
    val error: String? = null,
)
