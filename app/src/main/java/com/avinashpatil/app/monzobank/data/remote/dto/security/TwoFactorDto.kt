package com.avinashpatil.app.monzobank.data.remote.dto.security

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EnableTwoFactorRequestDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "method")
    val method: String, // "sms", "email", "authenticator", "hardware_key"
    
    @Json(name = "phone_number")
    val phoneNumber: String?,
    
    @Json(name = "email")
    val email: String?,
    
    @Json(name = "backup_codes_requested")
    val backupCodesRequested: Boolean,
    
    @Json(name = "device_info")
    val deviceInfo: DeviceInfoDto?
)

@JsonClass(generateAdapter = true)
data class DisableTwoFactorRequestDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "verification_code")
    val verificationCode: String,
    
    @Json(name = "password")
    val password: String,
    
    @Json(name = "reason")
    val reason: String?, // Optional reason for disabling
    
    @Json(name = "device_info")
    val deviceInfo: DeviceInfoDto?
)

@JsonClass(generateAdapter = true)
data class VerifyTwoFactorRequestDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "verification_code")
    val verificationCode: String,
    
    @Json(name = "method")
    val method: String, // "sms", "email", "authenticator", "backup_code"
    
    @Json(name = "session_id")
    val sessionId: String?,
    
    @Json(name = "remember_device")
    val rememberDevice: Boolean,
    
    @Json(name = "device_info")
    val deviceInfo: DeviceInfoDto?
)

@JsonClass(generateAdapter = true)
data class TwoFactorSetupResponseDto(
    @Json(name = "setup_id")
    val setupId: String,
    
    @Json(name = "status")
    val status: String, // "pending", "completed", "failed"
    
    @Json(name = "method")
    val method: String,
    
    @Json(name = "qr_code")
    val qrCode: String?, // Base64 encoded QR code for authenticator apps
    
    @Json(name = "secret_key")
    val secretKey: String?, // For manual entry in authenticator apps
    
    @Json(name = "backup_codes")
    val backupCodes: List<String>?,
    
    @Json(name = "verification_required")
    val verificationRequired: Boolean,
    
    @Json(name = "expires_at")
    val expiresAt: String?,
    
    @Json(name = "next_step")
    val nextStep: String? // Instructions for next step
)

@JsonClass(generateAdapter = true)
data class TwoFactorVerificationResponseDto(
    @Json(name = "verification_id")
    val verificationId: String,
    
    @Json(name = "status")
    val status: String, // "success", "failed", "expired", "rate_limited"
    
    @Json(name = "access_token")
    val accessToken: String?,
    
    @Json(name = "refresh_token")
    val refreshToken: String?,
    
    @Json(name = "session_id")
    val sessionId: String?,
    
    @Json(name = "trusted_device_token")
    val trustedDeviceToken: String?,
    
    @Json(name = "expires_at")
    val expiresAt: String?,
    
    @Json(name = "attempts_remaining")
    val attemptsRemaining: Int?,
    
    @Json(name = "lockout_until")
    val lockoutUntil: String?,
    
    @Json(name = "error_message")
    val errorMessage: String?
)

@JsonClass(generateAdapter = true)
data class DeviceInfoDto(
    @Json(name = "device_id")
    val deviceId: String,
    
    @Json(name = "device_name")
    val deviceName: String?,
    
    @Json(name = "device_type")
    val deviceType: String, // "mobile", "desktop", "tablet"
    
    @Json(name = "os")
    val os: String,
    
    @Json(name = "os_version")
    val osVersion: String?,
    
    @Json(name = "app_version")
    val appVersion: String?,
    
    @Json(name = "ip_address")
    val ipAddress: String?,
    
    @Json(name = "user_agent")
    val userAgent: String?,
    
    @Json(name = "location")
    val location: LocationInfoDto?
)

@JsonClass(generateAdapter = true)
data class LocationInfoDto(
    @Json(name = "country")
    val country: String?,
    
    @Json(name = "city")
    val city: String?,
    
    @Json(name = "latitude")
    val latitude: Double?,
    
    @Json(name = "longitude")
    val longitude: Double?
)