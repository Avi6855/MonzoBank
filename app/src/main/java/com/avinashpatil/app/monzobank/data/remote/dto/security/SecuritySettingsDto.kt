package com.avinashpatil.app.monzobank.data.remote.dto.security

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SecuritySettingsDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "two_factor_authentication")
    val twoFactorAuthentication: TwoFactorSettingsDto,
    
    @Json(name = "login_security")
    val loginSecurity: LoginSecurityDto,
    
    @Json(name = "device_management")
    val deviceManagement: DeviceManagementDto,
    
    @Json(name = "session_management")
    val sessionManagement: SessionManagementDto,
    
    @Json(name = "privacy_settings")
    val privacySettings: PrivacySettingsDto,
    
    @Json(name = "security_alerts")
    val securityAlerts: SecurityAlertsDto,
    
    @Json(name = "account_recovery")
    val accountRecovery: AccountRecoveryDto,
    
    @Json(name = "last_updated")
    val lastUpdated: String,
    
    @Json(name = "security_score")
    val securityScore: SecurityScoreDto?
)

@JsonClass(generateAdapter = true)
data class TwoFactorSettingsDto(
    @Json(name = "enabled")
    val enabled: Boolean,
    
    @Json(name = "primary_method")
    val primaryMethod: String?, // "sms", "email", "authenticator", "hardware_key"
    
    @Json(name = "backup_methods")
    val backupMethods: List<String>,
    
    @Json(name = "backup_codes_remaining")
    val backupCodesRemaining: Int,
    
    @Json(name = "trusted_devices_count")
    val trustedDevicesCount: Int,
    
    @Json(name = "last_used")
    val lastUsed: String?,
    
    @Json(name = "setup_date")
    val setupDate: String?
)

@JsonClass(generateAdapter = true)
data class LoginSecurityDto(
    @Json(name = "password_last_changed")
    val passwordLastChanged: String,
    
    @Json(name = "password_strength")
    val passwordStrength: String, // "weak", "fair", "good", "strong", "very_strong"
    
    @Json(name = "failed_login_attempts")
    val failedLoginAttempts: Int,
    
    @Json(name = "account_locked")
    val accountLocked: Boolean,
    
    @Json(name = "lockout_until")
    val lockoutUntil: String?,
    
    @Json(name = "login_notifications")
    val loginNotifications: Boolean,
    
    @Json(name = "suspicious_activity_alerts")
    val suspiciousActivityAlerts: Boolean,
    
    @Json(name = "geo_blocking_enabled")
    val geoBlockingEnabled: Boolean,
    
    @Json(name = "allowed_countries")
    val allowedCountries: List<String>?
)

@JsonClass(generateAdapter = true)
data class DeviceManagementDto(
    @Json(name = "registered_devices")
    val registeredDevices: List<RegisteredDeviceDto>,
    
    @Json(name = "max_devices_allowed")
    val maxDevicesAllowed: Int,
    
    @Json(name = "device_verification_required")
    val deviceVerificationRequired: Boolean,
    
    @Json(name = "auto_logout_inactive_devices")
    val autoLogoutInactiveDevices: Boolean,
    
    @Json(name = "inactive_device_threshold_days")
    val inactiveDeviceThresholdDays: Int
)

@JsonClass(generateAdapter = true)
data class RegisteredDeviceDto(
    @Json(name = "device_id")
    val deviceId: String,
    
    @Json(name = "device_name")
    val deviceName: String,
    
    @Json(name = "device_type")
    val deviceType: String,
    
    @Json(name = "os")
    val os: String,
    
    @Json(name = "is_current_device")
    val isCurrentDevice: Boolean,
    
    @Json(name = "is_trusted")
    val isTrusted: Boolean,
    
    @Json(name = "last_active")
    val lastActive: String,
    
    @Json(name = "registered_at")
    val registeredAt: String,
    
    @Json(name = "location")
    val location: String?,
    
    @Json(name = "ip_address")
    val ipAddress: String?
)

@JsonClass(generateAdapter = true)
data class SessionManagementDto(
    @Json(name = "active_sessions_count")
    val activeSessionsCount: Int,
    
    @Json(name = "max_concurrent_sessions")
    val maxConcurrentSessions: Int,
    
    @Json(name = "session_timeout_minutes")
    val sessionTimeoutMinutes: Int,
    
    @Json(name = "auto_logout_enabled")
    val autoLogoutEnabled: Boolean,
    
    @Json(name = "remember_me_enabled")
    val rememberMeEnabled: Boolean,
    
    @Json(name = "session_notifications")
    val sessionNotifications: Boolean
)

