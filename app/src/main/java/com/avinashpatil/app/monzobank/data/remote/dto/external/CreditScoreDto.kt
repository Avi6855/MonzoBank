package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreditScoreDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "credit_score")
    val creditScore: Int,
    
    @Json(name = "score_range")
    val scoreRange: ScoreRangeDto,
    
    @Json(name = "score_grade")
    val scoreGrade: String, // "Excellent", "Good", "Fair", "Poor"
    
    @Json(name = "provider")
    val provider: String, // "Experian", "Equifax", "TransUnion"
    
    @Json(name = "report_date")
    val reportDate: String,
    
    @Json(name = "factors_affecting_score")
    val factorsAffectingScore: List<CreditFactorDto>,
    
    @Json(name = "score_history")
    val scoreHistory: List<CreditScoreHistoryDto>?,
    
    @Json(name = "recommendations")
    val recommendations: List<CreditRecommendationDto>,
    
    @Json(name = "monitoring_alerts")
    val monitoringAlerts: List<CreditAlertDto>?,
    
    @Json(name = "next_update_date")
    val nextUpdateDate: String,
    
    @Json(name = "report_summary")
    val reportSummary: CreditReportSummaryDto
)

@JsonClass(generateAdapter = true)
data class ScoreRangeDto(
    @Json(name = "min_score")
    val minScore: Int,
    
    @Json(name = "max_score")
    val maxScore: Int
)

@JsonClass(generateAdapter = true)
data class CreditFactorDto(
    @Json(name = "factor")
    val factor: String,
    
    @Json(name = "impact")
    val impact: String, // "positive", "negative", "neutral"
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "weight")
    val weight: String // "high", "medium", "low"
)

@JsonClass(generateAdapter = true)
data class CreditScoreHistoryDto(
    @Json(name = "date")
    val date: String,
    
    @Json(name = "score")
    val score: Int,
    
    @Json(name = "change")
    val change: Int // Change from previous score
)

@JsonClass(generateAdapter = true)
data class CreditRecommendationDto(
    @Json(name = "type")
    val type: String, // "payment_history", "credit_utilization", "credit_mix", etc.
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "potential_impact")
    val potentialImpact: String, // "significant", "moderate", "minor"
    
    @Json(name = "timeframe")
    val timeframe: String // "immediate", "short_term", "long_term"
)

@JsonClass(generateAdapter = true)
data class CreditAlertDto(
    @Json(name = "alert_type")
    val alertType: String, // "new_account", "hard_inquiry", "payment_missed", etc.
    
    @Json(name = "severity")
    val severity: String, // "critical", "warning", "info"
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "date")
    val date: String,
    
    @Json(name = "resolved")
    val resolved: Boolean
)

@JsonClass(generateAdapter = true)
data class CreditReportSummaryDto(
    @Json(name = "total_accounts")
    val totalAccounts: Int,
    
    @Json(name = "open_accounts")
    val openAccounts: Int,
    
    @Json(name = "closed_accounts")
    val closedAccounts: Int,
    
    @Json(name = "total_credit_limit")
    val totalCreditLimit: String,
    
    @Json(name = "total_balance")
    val totalBalance: String,
    
    @Json(name = "credit_utilization")
    val creditUtilization: Double, // Percentage
    
    @Json(name = "payment_history")
    val paymentHistory: String, // Percentage of on-time payments
    
    @Json(name = "average_account_age")
    val averageAccountAge: String,
    
    @Json(name = "hard_inquiries_last_24_months")
    val hardInquiriesLast24Months: Int,
    
    @Json(name = "derogatory_marks")
    val derogatoryMarks: Int
)