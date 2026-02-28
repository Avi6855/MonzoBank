package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: LocalDateTime,
    val readAt: LocalDateTime? = null,
    val expiresAt: LocalDateTime? = null,
    val actionUrl: String? = null,
    val actionText: String? = null,
    val imageUrl: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val channels: List<String> = listOf("push"), // push, email, sms
    val templateId: String? = null,
    val relatedEntityId: String? = null, // transaction id, payment id, etc.
    val relatedEntityType: String? = null // transaction, payment, card, etc.
)