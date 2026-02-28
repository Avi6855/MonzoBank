package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class NotificationTemplate(
    val id: String,
    val name: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val titleTemplate: String,
    val messageTemplate: String,
    val isActive: Boolean = true,
    val supportedChannels: List<String> = listOf("push", "email", "sms"),
    val defaultPriority: NotificationPriority = NotificationPriority.NORMAL,
    val actionUrlTemplate: String? = null,
    val actionTextTemplate: String? = null,
    val imageUrlTemplate: String? = null,
    val variables: List<String> = emptyList(), // List of variable names used in templates
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val version: Int = 1,
    val description: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)