package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.AccountType
import com.avinashpatil.monzobank.service.AccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class AccountController(
    private val accountService: AccountService
) {
    
    @PostMapping
    fun createAccount(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: CreateAccountRequest
    ): ResponseEntity<ApiResponse<AccountResponse>> {
        val response = accountService.createAccount(UUID.fromString(userId), request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Account created successfully", response))
    }
    
    @GetMapping
    fun getAccounts(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<AccountResponse>>> {
        val accounts = accountService.getAccountsByUserId(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts))
    }
    
    @GetMapping("/{accountId}")
    fun getAccount(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<AccountResponse>> {
        val account = accountService.getAccountById(accountId)
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account))
    }
    
    @GetMapping("/summary")
    fun getAccountSummary(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<AccountSummaryResponse>> {
        val summary = accountService.getAccountSummary(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Account summary retrieved successfully", summary))
    }
    
    @GetMapping("/by-type/{accountType}")
    fun getAccountsByType(
        @AuthenticationPrincipal userId: String,
        @PathVariable accountType: AccountType
    ): ResponseEntity<ApiResponse<List<AccountResponse>>> {
        val accounts = accountService.getAccountsByType(UUID.fromString(userId), accountType)
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts))
    }
    
    @GetMapping("/by-number/{accountNumber}")
    fun getAccountByNumber(
        @PathVariable accountNumber: String
    ): ResponseEntity<ApiResponse<AccountResponse>> {
        val account = accountService.getAccountByAccountNumber(accountNumber)
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account))
    }
    
    @PutMapping("/{accountId}")
    fun updateAccount(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: UpdateAccountRequest
    ): ResponseEntity<ApiResponse<AccountResponse>> {
        val account = accountService.updateAccount(accountId, request)
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully", account))
    }
    
    @PostMapping("/{accountId}/balance")
    fun updateBalance(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: BalanceUpdateRequest
    ): ResponseEntity<ApiResponse<AccountResponse>> {
        val operation = when (request.operation) {
            "ADD" -> com.avinashpatil.monzobank.service.BalanceOperation.ADD
            "SUBTRACT" -> com.avinashpatil.monzobank.service.BalanceOperation.SUBTRACT
            else -> throw IllegalArgumentException("Invalid operation: ${request.operation}")
        }
        
        val account = accountService.updateAccountBalance(accountId, request.amount, operation)
        return ResponseEntity.ok(ApiResponse.success("Balance updated successfully", account))
    }
    
    @GetMapping("/{accountId}/balance")
    fun getAccountBalance(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<AccountBalanceResponse>> {
        val account = accountService.getAccountById(accountId)
        val balanceResponse = AccountBalanceResponse(
            accountId = account.id,
            balance = account.balance,
            availableBalance = account.balance.add(account.overdraftLimit),
            currency = account.currency,
            lastUpdated = account.createdAt // In real implementation, track last balance update
        )
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved successfully", balanceResponse))
    }
    
    @DeleteMapping("/{accountId}")
    fun closeAccount(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<AccountResponse>> {
        val account = accountService.closeAccount(accountId)
        return ResponseEntity.ok(ApiResponse.success("Account closed successfully", account))
    }
    
    @GetMapping("/{accountId}/limits")
    fun getAccountLimits(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<AccountLimitsResponse>> {
        // In a real implementation, these would be stored in the database
        val limits = AccountLimitsResponse(
            dailyTransferLimit = java.math.BigDecimal("10000.00"),
            monthlyTransferLimit = java.math.BigDecimal("50000.00"),
            overdraftLimit = java.math.BigDecimal("1000.00"),
            dailyWithdrawalLimit = java.math.BigDecimal("500.00"),
            monthlyWithdrawalLimit = java.math.BigDecimal("2000.00")
        )
        return ResponseEntity.ok(ApiResponse.success("Account limits retrieved successfully", limits))
    }
    
    @PutMapping("/{accountId}/limits")
    fun updateAccountLimits(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: UpdateAccountLimitsRequest
    ): ResponseEntity<ApiResponse<AccountLimitsResponse>> {
        // In a real implementation, this would update the limits in the database
        val limits = AccountLimitsResponse(
            dailyTransferLimit = request.dailyTransferLimit ?: java.math.BigDecimal("10000.00"),
            monthlyTransferLimit = request.monthlyTransferLimit ?: java.math.BigDecimal("50000.00"),
            overdraftLimit = request.overdraftLimit ?: java.math.BigDecimal("1000.00"),
            dailyWithdrawalLimit = request.dailyWithdrawalLimit ?: java.math.BigDecimal("500.00"),
            monthlyWithdrawalLimit = request.monthlyWithdrawalLimit ?: java.math.BigDecimal("2000.00")
        )
        return ResponseEntity.ok(ApiResponse.success("Account limits updated successfully", limits))
    }
    
    @GetMapping("/{accountId}/interest")
    fun getAccountInterest(
        @PathVariable accountId: UUID
    ): ResponseEntity<ApiResponse<AccountInterestResponse>> {
        val account = accountService.getAccountById(accountId)
        
        // In a real implementation, calculate accrued interest based on balance and time
        val interestResponse = AccountInterestResponse(
            accountId = account.id,
            interestRate = account.interestRate,
            accruedInterest = account.balance.multiply(account.interestRate).divide(java.math.BigDecimal("12")), // Monthly interest
            lastInterestPayment = null, // Would be tracked in database
            nextInterestPayment = java.time.LocalDateTime.now().plusMonths(1)
        )
        
        return ResponseEntity.ok(ApiResponse.success("Interest information retrieved successfully", interestResponse))
    }
    
    @PostMapping("/{accountId}/statement")
    fun generateStatement(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: AccountStatementRequest
    ): ResponseEntity<ApiResponse<String>> {
        // In a real implementation, this would generate and return a statement
        val statementId = UUID.randomUUID().toString()
        return ResponseEntity.ok(ApiResponse.success("Statement generated successfully", statementId))
    }
    
    @PostMapping("/transfer")
    fun transferFunds(
        @Valid @RequestBody request: TransferRequest
    ): ResponseEntity<ApiResponse<String>> {
        // This would be handled by TransactionService in a real implementation
        // For now, just return a success message
        return ResponseEntity.ok(ApiResponse.success("Transfer initiated successfully", "Transfer ID: ${UUID.randomUUID()}"))