package com.avinashpatil.app.monzobank.domain.model

import java.util.Date

/**
 * Represents user preferences
 */
data class UserPreferences(
    val userId: String,
    val notificationPreferences: NotificationPreferences? = null,
    val privacyPreferences: PrivacyPreferences? = null,
    val displayPreferences: DisplayPreferences? = null,
    val language: String = "en",
    val currency: String = "USD",
    val timezone: String = "UTC",
    val dateFormat: String = "MM/dd/yyyy",
    val timeFormat: String = "12h", // 12h or 24h
    val autoLockTimeout: Int = 5, // minutes
    val biometricEnabled: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val marketingOptIn: Boolean = false,
    val dataAnalyticsOptIn: Boolean = true,
    val locationServicesEnabled: Boolean = false,
    val crashReportingEnabled: Boolean = true,
    val performanceMonitoringEnabled: Boolean = true,
    val customSettings: Map<String, Any> = emptyMap(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Privacy preferences for the user
 */
data class PrivacyPreferences(
    val userId: String,
    val shareDataWithPartners: Boolean = false,
    val allowPersonalizedAds: Boolean = false,
    val shareUsageAnalytics: Boolean = true,
    val allowLocationTracking: Boolean = false,
    val shareTransactionData: Boolean = false,
    val allowCookies: Boolean = true,
    val dataRetentionPeriod: Int = 365, // days
    val anonymizeData: Boolean = true,
    val allowThirdPartyIntegrations: Boolean = false,
    val shareContactInformation: Boolean = false,
    val allowBehavioralAnalysis: Boolean = false,
    val gdprCompliant: Boolean = true,
    val ccpaCompliant: Boolean = true,
    val dataProcessingConsent: Boolean = true,
    val marketingCommunicationConsent: Boolean = false,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Display preferences for the user interface
 */
data class DisplayPreferences(
    val userId: String,
    val theme: AppTheme = AppTheme.SYSTEM,
    val fontSize: FontSize = FontSize.MEDIUM,
    val colorScheme: ColorScheme = ColorScheme.DEFAULT,
    val showBalanceOnHomeScreen: Boolean = true,
    val showRecentTransactions: Boolean = true,
    val transactionListLimit: Int = 20,
    val showSpendingInsights: Boolean = true,
    val showBudgetProgress: Boolean = true,
    val showGoalProgress: Boolean = true,
    val enableAnimations: Boolean = true,
    val enableHapticFeedback: Boolean = true,
    val showCurrencySymbol: Boolean = true,
    val roundAmounts: Boolean = false,
    val compactView: Boolean = false,
    val showCardNumbers: Boolean = false, // masked by default
    val dashboardLayout: DashboardLayout = DashboardLayout.DEFAULT,
    val chartType: ChartType = ChartType.BAR,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * App theme options
 */
enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM // Follow system theme
}

/**
 * Font size options
 */
enum class FontSize {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE
}

/**
 * Color scheme options
 */
enum class ColorScheme {
    DEFAULT,
    BLUE,
    GREEN,
    PURPLE,
    ORANGE,
    HIGH_CONTRAST
}

/**
 * Dashboard layout options
 */
enum class DashboardLayout {
    DEFAULT,
    COMPACT,
    DETAILED,
    MINIMAL
}

/**
 * Chart type preferences
 */
enum class ChartType {
    BAR,
    LINE,
    PIE,
    DONUT
}