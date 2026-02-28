package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TransactionRepository {
    
    // Transaction CRUD Operations
    suspend fun createTransaction(transaction: Transaction): Result<Transaction>
    
    suspend fun getTransaction(transactionId: String): Result<Transaction?>
    
    suspend fun getTransactionsByAccountId(
        accountId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<Transaction>>
    
    suspend fun getTransactionsByUserId(
        userId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<Transaction>>
    
    suspend fun updateTransaction(transaction: Transaction): Result<Transaction>
    
    suspend fun deleteTransaction(transactionId: String): Result<Unit>
    
    // Transaction Status Management
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        reason: String? = null
    ): Result<Unit>
    
    suspend fun processTransaction(transactionId: String): Result<Transaction>
    
    suspend fun cancelTransaction(transactionId: String): Result<Unit>
    
    suspend fun reverseTransaction(
        transactionId: String,
        reason: String
    ): Result<Transaction>
    
    // Transaction Search and Filtering
    suspend fun searchTransactions(
        userId: String,
        query: String,
        filters: TransactionFilters? = null
    ): Result<List<Transaction>>
    
    suspend fun getTransactionsByDateRange(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>>
    
    suspend fun getTransactionsByCategory(
        accountId: String,
        category: TransactionCategory,
        startDate: Date? = null,
        endDate: Date? = null
    ): Result<List<Transaction>>
    
    suspend fun getTransactionsByMerchant(
        accountId: String,
        merchantId: String,
        limit: Int = 50
    ): Result<List<Transaction>>
    
    suspend fun getTransactionsByAmount(
        accountId: String,
        minAmount: Double? = null,
        maxAmount: Double? = null
    ): Result<List<Transaction>>
    
    // Transaction Analytics
    suspend fun getTransactionAnalytics(
        accountId: String,
        period: AnalyticsPeriod,
        startDate: Date? = null,
        endDate: Date? = null
    ): Result<TransactionAnalytics>
    
    suspend fun getSpendingByCategory(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<Map<TransactionCategory, Double>>
    
    suspend fun getSpendingByMerchant(
        accountId: String,
        startDate: Date,
        endDate: Date,
        limit: Int = 10
    ): Result<List<MerchantAnalytics>>
    
    suspend fun getMonthlySpendingTrend(
        accountId: String,
        months: Int = 12
    ): Result<List<MonthlyTrend>>
    
    suspend fun getRecurringTransactions(
        accountId: String
    ): Result<List<RecurringTransactionSummary>>
    
    // Transaction Categorization
    suspend fun categorizeTransaction(
        transactionId: String,
        category: TransactionCategory,
        subcategory: String? = null
    ): Result<Unit>
    
    suspend fun bulkCategorizeTransactions(
        transactionIds: List<String>,
        category: TransactionCategory,
        subcategory: String? = null
    ): Result<Unit>
    
    suspend fun autoCategorizePendingTransactions(
        accountId: String
    ): Result<Int> // Returns number of categorized transactions
    
    suspend fun suggestCategory(
        transaction: Transaction
    ): Result<List<CategorySuggestion>>
    
    // Transaction Notes and Tags
    suspend fun addTransactionNote(
        transactionId: String,
        note: String
    ): Result<Unit>
    
    suspend fun updateTransactionNote(
        transactionId: String,
        note: String
    ): Result<Unit>
    
    suspend fun removeTransactionNote(transactionId: String): Result<Unit>
    
    suspend fun addTransactionTags(
        transactionId: String,
        tags: List<String>
    ): Result<Unit>
    
    suspend fun removeTransactionTags(
        transactionId: String,
        tags: List<String>
    ): Result<Unit>
    
    // Transaction Receipts
    suspend fun attachReceipt(
        transactionId: String,
        receipt: Receipt
    ): Result<Unit>
    
    suspend fun getTransactionReceipt(
        transactionId: String
    ): Result<Receipt?>
    
    suspend fun updateReceipt(
        transactionId: String,
        receipt: Receipt
    ): Result<Unit>
    
    suspend fun removeReceipt(transactionId: String): Result<Unit>
    
    // Transaction Disputes
    suspend fun disputeTransaction(
        transactionId: String,
        reason: DisputeReason,
        description: String,
        evidence: List<String> = emptyList()
    ): Result<String> // Returns dispute ID
    
    suspend fun getTransactionDispute(
        transactionId: String
    ): Result<TransactionDispute?>
    
    suspend fun updateDisputeStatus(
        disputeId: String,
        status: DisputeStatus,
        resolution: String? = null
    ): Result<Unit>
    
    // Transaction Splitting
    suspend fun splitTransaction(
        transactionId: String,
        splits: List<TransactionSplit>
    ): Result<List<Transaction>>
    
    suspend fun getSplitTransactions(
        parentTransactionId: String
    ): Result<List<Transaction>>
    
    suspend fun mergeSplitTransactions(
        splitTransactionIds: List<String>
    ): Result<Transaction>
    
    // Recurring Transactions
    suspend fun createRecurringTransaction(
        transaction: Transaction,
        pattern: RecurringPattern
    ): Result<String> // Returns recurring transaction ID
    
    suspend fun updateRecurringTransaction(
        recurringId: String,
        pattern: RecurringPattern
    ): Result<Unit>
    
    suspend fun pauseRecurringTransaction(recurringId: String): Result<Unit>
    
    suspend fun resumeRecurringTransaction(recurringId: String): Result<Unit>
    
    suspend fun cancelRecurringTransaction(recurringId: String): Result<Unit>
    
    suspend fun getNextRecurringTransactions(
        accountId: String,
        days: Int = 30
    ): Result<List<Transaction>>
    
    // Transaction Export
    suspend fun exportTransactions(
        accountId: String,
        startDate: Date,
        endDate: Date,
        format: ExportFormat,
        categories: List<TransactionCategory>? = null
    ): Result<String> // Returns file path or URL
    
    suspend fun exportTransactionsByCategory(
        accountId: String,
        category: TransactionCategory,
        startDate: Date,
        endDate: Date,
        format: ExportFormat
    ): Result<String>
    
    // Real-time Updates
    fun observeTransactionsByAccountId(
        accountId: String
    ): Flow<List<Transaction>>
    
    fun observeTransaction(transactionId: String): Flow<Transaction?>
    
    fun observePendingTransactions(
        accountId: String
    ): Flow<List<Transaction>>
    
    fun observeRecentTransactions(
        accountId: String,
        limit: Int = 10
    ): Flow<List<Transaction>>
    
    // Transaction Validation
    suspend fun validateTransaction(
        transaction: Transaction
    ): Result<TransactionValidationResult>
    
    suspend fun checkDuplicateTransaction(
        transaction: Transaction
    ): Result<List<Transaction>>
    
    suspend fun verifyTransactionIntegrity(
        transactionId: String
    ): Result<Boolean>
    
    // Transaction Synchronization
    suspend fun syncTransactionsWithBank(
        accountId: String,
        startDate: Date? = null
    ): Result<Int> // Returns number of synced transactions
    
    suspend fun reconcileTransactions(
        accountId: String,
        bankTransactions: List<BankTransaction>
    ): Result<ReconciliationResult>
    
    // Transaction Notifications
    suspend fun enableTransactionNotifications(
        accountId: String,
        settings: TransactionNotificationSettings
    ): Result<Unit>
    
    suspend fun disableTransactionNotifications(
        accountId: String
    ): Result<Unit>
    
    // Bulk Operations
    suspend fun bulkUpdateTransactions(
        transactionIds: List<String>,
        updates: TransactionBulkUpdate
    ): Result<Int> // Returns number of updated transactions
    
    suspend fun bulkDeleteTransactions(
        transactionIds: List<String>
    ): Result<Int> // Returns number of deleted transactions
}

data class TransactionFilters(
    val types: List<TransactionType>? = null,
    val statuses: List<TransactionStatus>? = null,
    val categories: List<TransactionCategory>? = null,
    val paymentMethods: List<PaymentMethodType>? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val merchantIds: List<String>? = null,
    val tags: List<String>? = null,
    val hasReceipt: Boolean? = null,
    val isRecurring: Boolean? = null,
    val isDisputed: Boolean? = null,
    val isInternational: Boolean? = null
)

data class CategorySuggestion(
    val category: TransactionCategory,
    val subcategory: String? = null,
    val confidence: Double, // 0.0 to 1.0
    val reason: String
)

data class TransactionDispute(
    val id: String,
    val transactionId: String,
    val reason: DisputeReason,
    val description: String,
    val status: DisputeStatus,
    val evidence: List<String> = emptyList(),
    val resolution: String? = null,
    val createdAt: Date,
    val updatedAt: Date,
    val resolvedAt: Date? = null
)

data class TransactionSplit(
    val amount: Double,
    val description: String,
    val category: TransactionCategory? = null,
    val tags: List<String> = emptyList()
)

data class TransactionValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError> = emptyList(),
    val warnings: List<ValidationWarning> = emptyList()
)

data class ValidationError(
    val field: String,
    val message: String,
    val code: String
)

data class ValidationWarning(
    val field: String,
    val message: String,
    val code: String
)

data class BankTransaction(
    val id: String,
    val amount: Double,
    val description: String,
    val date: Date,
    val reference: String? = null,
    val balance: Double? = null
)

data class ReconciliationResult(
    val matchedTransactions: Int,
    val newTransactions: Int,
    val discrepancies: List<TransactionDiscrepancy>,
    val totalProcessed: Int
)

data class TransactionDiscrepancy(
    val localTransaction: Transaction?,
    val bankTransaction: BankTransaction?,
    val type: DiscrepancyType,
    val description: String
)

data class TransactionNotificationSettings(
    val enabled: Boolean = true,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val categories: List<TransactionCategory>? = null,
    val merchants: List<String>? = null,
    val international: Boolean = true,
    val largeTransactions: Boolean = true,
    val duplicateTransactions: Boolean = true
)

data class TransactionBulkUpdate(
    val category: TransactionCategory? = null,
    val subcategory: String? = null,
    val tags: List<String>? = null,
    val notes: String? = null,
    val status: TransactionStatus? = null
)

enum class DisputeReason {
    UNAUTHORIZED_TRANSACTION,
    DUPLICATE_CHARGE,
    INCORRECT_AMOUNT,
    SERVICE_NOT_RECEIVED,
    PRODUCT_NOT_RECEIVED,
    DEFECTIVE_PRODUCT,
    CANCELLED_SUBSCRIPTION,
    BILLING_ERROR,
    FRAUD,
    OTHER
}

enum class DisputeStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ADDITIONAL_INFO_REQUIRED,
    APPROVED,
    DENIED,
    RESOLVED,
    CLOSED
}

// ExportFormat is defined in ReportingRepository.kt
// Import from: com.avinashpatil.app.monzobank.domain.repository.ExportFormat

enum class DiscrepancyType {
    MISSING_LOCAL,
    MISSING_BANK,
    AMOUNT_MISMATCH,
    DATE_MISMATCH,
    DESCRIPTION_MISMATCH
}