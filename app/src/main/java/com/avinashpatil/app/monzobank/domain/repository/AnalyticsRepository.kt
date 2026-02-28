package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.AnalyticsEvent
import com.avinashpatil.app.monzobank.domain.model.AnalyticsPeriod
import com.avinashpatil.app.monzobank.domain.model.SpendingAnalytics
import com.avinashpatil.app.monzobank.domain.model.UserBehaviorAnalytics
import com.avinashpatil.app.monzobank.domain.model.TransactionAnalytics
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for analytics and reporting operations
 */
interface AnalyticsRepository {
    
    /**
     * Track an analytics event
     */
    suspend fun trackEvent(
        userId: String,
        eventName: String,
        properties: Map<String, Any> = emptyMap(),
        timestamp: Date = Date()
    ): Result<Unit>
    
    /**
     * Get spending analytics for a user
     */
    suspend fun getSpendingAnalytics(
        userId: String,
        period: AnalyticsPeriod,
        startDate: Date? = null,
        endDate: Date? = null
    ): Result<SpendingAnalytics>
    
    /**
     * Get transaction analytics for a user
     */
    suspend fun getTransactionAnalytics(
        userId: String,
        period: AnalyticsPeriod,
        startDate: Date? = null,
        endDate: Date? = null
    ): Result<TransactionAnalytics>
    
    /**
     * Get user behavior analytics
     */
    suspend fun getUserBehaviorAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Result<UserBehaviorAnalytics>
    
    /**
     * Get analytics events for a user
     */
    suspend fun getAnalyticsEvents(
        userId: String,
        eventName: String? = null,
        startDate: Date? = null,
        endDate: Date? = null,
        limit: Int = 100
    ): Result<List<AnalyticsEvent>>
    
    /**
     * Get spending trends over time
     */
    suspend fun getSpendingTrends(
        userId: String,
        period: AnalyticsPeriod,
        categoryFilter: String? = null
    ): Result<Map<String, Double>>
    
    /**
     * Get income vs expense comparison
     */
    suspend fun getIncomeVsExpenseAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Map<String, Double>>
    
    /**
     * Get budget performance analytics
     */
    suspend fun getBudgetPerformance(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Map<String, Any>>
    
    /**
     * Get merchant spending analytics
     */
    suspend fun getMerchantSpendingAnalytics(
        userId: String,
        period: AnalyticsPeriod,
        limit: Int = 10
    ): Result<Map<String, Double>>
    
    /**
     * Get category spending breakdown
     */
    suspend fun getCategorySpendingBreakdown(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Map<String, Double>>
    
    /**
     * Get payment method usage analytics
     */
    suspend fun getPaymentMethodAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Map<String, Int>>
    
    /**
     * Get financial health score
     */
    suspend fun getFinancialHealthScore(userId: String): Result<Double>
    
    /**
     * Get savings rate analytics
     */
    suspend fun getSavingsRateAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Double>
    
    /**
     * Get cash flow analytics
     */
    suspend fun getCashFlowAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Map<String, Double>>
    
    /**
     * Get goal progress analytics
     */
    suspend fun getGoalProgressAnalytics(
        userId: String
    ): Result<Map<String, Double>>
    
    /**
     * Observe real-time analytics updates
     */
    fun observeAnalyticsUpdates(userId: String): Flow<AnalyticsEvent>
    
    /**
     * Generate analytics report
     */
    suspend fun generateAnalyticsReport(
        userId: String,
        reportType: String,
        period: AnalyticsPeriod,
        format: String = "JSON"
    ): Result<String>
    
    /**
     * Get comparative analytics (vs previous period)
     */
    suspend fun getComparativeAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Result<Map<String, Any>>
    
    /**
     * Get predictive analytics
     */
    suspend fun getPredictiveAnalytics(
        userId: String,
        predictionType: String,
        timeframe: Int // days into future
    ): Result<Map<String, Any>>
    
    /**
     * Clear analytics data for a user
     */
    suspend fun clearAnalyticsData(
        userId: String,
        olderThan: Date? = null
    ): Result<Unit>
}