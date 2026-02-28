package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.Location

/**
 * Repository interface for location services
 */
interface LocationRepository {
    
    /**
     * Get current location
     */
    suspend fun getCurrentLocation(): Result<Location>
    
    /**
     * Check if location permission is granted
     */
    suspend fun hasLocationPermission(): Result<Boolean>
    
    /**
     * Request location permission
     */
    suspend fun requestLocationPermission(): Result<Boolean>
    
    /**
     * Enable location tracking
     */
    suspend fun enableLocationTracking(userId: String): Result<Unit>
    
    /**
     * Disable location tracking
     */
    suspend fun disableLocationTracking(userId: String): Result<Unit>
    
    /**
     * Get location history
     */
    suspend fun getLocationHistory(
        userId: String,
        limit: Int = 50
    ): Result<List<Location>>
    
    /**
     * Clear location history
     */
    suspend fun clearLocationHistory(userId: String): Result<Unit>
}

/**
 * Location data model
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val bearing: Float? = null,
    val speed: Float? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String? = null,
    val city: String? = null,
    val country: String? = null
)