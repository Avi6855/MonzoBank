package com.avinashpatil.app.monzobank.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Base response DTO for all API responses
 */
data class BaseResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("error_code")
    val errorCode: String? = null
)

/**
 * Registration request DTO
 * Based on technical architecture API definition
 */
data class RegisterRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("dateOfBirth")
    val dateOfBirth: String, // YYYY-MM-DD format
    @SerializedName("address")
    val address: AddressDto
)

/**
 * Login request DTO
 */
data class LoginRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("biometric_token")
    val biometricToken: String? = null,
    @SerializedName("device_id")
    val deviceId: String? = null
)

/**
 * Authentication response DTO
 */
data class AuthResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("verificationToken")
    val verificationToken: String? = null,
    @SerializedName("message")
    val message: String,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("expires_in")
    val expiresIn: Long? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    @SerializedName("user")
    val user: UserDto? = null,
    @SerializedName("requires_2fa")
    val requiresTwoFactor: Boolean = false
)

/**
 * User DTO
 */
data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("address")
    val address: AddressDto,
    @SerializedName("kyc_status")
    val kycStatus: String,
    @SerializedName("biometric_enabled")
    val biometricEnabled: Boolean,
    @SerializedName("is_email_verified")
    val isEmailVerified: Boolean = false,
    @SerializedName("is_phone_verified")
    val isPhoneVerified: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * Address DTO
 */
data class AddressDto(
    @SerializedName("street")
    val street: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("postal_code")
    val postalCode: String,
    @SerializedName("country")
    val country: String
)

/**
 * Refresh token request DTO
 */
data class RefreshTokenRequestDto(
    @SerializedName("refresh_token")
    val refreshToken: String
)

/**
 * Password reset request DTO
 */
data class PasswordResetRequestDto(
    @SerializedName("email")
    val email: String
)

/**
 * Verify password reset DTO
 */
data class VerifyPasswordResetDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("new_password")
    val newPassword: String
)

/**
 * Email verification DTO
 */
data class EmailVerificationDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("verification_code")
    val verificationCode: String
)

/**
 * Phone verification DTO
 */
data class PhoneVerificationDto(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("verification_code")
    val verificationCode: String
)

/**
 * Resend verification DTO
 */
data class ResendVerificationDto(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("type")
    val type: String // EMAIL or PHONE
)

/**
 * Two-factor authentication setup DTO
 */
data class TwoFactorSetupDto(
    @SerializedName("method")
    val method: String, // SMS, EMAIL, AUTHENTICATOR_APP
    @SerializedName("phone")
    val phone: String? = null
)

/**
 * Two-factor authentication response DTO
 */
data class TwoFactorResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("qr_code")
    val qrCode: String? = null,
    @SerializedName("backup_codes")
    val backupCodes: List<String>? = null,
    @SerializedName("secret_key")
    val secretKey: String? = null
)

/**
 * Two-factor authentication verify DTO
 */
data class TwoFactorVerifyDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("method")
    val method: String
)

/**
 * User profile DTO
 */
data class UserProfileDto(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("preferences")
    val preferences: UserPreferencesDto? = null
)

/**
 * User preferences DTO
 */
data class UserPreferencesDto(
    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean,
    @SerializedName("biometric_enabled")
    val biometricEnabled: Boolean,
    @SerializedName("two_factor_enabled")
    val twoFactorEnabled: Boolean,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("language")
    val language: String
)

/**
 * Update profile DTO
 */
data class UpdateProfileDto(
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("address")
    val address: AddressDto? = null
)

/**
 * Change password DTO
 */
data class ChangePasswordDto(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String
)

/**
 * Delete account DTO
 */
data class DeleteAccountDto(
    @SerializedName("password")
    val password: String,
    @SerializedName("reason")
    val reason: String? = null
)

/**
 * Check exists response DTO
 */
data class CheckExistsResponseDto(
    @SerializedName("exists")
    val exists: Boolean
)

/**
 * Biometric setup DTO
 */
data class BiometricSetupDto(
    @SerializedName("biometric_key")
    val biometricKey: String,
    @SerializedName("device_id")
    val deviceId: String
)

/**
 * Biometric login DTO
 */
data class BiometricLoginDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("biometric_token")
    val biometricToken: String,
    @SerializedName("device_id")
    val deviceId: String
)