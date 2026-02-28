package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Investment(
    val id: String,
    val userId: String,
    val symbol: String,
    val name: String,
    val type: InvestmentType,
    val quantity: BigDecimal,
    val averageCost: BigDecimal,
    val currentPrice: BigDecimal,
    val marketValue: BigDecimal,
    val totalReturn: BigDecimal,
    val totalReturnPercentage: BigDecimal,
    val dayChange: BigDecimal,
    val dayChangePercentage: BigDecimal,
    val purchaseDate: LocalDateTime,
    val lastUpdated: LocalDateTime
)

data class Portfolio(
    val id: String,
    val userId: String,
    val name: String,
    val totalValue: BigDecimal,
    val totalCost: BigDecimal,
    val totalReturn: BigDecimal,
    val totalReturnPercentage: BigDecimal,
    val dayChange: BigDecimal,
    val dayChangePercentage: BigDecimal,
    val investments: List<Investment>,
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime
)

data class MarketData(
    val symbol: String,
    val name: String,
    val currentPrice: BigDecimal,
    val dayChange: BigDecimal,
    val dayChangePercentage: BigDecimal,
    val volume: Long,
    val marketCap: BigDecimal?,
    val high52Week: BigDecimal,
    val low52Week: BigDecimal,
    val lastUpdated: LocalDateTime
)

data class InvestmentOrder(
    val id: String,
    val userId: String,
    val portfolioId: String,
    val symbol: String,
    val orderType: OrderType,
    val quantity: BigDecimal,
    val price: BigDecimal?,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val executedAt: LocalDateTime?,
    val expiresAt: LocalDateTime?
)

data class WatchlistItem(
    val id: String,
    val userId: String,
    val symbol: String,
    val name: String,
    val currentPrice: BigDecimal,
    val dayChange: BigDecimal,
    val dayChangePercentage: BigDecimal,
    val addedAt: LocalDateTime
)

data class InvestmentAlert(
    val id: String,
    val userId: String,
    val symbol: String,
    val alertType: InvestmentAlertType,
    val targetPrice: BigDecimal?,
    val percentageChange: BigDecimal?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val triggeredAt: LocalDateTime?
)

enum class InvestmentType {
    STOCK,
    ETF,
    MUTUAL_FUND,
    BOND,
    CRYPTO,
    COMMODITY,
    REIT,
    INDEX_FUND
}

enum class OrderType {
    BUY,
    SELL,
    LIMIT_BUY,
    LIMIT_SELL,
    STOP_LOSS,
    STOP_LIMIT
}

enum class OrderStatus {
    PENDING,
    EXECUTED,
    CANCELLED,
    EXPIRED,
    PARTIALLY_FILLED
}

enum class InvestmentAlertType {
    PRICE_ABOVE,
    PRICE_BELOW,
    PERCENTAGE_GAIN,
    PERCENTAGE_LOSS,
    VOLUME_SPIKE
}

interface InvestmentRepository {
    suspend fun getPortfolios(userId: String): Result<List<Portfolio>>
    suspend fun getPortfolio(portfolioId: String): Result<Portfolio?>
    suspend fun createPortfolio(portfolio: Portfolio): Result<String>
    suspend fun updatePortfolio(portfolio: Portfolio): Result<Unit>
    suspend fun deletePortfolio(portfolioId: String): Result<Unit>
    
    suspend fun getInvestments(portfolioId: String): Result<List<Investment>>
    suspend fun getInvestment(investmentId: String): Result<Investment?>
    suspend fun addInvestment(investment: Investment): Result<String>
    suspend fun updateInvestment(investment: Investment): Result<Unit>
    suspend fun removeInvestment(investmentId: String): Result<Unit>
    
    suspend fun placeOrder(order: InvestmentOrder): Result<String>
    suspend fun getOrders(userId: String): Result<List<InvestmentOrder>>
    suspend fun getOrder(orderId: String): Result<InvestmentOrder?>
    suspend fun cancelOrder(orderId: String): Result<Unit>
    suspend fun executeOrder(orderId: String): Result<Unit>
    
    suspend fun getMarketData(symbol: String): Result<MarketData?>
    suspend fun getMarketData(symbols: List<String>): Result<List<MarketData>>
    suspend fun searchInvestments(query: String): Result<List<MarketData>>
    
    suspend fun getWatchlist(userId: String): Result<List<WatchlistItem>>
    suspend fun addToWatchlist(userId: String, symbol: String): Result<Unit>
    suspend fun removeFromWatchlist(userId: String, symbol: String): Result<Unit>
    
    suspend fun getAlerts(userId: String): Result<List<InvestmentAlert>>
    suspend fun createAlert(alert: InvestmentAlert): Result<String>
    suspend fun updateAlert(alert: InvestmentAlert): Result<Unit>
    suspend fun deleteAlert(alertId: String): Result<Unit>
    suspend fun checkAlerts(): Result<List<InvestmentAlert>>
    
    suspend fun getPerformanceHistory(portfolioId: String, days: Int): Result<List<Pair<LocalDateTime, BigDecimal>>>
    suspend fun calculateDiversification(portfolioId: String): Result<Map<InvestmentType, BigDecimal>>
    suspend fun getRecommendations(userId: String): Result<List<MarketData>>
}