package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Authentication API service interface
 * Based on the technical architecture API definitions
 */
interface AuthApiService {
    
    /**
     * User registration endpoint
     * POST /api/auth/register
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<AuthResponseDto>
    
    /**
     * User login endpoint
     * POST /api/auth/login
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<AuthResponseDto>
    
    /**
     * Refresh token endpoint
     * POST /api/auth/refresh
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Response<AuthResponseDto>
    
    /**
     * Logout endpoint
     * POST /api/auth/logout
     */
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<BaseResponseDto>
    
    /**
     * Password reset request
     * POST /api/auth/password-reset
     */
    @POST("auth/password-reset")
    suspend fun requestPasswordReset(
        @Body request: PasswordResetRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Verify password reset token
     * POST /api/auth/password-reset/verify
     */
    @POST("auth/password-reset/verify")
    suspend fun verifyPasswordReset(
        @Body request: VerifyPasswordResetDto
    ): Response<BaseResponseDto>
    
    /**
     * Email verification
     * POST /api/auth/verify-email
     */
    @POST("auth/verify-email")
    suspend fun verifyEmail(
        @Body request: EmailVerificationDto
    ): Response<BaseResponseDto>
    
    /**
     * Phone verification
     * POST /api/auth/verify-phone
     */
    @POST("auth/verify-phone")
    suspend fun verifyPhone(
        @Body request: PhoneVerificationDto
    ): Response<BaseResponseDto>
    
    /**
     * Resend verification code
     * POST /api/auth/resend-verification
     */
    @POST("auth/resend-verification")
    suspend fun resendVerificationCode(
        @Body request: ResendVerificationDto
    ): Response<BaseResponseDto>
    
    /**
     * Setup two-factor authentication
     * POST /api/auth/2fa/setup
     */
    @POST("auth/2fa/setup")
    suspend fun setupTwoFactorAuth(
        @Header("Authorization") token: String,
        @Body request: TwoFactorSetupDto
    ): Response<TwoFactorResponseDto>
    
    /**
     * Verify two-factor authentication
     * POST /api/auth/2fa/verify
     */
    @POST("auth/2fa/verify")
    suspend fun verifyTwoFactorAuth(
        @Body request: TwoFactorVerifyDto
    ): Response<AuthResponseDto>
    
    /**
     * Disable two-factor authentication
     * DELETE /api/auth/2fa
     */
    @DELETE("auth/2fa")
    suspend fun disableTwoFactorAuth(
        @Header("Authorization") token: String
    ): Response<BaseResponseDto>
    
    /**
     * Get user profile
     * GET /api/auth/profile
     */
    @GET("auth/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileDto>
    
    /**
     * Update user profile
     * PUT /api/auth/profile
     */
    @PUT("auth/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileDto
    ): Response<UserProfileDto>
    
    /**
     * Change password
     * PUT /api/auth/change-password
     */
    @PUT("auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordDto
    ): Response<BaseResponseDto>
    
    /**
     * Delete account
     * DELETE /api/auth/account
     */
    @DELETE("auth/account")
    suspend fun deleteAccount(
        @Header("Authorization") token: String,
        @Body request: DeleteAccountDto
    ): Response<BaseResponseDto>
    
    /**
     * Check if email exists
     * GET /api/auth/check-email
     */
    @GET("auth/check-email")
    suspend fun checkEmailExists(
        @Query("email") email: String
    ): Response<CheckExistsResponseDto>
    
    /**
     * Check if phone exists
     * GET /api/auth/check-phone
     */
    @GET("auth/check-phone")
    suspend fun checkPhoneExists(
        @Query("phone") phone: String
    ): Response<CheckExistsResponseDto>
    
    /**
     * Biometric authentication setup
     * POST /api/auth/biometric/setup
     */
    @POST("auth/biometric/setup")
    suspend fun setupBiometricAuth(
        @Header("Authorization") token: String,
        @Body request: BiometricSetupDto
    ): Response<BaseResponseDto>
    
    /**
     * Biometric authentication login
     * POST /api/auth/biometric/login
     */
    @POST("auth/biometric/login")
    suspend fun biometricLogin(
        @Body request: BiometricLoginDto
    ): Response<AuthResponseDto>
}