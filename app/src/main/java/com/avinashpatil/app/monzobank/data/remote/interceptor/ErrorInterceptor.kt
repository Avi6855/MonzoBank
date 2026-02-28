package com.avinashpatil.app.monzobank.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorInterceptor @Inject constructor() : Interceptor {
    
    companion object {
        private const val TAG = "ErrorInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        return try {
            val response = chain.proceed(request)
            
            // Handle HTTP error codes
            when (response.code) {
                401 -> {
                    Log.w(TAG, "Unauthorized request to ${request.url}")
                    // In a real app, you might trigger token refresh here
                }
                403 -> {
                    Log.w(TAG, "Forbidden request to ${request.url}")
                }
                404 -> {
                    Log.w(TAG, "Not found: ${request.url}")
                }
                500, 502, 503, 504 -> {
                    Log.e(TAG, "Server error ${response.code} for ${request.url}")
                }
            }
            
            response
        } catch (e: IOException) {
            Log.e(TAG, "Network error for ${request.url}", e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error for ${request.url}", e)
            throw e
        }
    }
}