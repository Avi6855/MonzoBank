package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.CardType
import com.avinashpatil.monzobank.entity.DeliveryStatus
import jakarta.validation.constraints.*
import java.time.LocalDateTime
import java.util.*

data class CreateCardRequest(
    @field:NotNull(message = "Account ID is required")
    val accountId: UUID,
    
    @field:NotNull(message = "Card type is required")
    val cardType: CardType,
    
    @field:NotBlank(message = "Delivery address is required")
    @field:Size(max = 500, message = "Delivery address must not exceed 500 characters")
    val deliveryAddress: String
)

data class CardResponse(
    val id: UUID,
    val userId: UUID,
    val accountId: UUID,
    val cardNumber: String, // Masked for security
    val cardType: CardType,
    val expiryDate: LocalDateTime,
    val isActive: Boolean,
    val isBlocked: Boolean,
    val deliveryStatus: DeliveryStatus,
    val contactlessEnabled: Boolean,
    val onlinePaymentsEnabled: Boolean,
    val atmWithdrawalsEnabled: Boolean,
    val magneticStripeEnabled: Boolean,
    val createdAt: LocalDateTime,
    val activatedAt: LocalDateTime?,
    val blockedAt: LocalDateTime?,
    val deliveredAt: LocalDateTime?
)

data class ActivateCardRequest(
    @field:Pattern(regexp = "^\\d{4}$", message = "PIN must be 4 digits")
    val pin: String? = null
)

data class UpdateCardSettingsRequest(
    val contactlessEnabled: Boolean? = null,
    val onlinePaymentsEnabled: Boolean? = null,
    val atmWithdrawalsEnabled: Boolean? = null,
    val magneticStripeEnabled: Boolean? = null
)

data class ChangePINRequest(
    @field:NotBlank(message = "Current PIN is required")
    @field:Pattern(regexp = "^\\d{4}$", message = "Current PIN must be 4 digits")
    val currentPin: String,
    
    @field:NotBlank(message = "New PIN is required")
    @field:Pattern(regexp = "^\\d{4}$", message = "New PIN must be 4 digits")
    val newPin: String
)

data class ReplaceCardRequest(
    @field:NotBlank(message = "Reason is required")
    @field:Size(max = 255, message = "Reason must not exceed 255 characters")
    val reason: String,
    
    @field:Size(max = 500, message = "Delivery address must not exceed 500 characters")
    val deliveryAddress: String? = null
)

data class CardLimitsResponse(
    val dailySpendingLimit: java.math.BigDecimal,
    val monthlySpendingLimit: java.math.BigDecimal,
    val dailyWithdrawalLimit: java.math.BigDecimal,
    val monthlyWithdrawalLimit: java.math.BigDecimal,
    val singleTransactionLimit: java.math.BigDecimal,
    val onlineTransactionLimit: java.math.BigDecimal,
    val contactlessLimit: java.math.BigDecimal
)

data class UpdateCardLimitsRequest(
    @field:DecimalMin(value = "0.00", message = "Daily spending limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Daily spending limit must have at most 2 decimal places")
    val dailySpendingLimit: java.math.BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Monthly spending limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Monthly spending limit must have at most 2 decimal places")
    val monthlySpendingLimit: java.math.BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Daily withdrawal limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Daily withdrawal limit must have at most 2 decimal places")
    val dailyWithdrawalLimit: java.math.BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Monthly withdrawal limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Monthly withdrawal limit must have at most 2 decimal places")
    val monthlyWithdrawalLimit: java.math.BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Single transaction limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Single transaction limit must have at most 2 decimal places")
    val singleTransactionLimit: java.math.BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Online transaction limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Online transaction limit must have at most 2 decimal places")
    val onlineTransactionLimit: java.math.BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Contactless limit must be non-negative")
    @field:Digits(integer = 10, fraction = 2, message = "Contactless limit must have at most 2 decimal places")
    val contactlessLimit: java.math.BigDecimal? = null
)

data class CardUsageResponse(
    val cardId: UUID,
    val totalTransactions: Long,
    val totalSpent: java.math.BigDecimal,
    val totalWithdrawn: java.math.BigDecimal,
    val averageTransactionAmount: java.math.BigDecimal,
    val largestTransaction: java.math.BigDecimal,
    val mostUsedMerchant: String?,
    val mostUsedCategory: String?,
    val contactlessTransactions: Long,
    val onlineTransactions: Long,
    val atmTransactions: Long,
    val period: String
)

data class CardSecurityResponse(
    val cardId: UUID,
    val isActive: Boolean,
    val isBlocked: Boolean,
    val blockReason: String?,
    val lastUsed: LocalDateTime?,
    val failedAttempts: Int,
    val securityAlerts: List<SecurityAlert>
)

data class SecurityAlert(
    val id: UUID,
    val type: String,
    val message: String,
    val severity: String,
    val timestamp: LocalDateTime,
    val resolved: Boolean
)

