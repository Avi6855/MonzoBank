package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.TransactionStatus
import com.avinashpatil.monzobank.entity.TransactionType
import com.avinashpatil.monzobank.service.TransactionService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class TransactionController(
    private val transactionService: TransactionService
) {
    
    @PostMapping
    fun createTransaction(
        @Valid @RequestBody request: CreateTransactionRequest
    ): ResponseEntity<ApiResponse<TransactionResponse>> {
        val response = transactionService.createTransaction(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Transaction created successfully", response))
    }
    
    @GetMapping("/{transactionId}")
    fun getTransaction(
        @PathVariable transactionId: UUID
    ): ResponseEntity<ApiResponse<TransactionResponse>> {
        val transaction = transactionService.getTransactionById(transactionId)
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction))
    }
    
    @GetMapping("/account/{accountId}")
    fun getTransactionsByAccount(
        @PathVariable accountId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val transactions = transactionService.getTransactionsByAccountId(accountId, pageable)
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions))
    }
    
    @GetMapping("/user")
    fun getTransactionsByUser(
        @AuthenticationPrincipal userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val transactions = transactionService.getTransactionsByUserId(UUID.fromString(userId), pageable)
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions))
    }
    
    @GetMapping("/account/{accountId}/date-range")
    fun getTransactionsByDateRange(
        @PathVariable accountId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val transactions = transactionService.getTransactionsByDateRange(accountId, startDate, endDate, pageable)
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions))
    }
    
    @GetMapping("/account/{accountId}/category/{category}")
    fun getTransactionsByCategory(
        @PathVariable accountId: UUID,
        @PathVariable category: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val transactions = transactionService.getTransactionsByCategory(accountId, category, pageable)
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions))
    }
    
    @GetMapping("/account/{accountId}/status/{status}")
    fun getTransactionsByStatus(
        @PathVariable accountId: UUID,
        @PathVariable status: TransactionStatus,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val transactions = transactionService.getTransactionsByStatus(accountId, status, pageable)
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions))
    }
    
    @PutMapping("/{transactionId}/status")
    fun updateTransactionStatus(
        @PathVariable transactionId: UUID,
        @RequestParam status: TransactionStatus
    ): ResponseEntity<ApiResponse<TransactionResponse>> {
        val transaction = transactionService.updateTransactionStatus(transactionId, status)
        return ResponseEntity.ok(ApiResponse.success("Transaction status updated successfully", transaction))
    }
    
    @PostMapping("/transfer")
    fun transferFunds(
        @Valid @RequestBody request: TransferRequest
    ): ResponseEntity<ApiResponse<TransferResponse>> {
        val response = transactionService.transferFunds(request)
        return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", response))
    }
    
    @GetMapping("/account/{accountId}/balance")
    fun getAccountBalance(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<AccountBalanceResponse>> {
        val balance = transactionService.getAccountBalance(accountId)
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved successfully", balance))
    }
    
    @GetMapping("/account/{accountId}/spending-by-category")
    fun getSpendingByCategory(
        @PathVariable accountId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<ApiResponse<List<SpendingByCategoryResponse>>> {
        val spending = transactionService.getSpendingByCategory(accountId, startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success("Spending by category retrieved successfully", spending))
    }
    
    @PostMapping("/bulk")
    fun createBulkTransactions(
        @Valid @RequestBody request: BulkTransactionRequest
    ): ResponseEntity<ApiResponse<BulkTransactionResponse>> {
        val results = mutableListOf<BulkTransactionResult>()
        var successful = 0
        var failed = 0
        
        request.transactions.forEachIndexed { index, transactionRequest ->
            try {
                val response = transactionService.createTransaction(transactionRequest)
                results.add(BulkTransactionResult(
                    index = index,
                    success = true,
                    transactionId = response.id,
                    error = null
                ))
                successful++
            } catch (e: Exception) {
                results.add(BulkTransactionResult(
                    index = index,
                    success = false,
                    transactionId = null,
                    error = e.message
                ))
                failed++
            }
        }
        
        val bulkResponse = BulkTransactionResponse(
            totalRequested = request.transactions.size,
            successful = successful,
            failed = failed,
            results = results
        )
        
        return ResponseEntity.ok(ApiResponse.success("Bulk transactions processed", bulkResponse))
    }
    
    @PostMapping("/export")
    fun exportTransactions(
        @Valid @RequestBody request: TransactionExportRequest
    ): ResponseEntity<ApiResponse<TransactionExportResponse>> {
        // In a real implementation, this would generate and return an export file
        val exportResponse = TransactionExportResponse(
            exportId = UUID.randomUUID(),
            status = "PROCESSING",
            downloadUrl = null,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(7)
        )
        
        return ResponseEntity.ok(ApiResponse.success("Export initiated successfully", exportResponse))
    }
    
    @GetMapping("/export/{exportId}")
    fun getExportStatus(
        @PathVariable exportId: UUID
    ): ResponseEntity<ApiResponse<TransactionExportResponse>> {
        // In a real implementation, this would check the actual export status
        val exportResponse = TransactionExportResponse(
            exportId = exportId,
            status = "COMPLETED",
            downloadUrl = "/api/transactions/export/$exportId/download",
            createdAt = LocalDateTime.now().minusMinutes(5),
            expiresAt = LocalDateTime.now().plusDays(7)
        )
        
        return ResponseEntity.ok(ApiResponse.success("Export status retrieved successfully", exportResponse))
    }
    
    @PostMapping("/search")
    fun searchTransactions(
        @Valid @RequestBody request: TransactionSearchRequest
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        // In a real implementation, this would perform full-text search
        val pageable: Pageable = PageRequest.of(request.page, request.size)
        val emptyPage = org.springframework.data.domain.PageImpl<TransactionResponse>(emptyList(), pageable, 0)
        
        return ResponseEntity.ok(ApiResponse.success("Search completed", emptyPage))
    }
    
    @PostMapping("/recurring")
    fun createRecurringTransaction(
        @Valid @RequestBody request: RecurringTransactionRequest
    ): ResponseEntity<ApiResponse<RecurringTransactionResponse>> {
        // In a real implementation, this would create a recurring transaction schedule
        val recurringResponse = RecurringTransactionResponse(
            id = UUID.randomUUID(),
            accountId = request.accountId,
            amount = request.amount,
            type = request.type,
            description = request.description,
            category = request.category,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            maxOccurrences = request.maxOccurrences,
            currentOccurrences = 0,
            nextExecutionDate = request.startDate,
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Recurring transaction created successfully", recurringResponse))
    }
    
    @GetMapping("/recurring/account/{accountId}")
    fun getRecurringTransactions(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<List<RecurringTransactionResponse>>> {
        // In a real implementation, this would retrieve actual recurring transactions
        val emptyList = emptyList<RecurringTransactionResponse>()
        return ResponseEntity.ok(ApiResponse.success("Recurring transactions retrieved successfully", emptyList))
    }
    
    @PostMapping("/{transactionId}/receipt")
    fun generateReceipt(
        @PathVariable transactionId: UUID,
        @Valid @RequestBody request: TransactionReceiptRequest
    ): ResponseEntity<ApiResponse<TransactionReceiptResponse>> {
        // In a real implementation, this would generate a transaction receipt
        val receiptResponse = TransactionReceiptResponse(
            receiptId = UUID.randomUUID(),
            transactionId = transactionId,
            receiptUrl = "/api/transactions/$transactionId/receipt/download",
            emailSent = request.email != null,
            createdAt = LocalDateTime.now()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Receipt generated successfully", receiptResponse))
    }
    
    @PostMapping("/{transactionId}/dispute")
    fun disputeTransaction(
        @PathVariable transactionId: UUID,
        @Valid @RequestBody request: TransactionDispute
    ): ResponseEntity<ApiResponse<TransactionDisputeResponse>> {
        // In a real implementation, this would create a transaction dispute
        val disputeResponse = TransactionDisputeResponse(
            disputeId = UUID.randomUUID(),
            transactionId = transactionId,
            status = "SUBMITTED",
            reason = request.reason,
            disputeType = request.disputeType,
            createdAt = LocalDateTime.now(),
            estimatedResolutionDate = LocalDateTime.now().plusDays(14)
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Dispute submitted successfully", disputeResponse))
    }
    
    @GetMapping("/summary/account/{accountId}")
    fun getTransactionSummary(
        @PathVariable accountId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<ApiResponse<TransactionSummaryResponse>> {
        // In a real implementation, this would calculate actual transaction summary
        val summary = TransactionSummaryResponse(
            totalTransactions = 0L,
            totalCredits = java.math.BigDecimal.ZERO,
            totalDebits = java.math.BigDecimal.ZERO,
            netAmount = java.math.BigDecimal.ZERO,
            averageTransactionAmount = java.math.BigDecimal.ZERO,
            largestTransaction = java.math.BigDecimal.ZERO,
            smallestTransaction = java.math.BigDecimal.ZERO,
            mostFrequentCategory = null,
            period = "${startDate.toLocalDate()} to ${endDate.toLocalDate()}"
        )
        
        return ResponseEntity.ok(ApiResponse.success("Transaction summary retrieved successfully", summary))
    }
}