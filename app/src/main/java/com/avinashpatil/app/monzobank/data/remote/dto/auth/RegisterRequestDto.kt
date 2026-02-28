package com.avinashpatil.app.monzobank.data.remote.dto.auth

import com.avinashpatil.app.monzobank.data.remote.dto.common.AddressDto

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val dateOfBirth: String,
    val address: AddressDto
)