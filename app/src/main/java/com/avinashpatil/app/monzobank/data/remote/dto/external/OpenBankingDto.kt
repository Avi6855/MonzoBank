package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class OpenBankingSyncRequestDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "bank_connection_id")
    val bankConnectionId: String,
    
    @Json(name = "sync_type")
    val syncType: String, // "full", "incremental", "accounts_only", "transactions_only"
    
    @Json(name = "date_range")
    val dateRange: DateRangeDto?,
    
    @Json(name = "account_ids")
    val accountIds: List<String>?, // Specific accounts to sync
    
    @Json(name = "include_pending")
    val includePending: Boolean,
    
    @Json(name = "webhook_url")
    val webhookUrl: String?
)

@JsonClass(generateAdapter = true)
data class OpenBankingSyncResponseDto(
    @Json(name = "sync_id")
    val syncId: String,
    
    @Json(name = "status")
    val status: String, // "initiated", "in_progress", "completed", "failed", "partial"
    
    @Json(name = "bank_name")
    val bankName: String,
    
    @Json(name = "accounts_synced")
    val accountsSynced: List<OpenBankingAccountDto>,
    
    @Json(name = "transactions_count")
    val transactionsCount: Int,
    
    @Json(name = "new_transactions_count")
    val newTransactionsCount: Int,
    
    @Json(name = "updated_transactions_count")
    val updatedTransactionsCount: Int,
    
    @Json(name = "sync_started_at")
    val syncStartedAt: String,
    
    @Json(name = "sync_completed_at")
    val syncCompletedAt: String?,
    
    @Json(name = "next_sync_available_at")
    val nextSyncAvailableAt: String?,
    
    @Json(name = "errors")
    val errors: List<OpenBankingErrorDto>?,
    
    @Json(name = "warnings")
    val warnings: List<String>?,
    
    @Json(name = "data_freshness")
    val dataFreshness: DataFreshnessDto
)

@JsonClass(generateAdapter = true)
data class OpenBankingAccountDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "external_account_id")
    val externalAccountId: String,
    
    @Json(name = "account_name")
    val accountName: String,
    
    @Json(name = "account_type")
    val accountType: String, // "checking", "savings", "credit_card", "loan", "investment"
    
    @Json(name = "account_subtype")
    val accountSubtype: String?,
    
    @Json(name = "balance")
    val balance: BigDecimal,
    
    @Json(name = "available_balance")
    val availableBalance: BigDecimal?,
    
    @Json(name = "currency")
    val currency: String,
    
    @Json(name = "account_number_masked")
    val accountNumberMasked: String,
    
    @Json(name = "routing_number")
    val routingNumber: String?,
    
    @Json(name = "is_active")
    val isActive: Boolean,
    
    @Json(name = "last_transaction_date")
    val lastTransactionDate: String?,
    
    @Json(name = "sync_status")
    val syncStatus: String // "success", "failed", "partial"
)

@JsonClass(generateAdapter = true)
data class OpenBankingErrorDto(
    @Json(name = "error_code")
    val errorCode: String,
    
    @Json(name = "error_message")
    val errorMessage: String,
    
    @Json(name = "account_id")
    val accountId: String?,
    
    @Json(name = "error_type")
    val errorType: String, // "authentication", "authorization", "rate_limit", "data", "network"
    
    @Json(name = "retry_after")
    val retryAfter: String?, // ISO 8601 datetime
    
    @Json(name = "resolution_steps")
    val resolutionSteps: List<String>?
)

@JsonClass(generateAdapter = true)
data class DataFreshnessDto(
    @Json(name = "accounts_last_updated")
    val accountsLastUpdated: String,
    
    @Json(name = "transactions_last_updated")
    val transactionsLastUpdated: String,
    
    @Json(name = "balance_last_updated")
    val balanceLastUpdated: String,
    
    @Json(name = "data_age_hours")
    val dataAgeHours: Int
)

@JsonClass(generateAdapter = true)
data class DateRangeDto(
    @Json(name = "start_date")
    val startDate: String,
    
    @Json(name = "end_date")
    val endDate: String
)