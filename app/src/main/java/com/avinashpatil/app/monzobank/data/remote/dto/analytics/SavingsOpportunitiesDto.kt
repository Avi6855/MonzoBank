package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class SavingsOpportunitiesDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "total_potential_savings")
    val totalPotentialSavings: BigDecimal,
    
    @Json(name = "opportunities")
    val opportunities: List<SavingsOpportunityDto>,
    
    @Json(name = "automated_savings_suggestions")
    val automatedSavingsSuggestions: List<AutomatedSavingsDto>,
    
    @Json(name = "subscription_analysis")
    val subscriptionAnalysis: SubscriptionAnalysisDto,
    
    @Json(name = "spending_patterns")
    val spendingPatterns: SpendingPatternsDto,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class SavingsOpportunityDto(
    @Json(name = "type")
    val type: String, // "subscription", "recurring", "category_optimization", "merchant_switching"
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "category")
    val category: String,
    
    @Json(name = "current_monthly_spending")
    val currentMonthlySpending: BigDecimal,
    
    @Json(name = "potential_monthly_savings")
    val potentialMonthlySavings: BigDecimal,
    
    @Json(name = "annual_savings")
    val annualSavings: BigDecimal,
    
    @Json(name = "confidence_level")
    val confidenceLevel: String, // "high", "medium", "low"
    
    @Json(name = "difficulty")
    val difficulty: String, // "easy", "moderate", "difficult"
    
    @Json(name = "action_required")
    val actionRequired: String,
    
    @Json(name = "merchants_involved")
    val merchantsInvolved: List<String>?
)

@JsonClass(generateAdapter = true)
data class AutomatedSavingsDto(
    @Json(name = "type")
    val type: String, // "round_up", "percentage_based", "fixed_amount", "goal_based"
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "suggested_amount")
    val suggestedAmount: BigDecimal,
    
    @Json(name = "frequency")
    val frequency: String, // "daily", "weekly", "monthly", "per_transaction"
    
    @Json(name = "projected_annual_savings")
    val projectedAnnualSavings: BigDecimal,
    
    @Json(name = "setup_difficulty")
    val setupDifficulty: String // "easy", "moderate"
)

@JsonClass(generateAdapter = true)
data class SubscriptionAnalysisDto(
    @Json(name = "total_monthly_subscriptions")
    val totalMonthlySubscriptions: BigDecimal,
    
    @Json(name = "active_subscriptions")
    val activeSubscriptions: List<SubscriptionDto>,
    
    @Json(name = "unused_subscriptions")
    val unusedSubscriptions: List<SubscriptionDto>,
    
    @Json(name = "duplicate_services")
    val duplicateServices: List<DuplicateServiceDto>,
    
    @Json(name = "potential_savings")
    val potentialSavings: BigDecimal
)

@JsonClass(generateAdapter = true)
data class SubscriptionDto(
    @Json(name = "merchant_name")
    val merchantName: String,
    
    @Json(name = "category")
    val category: String,
    
    @Json(name = "monthly_cost")
    val monthlyCost: BigDecimal,
    
    @Json(name = "last_used")
    val lastUsed: String?,
    
    @Json(name = "usage_frequency")
    val usageFrequency: String, // "high", "medium", "low", "never"
    
    @Json(name = "cancellation_difficulty")
    val cancellationDifficulty: String // "easy", "moderate", "difficult"
)

@JsonClass(generateAdapter = true)
data class DuplicateServiceDto(
    @Json(name = "service_type")
    val serviceType: String,
    
    @Json(name = "subscriptions")
    val subscriptions: List<SubscriptionDto>,
    
    @Json(name = "recommended_action")
    val recommendedAction: String,
    
    @Json(name = "potential_savings")
    val potentialSavings: BigDecimal
)

@JsonClass(generateAdapter = true)
data class SpendingPatternsDto(
    @Json(name = "impulse_purchases")
    val impulsePurchases: ImpulsePurchaseAnalysisDto,
    
    @Json(name = "peak_spending_times")
    val peakSpendingTimes: List<SpendingTimeDto>,
    
    @Json(name = "location_based_spending")
    val locationBasedSpending: List<LocationSpendingDto>
)

@JsonClass(generateAdapter = true)
data class ImpulsePurchaseAnalysisDto(
    @Json(name = "monthly_impulse_spending")
    val monthlyImpulseSpending: BigDecimal,
    
    @Json(name = "common_categories")
    val commonCategories: List<String>,
    
    @Json(name = "triggers")
    val triggers: List<String>,
    
    @Json(name = "suggested_controls")
    val suggestedControls: List<String>
)

@JsonClass(generateAdapter = true)
data class SpendingTimeDto(
    @Json(name = "time_period")
    val timePeriod: String, // "morning", "afternoon", "evening", "weekend"
    
    @Json(name = "average_amount")
    val averageAmount: BigDecimal,
    
    @Json(name = "frequency")
    val frequency: Int
)

@JsonClass(generateAdapter = true)
data class LocationSpendingDto(
    @Json(name = "location_type")
    val locationType: String,
    
    @Json(name = "monthly_spending")
    val monthlySpending: BigDecimal,
    
    @Json(name = "optimization_suggestion")
    val optimizationSuggestion: String?
)