package com.avinashpatil.app.monzobank.domain.model

import com.avinashpatil.app.monzobank.data.service.CurrencyUtils
import java.math.BigDecimal
import java.time.LocalDateTime

data class Currency(
    val code: String, // ISO 4217 currency code (e.g., "USD", "EUR")
    val name: String, // Full name (e.g., "US Dollar", "Euro")
    val symbol: String, // Currency symbol (e.g., "$", "€")
    val flag: String, // Flag emoji or country code
    val decimalPlaces: Int = 2,
    val isActive: Boolean = true
) {
    val displayName: String
        get() = "$code - $name"
    
    val flagAndCode: String
        get() = "$flag $code"
}

data class ExchangeRate(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: BigDecimal,
    val timestamp: LocalDateTime,
    val source: String,
    val bid: BigDecimal? = null, // Buy rate
    val ask: BigDecimal? = null, // Sell rate
    val spread: BigDecimal? = null, // Bid-ask spread
    val change24h: BigDecimal? = null, // 24-hour change
    val changePercentage24h: Double? = null // 24-hour change percentage
) {
    val currencyPair: String
        get() = "$fromCurrency/$toCurrency"
    
    val isStale: Boolean
        get() = timestamp.isBefore(LocalDateTime.now().minusMinutes(5))
    
    val formattedRate: String
        get() = "1 $fromCurrency = ${rate.setScale(4)} $toCurrency"
    
    val trendDirection: TrendDirection
        get() = when {
            changePercentage24h == null -> TrendDirection.NEUTRAL
            changePercentage24h > 0 -> TrendDirection.UP
            changePercentage24h < 0 -> TrendDirection.DOWN
            else -> TrendDirection.NEUTRAL
        }
}

enum class TrendDirection {
    UP, DOWN, STABLE, NEUTRAL
}

data class CurrencyConversion(
    val id: String,
    val fromAmount: BigDecimal,
    val fromCurrency: String,
    val toAmount: BigDecimal,
    val toCurrency: String,
    val exchangeRate: BigDecimal,
    val fee: BigDecimal,
    val feePercentage: BigDecimal,
    val timestamp: LocalDateTime,
    val expiresAt: LocalDateTime,
    val isLocked: Boolean = false
) {
    val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(expiresAt)
    
    val minutesUntilExpiry: Long
        get() = if (isExpired) 0 else java.time.temporal.ChronoUnit.MINUTES.between(LocalDateTime.now(), expiresAt)
    
    val totalCost: BigDecimal
        get() = fromAmount.add(fee)
    
    val effectiveRate: BigDecimal
        get() = toAmount.divide(fromAmount, 6, java.math.RoundingMode.HALF_UP)
}

data class InternationalTransfer(
    val id: String,
    val fromAccountId: String,
    val recipientName: String,
    val recipientAddress: String,
    val recipientBankName: String,
    val recipientBankAddress: String,
    val recipientAccountNumber: String,
    val recipientSortCode: String? = null, // For UK transfers
    val recipientIBAN: String? = null, // For SEPA transfers
    val recipientSwiftCode: String? = null, // For international transfers
    val recipientRoutingNumber: String? = null, // For US transfers
    val fromAmount: BigDecimal,
    val fromCurrency: String,
    val toAmount: BigDecimal,
    val toCurrency: String,
    val exchangeRate: BigDecimal,
    val transferFee: BigDecimal,
    val correspondentBankFee: BigDecimal? = null,
    val purpose: TransferPurpose,
    val reference: String,
    val status: TransferStatus,
    val createdAt: LocalDateTime,
    val estimatedArrival: LocalDateTime,
    val actualArrival: LocalDateTime? = null,
    val trackingNumber: String? = null,
    val complianceChecks: List<ComplianceCheck> = emptyList()
) {
    val totalFees: BigDecimal
        get() = transferFee.add(correspondentBankFee ?: BigDecimal.ZERO)
    
    val isCompleted: Boolean
        get() = status == TransferStatus.COMPLETED
    
    val isPending: Boolean
        get() = status in listOf(TransferStatus.PENDING, TransferStatus.PROCESSING)
    
    val daysSinceCreated: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDateTime.now().toLocalDate())
}

