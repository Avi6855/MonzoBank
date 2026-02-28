package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class FinancialHealthScoreDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "overall_score")
    val overallScore: Int, // 0-100
    
    @Json(name = "score_category")
    val scoreCategory: String, // "excellent", "good", "fair", "poor"
    
    @Json(name = "components")
    val components: FinancialHealthComponentsDto,
    
    @Json(name = "trends")
    val trends: FinancialHealthTrendsDto,
    
    @Json(name = "recommendations")
    val recommendations: List<HealthRecommendationDto>,
    
    @Json(name = "comparison")
    val comparison: FinancialHealthComparisonDto,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class FinancialHealthComponentsDto(
    @Json(name = "spending_control")
    val spendingControl: ComponentScoreDto,
    
    @Json(name = "savings_rate")
    val savingsRate: ComponentScoreDto,
    
    @Json(name = "debt_management")
    val debtManagement: ComponentScoreDto,
    
    @Json(name = "emergency_fund")
    val emergencyFund: ComponentScoreDto,
    
    @Json(name = "income_stability")
    val incomeStability: ComponentScoreDto
)

@JsonClass(generateAdapter = true)
data class ComponentScoreDto(
    @Json(name = "score")
    val score: Int, // 0-100
    
    @Json(name = "weight")
    val weight: Double, // Contribution to overall score
    
    @Json(name = "status")
    val status: String, // "excellent", "good", "needs_improvement", "critical"
    
    @Json(name = "description")
    val description: String
)

@JsonClass(generateAdapter = true)
data class FinancialHealthTrendsDto(
    @Json(name = "score_change_30_days")
    val scoreChange30Days: Int,
    
    @Json(name = "score_change_90_days")
    val scoreChange90Days: Int,
    
    @Json(name = "trend_direction")
    val trendDirection: String, // "improving", "declining", "stable"
    
    @Json(name = "historical_scores")
    val historicalScores: List<HistoricalScoreDto>
)

@JsonClass(generateAdapter = true)
data class HistoricalScoreDto(
    @Json(name = "date")
    val date: String,
    
    @Json(name = "score")
    val score: Int
)

@JsonClass(generateAdapter = true)
data class HealthRecommendationDto(
    @Json(name = "category")
    val category: String,
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "potential_impact")
    val potentialImpact: Int, // Score improvement potential
    
    @Json(name = "action_items")
    val actionItems: List<String>
)

@JsonClass(generateAdapter = true)
data class FinancialHealthComparisonDto(
    @Json(name = "peer_average")
    val peerAverage: Int,
    
    @Json(name = "percentile")
    val percentile: Int, // User's percentile ranking
    
    @Json(name = "age_group_average")
    val ageGroupAverage: Int,
    
    @Json(name = "income_bracket_average")
    val incomeBracketAverage: Int
)