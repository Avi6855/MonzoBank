package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class BiometricLoginDto(
    val userId: String,
    val biometricToken: String,
    val deviceId: String
)