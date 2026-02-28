package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationServicesDto(
    @Json(name = "location_id")
    val locationId: String,
    
    @Json(name = "coordinates")
    val coordinates: CoordinatesDto,
    
    @Json(name = "address")
    val address: LocationAddressDto,
    
    @Json(name = "nearby_services")
    val nearbyServices: NearbyServicesDto,
    
    @Json(name = "atm_locations")
    val atmLocations: List<AtmLocationDto>,
    
    @Json(name = "branch_locations")
    val branchLocations: List<BranchLocationDto>,
    
    @Json(name = "merchant_locations")
    val merchantLocations: List<MerchantLocationDto>,
    
    @Json(name = "search_radius_km")
    val searchRadiusKm: Double,
    
    @Json(name = "last_updated")
    val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class CoordinatesDto(
    @Json(name = "latitude")
    val latitude: Double,
    
    @Json(name = "longitude")
    val longitude: Double,
    
    @Json(name = "accuracy")
    val accuracy: Double?, // in meters
    
    @Json(name = "altitude")
    val altitude: Double?,
    
    @Json(name = "timestamp")
    val timestamp: String
)

@JsonClass(generateAdapter = true)
data class LocationAddressDto(
    @Json(name = "street_address")
    val streetAddress: String?,
    
    @Json(name = "city")
    val city: String?,
    
    @Json(name = "state")
    val state: String?,
    
    @Json(name = "postal_code")
    val postalCode: String?,
    
    @Json(name = "country")
    val country: String?,
    
    @Json(name = "formatted_address")
    val formattedAddress: String
)

@JsonClass(generateAdapter = true)
data class NearbyServicesDto(
    @Json(name = "total_atms")
    val totalAtms: Int,
    
    @Json(name = "total_branches")
    val totalBranches: Int,
    
    @Json(name = "total_merchants")
    val totalMerchants: Int,
    
    @Json(name = "closest_atm_distance")
    val closestAtmDistance: Double?, // in km
    
    @Json(name = "closest_branch_distance")
    val closestBranchDistance: Double? // in km
)

@JsonClass(generateAdapter = true)
data class AtmLocationDto(
    @Json(name = "atm_id")
    val atmId: String,
    
    @Json(name = "bank_name")
    val bankName: String,
    
    @Json(name = "coordinates")
    val coordinates: CoordinatesDto,
    
    @Json(name = "address")
    val address: LocationAddressDto,
    
    @Json(name = "distance_km")
    val distanceKm: Double,
    
    @Json(name = "services")
    val services: List<String>, // ["cash_withdrawal", "deposit", "balance_inquiry", etc.]
    
    @Json(name = "fees")
    val fees: AtmFeesDto?,
    
    @Json(name = "operating_hours")
    val operatingHours: OperatingHoursDto,
    
    @Json(name = "accessibility")
    val accessibility: AccessibilityDto?,
    
    @Json(name = "network")
    val network: String?, // "Visa", "Mastercard", "Plus", etc.
    
    @Json(name = "status")
    val status: String // "operational", "out_of_service", "maintenance"
)

@JsonClass(generateAdapter = true)
data class AtmFeesDto(
    @Json(name = "withdrawal_fee")
    val withdrawalFee: String?,
    
    @Json(name = "balance_inquiry_fee")
    val balanceInquiryFee: String?,
    
    @Json(name = "foreign_card_fee")
    val foreignCardFee: String?,
    
    @Json(name = "currency")
    val currency: String
)

@JsonClass(generateAdapter = true)
data class BranchLocationDto(
    @Json(name = "branch_id")
    val branchId: String,
    
    @Json(name = "bank_name")
    val bankName: String,
    
    @Json(name = "branch_name")
    val branchName: String,
    
    @Json(name = "coordinates")
    val coordinates: CoordinatesDto,
    
    @Json(name = "address")
    val address: LocationAddressDto,
    
    @Json(name = "distance_km")
    val distanceKm: Double,
    
    @Json(name = "phone")
    val phone: String?,
    
    @Json(name = "services")
    val services: List<String>, // ["personal_banking", "business_banking", "loans", etc.]
    
    @Json(name = "operating_hours")
    val operatingHours: OperatingHoursDto,
    
    @Json(name = "accessibility")
    val accessibility: AccessibilityDto?,
    
    @Json(name = "parking_available")
    val parkingAvailable: Boolean,
    
    @Json(name = "drive_through")
    val driveThrough: Boolean
)
/*
@JsonClass(generateAdapter = true)
data class MerchantLocationDto(
    @Json(name = "merchant_id")
    val merchantId: String,
    
    @Json(name = "merchant_name")
    val merchantName: String,
    
    @Json(name = "category")
    val category: String,
    
    @Json(name = "coordinates")
    val coordinates: CoordinatesDto,
    
    @Json(name = "address")
    val address: LocationAddressDto,
    
    @Json(name = "distance_km")
    val distanceKm: Double,
    
    @Json(name = "phone")
    val phone: String?,
    
    @Json(name = "website")
    val website: String?,
    
    @Json(name = "payment_methods")
    val paymentMethods: List<String>, // ["card", "contactless", "mobile_pay", etc.]
    
    @Json(name = "operating_hours")
    val operatingHours: OperatingHoursDto,
    
    @Json(name = "rating")
    val rating: Double?,
    
    @Json(name = "price_range")
    val priceRange: String? // "$", "$$", "$$$", "$$$$"
)

 */

@JsonClass(generateAdapter = true)
data class OperatingHoursDto(
    @Json(name = "monday")
    val monday: DayHoursDto?,
    
    @Json(name = "tuesday")
    val tuesday: DayHoursDto?,
    
    @Json(name = "wednesday")
    val wednesday: DayHoursDto?,
    
    @Json(name = "thursday")
    val thursday: DayHoursDto?,
    
    @Json(name = "friday")
    val friday: DayHoursDto?,
    
    @Json(name = "saturday")
    val saturday: DayHoursDto?,
    
    @Json(name = "sunday")
    val sunday: DayHoursDto?,
    
    @Json(name = "is_24_hours")
    val is24Hours: Boolean,
    
    @Json(name = "timezone")
    val timezone: String
)

@JsonClass(generateAdapter = true)
data class DayHoursDto(
    @Json(name = "open_time")
    val openTime: String?, // "09:00"
    
    @Json(name = "close_time")
    val closeTime: String?, // "17:00"
    
    @Json(name = "is_closed")
    val isClosed: Boolean
)

@JsonClass(generateAdapter = true)
data class AccessibilityDto(
    @Json(name = "wheelchair_accessible")
    val wheelchairAccessible: Boolean,
    
    @Json(name = "audio_assistance")
    val audioAssistance: Boolean,
    
    @Json(name = "braille_support")
    val brailleSupport: Boolean,
    
    @Json(name = "accessible_parking")
    val accessibleParking: Boolean
)