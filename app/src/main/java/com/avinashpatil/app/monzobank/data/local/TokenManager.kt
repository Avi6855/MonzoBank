package com.avinashpatil.app.monzobank.data.local

import com.avinashpatil.app.monzobank.data.remote.dto.auth.JwtToken

interface TokenManager {
    suspend fun saveToken(token: JwtToken)
    suspend fun getToken(): JwtToken?
    suspend fun clearToken()
    suspend fun receiver()
    suspend fun refreshToken()
}