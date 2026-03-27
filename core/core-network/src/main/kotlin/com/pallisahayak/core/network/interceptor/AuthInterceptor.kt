package com.pallisahayak.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    @Volatile
    private var token: String? = null

    fun setToken(token: String?) {
        this.token = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val currentToken = token

        val authenticatedRequest = if (currentToken != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        } else {
            request
        }

        return chain.proceed(authenticatedRequest)
    }
}
