package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.common.BaseResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.security.ChangePasswordRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.security.DisableTwoFactorRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.security.EnableTwoFactorRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.security.SecuritySettingsDto
import com.avinashpatil.app.monzobank.data.remote.dto.security.TwoFactorSetupResponseDto as TwoFactorResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.security.VerifyTwoFactorRequestDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Security management API service interface
 * Based on the technical architecture API definitions
 */
interface SecurityApiService {
    
    /**
     * Enable two-factor authentication
     * POST /api/security/2fa/enable
     */
    @POST("security/2fa/enable")
    suspend fun enableTwoFactor(
        @Header("Authorization") token: String,
        @Body request: EnableTwoFactorRequestDto
    ): Response<TwoFactorResponseDto>
    
    /**
     * Disable two-factor authentication
     * POST /api/security/2fa/disable
     */
    @POST("security/2fa/disable")
    suspend fun disableTwoFactor(
        @Header("Authorization") token: String,
        @Body request: DisableTwoFactorRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Verify two-factor authentication code
     * POST /api/security/2fa/verify
     */
    @POST("security/2fa/verify")
    suspend fun verifyTwoFactor(
        @Header("Authorization") token: String,
        @Body request: VerifyTwoFactorRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Change password
     * PUT /api/security/password
     */
    @PUT("security/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Get security settings
     * GET /api/security/settings
     */
    @GET("security/settings")
    suspend fun getSecuritySettings(
        @Header("Authorization") token: String
    ): Response<SecuritySettingsDto>
    
    /**
     * Update security settings
     * PUT /api/security/settings
     */
    @PUT("security/settings")
    suspend fun updateSecuritySettings(
        @Header("Authorization") token: String,
        @Body request: SecuritySettingsDto
    ): Response<BaseResponseDto>
    
    // TODO: Implement these methods when additional DTOs are created
    /*
    /**
     * Get login history
     * GET /api/security/login-history
     */
    @GET("security/login-history")
    suspend fun getLoginHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<LoginHistoryDto>
    
    /**
     * Report suspicious activity
     * POST /api/security/report-suspicious
     */
    @POST("security/report-suspicious")
    suspend fun reportSuspiciousActivity(
        @Header("Authorization") token: String,
        @Body request: SuspiciousActivityRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Lock account
     * POST /api/security/lock-account
     */
    @POST("security/lock-account")
    suspend fun lockAccount(
        @Header("Authorization") token: String,
        @Body request: LockAccountRequestDto
    ): Response<BaseResponseDto>
    */
}