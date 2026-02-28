package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.data.local.dao.TransactionDao
import com.avinashpatil.app.monzobank.data.local.dao.AccountDao
import com.avinashpatil.app.monzobank.data.local.dao.CategorySpending
import com.avinashpatil.app.monzobank.data.local.entity.TransactionEntity
import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.model.TransactionLocation
import com.avinashpatil.app.monzobank.domain.model.Merchant
import com.avinashpatil.app.monzobank.domain.model.Receipt
import com.avinashpatil.app.monzobank.domain.model.RecurringTransactionSummary
import com.avinashpatil.app.monzobank.domain.model.RecurringFrequency
import com.avinashpatil.app.monzobank.domain.model.RecurringPattern
import com.avinashpatil.app.monzobank.domain.repository.TransactionRepository
import com.avinashpatil.app.monzobank.domain.repository.ExportFormat
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao
) : TransactionRepository {
    
    private val gson = Gson()
    
    override suspend fun getTransactionsByAccountId(
        accountId: String,
        limit: Int,
        offset: Int
    ): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getTransactionsByAccountId(accountId)
                .map { entities -> entities.map { it.toDomainModel() } }
            // For now, we'll collect the flow and apply limit/offset
            // In a real implementation, this should be done at the DAO level
            Result.success(emptyList()) // TODO: Implement proper pagination
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransaction(transactionId: String): Result<Transaction?> {
        return try {
            val transaction = transactionDao.getTransactionById(transactionId)?.toDomainModel()
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionsByDateRange(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getTransactionsByDateRange(accountId, startDate, endDate)
                .map { it.toDomainModel() }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionsByCategory(
        accountId: String,
        category: com.avinashpatil.app.monzobank.domain.model.TransactionCategory,
        startDate: Date?,
        endDate: Date?
    ): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getTransactionsByCategory(accountId, category.name)
                .map { it.toDomainModel() }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Add missing methods to match interface
    override suspend fun getTransactionsByUserId(
        userId: String,
        limit: Int,
        offset: Int
    ): Result<List<Transaction>> {
        return try {
            // TODO: Implement proper user-based transaction retrieval
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        reason: String?
    ): Result<Unit> {
        return try {
            transactionDao.updateTransactionStatus(transactionId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processTransaction(transactionId: String): Result<Transaction> {
        return try {
            val transaction = transactionDao.getTransactionById(transactionId)
            if (transaction != null) {
                transactionDao.updateTransactionStatus(transactionId, TransactionStatus.COMPLETED)
                Result.success(transaction.toDomainModel())
            } else {
                Result.failure(Exception("Transaction not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelTransaction(transactionId: String): Result<Unit> {
        return try {
            transactionDao.updateTransactionStatus(transactionId, TransactionStatus.CANCELLED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reverseTransaction(
        transactionId: String,
        reason: String
    ): Result<Transaction> {
        return try {
            val originalTransaction = transactionDao.getTransactionById(transactionId)
            if (originalTransaction != null) {
                // Create reverse transaction
                val reverseTransaction = originalTransaction.copy(
                    id = UUID.randomUUID().toString(),
                    amount = originalTransaction.amount.negate(),
                    description = "Reversal: ${originalTransaction.description}",
                    status = TransactionStatus.COMPLETED,
                    transactionDate = Date()
                )
                transactionDao.insertTransaction(reverseTransaction)
                Result.success(reverseTransaction.toDomainModel())
            } else {
                Result.failure(Exception("Transaction not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchTransactions(
        userId: String,
        query: String,
        filters: com.avinashpatil.app.monzobank.domain.repository.TransactionFilters?
    ): Result<List<Transaction>> {
        return try {
            // TODO: Implement proper search with filters
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSpendingByCategory(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<Map<com.avinashpatil.app.monzobank.domain.model.TransactionCategory, Double>> {
        return try {
            val categorySpending = transactionDao.getSpendingByCategory(accountId, startDate, endDate)
            val result = categorySpending.associate { spending ->
                com.avinashpatil.app.monzobank.domain.model.TransactionCategory.valueOf(spending.category) to spending.total.toDouble()
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Removed getTransferTransactions as it's not in the interface
    
    override suspend fun createTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val entity = transaction.toEntity()
            transactionDao.insertTransaction(entity)
            
            // Update account balance
            updateAccountBalance(transaction)
            
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val entity = transaction.toEntity()
            transactionDao.updateTransaction(entity)
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            val transaction = transactionDao.getTransactionById(transactionId)
            if (transaction != null) {
                transactionDao.deleteTransaction(transaction)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Transaction not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Remove duplicate methods - they are already implemented above
    
    // Add missing abstract methods from interface
    override suspend fun getTransactionsByMerchant(
        accountId: String,
        merchantId: String,
        limit: Int
    ): Result<List<Transaction>> {
        return try {
            // TODO: Implement merchant-based transaction retrieval
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionsByAmount(
        accountId: String,
        minAmount: Double?,
        maxAmount: Double?
    ): Result<List<Transaction>> {
        return try {
            // TODO: Implement amount-based transaction filtering
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionAnalytics(
        accountId: String,
        period: com.avinashpatil.app.monzobank.domain.model.AnalyticsPeriod,
        startDate: Date?,
        endDate: Date?
    ): Result<com.avinashpatil.app.monzobank.domain.model.TransactionAnalytics> {
        return try {
            // TODO: Implement transaction analytics
            Result.failure(Exception("Not implemented"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSpendingByMerchant(
        accountId: String,
        startDate: Date,
        endDate: Date,
        limit: Int
    ): Result<List<com.avinashpatil.app.monzobank.domain.model.MerchantAnalytics>> {
        return try {
            val transactions = transactionDao.getTransactionsByDateRange(accountId, startDate, endDate)
            val merchantSpending = transactions
                .filter { it.merchantName != null && it.amount < BigDecimal.ZERO } // Only expenses
                .groupBy { it.merchantName!! }
                .map { (merchantName, merchantTransactions) ->
                    val totalAmount = merchantTransactions.sumOf { it.amount.abs() }
                    val transactionCount = merchantTransactions.size
                    val averageAmount = if (transactionCount > 0) totalAmount.divide(BigDecimal(transactionCount)) else BigDecimal.ZERO
                    val lastTransactionDate = merchantTransactions.maxByOrNull { it.transactionDate }?.transactionDate ?: Date()
                    
                    com.avinashpatil.app.monzobank.domain.model.MerchantAnalytics(
                        merchantName = merchantName,
                        totalAmount = totalAmount,
                        transactionCount = transactionCount,
                        averageAmount = averageAmount,
                        lastTransactionDate = lastTransactionDate,
                        frequency = com.avinashpatil.app.monzobank.domain.model.TransactionFrequency.REGULAR // Default (monthly)
                    )
                }
                .sortedByDescending { it.totalAmount }
                .take(limit)
            
            Result.success(merchantSpending)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMonthlySpendingTrend(
        accountId: String,
        months: Int
    ): Result<List<com.avinashpatil.app.monzobank.domain.model.MonthlyTrend>> {
        return try {
            val monthlyTrends = mutableListOf<com.avinashpatil.app.monzobank.domain.model.MonthlyTrend>()
            val calendar = Calendar.getInstance()
            
            repeat(months) { monthOffset ->
                calendar.time = Date()
                calendar.add(Calendar.MONTH, -monthOffset)
                
                val startOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                val endOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.time
                
                val transactions = transactionDao.getTransactionsByDateRange(accountId, startOfMonth, endOfMonth)
                val expenses = transactions.filter { it.amount < BigDecimal.ZERO }
                val totalAmount = expenses.sumOf { it.amount.abs() }
                val transactionCount = expenses.size
                
                // Calculate change from previous month
                val changeFromPrevious = if (monthOffset < months - 1 && monthlyTrends.isNotEmpty()) {
                    totalAmount - monthlyTrends.last().totalAmount
                } else {
                    BigDecimal.ZERO
                }
                
                val percentageChange = if (monthOffset < months - 1 && monthlyTrends.isNotEmpty() && monthlyTrends.last().totalAmount > BigDecimal.ZERO) {
                    changeFromPrevious.divide(monthlyTrends.last().totalAmount, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal(100))
                } else {
                    BigDecimal.ZERO
                }
                
                monthlyTrends.add(
                    com.avinashpatil.app.monzobank.domain.model.MonthlyTrend(
                        month = calendar.get(Calendar.MONTH) + 1,
                        year = calendar.get(Calendar.YEAR),
                        totalAmount = totalAmount,
                        transactionCount = transactionCount,
                        changeFromPrevious = changeFromPrevious,
                        percentageChange = percentageChange
                    )
                )
            }
            
            Result.success(monthlyTrends.reversed()) // Return in chronological order
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Removed processTransfer as it's not in the interface
    
    override suspend fun categorizeTransaction(
        transactionId: String,
        category: com.avinashpatil.app.monzobank.domain.model.TransactionCategory,
        subcategory: String?
    ): Result<Unit> {
        return try {
            transactionDao.updateTransactionCategory(transactionId, category.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun attachReceipt(
        transactionId: String,
        receipt: Receipt
    ): Result<Unit> {
        return try {
            val transaction = transactionDao.getTransactionById(transactionId)
            if (transaction != null) {
                // In a real implementation, you would store the receipt in a separate table
                // For now, we'll just store the receipt URL in the transaction
                val updatedTransaction = transaction.copy(receiptUrl = receipt.imageUrl)
                transactionDao.updateTransaction(updatedTransaction)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecurringTransactions(accountId: String): Result<List<RecurringTransactionSummary>> {
        return try {
            // Get all transactions for the account from the last 12 months
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.MONTH, -12)
            val startDate = calendar.time
            
            val transactions = transactionDao.getTransactionsByDateRange(accountId, startDate, endDate)
            
            // Group transactions by merchant and amount to identify recurring patterns
            val recurringPatterns = transactions
                .filter { it.merchantName != null && it.amount < BigDecimal.ZERO } // Only expenses with merchants
                .groupBy { "${it.merchantName}_${it.amount.abs()}" }
                .filter { (_, transactionList) -> transactionList.size >= 3 } // At least 3 occurrences
                .map { (_, transactionList) ->
                    val sortedTransactions = transactionList.sortedBy { it.transactionDate }
                    val firstTransaction = sortedTransactions.first()
                    val lastTransaction = sortedTransactions.last()
                    
                    // Calculate frequency based on transaction intervals
                    val intervals = sortedTransactions.zipWithNext { a, b ->
                        (b.transactionDate.time - a.transactionDate.time) / (1000 * 60 * 60 * 24) // Days
                    }
                    
                    val averageInterval = if (intervals.isNotEmpty()) intervals.average() else 0.0
                    val frequency = when {
                        averageInterval <= 2 -> RecurringFrequency.DAILY
                        averageInterval <= 9 -> RecurringFrequency.WEEKLY
                        averageInterval <= 16 -> RecurringFrequency.BIWEEKLY
                        averageInterval <= 35 -> RecurringFrequency.MONTHLY
                        averageInterval <= 100 -> RecurringFrequency.QUARTERLY
                        averageInterval <= 400 -> RecurringFrequency.YEARLY
                        else -> RecurringFrequency.IRREGULAR
                    }
                    
                    // Calculate confidence based on consistency of intervals
                    val intervalVariance = if (intervals.size > 1) {
                        val mean = intervals.average()
                        intervals.map { (it - mean) * (it - mean) }.average()
                    } else 0.0
                    
                    val confidence = when {
                        intervalVariance <= 1.0 -> 0.95
                        intervalVariance <= 4.0 -> 0.85
                        intervalVariance <= 9.0 -> 0.75
                        intervalVariance <= 16.0 -> 0.65
                        else -> 0.5
                    }
                    
                    // Predict next expected date
                    val nextExpectedDate = Date(lastTransaction.transactionDate.time + (averageInterval * 24 * 60 * 60 * 1000).toLong())
                    
                    val totalAmount = transactionList.sumOf { it.amount.abs() }
                    val averageAmount = totalAmount.divide(BigDecimal(transactionList.size), 2, java.math.RoundingMode.HALF_UP)
                    
                    RecurringTransactionSummary(
                        id = UUID.randomUUID().toString(),
                        accountId = accountId,
                        merchantName = firstTransaction.merchantName,
                        description = firstTransaction.description,
                        amount = firstTransaction.amount.abs(),
                        currency = firstTransaction.currency,
                        category = try {
                            com.avinashpatil.app.monzobank.domain.model.TransactionCategory.valueOf(firstTransaction.category ?: "OTHER")
                        } catch (e: Exception) {
                            com.avinashpatil.app.monzobank.domain.model.TransactionCategory.OTHER
                        },
                        frequency = frequency,
                        nextExpectedDate = nextExpectedDate,
                        lastTransactionDate = lastTransaction.transactionDate,
                        transactionCount = transactionList.size,
                        totalAmount = totalAmount,
                        averageAmount = averageAmount,
                        isActive = (System.currentTimeMillis() - lastTransaction.transactionDate.time) < (averageInterval * 2 * 24 * 60 * 60 * 1000),
                        confidence = confidence,
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                }
                .sortedByDescending { it.confidence }
            
            Result.success(recurringPatterns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Removed disputeTransaction as it has wrong signature - interface expects different parameters
    
    private suspend fun updateAccountBalance(transaction: Transaction) {
        val account = accountDao.getAccountById(transaction.accountId)
        if (account != null) {
            val newBalance = when (transaction.type) {
                TransactionType.DEBIT, TransactionType.TRANSFER_OUT, TransactionType.PAYMENT, TransactionType.FEE -> 
                    account.balance - transaction.amount
                TransactionType.CREDIT, TransactionType.TRANSFER_IN, TransactionType.REFUND, TransactionType.INTEREST -> 
                    account.balance + transaction.amount
                else -> account.balance // For other transaction types, don't change balance
            }
            accountDao.updateBalance(transaction.accountId, newBalance, Date())
        }
    }
    
    // Extension functions for mapping
    private fun TransactionEntity.toDomainModel(): Transaction {
        return Transaction(
            id = id,
            accountId = accountId,
            userId = "", // TODO: Add userId to TransactionEntity
            type = transactionType,
            status = status,
            amount = amount,
            currency = currency,
            description = description,
            reference = reference,
            category = try {
                com.avinashpatil.app.monzobank.domain.model.TransactionCategory.valueOf(category ?: "OTHER")
            } catch (e: Exception) {
                com.avinashpatil.app.monzobank.domain.model.TransactionCategory.OTHER
            },
            merchant = if (merchantName != null) {
                Merchant(
                    id = merchantId ?: UUID.randomUUID().toString(),
                    name = merchantName,
                    category = "General",
                    mcc = "0000"
                )
            } else null,
            location = location?.let { gson.fromJson(it, TransactionLocation::class.java) },
            paymentMethod = com.avinashpatil.app.monzobank.domain.model.PaymentMethodType.CARD, // Default
            balanceAfter = balanceAfter ?: BigDecimal.ZERO,
            runningBalance = balanceAfter ?: BigDecimal.ZERO,
            createdAt = createdAt,
            processedAt = null,
            settledAt = null,
            updatedAt = createdAt,
            merchantName = merchantName,
            isOnlineTransaction = isOnlineTransaction,
            isAtmTransaction = isAtmTransaction,
            isContactlessTransaction = isContactlessTransaction,
            isInternationalTransaction = isInternationalTransaction
        )
    }
    
    // Missing methods from interface
    override suspend fun bulkCategorizeTransactions(
        transactionIds: List<String>,
        category: com.avinashpatil.app.monzobank.domain.model.TransactionCategory,
        subcategory: String?
    ): Result<Unit> {
        return try {
            transactionIds.forEach { transactionId ->
                transactionDao.updateTransactionCategory(transactionId, category.name)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun autoCategorizePendingTransactions(accountId: String): Result<Int> {
        return try {
            // TODO: Implement auto-categorization logic
            Result.success(0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun suggestCategory(transaction: Transaction): Result<List<com.avinashpatil.app.monzobank.domain.repository.CategorySuggestion>> {
        return try {
            // TODO: Implement category suggestion logic
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTransactionNote(transactionId: String, note: String): Result<Unit> {
        return try {
            // TODO: Implement note functionality
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransactionNote(transactionId: String, note: String): Result<Unit> {
        return try {
            // TODO: Implement note functionality
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeTransactionNote(transactionId: String): Result<Unit> {
        return try {
            // TODO: Implement note functionality
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTransactionTags(transactionId: String, tags: List<String>): Result<Unit> {
        return try {
            // TODO: Implement tags functionality
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeTransactionTags(transactionId: String, tags: List<String>): Result<Unit> {
        return try {
            // TODO: Implement tags functionality
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionReceipt(transactionId: String): Result<Receipt?> {
        return try {
            // TODO: Implement receipt retrieval
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReceipt(transactionId: String, receipt: Receipt): Result<Unit> {
        return try {
            // TODO: Implement receipt update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeReceipt(transactionId: String): Result<Unit> {
        return try {
            // TODO: Implement receipt removal
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disputeTransaction(
        transactionId: String,
        reason: com.avinashpatil.app.monzobank.domain.repository.DisputeReason,
        description: String,
        evidence: List<String>
    ): Result<String> {
        return try {
            val disputeId = UUID.randomUUID().toString()
            // TODO: Implement dispute creation
            Result.success(disputeId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionDispute(transactionId: String): Result<com.avinashpatil.app.monzobank.domain.repository.TransactionDispute?> {
        return try {
            // TODO: Implement dispute retrieval
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDisputeStatus(
        disputeId: String,
        status: com.avinashpatil.app.monzobank.domain.repository.DisputeStatus,
        resolution: String?
    ): Result<Unit> {
        return try {
            // TODO: Implement dispute status update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun splitTransaction(
        transactionId: String,
        splits: List<com.avinashpatil.app.monzobank.domain.repository.TransactionSplit>
    ): Result<List<Transaction>> {
        return try {
            // TODO: Implement transaction splitting
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSplitTransactions(parentTransactionId: String): Result<List<Transaction>> {
        return try {
            // TODO: Implement split transaction retrieval
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun mergeSplitTransactions(splitTransactionIds: List<String>): Result<Transaction> {
        return try {
            // TODO: Implement transaction merging
            Result.failure(Exception("Not implemented"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRecurringTransaction(
        transaction: Transaction,
        pattern: RecurringPattern
    ): Result<String> {
        return try {
            val recurringId = UUID.randomUUID().toString()
            // TODO: Implement recurring transaction creation
            Result.success(recurringId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecurringTransaction(
        recurringId: String,
        pattern: RecurringPattern
    ): Result<Unit> {
        return try {
            // TODO: Implement recurring transaction update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pauseRecurringTransaction(recurringId: String): Result<Unit> {
        return try {
            // TODO: Implement recurring transaction pause
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resumeRecurringTransaction(recurringId: String): Result<Unit> {
        return try {
            // TODO: Implement recurring transaction resume
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelRecurringTransaction(recurringId: String): Result<Unit> {
        return try {
            // TODO: Implement recurring transaction cancellation
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNextRecurringTransactions(
        accountId: String,
        days: Int
    ): Result<List<Transaction>> {
        return try {
            // TODO: Implement next recurring transactions retrieval
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportTransactions(
        accountId: String,
        startDate: Date,
        endDate: Date,
        format: ExportFormat,
        categories: List<com.avinashpatil.app.monzobank.domain.model.TransactionCategory>?
    ): Result<String> {
        return try {
            // TODO: Implement transaction export
            Result.success("export_file_path")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportTransactionsByCategory(
        accountId: String,
        category: com.avinashpatil.app.monzobank.domain.model.TransactionCategory,
        startDate: Date,
        endDate: Date,
        format: ExportFormat
    ): Result<String> {
        return try {
            // TODO: Implement category-based export
            Result.success("export_file_path")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTransactionsByAccountId(accountId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByAccountId(accountId)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun observeTransaction(transactionId: String): Flow<Transaction?> {
        return transactionDao.observeTransaction(transactionId)
            .map { it?.toDomainModel() }
    }

    override fun observePendingTransactions(accountId: String): Flow<List<Transaction>> {
        return transactionDao.getPendingTransactions(accountId)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun observeRecentTransactions(accountId: String, limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(accountId, limit)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun validateTransaction(transaction: Transaction): Result<com.avinashpatil.app.monzobank.domain.repository.TransactionValidationResult> {
        return try {
            // TODO: Implement transaction validation
            val result = com.avinashpatil.app.monzobank.domain.repository.TransactionValidationResult(
                isValid = true,
                errors = emptyList(),
                warnings = emptyList()
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkDuplicateTransaction(transaction: Transaction): Result<List<Transaction>> {
        return try {
            // TODO: Implement duplicate check
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyTransactionIntegrity(transactionId: String): Result<Boolean> {
        return try {
            // TODO: Implement integrity verification
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncTransactionsWithBank(
        accountId: String,
        startDate: Date?
    ): Result<Int> {
        return try {
            // TODO: Implement bank synchronization
            Result.success(0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reconcileTransactions(
        accountId: String,
        bankTransactions: List<com.avinashpatil.app.monzobank.domain.repository.BankTransaction>
    ): Result<com.avinashpatil.app.monzobank.domain.repository.ReconciliationResult> {
        return try {
            // TODO: Implement reconciliation
            val result = com.avinashpatil.app.monzobank.domain.repository.ReconciliationResult(
                matchedTransactions = 0,
                newTransactions = 0,
                discrepancies = emptyList(),
                totalProcessed = 0
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enableTransactionNotifications(
        accountId: String,
        settings: com.avinashpatil.app.monzobank.domain.repository.TransactionNotificationSettings
    ): Result<Unit> {
        return try {
            // TODO: Implement notification settings
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disableTransactionNotifications(accountId: String): Result<Unit> {
        return try {
            // TODO: Implement notification disable
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bulkUpdateTransactions(
        transactionIds: List<String>,
        updates: com.avinashpatil.app.monzobank.domain.repository.TransactionBulkUpdate
    ): Result<Int> {
        return try {
            // TODO: Implement bulk update
            Result.success(transactionIds.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bulkDeleteTransactions(transactionIds: List<String>): Result<Int> {
        return try {
            // TODO: Implement bulk delete
            Result.success(transactionIds.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id.ifEmpty { UUID.randomUUID().toString() },
            accountId = accountId,
            amount = amount,
            currency = currency,
            description = description,
            category = category.name,
            merchantName = merchantName ?: merchant?.name,
            merchantId = merchant?.id,
            location = location?.let { gson.toJson(it) },
            status = status,
            transactionDate = createdAt,
            createdAt = createdAt,
            reference = reference,
            transactionType = type,
            balanceAfter = balanceAfter,
            isOnlineTransaction = isOnlineTransaction,
            isAtmTransaction = isAtmTransaction,
            isContactlessTransaction = isContactlessTransaction,
            isInternationalTransaction = isInternationalTransaction
        )
    }
}