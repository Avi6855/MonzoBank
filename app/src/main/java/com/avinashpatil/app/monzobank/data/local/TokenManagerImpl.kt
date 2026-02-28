package com.avinashpatil.app.monzobank.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.avinashpatil.app.monzobank.data.remote.dto.auth.JwtToken
import com.avinashpatil.app.monzobank.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenManager {
    
    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            Constants.ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    override suspend fun saveToken(token: JwtToken) {
        encryptedPrefs.edit()
            .putString(Constants.PREF_ACCESS_TOKEN, token.token)
            .putString(Constants.PREF_REFRESH_TOKEN, token.refreshToken)
            .putLong(Constants.PREF_TOKEN_EXPIRES, token.expiresAt)
            .putString(Constants.PREF_TOKEN_TYPE, token.tokenType)
            .apply()
    }
    
    override suspend fun getToken(): JwtToken? {
        val token = encryptedPrefs.getString(Constants.PREF_ACCESS_TOKEN, null)
        val refreshToken = encryptedPrefs.getString(Constants.PREF_REFRESH_TOKEN, null)
        val expiresAt = encryptedPrefs.getLong(Constants.PREF_TOKEN_EXPIRES, 0)
        val tokenType = encryptedPrefs.getString(Constants.PREF_TOKEN_TYPE, "Bearer")
        
        return if (token != null && refreshToken != null) {
            JwtToken(token, refreshToken, expiresAt, tokenType ?: "Bearer")
        } else null
    }
    
    override suspend fun clearToken() {
        encryptedPrefs.edit()
            .remove(Constants.PREF_ACCESS_TOKEN)
            .remove(Constants.PREF_REFRESH_TOKEN)
            .remove(Constants.PREF_TOKEN_EXPIRES)
            .remove(Constants.PREF_TOKEN_TYPE)
            .apply()
    }
    
    override suspend fun receiver() {
        // Implementation for receiving token updates or notifications
        // This could be used for real-time token validation or updates
    }
    
    override suspend fun refreshToken() {
        // Implementation for refreshing the access token using refresh token
        val currentToken = getToken()
        if (currentToken?.refreshToken != null) {
            // Here you would typically call your API to refresh the token
            // For now, this is a placeholder implementation
        }
    }
    
    suspend fun isTokenValid(): Boolean {
        val token = getToken()
        return token != null && System.currentTimeMillis() < token.expiresAt
    }
}