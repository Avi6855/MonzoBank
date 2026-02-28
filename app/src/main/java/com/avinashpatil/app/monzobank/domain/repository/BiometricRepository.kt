package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.BiometricType
import com.avinashpatil.app.monzobank.domain.model.BiometricAuthResult

/**
 * Repository interface for biometric authentication operations
 */
interface BiometricRepository {
    
    /**
     * Check if biometric authentication is available on the device
     */
    suspend fun isBiometricAvailable(): Result<Boolean>
    
    /**
     * Get available biometric types
     */
    suspend fun getAvailableBiometricTypes(): Result<List<BiometricType>>
    
    /**
     * Enable biometric authentication for a user
     */
    suspend fun enableBiometricAuth(userId: String): Result<Unit>
    
    /**
     * Disable biometric authentication for a user
     */
    suspend fun disableBiometricAuth(userId: String): Result<Unit>
    
    /**
     * Check if biometric is enabled for a user
     */
    suspend fun isBiometricEnabled(userId: String): Result<Boolean>
    
    /**
     * Authenticate using biometrics
     */
    suspend fun authenticateWithBiometric(
        userId: String,
        promptTitle: String,
        promptSubtitle: String
    ): Result<BiometricAuthResult>
    
    /**
     * Register biometric template
     */
    suspend fun registerBiometric(
        userId: String,
        biometricType: BiometricType
    ): Result<Unit>
    
    /**
     * Remove biometric template
     */
    suspend fun removeBiometric(
        userId: String,
        biometricType: BiometricType
    ): Result<Unit>
    
    /**
     * Validate biometric hardware
     */
    suspend fun validateBiometricHardware(): Result<Boolean>
    
    /**
     * Check if biometric templates are enrolled
     */
    suspend fun hasEnrolledBiometrics(): Result<Boolean>
}

/**
 * Types of biometric authentication
 */
enum class BiometricType {
    FINGERPRINT,
    FACE,
    IRIS,
    VOICE
}

/**
 * Result of biometric authentication
 */
data class BiometricAuthResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val biometricType: BiometricType? = null
)