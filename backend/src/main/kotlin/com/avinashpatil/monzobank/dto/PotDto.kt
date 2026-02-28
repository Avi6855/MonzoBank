package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.DepositFrequency
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CreatePotRequest(
    @field:NotBlank(message = "Pot name is required")
    @field:Size(min = 1, max = 100, message = "Pot name must be between 1 and 100 characters")
    val name: String,
    
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null,
    
    @field:NotNull(message = "Target amount is required")
    @field:DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Target amount must have at most 2 decimal places")
    val targetAmount: BigDecimal,
    
    val targetDate: LocalDateTime? = null,
    
    val autoDepositEnabled: Boolean? = false,
    
    @field:DecimalMin(value = "0.01", message = "Auto deposit amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Auto deposit amount must have at most 2 decimal places")
    val autoDepositAmount: BigDecimal? = null,
    
    val depositFrequency: DepositFrequency? = null
)

data class UpdatePotRequest(
    @field:Size(min = 1, max = 100, message = "Pot name must be between 1 and 100 characters")
    val name: String? = null,
    
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null,
    
    @field:DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Target amount must have at most 2 decimal places")
    val targetAmount: BigDecimal? = null,
    
    val targetDate: LocalDateTime? = null,
    
    val autoDepositEnabled: Boolean? = null,
    
    @field:DecimalMin(value = "0.01", message = "Auto deposit amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Auto deposit amount must have at most 2 decimal places")
    val autoDepositAmount: BigDecimal? = null,
    
    val depositFrequency: DepositFrequency? = null
)

data class PotResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val description: String?,
    val targetAmount: BigDecimal,
    val currentBalance: BigDecimal,
    val progressPercentage: BigDecimal,
    val targetDate: LocalDateTime?,
    val autoDepositEnabled: Boolean,
    val autoDepositAmount: BigDecimal?,
    val depositFrequency: DepositFrequency?,
    val nextDepositDate: LocalDateTime?,
    val isActive: Boolean,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class AddMoneyToPotRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    val amount: BigDecimal,
    
    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String? = null
)

data class WithdrawMoneyFromPotRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    val amount: BigDecimal,
    
    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String? = null
)

data class PotSummaryResponse(
    val totalPots: Long,
    val totalSavings: BigDecimal,
    val totalTargetAmount: BigDecimal,
    val completedPots: Long,
    val averageSavings: BigDecimal,
    val progressPercentage: BigDecimal
)

data class PotTransactionResponse(
    val id: UUID,
    val potId: UUID,
    val type: String, // DEPOSIT, WITHDRAWAL, AUTO_DEPOSIT
    val amount: BigDecimal,
    val description: String?,
    val balanceAfter: BigDecimal,
    val createdAt: LocalDateTime
)

data class PotTransactionHistoryRequest(
    @field:NotNull(message = "Pot ID is required")
    val potId: UUID,
    
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val transactionType: String? = null, // DEPOSIT, WITHDRAWAL, AUTO_DEPOSIT
    val page: Int = 0,
    val size: Int = 20
)

data class PotGoalResponse(
    val potId: UUID,
    val name: String,
    val targetAmount: BigDecimal,
    val currentBalance: BigDecimal,
    val progressPercentage: BigDecimal,
    val targetDate: LocalDateTime?,
    val daysRemaining: Long?,
    val recommendedDailyDeposit: BigDecimal?,
    val recommendedWeeklyDeposit: BigDecimal?,
    val recommendedMonthlyDeposit: BigDecimal?,
    val onTrack: Boolean,
    val projectedCompletionDate: LocalDateTime?
)

data class PotAnalyticsResponse(
    val potId: UUID,
    val period: String,
    val totalDeposits: BigDecimal,
    val totalWithdrawals: BigDecimal,
    val netSavings: BigDecimal,
    val depositCount: Long,
    val withdrawalCount: Long,
    val averageDepositAmount: BigDecimal,
    val averageWithdrawalAmount: BigDecimal,
    val savingsRate: BigDecimal, // Percentage of target achieved
    val monthlyTrend: List<MonthlySavings>
)

data class MonthlySavings(
    val month: String,
    val year: Int,
    val deposits: BigDecimal,
    val withdrawals: BigDecimal,
    val netSavings: BigDecimal,
    val endingBalance: BigDecimal
)

data class PotReminderRequest(
    @field:NotNull(message = "Pot ID is required")
    val potId: UUID,
    
    @field:NotNull(message = "Reminder type is required")
    val reminderType: ReminderType,
    
    @field:NotNull(message = "Frequency is required")
    val frequency: ReminderFrequency,
    
    val customMessage: String? = null,
    val enabled: Boolean = true
)

