package com.avinashpatil.app.monzobank.domain.model

import java.util.Date

/**
 * Represents security settings for a user
 */
data class SecuritySettings(
    val userId: String,
    val twoFactorEnabled: Boolean = false,
    val twoFactorMethod: TwoFactorAuthMethod? = null,
    val biometricEnabled: Boolean = false,
    val loginNotifications: Boolean = true,
    val transactionNotifications: Boolean = true,
    val securityAlerts: Boolean = true,
    val sessionTimeout: Int = 30, // minutes
    val maxFailedAttempts: Int = 5,
    val accountLockDuration: Int = 30, // minutes
    val passwordExpiryDays: Int = 90,
    val requireStrongPassword: Boolean = true,
    val allowedDevices: List<String> = emptyList(),
    val trustedIpAddresses: List<String> = emptyList(),
    val geolocationTracking: Boolean = true,
    val suspiciousActivityDetection: Boolean = true,
    val fraudDetectionEnabled: Boolean = true,
    val dataEncryptionEnabled: Boolean = true,
    val apiAccessEnabled: Boolean = false,
    val thirdPartyAccessEnabled: Boolean = false,
    val lastPasswordChange: Date? = null,
    val lastSecurityReview: Date? = null,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Two-factor authentication methods
 */
enum class TwoFactorAuthMethod {
    SMS,
    EMAIL,
    AUTHENTICATOR_APP,
    HARDWARE_TOKEN,
    BIOMETRIC,
    VOICE_CALL
}