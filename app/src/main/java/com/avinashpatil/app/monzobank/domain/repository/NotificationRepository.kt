package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Notification repository interface
 * Defines the contract for notification operations
 */
interface NotificationRepository {
    
    // Notification CRUD Operations
    suspend fun getNotifications(
        userId: String,
        page: Int = 1,
        limit: Int = 50,
        unreadOnly: Boolean = false
    ): Result<List<Notification>>
    
    suspend fun getNotificationById(notificationId: String): Result<Notification>
    
    suspend fun markAsRead(notificationId: String): Result<Unit>
    
    suspend fun markAllAsRead(userId: String): Result<Unit>
    
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    
    suspend fun deleteAllNotifications(userId: String): Result<Unit>
    
    // Notification Creation
    suspend fun createNotification(
        userId: String,
        type: NotificationType,
        title: String,
        message: String,
        data: Map<String, Any>? = null,
        priority: NotificationPriority = NotificationPriority.NORMAL
    ): Result<Notification>
    
    suspend fun sendPushNotification(
        userId: String,
        title: String,
        message: String,
        data: Map<String, Any>? = null
    ): Result<Unit>
    
    suspend fun sendEmailNotification(
        userId: String,
        subject: String,
        body: String,
        templateId: String? = null
    ): Result<Unit>
    
    suspend fun sendSmsNotification(
        userId: String,
        message: String
    ): Result<Unit>
    
    // Notification Settings
    suspend fun getNotificationSettings(userId: String): Result<NotificationSettings>
    
    suspend fun updateNotificationSettings(
        userId: String,
        settings: NotificationSettings
    ): Result<Unit>
    
    suspend fun updatePushTokens(
        userId: String,
        tokens: List<String>
    ): Result<Unit>
    
    // Notification Categories
    suspend fun getNotificationsByCategory(
        userId: String,
        category: NotificationCategory,
        limit: Int = 50
    ): Result<List<Notification>>
    
    suspend fun getUnreadCount(userId: String): Result<Int>
    
    suspend fun getUnreadCountByCategory(
        userId: String,
        category: NotificationCategory
    ): Result<Int>
    
    // Notification Scheduling
    suspend fun scheduleNotification(
        userId: String,
        type: NotificationType,
        title: String,
        message: String,
        scheduledTime: Date,
        data: Map<String, Any>? = null
    ): Result<ScheduledNotification>
    
    suspend fun cancelScheduledNotification(notificationId: String): Result<Unit>
    
    suspend fun getScheduledNotifications(userId: String): Result<List<ScheduledNotification>>
    
    // Notification Templates
    suspend fun getNotificationTemplates(): Result<List<NotificationTemplate>>
    
    suspend fun createNotificationTemplate(
        name: String,
        subject: String,
        body: String,
        type: NotificationType
    ): Result<NotificationTemplate>
    
    // Real-time updates
    fun observeNotifications(userId: String): Flow<List<Notification>>
    
    fun observeUnreadCount(userId: String): Flow<Int>
    
    fun observeNotificationSettings(userId: String): Flow<NotificationSettings>
    
    // Notification Analytics
    suspend fun getNotificationAnalytics(
        userId: String,
        startDate: Date,
        endDate: Date
    ): Result<NotificationAnalytics>
    
    suspend fun markNotificationDelivered(
        notificationId: String,
        deliveryMethod: NotificationDeliveryMethod
    ): Result<Unit>
    
    suspend fun markNotificationOpened(notificationId: String): Result<Unit>
    
    // Bulk Operations
    suspend fun bulkMarkAsRead(
        userId: String,
        notificationIds: List<String>
    ): Result<Unit>
    
    suspend fun bulkDeleteNotifications(
        userId: String,
        notificationIds: List<String>
    ): Result<Unit>
}