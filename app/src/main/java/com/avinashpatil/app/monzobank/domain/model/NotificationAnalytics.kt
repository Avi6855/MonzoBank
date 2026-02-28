package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

/**
 * Notification analytics domain model
 * Contains metrics and statistics about notification performance
 */
data class NotificationAnalytics(
    val userId: String,
    val period: AnalyticsPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val totalSent: Int,
    val totalDelivered: Int,
    val totalOpened: Int,
    val totalClicked: Int,
    val deliveryRate: Double, // Percentage
    val openRate: Double,     // Percentage
    val clickRate: Double,    // Percentage
    val categoryBreakdown: Map<NotificationCategory, CategoryStats>,
    val typeBreakdown: Map<NotificationType, TypeStats>,
    val deliveryMethodBreakdown: Map<NotificationDeliveryMethod, DeliveryStats>,
    val hourlyDistribution: Map<Int, Int>, // Hour (0-23) to count
    val dailyDistribution: Map<String, Int>, // Date to count
    val generatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Statistics for notification categories
 */
data class CategoryStats(
    val sent: Int,
    val delivered: Int,
    val opened: Int,
    val clicked: Int,
    val deliveryRate: Double,
    val openRate: Double,
    val clickRate: Double
)

/**
 * Statistics for notification types
 */
data class TypeStats(
    val sent: Int,
    val delivered: Int,
    val opened: Int,
    val clicked: Int,
    val deliveryRate: Double,
    val openRate: Double,
    val clickRate: Double
)

/**
 * Statistics for delivery methods
 */
data class DeliveryStats(
    val sent: Int,
    val delivered: Int,
    val opened: Int,
    val clicked: Int,
    val deliveryRate: Double,
    val openRate: Double,
    val clickRate: Double,
    val averageDeliveryTime: Long // milliseconds
)