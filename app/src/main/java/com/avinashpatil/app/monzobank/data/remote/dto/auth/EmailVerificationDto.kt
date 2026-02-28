package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class EmailVerificationDto(
    val email: String,
    val verificationCode: String
)