package com.avinashpatil.app.monzobank.domain.model

import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import java.util.*
import java.math.BigDecimal

data class Transaction(
    val id: String,
    val accountId: String,
    val cardId: String? = null,
    val userId: String,
    val type: TransactionType,
    val status: TransactionStatus,
    val amount: java.math.BigDecimal,
    val currency: String = "GBP",
    val originalAmount: java.math.BigDecimal? = null,
    val originalCurrency: String? = null,
    val exchangeRate: java.math.BigDecimal? = null,
    val description: String,
    val reference: String? = null,
    val category: TransactionCategory,
    val subcategory: String? = null,
    val merchant: Merchant? = null,
    val location: TransactionLocation? = null,
    val paymentMethod: PaymentMethodType,
    val balanceAfter: java.math.BigDecimal,
    val runningBalance: java.math.BigDecimal,
    val fees: List<TransactionFee> = emptyList(),
    val tags: List<String> = emptyList(),
    val notes: String? = null,
    val receipt: Receipt? = null,
    val isRecurring: Boolean = false,
    val recurringPattern: RecurringPattern? = null,
    val parentTransactionId: String? = null,
    val childTransactionIds: List<String> = emptyList(),
    val disputeId: String? = null,
    val isDisputed: Boolean = false,
    val createdAt: Date,
    val processedAt: Date? = null,
    val settledAt: Date? = null,
    val updatedAt: Date,
    val metadata: Map<String, Any> = emptyMap(),
    val merchantName: String? = null,
    val isOnlineTransaction: Boolean = false,
    val isAtmTransaction: Boolean = false,
    val isContactlessTransaction: Boolean = false,
    val isInternationalTransaction: Boolean = false
) {
    val isDebit: Boolean
        get() = amount < java.math.BigDecimal.ZERO
    
    val isCredit: Boolean
        get() = amount > java.math.BigDecimal.ZERO
    
    val absoluteAmount: java.math.BigDecimal
        get() = amount.abs()
    
    val totalFees: java.math.BigDecimal
        get() = fees.map { it.amount }.fold(java.math.BigDecimal.ZERO) { acc, fee -> acc + fee }
    
    val netAmount: java.math.BigDecimal
        get() = amount - totalFees
    
    val isInternational: Boolean
        get() = originalCurrency != null && originalCurrency != currency
    
    val isPending: Boolean
        get() = status == TransactionStatus.PENDING
    
    val isCompleted: Boolean
        get() = status == TransactionStatus.COMPLETED
    
    val isFailed: Boolean
        get() = status == TransactionStatus.FAILED || status == TransactionStatus.DECLINED
    
    val formattedAmount: String
        get() = "£${String.format("%.2f", absoluteAmount.toDouble())}"
    
    val displayDescription: String
        get() = merchantName ?: merchant?.displayName ?: merchant?.name ?: description
    
    val transactionDate: Date
        get() = processedAt ?: createdAt
    
    val categoryIcon: String
        get() = when (category) {
            TransactionCategory.GROCERIES -> "🛒"
            TransactionCategory.TRANSPORT -> "🚗"
            TransactionCategory.ENTERTAINMENT -> "🎬"
            TransactionCategory.RESTAURANTS -> "🍽️"
            TransactionCategory.SHOPPING -> "🛍️"
            TransactionCategory.BILLS -> "📄"
            TransactionCategory.HEALTHCARE -> "🏥"
            TransactionCategory.EDUCATION -> "📚"
            TransactionCategory.TRAVEL -> "✈️"
            TransactionCategory.FUEL -> "⛽"
            else -> "💳"
        }
    
    val isIncoming: Boolean
        get() = isCredit
    
    val transactionType: TransactionType
        get() = type
}

data class Merchant(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val category: String,
    val subcategory: String? = null,
    val mcc: String, // Merchant Category Code
    val website: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val logo: String? = null,
    val address: Address? = null,
    val location: TransactionLocation? = null,
    val rating: Double? = null,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val metadata: Map<String, Any> = emptyMap()
)

data class TransactionLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val accuracy: Double? = null,
    val timestamp: Date? = null
)

data class TransactionFee(
    val type: FeeType,
    val amount: java.math.BigDecimal,
    val currency: String = "GBP",
    val description: String,
    val isRefundable: Boolean = false
)

data class Receipt(
    val id: String,
    val transactionId: String,
    val merchantName: String,
    val items: List<ReceiptItem> = emptyList(),
    val subtotal: java.math.BigDecimal,
    val tax: java.math.BigDecimal,
    val tip: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    val total: java.math.BigDecimal,
    val currency: String = "GBP",
    val receiptNumber: String? = null,
    val imageUrl: String? = null,
    val pdfUrl: String? = null,
    val createdAt: Date
)

