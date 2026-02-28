package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class Location(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val bearing: Float? = null,
    val speed: Float? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val postalCode: String? = null,
    val placeName: String? = null,
    val placeType: LocationType = LocationType.UNKNOWN,
    val isApproximate: Boolean = false,
    val source: LocationSource = LocationSource.GPS,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null
)

enum class LocationType {
    HOME,
    WORK,
    MERCHANT,
    ATM,
    BANK_BRANCH,
    RESTAURANT,
    SHOPPING,
    TRANSPORT,
    ENTERTAINMENT,
    HEALTHCARE,
    EDUCATION,
    GOVERNMENT,
    UNKNOWN
}

enum class LocationSource {
    GPS,
    NETWORK,
    PASSIVE,
    FUSED,
    MANUAL,
    CACHED
}