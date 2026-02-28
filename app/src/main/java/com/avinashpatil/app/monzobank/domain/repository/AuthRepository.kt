package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Authentication repository interface
 * Defines the contract for authentication operations
 */
interface AuthRepository {
    
    /**
     * Register a new user
     */
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    
    /**
     * Login user with email and password
     */
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    
    /**
     * Login user with biometric authentication
     */
    suspend fun biometricLogin(userId: String, biometricToken: String): Result<AuthResponse>
    
    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse>
    
    /**
     * Logout current user
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>
    
    /**
     * Verify password reset token and set new password
     */
    suspend fun verifyPasswordReset(token: String, newPassword: String): Result<Unit>
    
    /**
     * Verify email with OTP
     */
    suspend fun verifyEmail(email: String, code: String): Result<Unit>
    
    /**
     * Verify phone with OTP
     */
    suspend fun verifyPhone(phone: String, code: String): Result<Unit>
    
    /**
     * Resend verification code
     */
    suspend fun resendVerificationCode(email: String?, phone: String?, type: String): Result<Unit>
    
    /**
     * Setup two-factor authentication
     */
    suspend fun setupTwoFactorAuth(method: TwoFactorMethod, phone: String? = null): Result<TwoFactorAuthSetup>
    
    /**
     * Verify two-factor authentication code
     */
    suspend fun verifyTwoFactorAuth(userId: String, code: String, method: TwoFactorMethod): Result<AuthResponse>
    
    /**
     * Disable two-factor authentication
     */
    suspend fun disableTwoFactorAuth(): Result<Unit>
    
    /**
     * Get current user profile
     */
    suspend fun getUserProfile(): Result<User>
    
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(user: User): Result<User>
    
    /**
     * Change password
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    
    /**
     * Delete user account
     */
    suspend fun deleteAccount(password: String, reason: String?): Result<Unit>
    
    /**
     * Check if email exists
     */
    suspend fun checkEmailExists(email: String): Result<Boolean>
    
    /**
     * Check if phone exists
     */
    suspend fun checkPhoneExists(phone: String): Result<Boolean>
    
    /**
     * Setup biometric authentication
     */
    suspend fun setupBiometricAuth(biometricKey: String, deviceId: String): Result<Unit>
    
    /**
     * Get current user from local storage
     */
    suspend fun getCurrentUser(): User?
    
    /**
     * Get authentication token from local storage
     */
    suspend fun getAuthToken(): JwtToken?
    
    /**
     * Save user to local storage
     */
    suspend fun saveUser(user: User)
    
    /**
     * Save authentication token to local storage
     */
    suspend fun saveAuthToken(token: JwtToken)
    
    /**
     * Clear all local authentication data
     */
    suspend fun clearAuthData()
    
    /**
     * Check if user is logged in
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Check if biometric authentication is enabled
     */
    suspend fun isBiometricEnabled(): Boolean
    
    /**
     * Enable/disable biometric authentication
     */
    suspend fun setBiometricEnabled(enabled: Boolean)
    
    /**
     * Get authentication state as Flow
     */
    fun getAuthState(): Flow<AuthState>
    
    /**
     * Validate token expiry
     */
    suspend fun isTokenValid(): Boolean
}

/**
 * Authentication state sealed class
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}