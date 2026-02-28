package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class ResendVerificationDto(
    val email: String?,
    val phone: String?,
    val type: String
)