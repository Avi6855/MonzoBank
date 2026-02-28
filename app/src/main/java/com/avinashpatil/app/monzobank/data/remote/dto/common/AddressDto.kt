package com.avinashpatil.app.monzobank.data.remote.dto.common

data class AddressDto(
    val line1: String,
    val line2: String?,
    val city: String,
    val county: String,
    val postcode: String,
    val country: String
)