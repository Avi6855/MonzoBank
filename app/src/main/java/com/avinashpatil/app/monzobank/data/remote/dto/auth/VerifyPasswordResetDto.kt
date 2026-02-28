package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class VerifyPasswordResetDto(
    val email: String,
    val resetToken: String,
    val newPassword: String,
    val confirmPassword: String
)