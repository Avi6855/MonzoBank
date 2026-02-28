package com.avinashpatil.app.monzobank.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserActivityDto(
    @Json(name = "id")
    val id: String,
    
    @Json(name = "userId")
    val userId: String,
    
    @Json(name = "activityType")
    val activityType: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "timestamp")
    val timestamp: String,
    
    @Json(name = "ipAddress")
    val ipAddress: String?,
    
    @Json(name = "deviceInfo")
    val deviceInfo: String?,
    
    @Json(name = "location")
    val location: String?,
    
    @Json(name = "metadata")
    val metadata: Map<String, Any>?
)