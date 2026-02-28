package com.avinashpatil.app.monzobank.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

data class AccountDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("account_type")
    val accountType: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("sort_code")
    val sortCode: String,
    @SerializedName("balance")
    val balance: BigDecimal,
    @SerializedName("currency")
    val currency: String = "GBP",
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date,
    @SerializedName("account_name")
    val accountName: String? = null,
    @SerializedName("interest_rate")
    val interestRate: Double? = null,
    @SerializedName("overdraft_limit")
    val overdraftLimit: BigDecimal? = null,
    @SerializedName("minimum_balance")
    val minimumBalance: BigDecimal? = null
)

data class CreateAccountRequestDto(
    @SerializedName("account_type")
    val accountType: String,
    @SerializedName("account_name")
    val accountName: String? = null,
    @SerializedName("initial_deposit")
    val initialDeposit: BigDecimal? = null,
    @SerializedName("currency")
    val currency: String = "GBP"
)

data class UpdateAccountRequestDto(
    @SerializedName("account_name")
    val accountName: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean? = null,
    @SerializedName("overdraft_limit")
    val overdraftLimit: BigDecimal? = null
)

data class AccountBalanceDto(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("balance")
    val balance: BigDecimal,
    @SerializedName("available_balance")
    val availableBalance: BigDecimal,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("last_updated")
    val lastUpdated: Date
)

data class AccountStatementDto(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("statement_period")
    val statementPeriod: String,
    @SerializedName("opening_balance")
    val openingBalance: BigDecimal,
    @SerializedName("closing_balance")
    val closingBalance: BigDecimal,
    @SerializedName("transactions")
    val transactions: List<TransactionDto>,
    @SerializedName("generated_at")
    val generatedAt: Date
)

// TransactionDto moved to separate file

data class CloseAccountRequestDto(
    @SerializedName("reason")
    val reason: String,
    @SerializedName("transfer_remaining_balance_to")
    val transferRemainingBalanceTo: String? = null
)

data class AccountLimitsDto(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("daily_spending_limit")
    val dailySpendingLimit: BigDecimal,
    @SerializedName("monthly_spending_limit")
    val monthlySpendingLimit: BigDecimal,
    @SerializedName("atm_withdrawal_limit")
    val atmWithdrawalLimit: BigDecimal,
    @SerializedName("online_payment_limit")
    val onlinePaymentLimit: BigDecimal,
    @SerializedName("international_payment_enabled")
    val internationalPaymentEnabled: Boolean
)

data class UpdateAccountLimitsDto(
    @SerializedName("daily_spending_limit")
    val dailySpendingLimit: BigDecimal? = null,
    @SerializedName("monthly_spending_limit")
    val monthlySpendingLimit: BigDecimal? = null,
    @SerializedName("atm_withdrawal_limit")
    val atmWithdrawalLimit: BigDecimal? = null,
    @SerializedName("online_payment_limit")
    val onlinePaymentLimit: BigDecimal? = null,
    @SerializedName("international_payment_enabled")
    val internationalPaymentEnabled: Boolean? = null
)

data class NotificationSettingsDto(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("transaction_notifications")
    val transactionNotifications: Boolean,
    @SerializedName("balance_alerts")
    val balanceAlerts: Boolean,
    @SerializedName("security_alerts")
    val securityAlerts: Boolean,
    @SerializedName("marketing_notifications")
    val marketingNotifications: Boolean,
    @SerializedName("low_balance_threshold")
    val lowBalanceThreshold: BigDecimal? = null
)

data class UpdateNotificationSettingsDto(
    @SerializedName("transaction_notifications")
    val transactionNotifications: Boolean? = null,
    @SerializedName("balance_alerts")
    val balanceAlerts: Boolean? = null,
    @SerializedName("security_alerts")
    val securityAlerts: Boolean? = null,
    @SerializedName("marketing_notifications")
    val marketingNotifications: Boolean? = null,
    @SerializedName("low_balance_threshold")
    val lowBalanceThreshold: BigDecimal? = null
)