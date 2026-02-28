package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.common.BaseResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.notification.NotificationListDto
import com.avinashpatil.app.monzobank.data.remote.dto.notification.NotificationPreferencesDto as NotificationSettingsDto
import com.avinashpatil.app.monzobank.data.remote.dto.notification.PushNotificationRequestDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Notification management API service interface
 * Based on the technical architecture API definitions
 */
interface NotificationApiService {
    
    /**
     * Get user notifications
     * GET /api/notifications
     */
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("unreadOnly") unreadOnly: Boolean = false
    ): Response<NotificationListDto>
    
    /**
     * Mark notification as read
     * PUT /api/notifications/{id}/read
     */
    @PUT("notifications/{id}/read")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: String
    ): Response<BaseResponseDto>
    
    /**
     * Mark all notifications as read
     * PUT /api/notifications/read-all
     */
    @PUT("notifications/read-all")
    suspend fun markAllAsRead(
        @Header("Authorization") token: String
    ): Response<BaseResponseDto>
    
    /**
     * Delete notification
     * DELETE /api/notifications/{id}
     */
    @DELETE("notifications/{id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("id") notificationId: String
    ): Response<BaseResponseDto>
    
    /**
     * Get notification settings
     * GET /api/notifications/settings
     */
    @GET("notifications/settings")
    suspend fun getNotificationSettings(
        @Header("Authorization") token: String
    ): Response<NotificationSettingsDto>
    
    /**
     * Update notification settings
     * PUT /api/notifications/settings
     */
    @PUT("notifications/settings")
    suspend fun updateNotificationSettings(
        @Header("Authorization") token: String,
        @Body request: NotificationSettingsDto
    ): Response<BaseResponseDto>
    
    /**
     * Send push notification
     * POST /api/notifications/push
     */
    @POST("notifications/push")
    suspend fun sendPushNotification(
        @Header("Authorization") token: String,
        @Body request: PushNotificationRequestDto
    ): Response<BaseResponseDto>
}