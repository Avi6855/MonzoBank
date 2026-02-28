package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class ScheduledNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val title: String,
    val message: String,
    val scheduledAt: LocalDateTime,
    val status: ScheduledNotificationStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val sentAt: LocalDateTime? = null,
    val failureReason: String? = null,
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val channels: List<String> = listOf("push"),
    val templateId: String? = null,
    val actionUrl: String? = null,
    val actionText: String? = null,
    val imageUrl: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val relatedEntityId: String? = null,
    val relatedEntityType: String? = null
)