enum class TransferStatus {
    PENDING,
    PROCESSING,
    COMPLIANCE_REVIEW,
    SENT,
    COMPLETED,
    FAILED,
    CANCELLED,
    RETURNED;
    
    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            PROCESSING -> "Processing"
            COMPLIANCE_REVIEW -> "Under Review"
            SENT -> "Sent"
            COMPLETED -> "Completed"
            FAILED -> "Failed"
            CANCELLED -> "Cancelled"
            RETURNED -> "Returned"
        }
    
    val isActive: Boolean
        get() = this in listOf(PENDING, PROCESSING, COMPLIANCE_REVIEW, SENT)
}

enum class TransferPurpose {
    FAMILY_SUPPORT,
    EDUCATION,
    MEDICAL,
    BUSINESS,
    INVESTMENT,
    PROPERTY,
    TRAVEL,
    GIFT,
    LOAN_REPAYMENT,
    OTHER;
    
    val displayName: String
        get() = when (this) {
            FAMILY_SUPPORT -> "Family Support"
            EDUCATION -> "Education"
            MEDICAL -> "Medical Expenses"
            BUSINESS -> "Business Transaction"
            INVESTMENT -> "Investment"
            PROPERTY -> "Property Purchase"
            TRAVEL -> "Travel Expenses"
            GIFT -> "Gift"
            LOAN_REPAYMENT -> "Loan Repayment"
            OTHER -> "Other"
        }
}

data class ComplianceCheck(
    val id: String,
    val type: ComplianceCheckType,
    val status: ComplianceCheckStatus,
    val description: String,
    val performedAt: LocalDateTime,
    val performedBy: String, // System or user ID
    val result: String? = null,
    val notes: String? = null
)

enum class ComplianceCheckType {
    AML_SCREENING,
    SANCTIONS_CHECK,
    PEP_CHECK,
    FRAUD_CHECK,
    REGULATORY_LIMIT,
    SOURCE_OF_FUNDS;
    
    val displayName: String
        get() = when (this) {
            AML_SCREENING -> "AML Screening"
            SANCTIONS_CHECK -> "Sanctions Check"
            PEP_CHECK -> "PEP Check"
            FRAUD_CHECK -> "Fraud Check"
            REGULATORY_LIMIT -> "Regulatory Limit Check"
            SOURCE_OF_FUNDS -> "Source of Funds Verification"
        }
}

enum class ComplianceCheckStatus {
    PENDING,
    PASSED,
    FAILED,
    REQUIRES_REVIEW,
    MANUAL_REVIEW;
    
    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            PASSED -> "Passed"
            FAILED -> "Failed"
            REQUIRES_REVIEW -> "Requires Review"
            MANUAL_REVIEW -> "Manual Review"
        }
}

data class SwiftMessage(
    val messageType: String, // MT103, MT202, etc.
    val senderBIC: String,
    val receiverBIC: String,
    val reference: String,
    val amount: BigDecimal,
    val currency: String,
    val valueDate: LocalDateTime,
    val orderingCustomer: String,
    val beneficiaryCustomer: String,
    val remittanceInfo: String,
    val charges: ChargeBearer = ChargeBearer.OUR
)

enum class ChargeBearer {
    OUR, // All charges borne by sender
    BEN, // All charges borne by beneficiary
    SHA; // Charges shared
    
    val displayName: String
        get() = when (this) {
            OUR -> "Sender pays all fees"
            BEN -> "Recipient pays all fees"
            SHA -> "Fees shared"
        }
}

data class CountryRegulation(
    val countryCode: String,
    val countryName: String,
    val maxTransferAmount: BigDecimal?,
    val dailyLimit: BigDecimal?,
    val monthlyLimit: BigDecimal?,
    val yearlyLimit: BigDecimal?,
    val requiresDocumentation: Boolean,
    val documentationThreshold: BigDecimal?,
    val allowedPurposes: List<TransferPurpose>,
    val processingDays: Int,
    val isHighRiskCountry: Boolean = false,
    val additionalRequirements: List<String> = emptyList()
)

data class ExchangeRateAlert(
    val id: String,
    val userId: String,
    val fromCurrency: String,
    val toCurrency: String,
    val targetRate: BigDecimal,
    val alertType: InternationalAlertType,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val lastTriggered: LocalDateTime? = null,
    val triggerCount: Int = 0
) {
    val currencyPair: String
        get() = "$fromCurrency/$toCurrency"
}

