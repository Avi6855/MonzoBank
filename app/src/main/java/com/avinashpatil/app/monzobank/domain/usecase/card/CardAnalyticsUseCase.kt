package com.avinashpatil.app.monzobank.domain.usecase.card

import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import com.avinashpatil.app.monzobank.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class CardAnalyticsUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun getCardSpendingInsights(
        cardId: String,
        period: AnalyticsPeriod = AnalyticsPeriod.CURRENT_MONTH
    ): Result<CardSpendingInsights> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            val (startDate, endDate) = getPeriodDates(period)
            val transactions = transactionRepository.getTransactionsByDateRange(
                accountId = card.accountId,
                startDate = startDate,
                endDate = endDate
            ).getOrElse { emptyList() }
            
            val insights = calculateSpendingInsights(card, transactions, period)
            Result.success(insights)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCardUsageStats(
        cardId: String,
        period: AnalyticsPeriod = AnalyticsPeriod.CURRENT_MONTH
    ): Result<CardUsageStats> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            val (startDate, endDate) = getPeriodDates(period)
            val transactions = transactionRepository.getTransactionsByDateRange(
                accountId = card.accountId,
                startDate = startDate,
                endDate = endDate
            ).getOrElse { emptyList() }
            
            val stats = calculateUsageStats(card, transactions, period)
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSpendingByCategory(
        cardId: String,
        period: AnalyticsPeriod = AnalyticsPeriod.CURRENT_MONTH
    ): Result<List<CategorySpending>> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            val (startDate, endDate) = getPeriodDates(period)
            val transactions = transactionRepository.getTransactionsByDateRange(
                accountId = card.accountId,
                startDate = startDate,
                endDate = endDate
            ).getOrElse { emptyList() }
            
            val categorySpending = transactions
                .filter { it.amount < BigDecimal.ZERO } // Only spending transactions
                .groupBy { it.category }
                .map { (category, categoryTransactions) ->
                    CategorySpending(
                        category = category.name,
                        amount = categoryTransactions.sumOf { it.amount.abs() },
                        transactionCount = categoryTransactions.size,
                        percentage = 0.0 // Will be calculated below
                    )
                }
                .sortedByDescending { it.amount }
            
            // Calculate percentages
            val totalSpending = categorySpending.sumOf { it.amount }
            val categorySpendingWithPercentages = categorySpending.map { spending ->
                spending.copy(
                    percentage = if (totalSpending > BigDecimal.ZERO) {
                        (spending.amount.divide(totalSpending, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(BigDecimal("100"))).toDouble()
                    } else 0.0
                )
            }
            
            Result.success(categorySpendingWithPercentages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMonthlySpendingTrend(
        cardId: String,
        months: Int = 6
    ): Result<List<MonthlySpending>> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            val monthlySpending = mutableListOf<MonthlySpending>()
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
                
                val transactions = transactionRepository.getTransactionsByDateRange(
                    accountId = card.accountId,
                    startDate = startOfMonth,
                    endDate = endOfMonth
                ).getOrElse { emptyList() }
                
                val spending = transactions
                    .filter { it.amount < BigDecimal.ZERO }
                    .sumOf { it.amount.abs() }
                
                monthlySpending.add(
                    MonthlySpending(
                        month = calendar.get(Calendar.MONTH),
                        year = calendar.get(Calendar.YEAR),
                        amount = spending,
                        transactionCount = transactions.size
                    )
                )
            }
            
            Result.success(monthlySpending.reversed()) // Most recent first
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateSpendingInsights(
        card: Card,
        transactions: List<Transaction>,
        period: AnalyticsPeriod
    ): CardSpendingInsights {
        val spendingTransactions = transactions.filter { it.amount < BigDecimal.ZERO }
        val totalSpent = spendingTransactions.sumOf { it.amount.abs() }
        val averageTransaction = if (spendingTransactions.isNotEmpty()) {
            totalSpent.divide(BigDecimal(spendingTransactions.size), 2, BigDecimal.ROUND_HALF_UP)
        } else BigDecimal.ZERO
        
        val dailyLimitUsage = (totalSpent.divide(card.dailyLimit, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(BigDecimal("100"))).toDouble()
        
        val monthlyLimitUsage = (totalSpent.divide(card.monthlyLimit, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(BigDecimal("100"))).toDouble()
        
        return CardSpendingInsights(
            cardId = card.id,
            period = period,
            totalSpent = totalSpent,
            transactionCount = spendingTransactions.size,
            averageTransactionAmount = averageTransaction,
            dailyLimitUsagePercentage = dailyLimitUsage,
            monthlyLimitUsagePercentage = monthlyLimitUsage,
            topMerchant = findTopMerchant(spendingTransactions),
            topCategory = findTopCategory(spendingTransactions)
        )
    }
    
    private fun calculateUsageStats(
        card: Card,
        transactions: List<Transaction>,
        period: AnalyticsPeriod
    ): CardUsageStats {
        val onlineTransactions = transactions.filter { it.isOnlineTransaction }
        val atmTransactions = transactions.filter { it.isAtmTransaction }
        val contactlessTransactions = transactions.filter { it.isContactlessTransaction }
        val internationalTransactions = transactions.filter { it.isInternationalTransaction }
        
        return CardUsageStats(
            cardId = card.id,
            period = period,
            totalTransactions = transactions.size,
            onlineTransactions = onlineTransactions.size,
            atmTransactions = atmTransactions.size,
            contactlessTransactions = contactlessTransactions.size,
            internationalTransactions = internationalTransactions.size,
            declinedTransactions = 0, // Would need to track declined transactions
            averageTransactionsPerDay = calculateAverageTransactionsPerDay(transactions, period)
        )
    }
    
    private fun findTopMerchant(transactions: List<Transaction>): String? {
        return transactions
            .groupBy { it.merchantName }
            .maxByOrNull { it.value.sumOf { transaction -> transaction.amount.abs() } }
            ?.key
    }
    
    private fun findTopCategory(transactions: List<Transaction>): String? {
        return transactions
            .groupBy { it.category }
            .maxByOrNull { it.value.sumOf { transaction -> transaction.amount.abs() } }
            ?.key?.name
    }
    
    private fun calculateAverageTransactionsPerDay(
        transactions: List<Transaction>,
        period: AnalyticsPeriod
    ): Double {
        val days = when (period) {
            AnalyticsPeriod.CURRENT_WEEK -> 7
            AnalyticsPeriod.CURRENT_MONTH -> 30
            AnalyticsPeriod.LAST_3_MONTHS -> 90
            AnalyticsPeriod.CURRENT_YEAR -> 365
        }
        
        return if (transactions.isNotEmpty()) {
            transactions.size.toDouble() / days
        } else 0.0
    }
    
    private fun getPeriodDates(period: AnalyticsPeriod): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        when (period) {
            AnalyticsPeriod.CURRENT_WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
            AnalyticsPeriod.CURRENT_MONTH -> calendar.add(Calendar.MONTH, -1)
            AnalyticsPeriod.LAST_3_MONTHS -> calendar.add(Calendar.MONTH, -3)
            AnalyticsPeriod.CURRENT_YEAR -> calendar.add(Calendar.YEAR, -1)
        }
        
        val startDate = calendar.time
        return Pair(startDate, endDate)
    }
}

data class CardSpendingInsights(
    val cardId: String,
    val period: AnalyticsPeriod,
    val totalSpent: BigDecimal,
    val transactionCount: Int,
    val averageTransactionAmount: BigDecimal,
    val dailyLimitUsagePercentage: Double,
    val monthlyLimitUsagePercentage: Double,
    val topMerchant: String?,
    val topCategory: String?
)

data class CardUsageStats(
    val cardId: String,
    val period: AnalyticsPeriod,
    val totalTransactions: Int,
    val onlineTransactions: Int,
    val atmTransactions: Int,
    val contactlessTransactions: Int,
    val internationalTransactions: Int,
    val declinedTransactions: Int,
    val averageTransactionsPerDay: Double
)

data class CategorySpending(
    val category: String,
    val amount: BigDecimal,
    val transactionCount: Int,
    val percentage: Double
)

data class MonthlySpending(
    val month: Int,
    val year: Int,
    val amount: BigDecimal,
    val transactionCount: Int
)

enum class AnalyticsPeriod {
    CURRENT_WEEK,
    CURRENT_MONTH,
    LAST_3_MONTHS,
    CURRENT_YEAR
}