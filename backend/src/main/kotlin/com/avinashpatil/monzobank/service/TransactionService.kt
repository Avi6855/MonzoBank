package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.*
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.AccountRepository
import com.avinashpatil.monzobank.repository.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val emailService: EmailService,
    private val smsService: SmsService
) {
    
    private val logger = LoggerFactory.getLogger(TransactionService::class.java)
    
    fun createTransaction(request: CreateTransactionRequest): TransactionResponse {
        logger.info("Creating transaction for account: ${request.accountId}")
        
        val account = accountRepository.findById(request.accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: ${request.accountId}") }
        
        // Validate transaction amount
        if (request.amount <= BigDecimal.ZERO) {
            throw InvalidTransactionException("Transaction amount must be positive")
        }
        
        // Check for sufficient funds for debit transactions
        if (request.type == TransactionType.DEBIT) {
            val availableBalance = account.balance.add(account.overdraftLimit)
            if (request.amount > availableBalance) {
                throw InsufficientFundsException("Insufficient funds for transaction")
            }
        }
        
        val transaction = Transaction(
            id = UUID.randomUUID(),
            account = account,
            amount = request.amount,
            currency = account.currency,
            transactionType = request.type,
            status = TransactionStatus.PENDING,
            description = request.description,
            category = request.category,
            referenceNumber = generateTransactionReference(),
            externalTransactionId = request.externalId,
            merchantName = request.merchantName,
            location = request.location,
            transactionDate = LocalDateTime.now()
        )
        
        val savedTransaction = transactionRepository.save(transaction)
        
        // Process the transaction
        processTransaction(savedTransaction)
        
        logger.info("Transaction created successfully: ${savedTransaction.id}")
        return mapToTransactionResponse(savedTransaction)
    }
    
    @Transactional(readOnly = true)
    fun getTransactionById(transactionId: UUID): TransactionResponse {
        val transaction = transactionRepository.findById(transactionId)
            .orElseThrow { InvalidTransactionException("Transaction not found with ID: $transactionId") }
        
        return mapToTransactionResponse(transaction)
    }
    
    @Transactional(readOnly = true)
    fun getTransactionsByAccountId(accountId: UUID, pageable: Pageable): Page<TransactionResponse> {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: $accountId") }
        
        return transactionRepository.findByAccount(account, pageable)
            .map { mapToTransactionResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun getTransactionsByUserId(userId: UUID, pageable: Pageable): Page<TransactionResponse> {
        return transactionRepository.findByUserIdOrderByDateDesc(userId, pageable)
            .map { mapToTransactionResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun getTransactionsByDateRange(
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<TransactionResponse> {
        val transactions = transactionRepository.findByAccountAndDateRange(accountId, startDate, endDate)
        return org.springframework.data.domain.PageImpl(
            transactions.map { mapToTransactionResponse(it) },
            pageable,
            transactions.size.toLong()
        )
    }
    
    @Transactional(readOnly = true)
    fun getTransactionsByCategory(
        accountId: UUID,
        category: String,
        pageable: Pageable
    ): Page<TransactionResponse> {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: $accountId") }
        return transactionRepository.findByAccountAndCategory(accountId, category)
            .map { mapToTransactionResponse(it) }
            .let { org.springframework.data.domain.PageImpl(it, pageable, it.size.toLong()) }
    }
    
    @Transactional(readOnly = true)
    fun getTransactionsByStatus(
        accountId: UUID,
        status: TransactionStatus,
        pageable: Pageable
    ): Page<TransactionResponse> {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: $accountId") }
        return transactionRepository.findByAccountAndStatus(account, status)
            .map { mapToTransactionResponse(it) }
            .let { org.springframework.data.domain.PageImpl(it, pageable, it.size.toLong()) }
    }
    
    fun updateTransactionStatus(transactionId: UUID, status: TransactionStatus): TransactionResponse {
        val transaction = transactionRepository.findById(transactionId)
            .orElseThrow { InvalidTransactionException("Transaction not found with ID: $transactionId") }
        
        val updatedTransaction = transaction.copy(status = status)
        val savedTransaction = transactionRepository.save(updatedTransaction)
        
        // Send notification for status changes
        if (status == TransactionStatus.COMPLETED || status == TransactionStatus.FAILED) {
            sendTransactionNotification(savedTransaction)
        }
        
        return mapToTransactionResponse(savedTransaction)
    }
    
    fun transferFunds(request: TransferRequest): TransferResponse {
        logger.info("Processing transfer from ${request.fromAccountId} to ${request.toAccountId}")
        
        val fromAccount = accountRepository.findById(request.fromAccountId)
            .orElseThrow { AccountNotFoundException("Source account not found") }
        
        val toAccount = accountRepository.findById(request.toAccountId)
            .orElseThrow { AccountNotFoundException("Destination account not found") }
        
        // Validate transfer amount
        if (request.amount <= BigDecimal.ZERO) {
            throw InvalidTransactionException("Transfer amount must be positive")
        }
        
        // Check sufficient funds
        val availableBalance = fromAccount.balance.add(fromAccount.overdraftLimit)
        if (request.amount > availableBalance) {
            throw InsufficientFundsException("Insufficient funds for transfer")
        }
        
        val transferId = UUID.randomUUID()
        val reference = "TXF${System.currentTimeMillis()}"
        
        // Create debit transaction for source account
        val debitTransaction = Transaction(
            id = UUID.randomUUID(),
            account = fromAccount,
            amount = request.amount,
            currency = fromAccount.currency,
            transactionType = TransactionType.DEBIT,
            status = TransactionStatus.PENDING,
            description = "Transfer to ${toAccount.accountNumber}",
            category = "Transfer",
            referenceNumber = reference,
            externalTransactionId = transferId.toString(),
            transactionDate = LocalDateTime.now()
        )
        
        // Create credit transaction for destination account
        val creditTransaction = Transaction(
            id = UUID.randomUUID(),
            account = toAccount,
            amount = request.amount,
            currency = toAccount.currency,
            transactionType = TransactionType.CREDIT,
            status = TransactionStatus.PENDING,
            description = "Transfer from ${fromAccount.accountNumber}",
            category = "Transfer",
            referenceNumber = reference,
            externalTransactionId = transferId.toString(),
            transactionDate = LocalDateTime.now()
        )
        
        // Save transactions
        val savedDebitTransaction = transactionRepository.save(debitTransaction)
        val savedCreditTransaction = transactionRepository.save(creditTransaction)
        
        // Update account balances
        val updatedFromAccount = fromAccount.copy(balance = fromAccount.balance.subtract(request.amount))
        val updatedToAccount = toAccount.copy(balance = toAccount.balance.add(request.amount))
        
        accountRepository.save(updatedFromAccount)
        accountRepository.save(updatedToAccount)
        
        // Update transaction statuses
        val completedDebitTransaction = savedDebitTransaction.copy(status = TransactionStatus.COMPLETED)
        val completedCreditTransaction = savedCreditTransaction.copy(status = TransactionStatus.COMPLETED)
        
        transactionRepository.save(completedDebitTransaction)
        transactionRepository.save(completedCreditTransaction)
        
        logger.info("Transfer completed successfully: $transferId")
        
        return TransferResponse(
            transferId = transferId,
            fromAccountId = request.fromAccountId,
            toAccountId = request.toAccountId,
            amount = request.amount,
            status = "COMPLETED",
            reference = reference,
            createdAt = LocalDateTime.now()
        )
    }
    
    @Transactional(readOnly = true)
    fun getAccountBalance(accountId: UUID): AccountBalanceResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: $accountId") }
        
        return AccountBalanceResponse(
            accountId = account.id,
            balance = account.balance,
            availableBalance = account.balance.add(account.overdraftLimit),
            currency = account.currency,
            lastUpdated = account.createdAt // Use createdAt since updatedAt doesn't exist
        )
    }
    
    @Transactional(readOnly = true)
    fun getSpendingByCategory(accountId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<SpendingByCategoryResponse> {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: $accountId") }
        
        return transactionRepository.getSpendingByCategory(account.user.id, startDate, endDate)
            .map { result: Array<Any> ->
                SpendingByCategoryResponse(
                    category = result[0] as String? ?: "Other",
                    totalAmount = result[1] as BigDecimal,
                    transactionCount = 0L // Repository doesn't return count, set to 0
                )
            }
    }
    
    private fun processTransaction(transaction: Transaction) {
        try {
            // Update account balance
            val account = transaction.account
            val updatedAccount = when (transaction.transactionType) {
                TransactionType.CREDIT -> {
                    account.copy(balance = account.balance.add(transaction.amount))
                }
                TransactionType.DEBIT -> {
                    account.copy(balance = account.balance.subtract(transaction.amount))
                }
                else -> {
                    account // No balance change for other transaction types
                }
            }
            
            accountRepository.save(updatedAccount)
            
            // Update transaction status - create new transaction with updated status
            val updatedTransaction = transaction.copy(status = TransactionStatus.COMPLETED)
            transactionRepository.save(updatedTransaction)
            
            // Send notification
            sendTransactionNotification(transaction)
            
        } catch (e: Exception) {
            logger.error("Failed to process transaction: ${transaction.id}", e)
            val failedTransaction = transaction.copy(status = TransactionStatus.FAILED)
            transactionRepository.save(failedTransaction)
            throw e
        }
    }
    
    private fun sendTransactionNotification(transaction: Transaction) {
        try {
            val user = transaction.account.user
            val message = "Transaction ${transaction.transactionType.name.lowercase()} of ${transaction.amount} ${transaction.currency} ${if (transaction.status == TransactionStatus.COMPLETED) "completed" else "failed"}"
            
            // Send email notification
            val transactionDetails = "${transaction.transactionType.name} of ${transaction.amount} ${transaction.currency} - Status: ${transaction.status.name}"
            emailService.sendTransactionNotification(
                user.email,
                transactionDetails
            )
            
            // Send SMS notification for large transactions
            if (transaction.amount > BigDecimal("1000")) {
                smsService.sendTransactionAlert(
                    user.phone,
                    "${transaction.amount} ${transaction.currency}",
                    transaction.merchantName ?: "Unknown Merchant"
                )
            }
            
        } catch (e: Exception) {
            logger.error("Failed to send transaction notification", e)
            // Don't fail the transaction if notification fails
        }
    }
    
    private fun generateTransactionReference(): String {
        return "TXN${System.currentTimeMillis()}${(1000..9999).random()}"
    }
    
    private fun mapToTransactionResponse(transaction: Transaction): TransactionResponse {
        return TransactionResponse(
            id = transaction.id,
            accountId = transaction.account.id,
            amount = transaction.amount,
            type = transaction.transactionType,
            status = transaction.status,
            description = transaction.description,
            category = transaction.category ?: "Other",
            reference = transaction.referenceNumber ?: "",
            externalId = transaction.externalTransactionId,
            merchantName = transaction.merchantName,
            merchantCategory = "General", // Default value since not in entity
            location = transaction.location,
            createdAt = transaction.createdAt,
            updatedAt = transaction.createdAt // Use createdAt as updatedAt since entity doesn't have updatedAt
        )
    }
}