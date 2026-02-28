package com.avinashpatil.app.monzobank.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteAccountRequestDto(
    @Json(name = "password")
    val password: String,
    
    @Json(name = "reason")
    val reason: String?,
    
    @Json(name = "feedback")
    val feedback: String?,
    
    @Json(name = "confirmDeletion")
    val confirmDeletion: Boolean = true
)