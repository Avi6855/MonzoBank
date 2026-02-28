package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvestmentRepositoryImpl @Inject constructor() : InvestmentRepository {
    
    private val portfolios = mutableListOf<Portfolio>()
    private val investments = mutableListOf<Investment>()
    private val orders = mutableListOf<InvestmentOrder>()
    private val watchlist = mutableListOf<WatchlistItem>()
    private val alerts = mutableListOf<InvestmentAlert>()
    
    // Mock market data
    private val marketData = mapOf(
        "AAPL" to MarketData(
            symbol = "AAPL",
            name = "Apple Inc.",
            currentPrice = BigDecimal("150.25"),
            dayChange = BigDecimal("2.15"),
            dayChangePercentage = BigDecimal("1.45"),
            volume = 45000000L,
            marketCap = BigDecimal("2400000000000"),
            high52Week = BigDecimal("180.95"),
            low52Week = BigDecimal("124.17"),
            lastUpdated = LocalDateTime.now()
        ),
        "GOOGL" to MarketData(
            symbol = "GOOGL",
            name = "Alphabet Inc.",
            currentPrice = BigDecimal("2750.80"),
            dayChange = BigDecimal("-15.20"),
            dayChangePercentage = BigDecimal("-0.55"),
            volume = 1200000L,
            marketCap = BigDecimal("1800000000000"),
            high52Week = BigDecimal("3030.93"),
            low52Week = BigDecimal("2193.62"),
            lastUpdated = LocalDateTime.now()
        )
    )
    
    override suspend fun getPortfolios(userId: String): Result<List<Portfolio>> {
        return try {
            val userPortfolios = portfolios.filter { it.userId == userId }
            Result.success(userPortfolios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPortfolio(portfolioId: String): Result<Portfolio?> {
        return try {
            val portfolio = portfolios.find { it.id == portfolioId }
            Result.success(portfolio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPortfolio(portfolio: Portfolio): Result<String> {
        return try {
            portfolios.add(portfolio)
            Result.success(portfolio.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePortfolio(portfolio: Portfolio): Result<Unit> {
        return try {
            val index = portfolios.indexOfFirst { it.id == portfolio.id }
            if (index != -1) {
                portfolios[index] = portfolio
                Result.success(Unit)
            } else {
                Result.failure(Exception("Portfolio not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deletePortfolio(portfolioId: String): Result<Unit> {
        return try {
            val removed = portfolios.removeIf { it.id == portfolioId }
            if (removed) {
                // Also remove associated investments
                investments.removeIf { it.userId == portfolioId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Portfolio not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getInvestments(portfolioId: String): Result<List<Investment>> {
        return try {
            val portfolioInvestments = investments.filter { it.userId == portfolioId }
            Result.success(portfolioInvestments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getInvestment(investmentId: String): Result<Investment?> {
        return try {
            val investment = investments.find { it.id == investmentId }
            Result.success(investment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addInvestment(investment: Investment): Result<String> {
        return try {
            investments.add(investment)
            Result.success(investment.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateInvestment(investment: Investment): Result<Unit> {
        return try {
            val index = investments.indexOfFirst { it.id == investment.id }
            if (index != -1) {
                investments[index] = investment
                Result.success(Unit)
            } else {
                Result.failure(Exception("Investment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeInvestment(investmentId: String): Result<Unit> {
        return try {
            val removed = investments.removeIf { it.id == investmentId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Investment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun placeOrder(order: InvestmentOrder): Result<String> {
        return try {
            orders.add(order)
            Result.success(order.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrders(userId: String): Result<List<InvestmentOrder>> {
        return try {
            val userOrders = orders.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(userOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrder(orderId: String): Result<InvestmentOrder?> {
        return try {
            val order = orders.find { it.id == orderId }
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            val index = orders.indexOfFirst { it.id == orderId }
            if (index != -1) {
                val order = orders[index]
                if (order.status == OrderStatus.PENDING) {
                    val cancelledOrder = order.copy(status = OrderStatus.CANCELLED)
                    orders[index] = cancelledOrder
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Order cannot be cancelled"))
                }
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun executeOrder(orderId: String): Result<Unit> {
        return try {
            val index = orders.indexOfFirst { it.id == orderId }
            if (index != -1) {
                val order = orders[index]
                if (order.status == OrderStatus.PENDING) {
                    val executedOrder = order.copy(
                        status = OrderStatus.EXECUTED,
                        executedAt = LocalDateTime.now()
                    )
                    orders[index] = executedOrder
                    
                    // Create or update investment
                    when (order.orderType) {
                        OrderType.BUY, OrderType.LIMIT_BUY -> {
                            val existingInvestment = investments.find { 
                                it.userId == order.portfolioId && it.symbol == order.symbol 
                            }
                            
                            if (existingInvestment != null) {
                                // Update existing investment
                                val totalQuantity = existingInvestment.quantity + order.quantity
                                val totalCost = (existingInvestment.averageCost * existingInvestment.quantity) + 
                                              (order.price ?: BigDecimal.ZERO) * order.quantity
                                val newAverageCost = totalCost.divide(totalQuantity, 4, RoundingMode.HALF_UP)
                                
                                val updatedInvestment = existingInvestment.copy(
                                    quantity = totalQuantity,
                                    averageCost = newAverageCost
                                )
                                
                                val investmentIndex = investments.indexOfFirst { it.id == existingInvestment.id }
                                investments[investmentIndex] = updatedInvestment
                            } else {
                                // Create new investment
                                val marketPrice = getMarketData(order.symbol).getOrNull()?.currentPrice ?: BigDecimal.ZERO
                                val investment = Investment(
                                    id = UUID.randomUUID().toString(),
                                    userId = order.portfolioId,
                                    symbol = order.symbol,
                                    name = getMarketData(order.symbol).getOrNull()?.name ?: order.symbol,
                                    type = InvestmentType.STOCK,
                                    quantity = order.quantity,
                                    averageCost = order.price ?: marketPrice,
                                    currentPrice = marketPrice,
                                    marketValue = marketPrice * order.quantity,
                                    totalReturn = BigDecimal.ZERO,
                                    totalReturnPercentage = BigDecimal.ZERO,
                                    dayChange = BigDecimal.ZERO,
                                    dayChangePercentage = BigDecimal.ZERO,
                                    purchaseDate = LocalDateTime.now(),
                                    lastUpdated = LocalDateTime.now()
                                )
                                investments.add(investment)
                            }
                        }
                        OrderType.SELL, OrderType.LIMIT_SELL -> {
                            // Handle sell orders
                            val existingInvestment = investments.find { 
                                it.userId == order.portfolioId && it.symbol == order.symbol 
                            }
                            
                            if (existingInvestment != null && existingInvestment.quantity >= order.quantity) {
                                val newQuantity = existingInvestment.quantity - order.quantity
                                if (newQuantity > BigDecimal.ZERO) {
                                    val updatedInvestment = existingInvestment.copy(quantity = newQuantity)
                                    val investmentIndex = investments.indexOfFirst { it.id == existingInvestment.id }
                                    investments[investmentIndex] = updatedInvestment
                                } else {
                                    investments.removeIf { it.id == existingInvestment.id }
                                }
                            }
                        }
                        else -> { /* Handle other order types */ }
                    }
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Order cannot be executed"))
                }
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMarketData(symbol: String): Result<MarketData?> {
        return try {
            val data = marketData[symbol]
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMarketData(symbols: List<String>): Result<List<MarketData>> {
        return try {
            val data = symbols.mapNotNull { marketData[it] }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchInvestments(query: String): Result<List<MarketData>> {
        return try {
            val results = marketData.values.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.symbol.contains(query, ignoreCase = true) 
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWatchlist(userId: String): Result<List<WatchlistItem>> {
        return try {
            val userWatchlist = watchlist.filter { it.userId == userId }
            Result.success(userWatchlist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addToWatchlist(userId: String, symbol: String): Result<Unit> {
        return try {
            val marketInfo = marketData[symbol]
            if (marketInfo != null) {
                val watchlistItem = WatchlistItem(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    symbol = symbol,
                    name = marketInfo.name,
                    currentPrice = marketInfo.currentPrice,
                    dayChange = marketInfo.dayChange,
                    dayChangePercentage = marketInfo.dayChangePercentage,
                    addedAt = LocalDateTime.now()
                )
                watchlist.add(watchlistItem)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Symbol not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeFromWatchlist(userId: String, symbol: String): Result<Unit> {
        return try {
            val removed = watchlist.removeIf { it.userId == userId && it.symbol == symbol }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Watchlist item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAlerts(userId: String): Result<List<InvestmentAlert>> {
        return try {
            val userAlerts = alerts.filter { it.userId == userId }
            Result.success(userAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createAlert(alert: InvestmentAlert): Result<String> {
        return try {
            alerts.add(alert)
            Result.success(alert.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateAlert(alert: InvestmentAlert): Result<Unit> {
        return try {
            val index = alerts.indexOfFirst { it.id == alert.id }
            if (index != -1) {
                alerts[index] = alert
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAlert(alertId: String): Result<Unit> {
        return try {
            val removed = alerts.removeIf { it.id == alertId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkAlerts(): Result<List<InvestmentAlert>> {
        return try {
            val triggeredAlerts = mutableListOf<InvestmentAlert>()
            
            alerts.filter { it.isActive && it.triggeredAt == null }.forEach { alert ->
                val currentData = marketData[alert.symbol]
                if (currentData != null) {
                    val shouldTrigger = when (alert.alertType) {
                        InvestmentAlertType.PRICE_ABOVE -> {
                            alert.targetPrice != null && currentData.currentPrice >= alert.targetPrice
                        }
                        InvestmentAlertType.PRICE_BELOW -> {
                            alert.targetPrice != null && currentData.currentPrice <= alert.targetPrice
                        }
                        InvestmentAlertType.PERCENTAGE_GAIN -> {
                            alert.percentageChange != null && currentData.dayChangePercentage >= alert.percentageChange
                        }
                        InvestmentAlertType.PERCENTAGE_LOSS -> {
                            alert.percentageChange != null && currentData.dayChangePercentage <= alert.percentageChange.negate()
                        }
                        InvestmentAlertType.VOLUME_SPIKE -> {
                            currentData.volume > 50000000L // Mock volume spike threshold
                        }
                    }
                    
                    if (shouldTrigger) {
                        val triggeredAlert = alert.copy(triggeredAt = LocalDateTime.now())
                        val index = alerts.indexOfFirst { it.id == alert.id }
                        alerts[index] = triggeredAlert
                        triggeredAlerts.add(triggeredAlert)
                    }
                }
            }
            
            Result.success(triggeredAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPerformanceHistory(portfolioId: String, days: Int): Result<List<Pair<LocalDateTime, BigDecimal>>> {
        return try {
            // Mock performance history data
            val history = mutableListOf<Pair<LocalDateTime, BigDecimal>>()
            val baseValue = BigDecimal("10000")
            
            for (i in days downTo 0) {
                val date = LocalDateTime.now().minusDays(i.toLong())
                val randomChange = (Math.random() - 0.5) * 200 // Random change between -100 and +100
                val value = baseValue.add(BigDecimal(randomChange))
                history.add(Pair(date, value))
            }
            
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateDiversification(portfolioId: String): Result<Map<InvestmentType, BigDecimal>> {
        return try {
            val portfolioInvestments = investments.filter { it.userId == portfolioId }
            val totalValue = portfolioInvestments.sumOf { it.marketValue }
            
            val diversification = portfolioInvestments
                .groupBy { it.type }
                .mapValues { (_, investments) ->
                    val typeValue = investments.sumOf { it.marketValue }
                    if (totalValue > BigDecimal.ZERO) {
                        typeValue.divide(totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal("100"))
                    } else {
                        BigDecimal.ZERO
                    }
                }
            
            Result.success(diversification)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecommendations(userId: String): Result<List<MarketData>> {
        return try {
            // Mock recommendations - return some popular stocks
            val recommendations = marketData.values.take(5)
            Result.success(recommendations.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}