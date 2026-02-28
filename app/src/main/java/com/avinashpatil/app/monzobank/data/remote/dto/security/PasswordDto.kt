package com.avinashpatil.app.monzobank.data.remote.dto.security

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangePasswordRequestDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "current_password")
    val currentPassword: String,
    
    @Json(name = "new_password")
    val newPassword: String,
    
    @Json(name = "confirm_password")
    val confirmPassword: String,
    
    @Json(name = "two_factor_code")
    val twoFactorCode: String?,
    
    @Json(name = "session_id")
    val sessionId: String?,
    
    @Json(name = "device_info")
    val deviceInfo: DeviceInfoDto?,
    
    @Json(name = "logout_other_sessions")
    val logoutOtherSessions: Boolean
)

@JsonClass(generateAdapter = true)
data class ChangePasswordResponseDto(
    @Json(name = "change_id")
    val changeId: String,
    
    @Json(name = "status")
    val status: String, // "success", "failed", "requires_verification"
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "password_strength")
    val passwordStrength: PasswordStrengthDto?,
    
    @Json(name = "sessions_terminated")
    val sessionsTerminated: Int?,
    
    @Json(name = "new_access_token")
    val newAccessToken: String?,
    
    @Json(name = "verification_required")
    val verificationRequired: Boolean,
    
    @Json(name = "verification_methods")
    val verificationMethods: List<String>?,
    
    @Json(name = "changed_at")
    val changedAt: String,
    
    @Json(name = "next_change_allowed")
    val nextChangeAllowed: String? // Minimum time before next password change
)

@JsonClass(generateAdapter = true)
data class PasswordStrengthDto(
    @Json(name = "score")
    val score: Int, // 0-100
    
    @Json(name = "level")
    val level: String, // "weak", "fair", "good", "strong", "very_strong"
    
    @Json(name = "requirements_met")
    val requirementsMet: PasswordRequirementsDto,
    
    @Json(name = "suggestions")
    val suggestions: List<String>?,
    
    @Json(name = "estimated_crack_time")
    val estimatedCrackTime: String?,
    
    @Json(name = "common_password")
    val commonPassword: Boolean,
    
    @Json(name = "previously_used")
    val previouslyUsed: Boolean
)

@JsonClass(generateAdapter = true)
data class PasswordRequirementsDto(
    @Json(name = "min_length")
    val minLength: Boolean,
    
    @Json(name = "has_uppercase")
    val hasUppercase: Boolean,
    
    @Json(name = "has_lowercase")
    val hasLowercase: Boolean,
    
    @Json(name = "has_numbers")
    val hasNumbers: Boolean,
    
    @Json(name = "has_special_chars")
    val hasSpecialChars: Boolean,
    
    @Json(name = "no_common_patterns")
    val noCommonPatterns: Boolean,
    
    @Json(name = "no_personal_info")
    val noPersonalInfo: Boolean
)

@JsonClass(generateAdapter = true)
data class ResetPasswordRequestDto(
    @Json(name = "email")
    val email: String?,
    
    @Json(name = "phone_number")
    val phoneNumber: String?,
    
    @Json(name = "user_id")
    val userId: String?,
    
    @Json(name = "reset_method")
    val resetMethod: String, // "email", "sms", "security_questions"
    
    @Json(name = "device_info")
    val deviceInfo: DeviceInfoDto?
)

@JsonClass(generateAdapter = true)
data class ResetPasswordResponseDto(
    @Json(name = "reset_id")
    val resetId: String,
    
    @Json(name = "status")
    val status: String, // "sent", "failed", "rate_limited"
    
    @Json(name = "method")
    val method: String,
    
    @Json(name = "masked_destination")
    val maskedDestination: String?, // "j***@example.com" or "+1***1234"
    
    @Json(name = "expires_at")
    val expiresAt: String,
    
    @Json(name = "attempts_remaining")
    val attemptsRemaining: Int?,
    
    @Json(name = "retry_after")
    val retryAfter: String?, // Time when next attempt is allowed
    
    @Json(name = "message")
    val message: String
)

@JsonClass(generateAdapter = true)
data class ConfirmPasswordResetDto(
    @Json(name = "reset_token")
    val resetToken: String,
    
    @Json(name = "new_password")
    val newPassword: String,
    
    @Json(name = "confirm_password")
    val confirmPassword: String,
    
    @Json(name = "verification_code")
    val verificationCode: String?,
    
    @Json(name = "device_info")
    val deviceInfo: DeviceInfoDto?
)