data class ReceiptItem(
    val name: String,
    val quantity: Int,
    val unitPrice: java.math.BigDecimal,
    val totalPrice: java.math.BigDecimal,
    val category: String? = null,
    val sku: String? = null
)

data class RecurringPattern(
    val frequency: RecurringFrequency,
    val interval: Int = 1, // Every N periods
    val dayOfMonth: Int? = null, // For monthly patterns
    val dayOfWeek: Int? = null, // For weekly patterns
    val endDate: Date? = null,
    val maxOccurrences: Int? = null,
    val nextOccurrence: Date? = null
)

data class TransactionAnalytics(
    val accountId: String,
    val period: AnalyticsPeriod,
    val totalIncome: java.math.BigDecimal,
    val totalExpenses: java.math.BigDecimal,
    val netFlow: java.math.BigDecimal,
    val transactionCount: Int,
    val averageTransactionAmount: java.math.BigDecimal,
    val largestTransaction: java.math.BigDecimal,
    val smallestTransaction: java.math.BigDecimal,
    val categoryBreakdown: Map<TransactionCategory, CategoryAnalytics>,
    val merchantBreakdown: Map<String, MerchantAnalytics>,
    val dailyAverages: Map<Int, java.math.BigDecimal>, // Day of week (1-7) to average amount
    val monthlyTrend: List<MonthlyTrend>,
    val recurringTransactions: List<RecurringTransactionSummary>
)

data class CategoryAnalytics(
    val category: TransactionCategory,
    val totalAmount: java.math.BigDecimal,
    val transactionCount: Int,
    val averageAmount: java.math.BigDecimal,
    val percentage: java.math.BigDecimal,
    val trend: TrendDirection,
    val budgetLimit: java.math.BigDecimal? = null,
    val budgetUsed: java.math.BigDecimal? = null
)

data class MerchantAnalytics(
    val merchantName: String,
    val totalAmount: java.math.BigDecimal,
    val transactionCount: Int,
    val averageAmount: java.math.BigDecimal,
    val lastTransactionDate: Date,
    val frequency: TransactionFrequency
)

data class MonthlyTrend(
    val month: Int,
    val year: Int,
    val totalAmount: java.math.BigDecimal,
    val transactionCount: Int,
    val changeFromPrevious: java.math.BigDecimal,
    val percentageChange: java.math.BigDecimal
)

// RecurringTransactionSummary is defined in RecurringTransactionSummary.kt
// Import from: com.avinashpatil.app.monzobank.domain.model.RecurringTransactionSummary

// TransactionType and TransactionStatus enums moved to data.local.entity package to avoid conflicts
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.TransactionType
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus

enum class TransactionCategory {
    GROCERIES,
    RESTAURANTS,
    TRANSPORT,
    ENTERTAINMENT,
    SHOPPING,
    BILLS,
    HEALTHCARE,
    EDUCATION,
    TRAVEL,
    FUEL,
    INSURANCE,
    INVESTMENTS,
    SAVINGS,
    CHARITY,
    CASH,
    TRANSFERS,
    FEES,
    INTEREST,
    SALARY,
    FREELANCE,
    BUSINESS,
    OTHER
}

/*
enum class PaymentMethodType {
    CARD,
    BANK_TRANSFER,
    DIRECT_DEBIT,
    STANDING_ORDER,
    CASH,
    CHEQUE,
    MOBILE_PAYMENT,
    CONTACTLESS,
    CHIP_AND_PIN,
    ONLINE,
    ATM,
    WIRE_TRANSFER
}


 */
enum class FeeType {
    TRANSACTION_FEE,
    FOREIGN_EXCHANGE_FEE,
    ATM_FEE,
    OVERDRAFT_FEE,
    MONTHLY_FEE,
    ANNUAL_FEE,
    LATE_PAYMENT_FEE,
    RETURNED_PAYMENT_FEE,
    WIRE_TRANSFER_FEE,
    CASH_ADVANCE_FEE,
    OTHER
}

// RecurringFrequency is defined in RecurringTransactionSummary.kt
// Import from: com.avinashpatil.app.monzobank.domain.model.RecurringFrequency

enum class AnalyticsPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY,
    CUSTOM
}

enum class TransactionFrequency {
    VERY_FREQUENT, // Multiple times per week
    FREQUENT,      // Weekly
    REGULAR,       // Monthly
    OCCASIONAL,    // Few times per year
    RARE          // Once or twice per year
}
/*
enum class TrendDirection {
    UP,
    DOWN,
    NEUTRAL
}

 */