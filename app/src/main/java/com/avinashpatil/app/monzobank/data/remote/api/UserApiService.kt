package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.*
import com.avinashpatil.app.monzobank.data.remote.dto.common.BaseResponseDto
import retrofit2.Response
import retrofit2.http.*

/**
 * User management API service interface
 * Based on the technical architecture API definitions
 */
interface UserApiService {
    
    /**
     * Get user profile
     * GET /api/users/profile
     */
    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileDto>
    
    /**
     * Update user profile
     * PUT /api/users/profile
     */
    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateUserProfileRequestDto
    ): Response<UserProfileDto>
    
    /**
     * Update user preferences
     * PUT /api/users/preferences
     */
    @PUT("users/preferences")
    suspend fun updateUserPreferences(
        @Header("Authorization") token: String,
        @Body request: UserPreferencesDto
    ): Response<BaseResponseDto>
    
    /**
     * Get user preferences
     * GET /api/users/preferences
     */
    @GET("users/preferences")
    suspend fun getUserPreferences(
        @Header("Authorization") token: String
    ): Response<UserPreferencesDto>
    
    /**
     * Delete user account
     * DELETE /api/users/account
     */
    @DELETE("users/account")
    suspend fun deleteUserAccount(
        @Header("Authorization") token: String,
        @Body request: DeleteAccountRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Get user activity log
     * GET /api/users/activity
     */
    @GET("users/activity")
    suspend fun getUserActivity(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<UserActivityDto>
}