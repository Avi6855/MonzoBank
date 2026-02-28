package com.avinashpatil.app.monzobank.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notification_preferences")
data class NotificationPreferences(
    @PrimaryKey
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
    val quietHoursStart: String? = null, // Format: "22:00"
    val quietHoursEnd: String? = null, // Format: "08:00"
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)