package com.avinashpatil.app.monzobank.domain.model

import java.util.Date

/**
 * Notification preferences domain model
 * Represents user's notification preferences
 */
data class NotificationPreferences(
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
    val goalAlerts: Boolean = true,
    val promotionalOffers: Boolean = false,
    val weeklyDigest: Boolean = true,
    val monthlyStatement: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String? = null, // Format: "HH:mm"
    val quietHoursEnd: String? = null, // Format: "HH:mm"
    val timezone: String = "UTC",
    val language: String = "en",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)