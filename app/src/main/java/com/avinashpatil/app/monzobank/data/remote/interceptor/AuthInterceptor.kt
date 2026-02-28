package com.avinashpatil.app.monzobank.data.remote.interceptor

import com.avinashpatil.app.monzobank.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip authentication for login/register endpoints
        val url = originalRequest.url.toString()
        if (url.contains("/auth/login") || url.contains("/auth/register") || url.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }
        
        // Add authorization header for authenticated requests
        val token = runCatching {
            // This would normally be a suspend function call, but interceptors are synchronous
            // In a real implementation, you'd need to handle this differently
            // For now, we'll just add a placeholder
            "Bearer placeholder_token"
        }.getOrNull()
        
        val authenticatedRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(authenticatedRequest)
    }
}