enum class ReminderType {
    DEPOSIT_REMINDER, TARGET_DATE_APPROACHING, GOAL_ACHIEVED, INACTIVITY_REMINDER
}

enum class ReminderFrequency {
    DAILY, WEEKLY, MONTHLY, CUSTOM
}

data class PotReminderResponse(
    val id: UUID,
    val potId: UUID,
    val reminderType: ReminderType,
    val frequency: ReminderFrequency,
    val customMessage: String?,
    val enabled: Boolean,
    val nextReminderDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class PotChallengeRequest(
    @field:NotBlank(message = "Challenge name is required")
    @field:Size(max = 100, message = "Challenge name must not exceed 100 characters")
    val challengeName: String,
    
    @field:NotNull(message = "Challenge type is required")
    val challengeType: ChallengeType,
    
    @field:NotNull(message = "Target amount is required")
    @field:DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    val targetAmount: BigDecimal,
    
    @field:NotNull(message = "Duration in days is required")
    @field:Min(value = 1, message = "Duration must be at least 1 day")
    val durationInDays: Int,
    
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null
)

enum class ChallengeType {
    DAILY_SAVINGS, WEEKLY_SAVINGS, MONTHLY_SAVINGS, ROUND_UP_SAVINGS, NO_SPEND_CHALLENGE
}

data class PotChallengeResponse(
    val id: UUID,
    val potId: UUID,
    val challengeName: String,
    val challengeType: ChallengeType,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val progressPercentage: BigDecimal,
    val durationInDays: Int,
    val daysRemaining: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isCompleted: Boolean,
    val isActive: Boolean,
    val description: String?,
    val createdAt: LocalDateTime
)

data class PotInsightsResponse(
    val potId: UUID,
    val savingsVelocity: BigDecimal, // Average savings per month
    val consistencyScore: BigDecimal, // How consistent are the deposits (0-100)
    val timeToGoal: Long?, // Days to reach target at current rate
    val bestSavingsMonth: String?,
    val worstSavingsMonth: String?,
    val averageMonthlyDeposit: BigDecimal,
    val longestSavingsStreak: Int, // Days of consecutive deposits
    val currentSavingsStreak: Int,
    val recommendations: List<SavingsRecommendation>
)

data class SavingsRecommendation(
    val type: String,
    val title: String,
    val description: String,
    val impact: String, // HIGH, MEDIUM, LOW
    val actionRequired: String?
)

data class PotComparisonResponse(
    val userPots: List<PotPerformance>,
    val averageUserSavings: BigDecimal,
    val userRanking: Int, // Percentile ranking
    val topPerformingPot: String?,
    val improvementSuggestions: List<String>
)

data class PotPerformance(
    val potId: UUID,
    val name: String,
    val progressPercentage: BigDecimal,
    val savingsRate: BigDecimal, // Monthly average
    val timeToCompletion: Long?, // Days
    val performance: String // EXCELLENT, GOOD, AVERAGE, NEEDS_IMPROVEMENT
)

data class PotExportRequest(
    val potIds: List<UUID>? = null, // If null, export all pots
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val format: ExportFormat = ExportFormat.CSV,
    val includeTransactions: Boolean = true,
    val includeAnalytics: Boolean = false
)

data class PotExportResponse(
    val exportId: UUID,
    val status: String,
    val downloadUrl: String?,
    val format: ExportFormat,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
)

data class PotTemplateRequest(
    @field:NotBlank(message = "Template name is required")
    val templateName: String,
    
    @field:NotNull(message = "Template type is required")
    val templateType: PotTemplateType,
    
    val targetAmount: BigDecimal? = null,
    val durationInMonths: Int? = null,
    val autoDepositAmount: BigDecimal? = null,
    val depositFrequency: DepositFrequency? = null
)

enum class PotTemplateType {
    EMERGENCY_FUND, VACATION, CAR_PURCHASE, HOME_DEPOSIT, WEDDING, EDUCATION, RETIREMENT, GENERAL_SAVINGS
}

data class PotTemplateResponse(
    val id: UUID,
    val templateName: String,
    val templateType: PotTemplateType,
    val suggestedTargetAmount: BigDecimal?,
    val suggestedDurationInMonths: Int?,
    val suggestedAutoDepositAmount: BigDecimal?,
    val suggestedDepositFrequency: DepositFrequency?,
    val description: String,
    val tips: List<String>,
    val isPopular: Boolean
)