data class CardStatementRequest(
    @field:NotNull(message = "Card ID is required")
    val cardId: UUID,
    
    @field:NotNull(message = "Start date is required")
    val startDate: LocalDateTime,
    
    @field:NotNull(message = "End date is required")
    val endDate: LocalDateTime,
    
    val format: StatementFormat = StatementFormat.PDF,
    
    @field:Email(message = "Valid email is required")
    val email: String? = null
)

enum class StatementFormat {
    PDF, CSV, EXCEL
}

data class CardStatementResponse(
    val statementId: UUID,
    val cardId: UUID,
    val format: StatementFormat,
    val downloadUrl: String,
    val emailSent: Boolean,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
)

data class CardRewardsResponse(
    val cardId: UUID,
    val totalPoints: Long,
    val availablePoints: Long,
    val redeemedPoints: Long,
    val cashbackEarned: java.math.BigDecimal,
    val cashbackRedeemed: java.math.BigDecimal,
    val currentTier: String,
    val nextTierRequirement: Long?,
    val recentRewards: List<RewardTransaction>
)

data class RewardTransaction(
    val id: UUID,
    val type: String, // EARNED, REDEEMED
    val points: Long,
    val cashback: java.math.BigDecimal?,
    val description: String,
    val transactionId: UUID?,
    val createdAt: LocalDateTime
)

data class CardFraudAlertResponse(
    val cardId: UUID,
    val alertId: UUID,
    val alertType: String,
    val riskScore: Int,
    val description: String,
    val transactionId: UUID?,
    val location: String?,
    val amount: java.math.BigDecimal?,
    val merchant: String?,
    val timestamp: LocalDateTime,
    val status: String, // PENDING, CONFIRMED_FRAUD, FALSE_POSITIVE
    val actionTaken: String?
)

data class ConfirmFraudRequest(
    @field:NotNull(message = "Alert ID is required")
    val alertId: UUID,
    
    @field:NotNull(message = "Is fraud confirmation is required")
    val isFraud: Boolean,
    
    @field:Size(max = 500, message = "Comments must not exceed 500 characters")
    val comments: String? = null
)

data class CardDeliveryTrackingResponse(
    val cardId: UUID,
    val deliveryStatus: DeliveryStatus,
    val trackingNumber: String?,
    val estimatedDeliveryDate: LocalDateTime?,
    val deliveryAddress: String,
    val courierService: String?,
    val deliveryUpdates: List<DeliveryUpdate>
)

data class DeliveryUpdate(
    val status: String,
    val description: String,
    val location: String?,
    val timestamp: LocalDateTime
)

data class CardRenewalRequest(
    @field:NotNull(message = "Card ID is required")
    val cardId: UUID,
    
    @field:Size(max = 500, message = "Delivery address must not exceed 500 characters")
    val deliveryAddress: String? = null,
    
    val keepSamePIN: Boolean = true
)

data class CardRenewalResponse(
    val oldCardId: UUID,
    val newCardId: UUID,
    val newCardNumber: String, // Masked
    val expiryDate: LocalDateTime,
    val deliveryStatus: DeliveryStatus,
    val estimatedDeliveryDate: LocalDateTime?,
    val createdAt: LocalDateTime
)

data class CardPreferencesRequest(
    val notificationPreferences: NotificationPreferences? = null,
    val securityPreferences: SecurityPreferences? = null,
    val spendingPreferences: SpendingPreferences? = null
)

data class NotificationPreferences(
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val transactionAlerts: Boolean = true,
    val securityAlerts: Boolean = true,
    val marketingEmails: Boolean = false
)

data class SecurityPreferences(
    val requirePINForContactless: Boolean = false,
    val blockInternationalTransactions: Boolean = false,
    val blockOnlineTransactions: Boolean = false,
    val blockATMWithdrawals: Boolean = false,
    val velocityChecks: Boolean = true,
    val locationBasedSecurity: Boolean = true
)

data class SpendingPreferences(
    val budgetAlerts: Boolean = true,
    val categoryLimits: Map<String, java.math.BigDecimal> = emptyMap(),
    val merchantBlacklist: List<String> = emptyList(),
    val merchantWhitelist: List<String> = emptyList(),
    val recurringPaymentAlerts: Boolean = true
)

data class CardAnalyticsResponse(
    val cardId: UUID,
    val period: String,
    val totalSpending: java.math.BigDecimal,
    val transactionCount: Long,
    val averageTransactionSize: java.math.BigDecimal,
    val topMerchants: List<MerchantSpending>,
    val topCategories: List<CategorySpending>,
    val spendingTrend: List<DailySpending>,
    val comparisonToPreviousPeriod: SpendingComparison
)

data class MerchantSpending(
    val merchantName: String,
    val totalSpent: java.math.BigDecimal,
    val transactionCount: Long,
    val averageAmount: java.math.BigDecimal
)

data class CategorySpending(
    val category: String,
    val totalSpent: java.math.BigDecimal,
    val transactionCount: Long,
    val percentage: java.math.BigDecimal
)

data class DailySpending(
    val date: String,
    val amount: java.math.BigDecimal,
    val transactionCount: Long
)

data class SpendingComparison(
    val percentageChange: java.math.BigDecimal,
    val absoluteChange: java.math.BigDecimal,
    val trend: String // UP, DOWN, STABLE
)