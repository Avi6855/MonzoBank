package com.avinashpatil.app.monzobank.data.remote.dto.auth

data class ChangePasswordDto(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)