package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.AccountType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CreateAccountRequest(
    @field:NotNull(message = "Account type is required")
    val accountType: AccountType,
    
    @field:Size(max = 100, message = "Account name must not exceed 100 characters")
    val accountName: String? = null,
    
    @field:DecimalMin(value = "0.0", message = "Initial deposit must be non-negative")
    val initialDeposit: BigDecimal? = null,
    
    @field:Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    val currency: String? = null,
    
    @field:DecimalMin(value = "0.0", message = "Overdraft limit must be non-negative")
    val overdraftLimit: BigDecimal? = null
)

data class UpdateAccountRequest(
    @field:Size(max = 100, message = "Account name must not exceed 100 characters")
    val accountName: String? = null,
    
    @field:DecimalMin(value = "0.0", message = "Overdraft limit must be non-negative")
    val overdraftLimit: BigDecimal? = null,
    
    val isActive: Boolean? = null
)

data class AccountResponse(
    val id: UUID,
    val userId: UUID,
    val accountType: AccountType,
    val accountNumber: String,
    val sortCode: String,
    val balance: BigDecimal,
    val currency: String,
    val isActive: Boolean,
    val accountName: String?,
    val overdraftLimit: BigDecimal,
    val interestRate: BigDecimal,
    val createdAt: LocalDateTime
)

data class AccountSummaryResponse(
    val totalBalance: BigDecimal,
    val totalAccounts: Int,
    val accountsByType: Map<AccountType, Int>,
    val accounts: List<AccountResponse>
)

data class BalanceUpdateRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    val amount: BigDecimal,
    
    @field:NotBlank(message = "Operation is required")
    @field:Pattern(regexp = "^(ADD|SUBTRACT)$", message = "Operation must be ADD or SUBTRACT")
    val operation: String,
    
    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String? = null
)

data class TransferRequest(
    @field:NotNull(message = "From account ID is required")
    val fromAccountId: UUID,
    
    @field:NotNull(message = "To account ID is required")
    val toAccountId: UUID,
    
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    val amount: BigDecimal,
    
    @field:NotBlank(message = "Description is required")
    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String,
    
    val reference: String? = null
)

data class AccountBalanceResponse(
    val accountId: UUID,
    val balance: BigDecimal,
    val availableBalance: BigDecimal,
    val currency: String,
    val lastUpdated: LocalDateTime
)

data class AccountStatementRequest(
    @field:NotNull(message = "Start date is required")
    val startDate: LocalDateTime,
    
    @field:NotNull(message = "End date is required")
    val endDate: LocalDateTime,
    
    @field:Pattern(regexp = "^(PDF|CSV|JSON)$", message = "Format must be PDF, CSV, or JSON")
    val format: String = "PDF"
)

data class AccountLimitsResponse(
    val dailyTransferLimit: BigDecimal,
    val monthlyTransferLimit: BigDecimal,
    val overdraftLimit: BigDecimal,
    val dailyWithdrawalLimit: BigDecimal,
    val monthlyWithdrawalLimit: BigDecimal
)

data class UpdateAccountLimitsRequest(
    @field:DecimalMin(value = "0.0", message = "Daily transfer limit must be non-negative")
    val dailyTransferLimit: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.0", message = "Monthly transfer limit must be non-negative")
    val monthlyTransferLimit: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.0", message = "Overdraft limit must be non-negative")
    val overdraftLimit: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.0", message = "Daily withdrawal limit must be non-negative")
    val dailyWithdrawalLimit: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.0", message = "Monthly withdrawal limit must be non-negative")
    val monthlyWithdrawalLimit: BigDecimal? = null
)

data class AccountInterestResponse(
    val accountId: UUID,
    val interestRate: BigDecimal,
    val accruedInterest: BigDecimal,
    val lastInterestPayment: LocalDateTime?,
    val nextInterestPayment: LocalDateTime?
)