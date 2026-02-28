package com.avinashpatil.app.monzobank.domain.usecase.transaction

import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.domain.model.TransactionCategory
import com.avinashpatil.app.monzobank.domain.model.PaymentMethodType
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.repository.TransactionRepository
import com.avinashpatil.app.monzobank.domain.usecase.account.UpdateAccountBalanceUseCase
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CreateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase
) {
    suspend operator fun invoke(
        accountId: String,
        amount: BigDecimal,
        transactionType: TransactionType,
        description: String,
        merchantName: String? = null,
        category: String? = null,
        location: com.avinashpatil.app.monzobank.domain.model.TransactionLocation? = null,
        reference: String? = null
    ): Result<Transaction> {
        return try {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                accountId = accountId,
                cardId = null,
                userId = "", // This should be passed as parameter
                type = transactionType,
                status = TransactionStatus.PENDING,
                amount = amount,
                currency = "GBP",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null,
                description = description,
                reference = reference,
                category = categorizeTransactionCategory(description, merchantName),
                subcategory = null,
                merchant = null,
                location = location,
                paymentMethod = PaymentMethodType.CARD,
                balanceAfter = BigDecimal.ZERO,
                runningBalance = BigDecimal.ZERO,
                fees = emptyList(),
                tags = emptyList(),
                notes = null,
                receipt = null,
                isRecurring = false,
                recurringPattern = null,
                parentTransactionId = null,
                childTransactionIds = emptyList(),
                disputeId = null,
                isDisputed = false,
                createdAt = Date(),
                processedAt = null,
                settledAt = null,
                updatedAt = Date(),
                metadata = emptyMap()
            )
            
            // Create transaction first
            val createdTransactionResult = transactionRepository.createTransaction(transaction)
            
            if (createdTransactionResult.isFailure) {
                return Result.failure(createdTransactionResult.exceptionOrNull() ?: Exception("Failed to create transaction"))
            }
            
            val createdTransaction = createdTransactionResult.getOrThrow()
            
            // Update account balance
            val isDebit = transactionType == TransactionType.WITHDRAWAL
            val balanceUpdateResult = updateAccountBalanceUseCase(accountId, amount, isDebit)
            
            if (balanceUpdateResult.isSuccess) {
                // Update transaction status to completed
                transactionRepository.updateTransactionStatus(transaction.id, TransactionStatus.COMPLETED)
                Result.success(createdTransaction.copy(status = TransactionStatus.COMPLETED))
            } else {
                // Update transaction status to failed
                transactionRepository.updateTransactionStatus(transaction.id, TransactionStatus.FAILED)
                Result.failure(balanceUpdateResult.exceptionOrNull() ?: Exception("Balance update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun categorizeTransactionCategory(description: String, merchantName: String?): TransactionCategory {
        val text = "$description ${merchantName ?: ""}".lowercase()
        
        return when {
            text.contains("grocery") || text.contains("supermarket") || text.contains("tesco") || 
            text.contains("sainsbury") || text.contains("asda") || text.contains("morrisons") -> TransactionCategory.GROCERIES
            
            text.contains("restaurant") || text.contains("cafe") || text.contains("pizza") || 
            text.contains("mcdonald") || text.contains("kfc") || text.contains("subway") -> TransactionCategory.RESTAURANTS
            
            text.contains("fuel") || text.contains("petrol") || text.contains("shell") || 
            text.contains("bp") || text.contains("esso") -> TransactionCategory.TRANSPORT
            
            text.contains("amazon") || text.contains("ebay") || text.contains("shopping") || 
            text.contains("retail") -> TransactionCategory.SHOPPING
            
            text.contains("cinema") || text.contains("netflix") || text.contains("spotify") || 
            text.contains("entertainment") -> TransactionCategory.ENTERTAINMENT
            
            text.contains("pharmacy") || text.contains("hospital") || text.contains("doctor") || 
            text.contains("medical") -> TransactionCategory.HEALTHCARE
            
            text.contains("salary") || text.contains("wage") || text.contains("payroll") -> TransactionCategory.SALARY
            
            text.contains("transfer") -> TransactionCategory.TRANSFERS
            
            else -> TransactionCategory.OTHER
        }
    }
}