@JsonClass(generateAdapter = true)
data class PrivacySettingsDto(
    @Json(name = "data_sharing_consent")
    val dataSharingConsent: Boolean,
    
    @Json(name = "marketing_consent")
    val marketingConsent: Boolean,
    
    @Json(name = "analytics_consent")
    val analyticsConsent: Boolean,
    
    @Json(name = "third_party_sharing")
    val thirdPartySharing: Boolean,
    
    @Json(name = "profile_visibility")
    val profileVisibility: String, // "public", "private", "friends_only"
    
    @Json(name = "activity_tracking")
    val activityTracking: Boolean,
    
    @Json(name = "location_tracking")
    val locationTracking: Boolean
)

@JsonClass(generateAdapter = true)
data class SecurityAlertsDto(
    @Json(name = "login_alerts")
    val loginAlerts: Boolean,
    
    @Json(name = "transaction_alerts")
    val transactionAlerts: Boolean,
    
    @Json(name = "password_change_alerts")
    val passwordChangeAlerts: Boolean,
    
    @Json(name = "device_registration_alerts")
    val deviceRegistrationAlerts: Boolean,
    
    @Json(name = "suspicious_activity_alerts")
    val suspiciousActivityAlerts: Boolean,
    
    @Json(name = "data_breach_alerts")
    val dataBreachAlerts: Boolean,
    
    @Json(name = "alert_methods")
    val alertMethods: List<String>, // ["email", "sms", "push", "in_app"]
    
    @Json(name = "alert_frequency")
    val alertFrequency: String // "immediate", "daily_digest", "weekly_digest"
)

@JsonClass(generateAdapter = true)
data class AccountRecoveryDto(
    @Json(name = "recovery_email")
    val recoveryEmail: String?,
    
    @Json(name = "recovery_phone")
    val recoveryPhone: String?,
    
    @Json(name = "security_questions_set")
    val securityQuestionsSet: Boolean,
    
    @Json(name = "backup_codes_generated")
    val backupCodesGenerated: Boolean,
    
    @Json(name = "recovery_contacts")
    val recoveryContacts: List<RecoveryContactDto>?,
    
    @Json(name = "last_recovery_attempt")
    val lastRecoveryAttempt: String?
)

@JsonClass(generateAdapter = true)
data class RecoveryContactDto(
    @Json(name = "contact_id")
    val contactId: String,
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "email")
    val email: String?,
    
    @Json(name = "phone")
    val phone: String?,
    
    @Json(name = "relationship")
    val relationship: String,
    
    @Json(name = "verified")
    val verified: Boolean,
    
    @Json(name = "added_at")
    val addedAt: String
)

@JsonClass(generateAdapter = true)
data class SecurityScoreDto(
    @Json(name = "overall_score")
    val overallScore: Int, // 0-100
    
    @Json(name = "score_level")
    val scoreLevel: String, // "low", "medium", "high", "excellent"
    
    @Json(name = "factors")
    val factors: SecurityFactorsDto,
    
    @Json(name = "recommendations")
    val recommendations: List<SecurityRecommendationDto>,
    
    @Json(name = "last_calculated")
    val lastCalculated: String
)

@JsonClass(generateAdapter = true)
data class SecurityFactorsDto(
    @Json(name = "password_strength")
    val passwordStrength: Int,
    
    @Json(name = "two_factor_enabled")
    val twoFactorEnabled: Int,
    
    @Json(name = "device_security")
    val deviceSecurity: Int,
    
    @Json(name = "account_activity")
    val accountActivity: Int,
    
    @Json(name = "privacy_settings")
    val privacySettings: Int
)

@JsonClass(generateAdapter = true)
data class SecurityRecommendationDto(
    @Json(name = "recommendation_id")
    val recommendationId: String,
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "action_url")
    val actionUrl: String?,
    
    @Json(name = "estimated_time")
    val estimatedTime: String?, // "2 minutes", "5 minutes", etc.
    
    @Json(name = "impact_score")
    val impactScore: Int // Points added to security score if completed
)