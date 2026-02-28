package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchantInfoDto(
    @Json(name = "merchant_id")
    val merchantId: String,
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "category")
    val category: String,
    
    @Json(name = "subcategory")
    val subcategory: String?,
    
    @Json(name = "logo_url")
    val logoUrl: String?,
    
    @Json(name = "website")
    val website: String?,
    
    @Json(name = "phone")
    val phone: String?,
    
    @Json(name = "address")
    val address: MerchantAddressDto?,
    
    @Json(name = "location")
    val location: MerchantLocationDto?,
    
    @Json(name = "business_hours")
    val businessHours: List<BusinessHourDto>?,
    
    @Json(name = "rating")
    val rating: Double?,
    
    @Json(name = "review_count")
    val reviewCount: Int?,
    
    @Json(name = "price_level")
    val priceLevel: String?, // "$", "$$", "$$$", "$$$$"
    
    @Json(name = "tags")
    val tags: List<String>?,
    
    @Json(name = "verified")
    val verified: Boolean,
    
    @Json(name = "last_updated")
    val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class MerchantAddressDto(
    @Json(name = "street")
    val street: String,
    
    @Json(name = "city")
    val city: String,
    
    @Json(name = "state")
    val state: String?,
    
    @Json(name = "postal_code")
    val postalCode: String?,
    
    @Json(name = "country")
    val country: String
)

@JsonClass(generateAdapter = true)
data class MerchantLocationDto(
    @Json(name = "latitude")
    val latitude: Double,
    
    @Json(name = "longitude")
    val longitude: Double
)

@JsonClass(generateAdapter = true)
data class BusinessHourDto(
    @Json(name = "day")
    val day: String, // "monday", "tuesday", etc.
    
    @Json(name = "open_time")
    val openTime: String?, // "09:00"
    
    @Json(name = "close_time")
    val closeTime: String?, // "17:00"
    
    @Json(name = "is_closed")
    val isClosed: Boolean
)