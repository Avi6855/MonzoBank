package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Account management API service interface
 * Based on the technical architecture API definitions
 */
interface AccountApiService {
    
    /**
     * Get all accounts for user
     * GET /api/accounts
     */
    @GET("accounts")
    suspend fun getAccounts(
        @Header("Authorization") token: String
    ): Response<List<AccountDto>>
    
    /**
     * Get account by ID
     * GET /api/accounts/{id}
     */
    @GET("accounts/{id}")
    suspend fun getAccountById(
        @Header("Authorization") token: String,
        @Path("id") accountId: String
    ): Response<AccountDto>
    
    /**
     * Create new account
     * POST /api/accounts
     */
    @POST("accounts")
    suspend fun createAccount(
        @Header("Authorization") token: String,
        @Body request: CreateAccountRequestDto
    ): Response<AccountDto>
    
    /**
     * Update account details
     * PUT /api/accounts/{id}
     */
    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Header("Authorization") token: String,
        @Path("id") accountId: String,
        @Body request: UpdateAccountRequestDto
    ): Response<AccountDto>
    
    /**
     * Get account balance
     * GET /api/accounts/{id}/balance
     */
    @GET("accounts/{id}/balance")
    suspend fun getAccountBalance(
        @Header("Authorization") token: String,
        @Path("id") accountId: String
    ): Response<AccountBalanceDto>
    
    /**
     * Get account statement
     * GET /api/accounts/{id}/statement
     */
    @GET("accounts/{id}/statement")
    suspend fun getAccountStatement(
        @Header("Authorization") token: String,
        @Path("id") accountId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("format") format: String = "json"
    ): Response<AccountStatementDto>
    
    /**
     * Close account
     * DELETE /api/accounts/{id}
     */
    @DELETE("accounts/{id}")
    suspend fun closeAccount(
        @Header("Authorization") token: String,
        @Path("id") accountId: String,
        @Body request: CloseAccountRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Get account limits
     * GET /api/accounts/{id}/limits
     */
    @GET("accounts/{id}/limits")
    suspend fun getAccountLimits(
        @Header("Authorization") token: String,
        @Path("id") accountId: String
    ): Response<AccountLimitsDto>
    
    /**
     * Update account limits
     * PUT /api/accounts/{id}/limits
     */
    @PUT("accounts/{id}/limits")
    suspend fun updateAccountLimits(
        @Header("Authorization") token: String,
        @Path("id") accountId: String,
        @Body request: UpdateAccountLimitsDto
    ): Response<AccountLimitsDto>
    
    /**
     * Get account notifications settings
     * GET /api/accounts/{id}/notifications
     */
    @GET("accounts/{id}/notifications")
    suspend fun getNotificationSettings(
        @Header("Authorization") token: String,
        @Path("id") accountId: String
    ): Response<NotificationSettingsDto>
    
    /**
     * Update account notifications settings
     * PUT /api/accounts/{id}/notifications
     */
    @PUT("accounts/{id}/notifications")
    suspend fun updateNotificationSettings(
        @Header("Authorization") token: String,
        @Path("id") accountId: String,
        @Body request: UpdateNotificationSettingsDto
    ): Response<NotificationSettingsDto>
}