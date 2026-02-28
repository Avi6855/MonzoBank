package com.avinashpatil.app.monzobank.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserProfileRequestDto(
    @Json(name = "firstName")
    val firstName: String?,
    
    @Json(name = "lastName")
    val lastName: String?,
    
    @Json(name = "email")
    val email: String?,
    
    @Json(name = "phoneNumber")
    val phoneNumber: String?,
    
    @Json(name = "dateOfBirth")
    val dateOfBirth: String?,
    
    @Json(name = "address")
    val address: AddressDto?,
    
    @Json(name = "profilePictureUrl")
    val profilePictureUrl: String?,
    
    @Json(name = "preferences")
    val preferences: Map<String, Any>?
)