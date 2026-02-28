package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class PhoneVerificationDto(
    val phoneNumber: String,
    val verificationCode: String
)