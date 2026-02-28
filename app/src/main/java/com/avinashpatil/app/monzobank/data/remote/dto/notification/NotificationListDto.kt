package com.avinashpatil.app.monzobank.data.remote.dto.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationListDto(
    @Json(name = "notifications")
    val notifications: List<NotificationDto>,
    
    @Json(name = "total_count")
    val totalCount: Int,
    
    @Json(name = "unread_count")
    val unreadCount: Int,
    
    @Json(name = "page")
    val page: Int,
    
    @Json(name = "page_size")
    val pageSize: Int,
    
    @Json(name = "has_more")
    val hasMore: Boolean,
    
    @Json(name = "last_updated")
    val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "notification_id")
    val notificationId: String,
    
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "type")
    val type: String, // "transaction", "security", "account", "marketing", "system"
    
    @Json(name = "category")
    val category: String, // "payment", "deposit", "withdrawal", "alert", "promotion"
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "short_message")
    val shortMessage: String?,
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "status")
    val status: String, // "unread", "read", "archived", "deleted"
    
    @Json(name = "is_actionable")
    val isActionable: Boolean,
    
    @Json(name = "action_url")
    val actionUrl: String?,
    
    @Json(name = "action_text")
    val actionText: String?,
    
    @Json(name = "metadata")
    val metadata: NotificationMetadataDto?,
    
    @Json(name = "created_at")
    val createdAt: String,
    
    @Json(name = "read_at")
    val readAt: String?,
    
    @Json(name = "expires_at")
    val expiresAt: String?,
    
    @Json(name = "delivery_channels")
    val deliveryChannels: List<String>, // ["push", "email", "sms", "in_app"]
    
    @Json(name = "tags")
    val tags: List<String>?
)

@JsonClass(generateAdapter = true)
data class NotificationMetadataDto(
    @Json(name = "transaction_id")
    val transactionId: String?,
    
    @Json(name = "account_id")
    val accountId: String?,
    
    @Json(name = "amount")
    val amount: String?,
    
    @Json(name = "currency")
    val currency: String?,
    
    @Json(name = "merchant_name")
    val merchantName: String?,
    
    @Json(name = "location")
    val location: String?,
    
    @Json(name = "reference_id")
    val referenceId: String?,
    
    @Json(name = "image_url")
    val imageUrl: String?,
    
    @Json(name = "icon_url")
    val iconUrl: String?,
    
    @Json(name = "deep_link")
    val deepLink: String?,
    
    @Json(name = "custom_data")
    val customData: Map<String, String>?
)

@JsonClass(generateAdapter = true)
data class NotificationPreferencesDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "push_notifications")
    val pushNotifications: Boolean,
    
    @Json(name = "email_notifications")
    val emailNotifications: Boolean,
    
    @Json(name = "sms_notifications")
    val smsNotifications: Boolean,
    
    @Json(name = "transaction_alerts")
    val transactionAlerts: Boolean,
    
    @Json(name = "security_alerts")
    val securityAlerts: Boolean,
    
    @Json(name = "marketing_notifications")
    val marketingNotifications: Boolean,
    
    @Json(name = "account_updates")
    val accountUpdates: Boolean,
    
    @Json(name = "quiet_hours_start")
    val quietHoursStart: String?, // "22:00"
    
    @Json(name = "quiet_hours_end")
    val quietHoursEnd: String?, // "08:00"
    
    @Json(name = "timezone")
    val timezone: String,
    
    @Json(name = "frequency_limits")
    val frequencyLimits: NotificationFrequencyDto?
)

@JsonClass(generateAdapter = true)
data class NotificationFrequencyDto(
    @Json(name = "max_daily_notifications")
    val maxDailyNotifications: Int,
    
    @Json(name = "max_weekly_marketing")
    val maxWeeklyMarketing: Int,
    
    @Json(name = "transaction_threshold")
    val transactionThreshold: String?, // Minimum amount for transaction notifications
    
    @Json(name = "batch_notifications")
    val batchNotifications: Boolean
)