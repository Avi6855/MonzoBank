package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashbackRepositoryImpl @Inject constructor() : CashbackRepository {
    
    private val cashbackOffers = mutableListOf<CashbackOffer>()
    private val cashbackEarnings = mutableListOf<CashbackEarning>()
    
    init {
        initializeMockOffers()
    }
    
    override suspend fun getAvailableOffers(): Result<List<CashbackOffer>> {
        return try {
            val activeOffers = cashbackOffers.filter { 
                it.isActive && it.endDate.isAfter(LocalDateTime.now()) 
            }
            Result.success(activeOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffersByCategory(category: CashbackCategory): Result<List<CashbackOffer>> {
        return try {
            val categoryOffers = cashbackOffers.filter { 
                it.category == category && it.isActive && it.endDate.isAfter(LocalDateTime.now()) 
            }
            Result.success(categoryOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffer(offerId: String): Result<CashbackOffer?> {
        return try {
            val offer = cashbackOffers.find { it.id == offerId }
            Result.success(offer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCashbackEarnings(userId: String): Result<List<CashbackEarning>> {
        return try {
            val userEarnings = cashbackEarnings.filter { it.userId == userId }
                .sortedByDescending { it.earnedDate }
            Result.success(userEarnings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCashbackEarning(earningId: String): Result<CashbackEarning?> {
        return try {
            val earning = cashbackEarnings.find { it.id == earningId }
            Result.success(earning)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addCashbackEarning(earning: CashbackEarning): Result<String> {
        return try {
            cashbackEarnings.add(earning)
            Result.success(earning.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCashbackSummary(userId: String): Result<CashbackSummary> {
        return try {
            val userEarnings = cashbackEarnings.filter { it.userId == userId }
            
            val totalEarned = userEarnings.sumOf { it.cashbackAmount }
            val totalCredited = userEarnings.filter { it.status == CashbackStatus.CREDITED }
                .sumOf { it.cashbackAmount }
            val pendingAmount = userEarnings.filter { it.status == CashbackStatus.PENDING }
                .sumOf { it.cashbackAmount }
            val expiredAmount = userEarnings.filter { it.status == CashbackStatus.EXPIRED }
                .sumOf { it.cashbackAmount }
            
            val currentMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            val currentMonthEarnings = userEarnings.filter { 
                it.earnedDate.isAfter(currentMonth) 
            }.sumOf { it.cashbackAmount }
            
            val summary = CashbackSummary(
                userId = userId,
                totalEarned = totalEarned,
                totalCredited = totalCredited,
                pendingAmount = pendingAmount,
                expiredAmount = expiredAmount,
                currentMonthEarnings = currentMonthEarnings,
                lastUpdated = LocalDateTime.now()
            )
            
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateCashback(transactionAmount: BigDecimal, offerId: String): Result<BigDecimal> {
        return try {
            val offer = cashbackOffers.find { it.id == offerId }
                ?: return Result.failure(Exception("Offer not found"))
            
            // Check minimum spend requirement
            if (offer.minSpend != null && transactionAmount < offer.minSpend) {
                return Result.success(BigDecimal.ZERO)
            }
            
            // Calculate cashback
            val cashbackAmount = transactionAmount.multiply(offer.cashbackRate)
                .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
            
            // Apply maximum cashback limit
            val finalCashback = if (offer.maxCashback != null) {
                cashbackAmount.min(offer.maxCashback)
            } else {
                cashbackAmount
            }
            
            Result.success(finalCashback)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processPendingCashback(userId: String): Result<List<CashbackEarning>> {
        return try {
            val processedEarnings = mutableListOf<CashbackEarning>()
            val pendingEarnings = cashbackEarnings.filter { 
                it.userId == userId && it.status == CashbackStatus.PENDING 
            }
            
            pendingEarnings.forEach { earning ->
                // Mock processing - credit cashback after 30 days
                if (earning.earnedDate.isBefore(LocalDateTime.now().minusDays(30))) {
                    val index = cashbackEarnings.indexOfFirst { it.id == earning.id }
                    if (index != -1) {
                        val creditedEarning = earning.copy(
                            status = CashbackStatus.CREDITED,
                            creditedDate = LocalDateTime.now()
                        )
                        cashbackEarnings[index] = creditedEarning
                        processedEarnings.add(creditedEarning)
                    }
                }
            }
            
            Result.success(processedEarnings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExpiringCashback(userId: String): Result<List<CashbackEarning>> {
        return try {
            val expiringDate = LocalDateTime.now().plusDays(30) // Expiring in 30 days
            val expiringEarnings = cashbackEarnings.filter { earning ->
                earning.userId == userId &&
                earning.status == CashbackStatus.PENDING &&
                earning.expiryDate != null &&
                earning.expiryDate.isBefore(expiringDate)
            }.sortedBy { it.expiryDate }
            
            Result.success(expiringEarnings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTopOffers(limit: Int): Result<List<CashbackOffer>> {
        return try {
            val topOffers = cashbackOffers.filter { 
                it.isActive && it.endDate.isAfter(LocalDateTime.now()) 
            }.sortedByDescending { it.cashbackRate }
                .take(limit)
            
            Result.success(topOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchOffers(query: String): Result<List<CashbackOffer>> {
        return try {
            val searchResults = cashbackOffers.filter { offer ->
                offer.isActive && 
                offer.endDate.isAfter(LocalDateTime.now()) &&
                (offer.title.contains(query, ignoreCase = true) ||
                 offer.description.contains(query, ignoreCase = true) ||
                 offer.merchant?.contains(query, ignoreCase = true) == true)
            }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun initializeMockOffers() {
        val mockOffers = listOf(
            CashbackOffer(
                id = UUID.randomUUID().toString(),
                title = "Grocery Cashback",
                description = "Earn 3% cashback on all grocery purchases",
                cashbackRate = BigDecimal("3.0"),
                category = CashbackCategory.GROCERIES,
                maxCashback = BigDecimal("50.00"),
                startDate = LocalDateTime.now().minusMonths(1),
                endDate = LocalDateTime.now().plusMonths(6)
            ),
            CashbackOffer(
                id = UUID.randomUUID().toString(),
                title = "Gas Station Rewards",
                description = "Get 2% cashback at gas stations",
                cashbackRate = BigDecimal("2.0"),
                category = CashbackCategory.GAS,
                maxCashback = BigDecimal("25.00"),
                startDate = LocalDateTime.now().minusMonths(2),
                endDate = LocalDateTime.now().plusMonths(3)
            ),
            CashbackOffer(
                id = UUID.randomUUID().toString(),
                title = "Restaurant Dining",
                description = "Enjoy 4% cashback on restaurant purchases",
                cashbackRate = BigDecimal("4.0"),
                category = CashbackCategory.RESTAURANTS,
                maxCashback = BigDecimal("75.00"),
                minSpend = BigDecimal("25.00"),
                startDate = LocalDateTime.now().minusWeeks(2),
                endDate = LocalDateTime.now().plusMonths(4)
            ),
            CashbackOffer(
                id = UUID.randomUUID().toString(),
                title = "Online Shopping Bonus",
                description = "Earn 5% cashback on online purchases",
                cashbackRate = BigDecimal("5.0"),
                category = CashbackCategory.ONLINE_SHOPPING,
                maxCashback = BigDecimal("100.00"),
                startDate = LocalDateTime.now().minusDays(10),
                endDate = LocalDateTime.now().plusMonths(2)
            ),
            CashbackOffer(
                id = UUID.randomUUID().toString(),
                title = "All Purchases",
                description = "Base 1% cashback on all purchases",
                cashbackRate = BigDecimal("1.0"),
                category = CashbackCategory.ALL_PURCHASES,
                startDate = LocalDateTime.now().minusYears(1),
                endDate = LocalDateTime.now().plusYears(1)
            )
        )
        
        cashbackOffers.addAll(mockOffers)
    }
}