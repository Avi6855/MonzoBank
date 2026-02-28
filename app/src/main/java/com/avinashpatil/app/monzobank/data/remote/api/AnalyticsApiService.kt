package com.avinashpatil.app.monzobank.data.remote.api


import com.avinashpatil.app.monzobank.data.remote.dto.analytics.BudgetInsightsDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.CashFlowAnalysisDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.CategoryBreakdownDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.ExportDataDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.FinancialHealthScoreDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.MerchantAnalyticsDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.MonthlyTrendsDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.SavingsOpportunitiesDto
import com.avinashpatil.app.monzobank.data.remote.dto.analytics.SpendingAnalyticsDto
import com.avinashpatil.app.monzobank.data.remote.dto.common.BaseResponseDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Analytics and insights API service interface
 * Based on the technical architecture API definitions
 */
interface AnalyticsApiService {
    
    /**
     * Get spending analytics
     * GET /api/analytics/spending
     */
    @GET("analytics/spending")
    suspend fun getSpendingAnalytics(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("period") period: String = "monthly",
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<SpendingAnalyticsDto>
    
    /**
     * Get category breakdown
     * GET /api/analytics/categories
     */
    @GET("analytics/categories")
    suspend fun getCategoryBreakdown(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("period") period: String = "monthly"
    ): Response<CategoryBreakdownDto>
    
    /**
     * Get merchant analytics
     * GET /api/analytics/merchants
     */
    @GET("analytics/merchants")
    suspend fun getMerchantAnalytics(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("limit") limit: Int = 10
    ): Response<MerchantAnalyticsDto>
    
    /**
     * Get monthly trends
     * GET /api/analytics/trends
     */
    @GET("analytics/trends")
    suspend fun getMonthlyTrends(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("months") months: Int = 12
    ): Response<MonthlyTrendsDto>
    
    /**
     * Get budget insights
     * GET /api/analytics/budget
     */
    @GET("analytics/budget")
    suspend fun getBudgetInsights(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String
    ): Response<BudgetInsightsDto>
    
    /**
     * Get financial health score
     * GET /api/analytics/health-score
     */
    @GET("analytics/health-score")
    suspend fun getFinancialHealthScore(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String
    ): Response<FinancialHealthScoreDto>
    
    /**
     * Get savings opportunities
     * GET /api/analytics/savings-opportunities
     */
    @GET("analytics/savings-opportunities")
    suspend fun getSavingsOpportunities(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String
    ): Response<SavingsOpportunitiesDto>
    
    /**
     * Get cash flow analysis
     * GET /api/analytics/cash-flow
     */
    @GET("analytics/cash-flow")
    suspend fun getCashFlowAnalysis(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("period") period: String = "monthly"
    ): Response<CashFlowAnalysisDto>
    
    /**
     * Export analytics data
     * GET /api/analytics/export
     */
    @GET("analytics/export")
    suspend fun exportAnalyticsData(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("format") format: String = "csv",
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<ExportDataDto>
}