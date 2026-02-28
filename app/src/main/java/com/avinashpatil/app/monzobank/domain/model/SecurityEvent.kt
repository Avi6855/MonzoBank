package com.avinashpatil.app.monzobank.domain.model

import java.util.Date

/**
 * Represents a security event in the system
 */
data class SecurityEvent(
    val id: String,
    val userId: String,
    val eventType: SecurityEventType,
    val description: String,
    val ipAddress: String?,
    val deviceId: String?,
    val location: String?,
    val userAgent: String?,
    val severity: SecuritySeverity,
    val resolved: Boolean = false,
    val resolvedBy: String? = null,
    val resolvedAt: Date? = null,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Types of security events
 */
enum class SecurityEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGIN_SUSPICIOUS,
    PASSWORD_CHANGED,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_RESET_COMPLETED,
    TWO_FACTOR_ENABLED,
    TWO_FACTOR_DISABLED,
    TWO_FACTOR_FAILED,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    DEVICE_REGISTERED,
    DEVICE_REMOVED,
    SUSPICIOUS_TRANSACTION,
    FRAUD_DETECTED,
    DATA_BREACH_ATTEMPT,
    UNAUTHORIZED_ACCESS,
    SECURITY_SETTINGS_CHANGED,
    API_KEY_GENERATED,
    API_KEY_REVOKED,
    BIOMETRIC_ENABLED,
    BIOMETRIC_DISABLED,
    LOCATION_ANOMALY,
    MULTIPLE_FAILED_ATTEMPTS,
    ACCOUNT_TAKEOVER_ATTEMPT
}

/**
 * Security event severity levels
 */
enum class SecuritySeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}