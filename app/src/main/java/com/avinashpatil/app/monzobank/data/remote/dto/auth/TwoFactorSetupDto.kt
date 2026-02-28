package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class TwoFactorSetupDto(
    val method: String,
    val phone: String?
)