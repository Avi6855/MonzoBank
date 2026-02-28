package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmsNotificationRequestDto(
    @Json(name = "recipient_phone")
    val recipientPhone: String,
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "notification_type")
    val notificationType: String, // "transaction", "security", "marketing", "alert"
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "sender_id")
    val senderId: String?,
    
    @Json(name = "template_id")
    val templateId: String?,
    
    @Json(name = "template_variables")
    val templateVariables: Map<String, String>?,
    
    @Json(name = "scheduled_time")
    val scheduledTime: String?, // ISO 8601 format
    
    @Json(name = "country_code")
    val countryCode: String,
    
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "tracking_id")
    val trackingId: String?
)

@JsonClass(generateAdapter = true)
data class EmailNotificationRequestDto(
    @Json(name = "recipient_email")
    val recipientEmail: String,
    
    @Json(name = "subject")
    val subject: String,
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "html_content")
    val htmlContent: String?,
    
    @Json(name = "notification_type")
    val notificationType: String, // "transaction", "security", "marketing", "alert", "statement"
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "sender_email")
    val senderEmail: String?,
    
    @Json(name = "sender_name")
    val senderName: String?,
    
    @Json(name = "template_id")
    val templateId: String?,
    
    @Json(name = "template_variables")
    val templateVariables: Map<String, String>?,
    
    @Json(name = "attachments")
    val attachments: List<EmailAttachmentDto>?,
    
    @Json(name = "scheduled_time")
    val scheduledTime: String?, // ISO 8601 format
    
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "tracking_id")
    val trackingId: String?,
    
    @Json(name = "reply_to")
    val replyTo: String?,
    
    @Json(name = "tags")
    val tags: List<String>?
)

@JsonClass(generateAdapter = true)
data class EmailAttachmentDto(
    @Json(name = "filename")
    val filename: String,
    
    @Json(name = "content_type")
    val contentType: String,
    
    @Json(name = "content")
    val content: String, // Base64 encoded
    
    @Json(name = "size_bytes")
    val sizeBytes: Long
)

@JsonClass(generateAdapter = true)
data class NotificationResponseDto(
    @Json(name = "notification_id")
    val notificationId: String,
    
    @Json(name = "status")
    val status: String, // "sent", "pending", "failed", "scheduled"
    
    @Json(name = "delivery_status")
    val deliveryStatus: String?, // "delivered", "bounced", "opened", "clicked"
    
    @Json(name = "sent_at")
    val sentAt: String?,
    
    @Json(name = "delivered_at")
    val deliveredAt: String?,
    
    @Json(name = "error_message")
    val errorMessage: String?,
    
    @Json(name = "error_code")
    val errorCode: String?,
    
    @Json(name = "cost")
    val cost: String?, // Cost in currency units
    
    @Json(name = "currency")
    val currency: String?,
    
    @Json(name = "provider")
    val provider: String, // "twilio", "sendgrid", "aws_ses", etc.
    
    @Json(name = "tracking_events")
    val trackingEvents: List<NotificationTrackingEventDto>?
)

@JsonClass(generateAdapter = true)
data class NotificationTrackingEventDto(
    @Json(name = "event_type")
    val eventType: String, // "sent", "delivered", "opened", "clicked", "bounced", "unsubscribed"
    
    @Json(name = "timestamp")
    val timestamp: String,
    
    @Json(name = "user_agent")
    val userAgent: String?,
    
    @Json(name = "ip_address")
    val ipAddress: String?,
    
    @Json(name = "location")
    val location: String?,
    
    @Json(name = "device_type")
    val deviceType: String? // "mobile", "desktop", "tablet"
)