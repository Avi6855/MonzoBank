package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDate

/**
 * Domain model for authentication response
 */
data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val refreshToken: String?,
    val user: User?,
    val message: String?,
    val requiresTwoFactor: Boolean = false,
    val twoFactorMethods: List<String>? = null
)

/**
 * Domain model for user registration request
 */
data class RegisterRequest(
    val email: String,
    val phone: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val address: Address
)

/**
 * Domain model for user login request
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Domain model for password reset request
 */
data class PasswordResetRequest(
    val email: String
)

/**
 * Domain model for password reset verification
 */
data class PasswordResetVerification(
    val email: String,
    val resetToken: String,
    val newPassword: String
)

/**
 * Domain model for email verification
 */
data class EmailVerification(
    val email: String,
    val verificationCode: String
)

/**
 * Domain model for phone verification
 */
data class PhoneVerification(
    val phone: String,
    val verificationCode: String
)

/**
 * Domain model for two-factor authentication setup
 */
data class TwoFactorSetup(
    val method: TwoFactorMethod,
    val enabled: Boolean
)

/**
 * Domain model for two-factor authentication setup response
 */
data class TwoFactorAuthSetup(
    val method: TwoFactorMethod,
    val isEnabled: Boolean,
    val phoneNumber: String?,
    val email: String?,
    val secretKey: String?,
    val backupCodes: List<String>
)

/**
 * Domain model for two-factor authentication verification
 */
data class TwoFactorVerification(
    val userId: String,
    val code: String,
    val method: TwoFactorMethod
)

/**
 * Enum for two-factor authentication methods
 */
enum class TwoFactorMethod {
    SMS,
    EMAIL,
    AUTHENTICATOR_APP,
    BACKUP_CODES
}

/**
 * Domain model for JWT token
 */
data class JwtToken(
    val token: String,
    val refreshToken: String,
    val expiresAt: Long,
    val tokenType: String = "Bearer"
)

/**
 * Domain model for biometric authentication data
 */
data class BiometricAuth(
    val userId: String,
    val biometricKey: String,
    val deviceId: String,
    val isEnabled: Boolean = false,
    val lastUsed: Long? = null
)