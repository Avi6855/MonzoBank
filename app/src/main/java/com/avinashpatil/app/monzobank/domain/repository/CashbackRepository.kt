package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class CashbackOffer(
    val id: String,
    val title: String,
    val description: String,
    val cashbackRate: BigDecimal,
    val category: CashbackCategory,
    val merchant: String? = null,
    val maxCashback: BigDecimal? = null,
    val minSpend: BigDecimal? = null,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isActive: Boolean = true,
    val terms: String? = null
)

data class CashbackEarning(
    val id: String,
    val userId: String,
    val offerId: String,
    val transactionId: String,
    val transactionAmount: BigDecimal,
    val cashbackAmount: BigDecimal,
    val cashbackRate: BigDecimal,
    val status: CashbackStatus,
    val earnedDate: LocalDateTime,
    val creditedDate: LocalDateTime? = null,
    val expiryDate: LocalDateTime? = null
)

data class CashbackSummary(
    val userId: String,
    val totalEarned: BigDecimal,
    val totalCredited: BigDecimal,
    val pendingAmount: BigDecimal,
    val expiredAmount: BigDecimal,
    val currentMonthEarnings: BigDecimal,
    val lastUpdated: LocalDateTime
)

enum class CashbackCategory {
    GROCERIES,
    GAS,
    RESTAURANTS,
    ONLINE_SHOPPING,
    TRAVEL,
    ENTERTAINMENT,
    UTILITIES,
    PHARMACY,
    DEPARTMENT_STORES,
    ALL_PURCHASES
}

enum class CashbackStatus {
    PENDING,
    CREDITED,
    EXPIRED,
    CANCELLED
}

interface CashbackRepository {
    suspend fun getAvailableOffers(): Result<List<CashbackOffer>>
    suspend fun getOffersByCategory(category: CashbackCategory): Result<List<CashbackOffer>>
    suspend fun getOffer(offerId: String): Result<CashbackOffer?>
    
    suspend fun getCashbackEarnings(userId: String): Result<List<CashbackEarning>>
    suspend fun getCashbackEarning(earningId: String): Result<CashbackEarning?>
    suspend fun addCashbackEarning(earning: CashbackEarning): Result<String>
    
    suspend fun getCashbackSummary(userId: String): Result<CashbackSummary>
    suspend fun calculateCashback(transactionAmount: BigDecimal, offerId: String): Result<BigDecimal>
    
    suspend fun processPendingCashback(userId: String): Result<List<CashbackEarning>>
    suspend fun getExpiringCashback(userId: String): Result<List<CashbackEarning>>
    
    suspend fun getTopOffers(limit: Int = 5): Result<List<CashbackOffer>>
    suspend fun searchOffers(query: String): Result<List<CashbackOffer>>
}