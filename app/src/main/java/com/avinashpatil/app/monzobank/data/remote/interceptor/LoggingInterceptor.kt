package com.avinashpatil.app.monzobank.data.remote.interceptor

import android.util.Log
import com.avinashpatil.app.monzobank.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingInterceptor @Inject constructor() : Interceptor {
    
    companion object {
        private const val TAG = "API_LOG"
        private val UTF8 = Charset.forName("UTF-8")
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        if (!BuildConfig.DEBUG) {
            return chain.proceed(request)
        }
        
        val startTime = System.nanoTime()
        
        // Log request
        Log.d(TAG, "→ ${request.method} ${request.url}")
        Log.d(TAG, "Headers: ${request.headers}")
        
        // Log request body if present
        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            val charset = body.contentType()?.charset(UTF8) ?: UTF8
            if (isPlaintext(buffer)) {
                Log.d(TAG, "Request Body: ${buffer.readString(charset)}")
            } else {
                Log.d(TAG, "Request Body: [binary ${body.contentLength()} bytes]")
            }
        }
        
        val response = chain.proceed(request)
        val endTime = System.nanoTime()
        
        // Log response
        Log.d(TAG, "← ${response.code} ${response.message} ${request.url} (${(endTime - startTime) / 1e6}ms)")
        Log.d(TAG, "Response Headers: ${response.headers}")
        
        // Log response body if present
        response.body?.let { body ->
            val source = body.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            val charset = body.contentType()?.charset(UTF8) ?: UTF8
            
            if (isPlaintext(buffer)) {
                Log.d(TAG, "Response Body: ${buffer.clone().readString(charset)}")
            } else {
                Log.d(TAG, "Response Body: [binary ${buffer.size} bytes]")
            }
        }
        
        return response
    }
    
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) break
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}