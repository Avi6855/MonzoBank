package com.avinashpatil.app.monzobank.domain.model

import java.util.Date

/**
 * Represents an analytics event in the system
 */
data class AnalyticsEvent(
    val id: String,
    val userId: String,
    val eventName: String,
    val eventType: AnalyticsEventType,
    val properties: Map<String, Any>,
    val sessionId: String?,
    val deviceId: String?,
    val platform: String?,
    val appVersion: String?,
    val location: String?,
    val timestamp: Date,
    val processed: Boolean = false,
    val createdAt: Date
)

/**
 * Types of analytics events
 */
enum class AnalyticsEventType {
    USER_ACTION,
    TRANSACTION,
    NAVIGATION,
    ERROR,
    PERFORMANCE,
    ENGAGEMENT,
    CONVERSION,
    RETENTION,
    CUSTOM
}

/**
 * Spending analytics data
 */
data class SpendingAnalytics(
    val userId: String,
    val period: AnalyticsPeriod,
    val totalSpending: Double,
    val averageSpending: Double,
    val spendingByCategory: Map<String, Double>,
    val spendingByMerchant: Map<String, Double>,
    val spendingTrend: List<DailySpending>,
    val largestTransaction: Double,
    val smallestTransaction: Double,
    val transactionCount: Int,
    val comparisonToPreviousPeriod: Double, // percentage change
    val generatedAt: Date
)

/**
 * Daily spending data point
 */
data class DailySpending(
    val date: Date,
    val amount: Double,
    val transactionCount: Int
)

/**
 * User behavior analytics
 */
data class UserBehaviorAnalytics(
    val userId: String,
    val period: AnalyticsPeriod,
    val sessionCount: Int,
    val averageSessionDuration: Long, // milliseconds
    val screenViews: Map<String, Int>,
    val featureUsage: Map<String, Int>,
    val errorCount: Int,
    val crashCount: Int,
    val conversionEvents: List<String>,
    val retentionRate: Double,
    val engagementScore: Double,
    val lastActiveDate: Date,
    val generatedAt: Date
)