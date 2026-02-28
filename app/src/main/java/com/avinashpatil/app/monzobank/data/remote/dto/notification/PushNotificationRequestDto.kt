package com.avinashpatil.app.monzobank.data.remote.dto.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PushNotificationRequestDto(
    @Json(name = "recipient_tokens")
    val recipientTokens: List<String>, // FCM/APNS device tokens
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "body")
    val body: String,
    
    @Json(name = "notification_type")
    val notificationType: String, // "transaction", "security", "marketing", "alert", "system"
    
    @Json(name = "priority")
    val priority: String, // "high", "normal", "low"
    
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "data_payload")
    val dataPayload: Map<String, String>?,
    
    @Json(name = "action_buttons")
    val actionButtons: List<NotificationActionDto>?,
    
    @Json(name = "image_url")
    val imageUrl: String?,
    
    @Json(name = "icon")
    val icon: String?,
    
    @Json(name = "sound")
    val sound: String?, // "default", "custom_sound.wav", "silent"
    
    @Json(name = "badge_count")
    val badgeCount: Int?,
    
    @Json(name = "category")
    val category: String?, // iOS notification category
    
    @Json(name = "thread_id")
    val threadId: String?, // iOS notification grouping
    
    @Json(name = "collapse_key")
    val collapseKey: String?, // Android notification grouping
    
    @Json(name = "time_to_live")
    val timeToLive: Int?, // TTL in seconds
    
    @Json(name = "scheduled_time")
    val scheduledTime: String?, // ISO 8601 format
    
    @Json(name = "deep_link")
    val deepLink: String?,
    
    @Json(name = "tracking_id")
    val trackingId: String?,
    
    @Json(name = "platform_specific")
    val platformSpecific: PlatformSpecificDto?
)

@JsonClass(generateAdapter = true)
data class NotificationActionDto(
    @Json(name = "action_id")
    val actionId: String,
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "icon")
    val icon: String?,
    
    @Json(name = "action_type")
    val actionType: String, // "button", "input", "dismiss"
    
    @Json(name = "action_url")
    val actionUrl: String?,
    
    @Json(name = "requires_authentication")
    val requiresAuthentication: Boolean,
    
    @Json(name = "destructive")
    val destructive: Boolean // For iOS, marks as destructive action
)

@JsonClass(generateAdapter = true)
data class PlatformSpecificDto(
    @Json(name = "android")
    val android: AndroidNotificationDto?,
    
    @Json(name = "ios")
    val ios: IosNotificationDto?
)

@JsonClass(generateAdapter = true)
data class AndroidNotificationDto(
    @Json(name = "channel_id")
    val channelId: String,
    
    @Json(name = "notification_priority")
    val notificationPriority: String, // "default", "high", "low", "max", "min"
    
    @Json(name = "visibility")
    val visibility: String, // "public", "private", "secret"
    
    @Json(name = "color")
    val color: String?, // Hex color code
    
    @Json(name = "led_color")
    val ledColor: String?,
    
    @Json(name = "vibration_pattern")
    val vibrationPattern: List<Long>?,
    
    @Json(name = "sticky")
    val sticky: Boolean,
    
    @Json(name = "ongoing")
    val ongoing: Boolean,
    
    @Json(name = "auto_cancel")
    val autoCancel: Boolean,
    
    @Json(name = "large_icon")
    val largeIcon: String?,
    
    @Json(name = "big_text")
    val bigText: String?,
    
    @Json(name = "big_picture")
    val bigPicture: String?
)

@JsonClass(generateAdapter = true)
data class IosNotificationDto(
    @Json(name = "badge")
    val badge: Int?,
    
    @Json(name = "sound")
    val sound: IosNotificationSoundDto?,
    
    @Json(name = "content_available")
    val contentAvailable: Boolean,
    
    @Json(name = "mutable_content")
    val mutableContent: Boolean,
    
    @Json(name = "thread_id")
    val threadId: String?,
    
    @Json(name = "category")
    val category: String?,
    
    @Json(name = "target_content_id")
    val targetContentId: String?,
    
    @Json(name = "interruption_level")
    val interruptionLevel: String?, // "passive", "active", "timeSensitive", "critical"
    
    @Json(name = "relevance_score")
    val relevanceScore: Double? // 0.0 to 1.0
)

@JsonClass(generateAdapter = true)
data class IosNotificationSoundDto(
    @Json(name = "name")
    val name: String,
    
    @Json(name = "critical")
    val critical: Boolean,
    
    @Json(name = "volume")
    val volume: Double? // 0.0 to 1.0
)

@JsonClass(generateAdapter = true)
data class PushNotificationResponseDto(
    @Json(name = "notification_id")
    val notificationId: String,
    
    @Json(name = "status")
    val status: String, // "sent", "failed", "partial", "scheduled"
    
    @Json(name = "sent_count")
    val sentCount: Int,
    
    @Json(name = "failed_count")
    val failedCount: Int,
    
    @Json(name = "delivery_results")
    val deliveryResults: List<DeliveryResultDto>?,
    
    @Json(name = "sent_at")
    val sentAt: String?,
    
    @Json(name = "scheduled_for")
    val scheduledFor: String?,
    
    @Json(name = "error_message")
    val errorMessage: String?,
    
    @Json(name = "tracking_id")
    val trackingId: String?
)

@JsonClass(generateAdapter = true)
data class DeliveryResultDto(
    @Json(name = "token")
    val token: String,
    
    @Json(name = "status")
    val status: String, // "success", "failed", "invalid_token"
    
    @Json(name = "error_code")
    val errorCode: String?,
    
    @Json(name = "error_message")
    val errorMessage: String?,
    
    @Json(name = "platform")
    val platform: String?
)