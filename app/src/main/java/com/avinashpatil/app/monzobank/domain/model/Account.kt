package com.avinashpatil.app.monzobank.domain.model

import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.domain.model.TrendDirection
import java.util.*
import java.math.BigDecimal

data class Account(
    val id: String,
    val userId: String,
    val name: String,
    val type: AccountType,
    val balance: BigDecimal,
    val availableBalance: BigDecimal,
    val currency: String = "GBP",
    val accountNumber: String,
    val sortCode: String,
    val iban: String,
    val status: AccountStatus,
    val overdraftLimit: BigDecimal = BigDecimal.ZERO,
    val minimumBalance: BigDecimal? = null,
    val interestRate: BigDecimal = BigDecimal.ZERO,
    val createdAt: Date,
    val updatedAt: Date,
    val isDefault: Boolean = false,
    val metadata: Map<String, Any> = emptyMap()
) {
    val formattedAccountNumber: String
        get() = "${sortCode.take(2)}-${sortCode.drop(2).take(2)}-${sortCode.drop(4)} ${accountNumber.chunked(4).joinToString(" ")}"
    
    val isOverdrawn: Boolean
        get() = balance < BigDecimal.ZERO
    
    val overdraftUsed: BigDecimal
        get() = if (isOverdrawn) balance.negate() else BigDecimal.ZERO
    
    val overdraftAvailable: BigDecimal
        get() = overdraftLimit - overdraftUsed
    
    val totalAvailableBalance: BigDecimal
        get() = availableBalance + overdraftAvailable
    
    val displayName: String
        get() = name
    
    val formattedBalance: String
        get() = "£${String.format("%.2f", balance.toDouble())}"
    
    val formattedAvailableBalance: String
        get() = "£${String.format("%.2f", availableBalance.toDouble())}"
}

data class AccountSummary(
    val totalBalance: BigDecimal,
    val totalAvailableBalance: BigDecimal,
    val accountCount: Int,
    val activeAccountCount: Int,
    val totalOverdraftUsed: BigDecimal,
    val totalOverdraftLimit: BigDecimal,
    val monthlyIncome: BigDecimal,
    val monthlyExpenses: BigDecimal,
    val savingsRate: BigDecimal
)

data class AccountInsight(
    val type: InsightType,
    val title: String,
    val description: String,
    val value: String,
    val trend: TrendDirection,
    val severity: InsightSeverity,
    val actionable: Boolean = false,
    val actionText: String? = null
)

enum class InsightType {
    SPENDING_TREND,
    SAVINGS_RATE,
    BUDGET_ALERT,
    INCOME_ANALYSIS,
    BALANCE_FORECAST,
    OVERDRAFT_WARNING
}

// TrendDirection enum moved to InternationalModels.kt to avoid redeclaration

enum class InsightSeverity {
    INFO, WARNING, CRITICAL
}

// AccountType and AccountStatus enums moved to data.local.entity package to avoid conflicts
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.AccountType
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.AccountStatus