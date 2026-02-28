package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.util.Date

/**
 * Represents a summary of recurring transactions for a specific pattern
 */
data class RecurringTransactionSummary(
    val id: String,
    val accountId: String,
    val merchantName: String?,
    val description: String,
    val amount: BigDecimal,
    val currency: String,
    val category: TransactionCategory,
    val frequency: RecurringFrequency,
    val nextExpectedDate: Date?,
    val lastTransactionDate: Date?,
    val transactionCount: Int,
    val totalAmount: BigDecimal,
    val averageAmount: BigDecimal,
    val isActive: Boolean,
    val confidence: Double, // Confidence level of the recurring pattern (0.0 to 1.0)
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Frequency of recurring transactions
 */
enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY,
    IRREGULAR
}