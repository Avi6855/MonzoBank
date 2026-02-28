package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.TransactionStatus
import com.avinashpatil.monzobank.entity.TransactionType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CreateTransactionRequest(
    @field:NotNull(message = "Account ID is required")
    val accountId: UUID,
    
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    val amount: BigDecimal,
    
    @field:NotNull(message = "Transaction type is required")
    val type: TransactionType,
    
    @field:NotBlank(message = "Description is required")
    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String,
    
    @field:Size(max = 100, message = "Category must not exceed 100 characters")
    val category: String? = null,
    
    @field:Size(max = 100, message = "External ID must not exceed 100 characters")
    val externalId: String? = null,
    
    @field:Size(max = 100, message = "Merchant name must not exceed 100 characters")
    val merchantName: String? = null,
    
    @field:Size(max = 100, message = "Merchant category must not exceed 100 characters")
    val merchantCategory: String? = null,
    
    @field:Size(max = 255, message = "Location must not exceed 255 characters")
    val location: String? = null
)

data class TransactionResponse(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: TransactionType,
    val status: TransactionStatus,
    val description: String,
    val category: String?,
    val reference: String,
    val externalId: String?,
    val merchantName: String?,
    val merchantCategory: String?,
    val location: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class TransferRequest(
    @field:NotNull(message = "From account ID is required")
    val fromAccountId: UUID,
    
    @field:NotNull(message = "To account ID is required")
    val toAccountId: UUID,
    
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    val amount: BigDecimal,
    
    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String? = null,
    
    @field:Size(max = 100, message = "Reference must not exceed 100 characters")
    val reference: String? = null
)

data class TransferResponse(
    val transferId: UUID,
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal,
    val status: String,
    val reference: String,
    val createdAt: LocalDateTime
)

data class TransactionFilterRequest(
    val accountId: UUID? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val category: String? = null,
    val type: TransactionType? = null,
    val status: TransactionStatus? = null,
    val minAmount: BigDecimal? = null,
    val maxAmount: BigDecimal? = null,
    val merchantName: String? = null,
    val page: Int = 0,
    val size: Int = 20
)

data class TransactionSummaryResponse(
    val totalTransactions: Long,
    val totalCredits: BigDecimal,
    val totalDebits: BigDecimal,
    val netAmount: BigDecimal,
    val averageTransactionAmount: BigDecimal,
    val largestTransaction: BigDecimal,
    val smallestTransaction: BigDecimal,
    val mostFrequentCategory: String?,
    val period: String
)

data class SpendingByCategoryResponse(
    val category: String,
    val totalAmount: BigDecimal,
    val transactionCount: Long
)

data class MonthlySpendingResponse(
    val month: String,
    val year: Int,
    val totalSpending: BigDecimal,
    val totalIncome: BigDecimal,
    val netAmount: BigDecimal,
    val transactionCount: Long,
    val categories: List<SpendingByCategoryResponse>
)

data class TransactionSearchRequest(
    @field:NotBlank(message = "Search query is required")
    @field:Size(min = 3, max = 100, message = "Search query must be between 3 and 100 characters")
    val query: String,
    
    val accountId: UUID? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val page: Int = 0,
    val size: Int = 20
)

data class BulkTransactionRequest(
    @field:NotEmpty(message = "Transactions list cannot be empty")
    @field:Size(max = 100, message = "Cannot process more than 100 transactions at once")
    val transactions: List<CreateTransactionRequest>
)

data class BulkTransactionResponse(
    val totalRequested: Int,
    val successful: Int,
    val failed: Int,
    val results: List<BulkTransactionResult>
)

data class BulkTransactionResult(
    val index: Int,
    val success: Boolean,
    val transactionId: UUID?,
    val error: String?
)

data class TransactionExportRequest(
    val accountId: UUID? = null,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val format: ExportFormat = ExportFormat.CSV,
    val includeCategories: List<String>? = null,
    val excludeCategories: List<String>? = null
)

enum class ExportFormat {
    CSV, PDF, EXCEL
}

data class TransactionExportResponse(
    val exportId: UUID,
    val status: String,
    val downloadUrl: String?,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
)

data class RecurringTransactionRequest(
    @field:NotNull(message = "Account ID is required")
    val accountId: UUID,
    
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    val amount: BigDecimal,
    
    @field:NotNull(message = "Transaction type is required")
    val type: TransactionType,
    
    @field:NotBlank(message = "Description is required")
    val description: String,
    
    val category: String? = null,
    
    @field:NotNull(message = "Frequency is required")
    val frequency: RecurringFrequency,
    
    @field:NotNull(message = "Start date is required")
    val startDate: LocalDateTime,
    
    val endDate: LocalDateTime? = null,
    
    @field:Min(value = 1, message = "Max occurrences must be at least 1")
    val maxOccurrences: Int? = null
)

enum class RecurringFrequency {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
}

data class RecurringTransactionResponse(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String,
    val category: String?,
    val frequency: RecurringFrequency,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val maxOccurrences: Int?,
    val currentOccurrences: Int,
    val nextExecutionDate: LocalDateTime?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class TransactionReceiptRequest(
    @field:NotNull(message = "Transaction ID is required")
    val transactionId: UUID,
    
    @field:Email(message = "Valid email is required")
    val email: String? = null,
    
    val includeAccountDetails: Boolean = false
)

data class TransactionReceiptResponse(
    val receiptId: UUID,
    val transactionId: UUID,
    val receiptUrl: String,
    val emailSent: Boolean,
    val createdAt: LocalDateTime
)

data class TransactionDispute(
    @field:NotNull(message = "Transaction ID is required")
    val transactionId: UUID,
    
    @field:NotBlank(message = "Reason is required")
    @field:Size(max = 500, message = "Reason must not exceed 500 characters")
    val reason: String,
    
    @field:NotNull(message = "Dispute type is required")
    val disputeType: DisputeType,
    
    val supportingDocuments: List<String>? = null
)

enum class DisputeType {
    UNAUTHORIZED, DUPLICATE, INCORRECT_AMOUNT, GOODS_NOT_RECEIVED, GOODS_DEFECTIVE, OTHER
}

data class TransactionDisputeResponse(
    val disputeId: UUID,
    val transactionId: UUID,
    val status: String,
    val reason: String,
    val disputeType: DisputeType,
    val createdAt: LocalDateTime,
    val estimatedResolutionDate: LocalDateTime
)