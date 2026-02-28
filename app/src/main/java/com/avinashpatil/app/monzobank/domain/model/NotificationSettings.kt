package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

/**
 * Notification settings domain model
 * Represents user's notification preferences
 */
data class NotificationSettings(
    val userId: String,
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val marketingNotifications: Boolean = false,
    val balanceAlerts: Boolean = true,
    val transactionAlerts: Boolean = true,
    val securityAlerts: Boolean = true,
    val paymentReminders: Boolean = true,
    val budgetAlerts: Boolean = true,
    val savingsGoalAlerts: Boolean = true,
    val cardAlerts: Boolean = true,
    val internationalTransactionAlerts: Boolean = true,
    val fraudAlerts: Boolean = true,
    val systemUpdates: Boolean = false,
    val socialNotifications: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String? = null, // Format: "HH:mm"
    val quietHoursEnd: String? = null,   // Format: "HH:mm"
    val timezone: String = "UTC",
    val language: String = "en",
    val updatedAt: LocalDateTime = LocalDateTime.now()
)