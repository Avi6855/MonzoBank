package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CashFlowAnalysisDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "period")
    val period: String,
    
    @Json(name = "total_inflow")
    val totalInflow: BigDecimal,
    
    @Json(name = "total_outflow")
    val totalOutflow: BigDecimal,
    
    @Json(name = "net_cash_flow")
    val netCashFlow: BigDecimal,
    
    @Json(name = "cash_flow_trend")
    val cashFlowTrend: String, // "positive", "negative", "stable"
    
    @Json(name = "inflow_breakdown")
    val inflowBreakdown: List<CashFlowCategoryDto>,
    
    @Json(name = "outflow_breakdown")
    val outflowBreakdown: List<CashFlowCategoryDto>,
    
    @Json(name = "monthly_patterns")
    val monthlyPatterns: List<MonthlyCashFlowDto>,
    
    @Json(name = "predictions")
    val predictions: CashFlowPredictionDto,
    
    @Json(name = "alerts")
    val alerts: List<CashFlowAlertDto>,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class CashFlowCategoryDto(
    @Json(name = "category")
    val category: String,
    
    @Json(name = "amount")
    val amount: BigDecimal,
    
    @Json(name = "percentage")
    val percentage: Double,
    
    @Json(name = "transaction_count")
    val transactionCount: Int,
    
    @Json(name = "average_amount")
    val averageAmount: BigDecimal,
    
    @Json(name = "trend")
    val trend: String, // "increasing", "decreasing", "stable"
    
    @Json(name = "regularity")
    val regularity: String // "regular", "irregular", "seasonal"
)

@JsonClass(generateAdapter = true)
data class MonthlyCashFlowDto(
    @Json(name = "year")
    val year: Int,
    
    @Json(name = "month")
    val month: Int,
    
    @Json(name = "month_name")
    val monthName: String,
    
    @Json(name = "inflow")
    val inflow: BigDecimal,
    
    @Json(name = "outflow")
    val outflow: BigDecimal,
    
    @Json(name = "net_flow")
    val netFlow: BigDecimal,
    
    @Json(name = "days_positive")
    val daysPositive: Int,
    
    @Json(name = "days_negative")
    val daysNegative: Int,
    
    @Json(name = "largest_inflow")
    val largestInflow: BigDecimal,
    
    @Json(name = "largest_outflow")
    val largestOutflow: BigDecimal
)

@JsonClass(generateAdapter = true)
data class CashFlowPredictionDto(
    @Json(name = "next_month_predicted_inflow")
    val nextMonthPredictedInflow: BigDecimal,
    
    @Json(name = "next_month_predicted_outflow")
    val nextMonthPredictedOutflow: BigDecimal,
    
    @Json(name = "next_month_predicted_net_flow")
    val nextMonthPredictedNetFlow: BigDecimal,
    
    @Json(name = "confidence_level")
    val confidenceLevel: String, // "high", "medium", "low"
    
    @Json(name = "seasonal_adjustments")
    val seasonalAdjustments: List<SeasonalAdjustmentDto>,
    
    @Json(name = "risk_factors")
    val riskFactors: List<String>
)

@JsonClass(generateAdapter = true)
data class SeasonalAdjustmentDto(
    @Json(name = "period")
    val period: String, // "holiday", "back_to_school", "summer", etc.
    
    @Json(name = "typical_impact")
    val typicalImpact: BigDecimal,
    
    @Json(name = "impact_type")
    val impactType: String // "increase", "decrease"
)

@JsonClass(generateAdapter = true)
data class CashFlowAlertDto(
    @Json(name = "type")
    val type: String, // "low_balance_warning", "unusual_outflow", "irregular_income"
    
    @Json(name = "severity")
    val severity: String, // "critical", "warning", "info"
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "recommended_action")
    val recommendedAction: String,
    
    @Json(name = "amount_involved")
    val amountInvolved: BigDecimal?,
    
    @Json(name = "date_range")
    val dateRange: String?
)