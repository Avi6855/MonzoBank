package com.avinashpatil.app.monzobank.data.remote.dto.auth

import com.avinashpatil.app.monzobank.data.remote.dto.common.AddressDto

data class UpdateProfileDto(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: AddressDto?
)