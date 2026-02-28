package com.avinashpatil.app.monzobank.domain.usecase.payment

import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.model.PaymentRequest
import com.avinashpatil.app.monzobank.domain.model.PaymentRequestStatus
import com.avinashpatil.app.monzobank.domain.model.PaymentRequestType
import com.avinashpatil.app.monzobank.domain.model.PaymentRequestAction
import com.avinashpatil.app.monzobank.domain.model.TransferStatus
import com.avinashpatil.app.monzobank.domain.repository.AccountRepository
import com.avinashpatil.app.monzobank.domain.repository.TransactionRepository
import com.avinashpatil.app.monzobank.utils.SecurityUtils
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import java.time.LocalDateTime
import javax.inject.Inject

data class FraudCheckResult(
    val isSuspicious: Boolean,
    val reason: String?
)

class P2PTransferUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val securityUtils: SecurityUtils
) {
    suspend fun transferMoney(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal,
        description: String,
        reference: String? = null
    ): Result<P2PTransferResult> {
        return try {
            // Validate transfer
            val validationResult = validateTransfer(fromAccountId, toAccountId, amount)
            if (validationResult.isFailure) {
                return Result.failure(validationResult.exceptionOrNull()!!)
            }
            
            val (fromAccount, toAccount) = validationResult.getOrThrow()
            
            // Check for fraud - Mock implementation
            val fraudCheck = FraudCheckResult(isSuspicious = false, reason = null)
            
            if (fraudCheck.isSuspicious) {
                return Result.failure(Exception("Transaction flagged for review: ${fraudCheck.reason}"))
            }
            
            // Create transaction records
            val transferId = UUID.randomUUID().toString()
            val timestamp = Date()
            
            // Debit transaction for sender
            val debitTransaction = Transaction(
                id = UUID.randomUUID().toString(),
                accountId = fromAccountId,
                userId = fromAccount.userId,
                type = com.avinashpatil.app.monzobank.data.local.entity.TransactionType.DEBIT,
                status = com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus.COMPLETED,
                amount = amount.negate(),
                description = description,
                category = com.avinashpatil.app.monzobank.domain.model.TransactionCategory.TRANSFERS,
                paymentMethod = com.avinashpatil.app.monzobank.domain.model.PaymentMethodType.CARD,
                balanceAfter = fromAccount.balance - amount,
                runningBalance = fromAccount.balance - amount,
                createdAt = timestamp,
                updatedAt = timestamp,
                reference = reference
            )
            
            // Credit transaction for recipient
            val creditTransaction = Transaction(
                id = UUID.randomUUID().toString(),
                accountId = toAccountId,
                userId = toAccount.userId,
                type = com.avinashpatil.app.monzobank.data.local.entity.TransactionType.CREDIT,
                status = com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus.COMPLETED,
                amount = amount,
                description = description,
                category = com.avinashpatil.app.monzobank.domain.model.TransactionCategory.TRANSFERS,
                paymentMethod = com.avinashpatil.app.monzobank.domain.model.PaymentMethodType.CARD,
                balanceAfter = toAccount.balance + amount,
                runningBalance = toAccount.balance + amount,
                createdAt = timestamp,
                updatedAt = timestamp,
                reference = reference
            )
            
            // Execute transfer (atomic operation)
            val transferResult = executeTransfer(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount,
                debitTransaction = debitTransaction,
                creditTransaction = creditTransaction
            )
            
            if (transferResult.isSuccess) {
                Result.success(
                    P2PTransferResult(
                        transferId = transferId,
                        fromAccount = fromAccount,
                        toAccount = toAccount,
                        amount = amount,
                        description = description,
                        timestamp = timestamp,
                        status = TransferStatus.COMPLETED,
                        debitTransactionId = debitTransaction.id,
                        creditTransactionId = creditTransaction.id
                    )
                )
            } else {
                Result.failure(transferResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun transferToContact(
        fromAccountId: String,
        contactPhoneNumber: String,
        amount: BigDecimal,
        description: String,
        reference: String? = null
    ): Result<P2PTransferResult> {
        return try {
            // Find account by phone number - Mock implementation
            // In real implementation, this would search for account by phone number
            return Result.failure(Exception("Phone number lookup not implemented in mock"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun requestMoney(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal,
        description: String,
        expiryDate: Date? = null
    ): Result<PaymentRequest> {
        return try {
            val fromAccountResult = accountRepository.getAccountById(fromAccountId)
            val toAccountResult = accountRepository.getAccountById(toAccountId)
            
            if (fromAccountResult.isFailure) {
                return Result.failure(Exception("Sender account not found"))
            }
            
            if (toAccountResult.isFailure) {
                return Result.failure(Exception("Recipient account not found"))
            }
            
            val fromAccount = fromAccountResult.getOrNull() ?: return Result.failure(Exception("Sender account not found"))
            val toAccount = toAccountResult.getOrNull() ?: return Result.failure(Exception("Recipient account not found"))
            
            val paymentRequest = PaymentRequest(
                id = UUID.randomUUID().toString(),
                requesterId = fromAccount.userId,
                requesterName = "Account ${fromAccount.accountNumber}",
                requesterEmail = null,
                requesterPhone = null,
                payerId = toAccount.userId,
                payerName = "Account ${toAccount.accountNumber}",
                payerEmail = null,
                payerPhone = null,
                amount = amount,
                description = description,
                reference = null,
                category = "TRANSFER",
                status = PaymentRequestStatus.PENDING,
                type = PaymentRequestType.PERSONAL,
                dueDate = null,
                expiryDate = expiryDate?.let { java.time.LocalDateTime.ofInstant(it.toInstant(), java.time.ZoneId.systemDefault()) } ?: java.time.LocalDateTime.now().plusDays(7),
                reminderFrequency = null,
                lastReminderSent = null,
                nextReminderDate = null,
                paymentId = null,
                paymentDate = null,
                notes = null,
                publicNotes = null,
                privateNotes = null,
                recurringSchedule = null,
                recurringEndDate = null,
                parentRequestId = null,
                groupId = null,
                acceptedAt = null,
                rejectedAt = null,
                cancelledAt = null,
                fulfilledAt = null
            )
            
            // Save payment request - Mock implementation
            // In real implementation, this would save to database
            
            Result.success(paymentRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun respondToPaymentRequest(
        requestId: String,
        response: PaymentRequestAction
    ): Result<P2PTransferResult?> {
        return try {
            // Mock implementation - in real app would fetch from database
            return Result.failure(Exception("Payment request operations not implemented in mock"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun validateTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal
    ): Result<Pair<Account, Account>> {
        // Basic validation
        if (amount <= BigDecimal.ZERO) {
            return Result.failure(Exception("Transfer amount must be greater than zero"))
        }
        
        if (fromAccountId == toAccountId) {
            return Result.failure(Exception("Cannot transfer to the same account"))
        }
        
        // Get accounts
        val fromAccountResult = accountRepository.getAccountById(fromAccountId)
        val toAccountResult = accountRepository.getAccountById(toAccountId)
        
        if (fromAccountResult.isFailure) {
            return Result.failure(Exception("Sender account not found"))
        }
        
        if (toAccountResult.isFailure) {
            return Result.failure(Exception("Recipient account not found"))
        }
        
        val fromAccount = fromAccountResult.getOrNull() ?: return Result.failure(Exception("Sender account not found"))
        val toAccount = toAccountResult.getOrNull() ?: return Result.failure(Exception("Recipient account not found"))
        
        // Check account status
        if (fromAccount.status != com.avinashpatil.app.monzobank.data.local.entity.AccountStatus.ACTIVE) {
            return Result.failure(Exception("Sender account is not active"))
        }
        
        if (toAccount.status != com.avinashpatil.app.monzobank.data.local.entity.AccountStatus.ACTIVE) {
            return Result.failure(Exception("Recipient account is not active"))
        }
        
        // Check balance
        if (fromAccount.balance < amount) {
            return Result.failure(Exception("Insufficient funds"))
        }
        
        // Check daily transfer limits - Mock implementation
        val dailyLimit = BigDecimal("10000") // £10,000 daily limit
        
        // In real implementation, would check today's transfer amount
        // For now, assume within limits
        
        return Result.success(Pair(fromAccount, toAccount))
    }
    
    private suspend fun executeTransfer(
        fromAccount: Account,
        toAccount: Account,
        amount: BigDecimal,
        debitTransaction: Transaction,
        creditTransaction: Transaction
    ): Result<Unit> {
        return try {
            // Update account balances
            val newFromBalance = fromAccount.balance - amount
            val newToBalance = toAccount.balance + amount
            
            accountRepository.updateBalance(fromAccount.id, newFromBalance.toDouble(), debitTransaction.id)
            accountRepository.updateBalance(toAccount.id, newToBalance.toDouble(), creditTransaction.id)
            
            // Record transactions
            transactionRepository.createTransaction(debitTransaction)
            transactionRepository.createTransaction(creditTransaction)
            
            Result.success(Unit)
        } catch (e: Exception) {
            // In a real app, this would trigger a rollback mechanism
            Result.failure(e)
        }
    }
}

data class P2PTransferResult(
    val transferId: String,
    val fromAccount: Account,
    val toAccount: Account,
    val amount: BigDecimal,
    val description: String,
    val timestamp: Date,
    val status: TransferStatus,
    val debitTransactionId: String,
    val creditTransactionId: String
)