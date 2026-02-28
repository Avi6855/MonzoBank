package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class TwoFactorVerifyDto(
    val userId: String,
    val code: String,
    val method: String
)