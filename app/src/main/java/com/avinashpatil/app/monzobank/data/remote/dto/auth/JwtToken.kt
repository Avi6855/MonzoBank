package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class JwtToken(
    val token: String,
    val refreshToken: String,
    val expiresAt: Long,
    val tokenType: String = "Bearer"
)