enum class InternationalAlertType {
    RATE_ABOVE,
    RATE_BELOW,
    RATE_CHANGE_PERCENT;
    
    val displayName: String
        get() = when (this) {
            RATE_ABOVE -> "Rate goes above"
            RATE_BELOW -> "Rate goes below"
            RATE_CHANGE_PERCENT -> "Rate changes by %"
        }
}

data class CurrencyAccount(
    val id: String,
    val userId: String,
    val currency: String,
    val balance: BigDecimal,
    val availableBalance: BigDecimal,
    val reservedBalance: BigDecimal,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val lastTransactionAt: LocalDateTime?
) {
    val formattedBalance: String
        get() = CurrencyUtils.formatAmount(balance, currency)
    
    val hasAvailableFunds: Boolean
        get() = availableBalance > BigDecimal.ZERO
}

data class CurrencyExchangeOrder(
    val id: String,
    val userId: String,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: BigDecimal,
    val toAmount: BigDecimal,
    val exchangeRate: BigDecimal,
    val orderType: OrderType,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val executedAt: LocalDateTime? = null,
    val targetRate: BigDecimal? = null // For limit orders
) {
    val isExpired: Boolean
        get() = expiresAt?.isBefore(LocalDateTime.now()) ?: false
    
    val isExecutable: Boolean
        get() = status == OrderStatus.PENDING && !isExpired
}

enum class OrderType {
    MARKET, // Execute immediately at current rate
    LIMIT; // Execute when target rate is reached
    
    val displayName: String
        get() = when (this) {
            MARKET -> "Market Order"
            LIMIT -> "Limit Order"
        }
}

enum class OrderStatus {
    PENDING,
    EXECUTED,
    CANCELLED,
    EXPIRED,
    PARTIALLY_FILLED;
    
    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            EXECUTED -> "Executed"
            CANCELLED -> "Cancelled"
            EXPIRED -> "Expired"
            PARTIALLY_FILLED -> "Partially Filled"
        }
}

// Utility data classes for API requests/responses
data class CurrencyConversionRequest(
    val fromAmount: BigDecimal,
    val fromCurrency: String,
    val toCurrency: String,
    val lockRate: Boolean = false
)

data class InternationalTransferRequest(
    val fromAccountId: String,
    val recipientDetails: RecipientDetails,
    val transferDetails: TransferDetails,
    val complianceInfo: ComplianceInfo
)

data class RecipientDetails(
    val name: String,
    val address: String,
    val bankName: String,
    val bankAddress: String,
    val accountNumber: String,
    val sortCode: String? = null,
    val iban: String? = null,
    val swiftCode: String? = null,
    val routingNumber: String? = null
)

data class TransferDetails(
    val amount: BigDecimal,
    val currency: String,
    val purpose: TransferPurpose,
    val reference: String,
    val urgency: TransferUrgency = TransferUrgency.STANDARD
)

enum class TransferUrgency {
    STANDARD,
    EXPRESS,
    URGENT;
    
    val displayName: String
        get() = when (this) {
            STANDARD -> "Standard (2-5 days)"
            EXPRESS -> "Express (1-2 days)"
            URGENT -> "Urgent (Same day)"
        }
    
    val additionalFee: BigDecimal
        get() = when (this) {
            STANDARD -> BigDecimal.ZERO
            EXPRESS -> BigDecimal("10.00")
            URGENT -> BigDecimal("25.00")
        }
}

data class ComplianceInfo(
    val sourceOfFunds: String,
    val relationshipToBeneficiary: String,
    val expectedFrequency: String,
    val businessPurpose: String? = null
)

// Market data and analytics
data class CurrencyMarketData(
    val currency: String,
    val currentRate: BigDecimal,
    val change24h: BigDecimal,
    val changePercentage24h: Double,
    val high24h: BigDecimal,
    val low24h: BigDecimal,
    val volume24h: BigDecimal,
    val marketCap: BigDecimal? = null,
    val lastUpdated: LocalDateTime
)

data class CurrencyForecast(
    val currencyPair: String,
    val currentRate: BigDecimal,
    val forecast1Week: BigDecimal,
    val forecast1Month: BigDecimal,
    val forecast3Months: BigDecimal,
    val confidence: Double, // 0.0 to 1.0
    val factors: List<String>, // Economic factors affecting the forecast
    val generatedAt: LocalDateTime
)