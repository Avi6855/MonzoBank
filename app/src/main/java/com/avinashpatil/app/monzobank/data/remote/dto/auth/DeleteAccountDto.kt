package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class DeleteAccountDto(
    val password: String,
    val reason: String?
)