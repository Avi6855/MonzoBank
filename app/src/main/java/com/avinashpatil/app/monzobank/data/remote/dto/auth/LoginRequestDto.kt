package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class LoginRequestDto(
    val email: String,
    val password: String,
    val biometricToken: String? = null,
    val deviceId: String? = null
)