package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.SecurityEvent
import com.avinashpatil.app.monzobank.domain.model.SecuritySettings
import com.avinashpatil.app.monzobank.domain.model.TwoFactorAuthMethod
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for security-related operations
 */
interface SecurityRepository {
    
    /**
     * Enable two-factor authentication for a user
     */
    suspend fun enableTwoFactorAuth(
        userId: String,
        method: TwoFactorAuthMethod,
        phoneNumber: String? = null,
        email: String? = null
    ): Result<Unit>
    
    /**
     * Disable two-factor authentication for a user
     */
    suspend fun disableTwoFactorAuth(userId: String): Result<Unit>
    
    /**
     * Verify two-factor authentication code
     */
    suspend fun verifyTwoFactorCode(
        userId: String,
        code: String
    ): Result<Boolean>
    
    /**
     * Get security settings for a user
     */
    suspend fun getSecuritySettings(userId: String): Result<SecuritySettings>
    
    /**
     * Update security settings for a user
     */
    suspend fun updateSecuritySettings(
        userId: String,
        settings: SecuritySettings
    ): Result<Unit>
    
    /**
     * Log a security event
     */
    suspend fun logSecurityEvent(
        userId: String,
        eventType: String,
        description: String,
        ipAddress: String? = null,
        deviceId: String? = null
    ): Result<Unit>
    
    /**
     * Get security events for a user
     */
    suspend fun getSecurityEvents(
        userId: String,
        startDate: Date? = null,
        endDate: Date? = null,
        limit: Int = 50
    ): Result<List<SecurityEvent>>
    
    /**
     * Check if account is locked
     */
    suspend fun isAccountLocked(userId: String): Result<Boolean>
    
    /**
     * Lock user account
     */
    suspend fun lockAccount(
        userId: String,
        reason: String,
        duration: Long? = null
    ): Result<Unit>
    
    /**
     * Unlock user account
     */
    suspend fun unlockAccount(userId: String): Result<Unit>
    
    /**
     * Change user password
     */
    suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit>
    
    /**
     * Reset user password
     */
    suspend fun resetPassword(
        email: String,
        resetToken: String,
        newPassword: String
    ): Result<Unit>
    
    /**
     * Generate password reset token
     */
    suspend fun generatePasswordResetToken(email: String): Result<String>
    
    /**
     * Observe security alerts for a user
     */
    fun observeSecurityAlerts(userId: String): Flow<List<SecurityEvent>>
    
    /**
     * Check password strength
     */
    suspend fun checkPasswordStrength(password: String): Result<Int>
    
    /**
     * Get failed login attempts count
     */
    suspend fun getFailedLoginAttempts(userId: String): Result<Int>
    
    /**
     * Reset failed login attempts
     */
    suspend fun resetFailedLoginAttempts(userId: String): Result<Unit>
}