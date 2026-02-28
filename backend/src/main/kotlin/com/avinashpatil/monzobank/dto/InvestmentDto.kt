package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.AssetType
import com.avinashpatil.monzobank.entity.InvestmentStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CreateInvestmentRequest(
    @field:NotBlank(message = "Symbol is required")
    @field:Size(min = 1, max = 10, message = "Symbol must be between 1 and 10 characters")
    val symbol: String,
    
    @field:NotBlank(message = "Investment name is required")
    @field:Size(max = 200, message = "Investment name must not exceed 200 characters")
    val name: String,
    
    @field:NotNull(message = "Asset type is required")
    val assetType: AssetType,
    
    @field:NotNull(message = "Quantity is required")
    @field:DecimalMin(value = "0.000001", message = "Quantity must be greater than 0")
    @field:Digits(integer = 10, fraction = 6, message = "Quantity must have at most 6 decimal places")
    val quantity: BigDecimal,
    
    @field:NotNull(message = "Purchase price is required")
    @field:DecimalMin(value = "0.01", message = "Purchase price must be greater than 0")
    @field:Digits(integer = 10, fraction = 4, message = "Purchase price must have at most 4 decimal places")
    val purchasePrice: BigDecimal
)

data class InvestmentResponse(
    val id: UUID,
    val userId: UUID,
    val symbol: String,
    val name: String,
    val assetType: AssetType,
    val quantity: BigDecimal,
    val purchasePrice: BigDecimal,
    val currentPrice: BigDecimal,
    val purchaseValue: BigDecimal,
    val currentValue: BigDecimal,
    val gainLoss: BigDecimal,
    val gainLossPercentage: BigDecimal,
    val status: InvestmentStatus,
    val purchaseDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class SellInvestmentRequest(
    @field:NotNull(message = "Quantity is required")
    @field:DecimalMin(value = "0.000001", message = "Quantity must be greater than 0")
    @field:Digits(integer = 10, fraction = 6, message = "Quantity must have at most 6 decimal places")
    val quantity: BigDecimal,
    
    @field:NotNull(message = "Sale price is required")
    @field:DecimalMin(value = "0.01", message = "Sale price must be greater than 0")
    @field:Digits(integer = 10, fraction = 4, message = "Sale price must have at most 4 decimal places")
    val salePrice: BigDecimal,
    
    @field:Size(max = 255, message = "Reason must not exceed 255 characters")
    val reason: String? = null
)

data class PortfolioSummaryResponse(
    val totalInvestments: Long,
    val totalInvestmentValue: BigDecimal,
    val currentPortfolioValue: BigDecimal,
    val totalGainLoss: BigDecimal,
    val gainLossPercentage: BigDecimal,
    val topPerformingInvestment: String?,
    val worstPerformingInvestment: String?,
    val assetAllocation: List<AssetAllocation>
)

data class AssetAllocation(
    val assetType: AssetType,
    val value: BigDecimal,
    val percentage: BigDecimal
)

data class InvestmentPerformanceResponse(
    val investmentId: UUID,
    val symbol: String,
    val name: String,
    val quantity: BigDecimal,
    val purchasePrice: BigDecimal,
    val currentPrice: BigDecimal,
    val purchaseValue: BigDecimal,
    val currentValue: BigDecimal,
    val gainLoss: BigDecimal,
    val gainLossPercentage: BigDecimal,
    val daysSincePurchase: Long,
    val annualizedReturn: BigDecimal
)

data class InvestmentSearchRequest(
    @field:NotBlank(message = "Search query is required")
    @field:Size(min = 1, max = 100, message = "Search query must be between 1 and 100 characters")
    val query: String,
    
    val assetType: AssetType? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val page: Int = 0,
    val size: Int = 20
)

data class MarketDataResponse(
    val symbol: String,
    val name: String,
    val currentPrice: BigDecimal,
    val previousClose: BigDecimal,
    val dayChange: BigDecimal,
    val dayChangePercentage: BigDecimal,
    val volume: Long,
    val marketCap: BigDecimal?,
    val high52Week: BigDecimal?,
    val low52Week: BigDecimal?,
    val lastUpdated: LocalDateTime
)

data class InvestmentOrderRequest(
    @field:NotBlank(message = "Symbol is required")
    val symbol: String,
    
    @field:NotNull(message = "Order type is required")
    val orderType: OrderType,
    
    @field:NotNull(message = "Order side is required")
    val orderSide: OrderSide,
    
    @field:NotNull(message = "Quantity is required")
    @field:DecimalMin(value = "0.000001", message = "Quantity must be greater than 0")
    val quantity: BigDecimal,
    
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    val limitPrice: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.01", message = "Stop price must be greater than 0")
    val stopPrice: BigDecimal? = null,
    
    val timeInForce: TimeInForce = TimeInForce.DAY
)

enum class OrderType {
    MARKET, LIMIT, STOP, STOP_LIMIT
}

enum class OrderSide {
    BUY, SELL
}

enum class TimeInForce {
    DAY, GTC, IOC, FOK // Good Till Cancelled, Immediate Or Cancel, Fill Or Kill
}

data class InvestmentOrderResponse(
    val orderId: UUID,
    val symbol: String,
    val orderType: OrderType,
    val orderSide: OrderSide,
    val quantity: BigDecimal,
    val limitPrice: BigDecimal?,
    val stopPrice: BigDecimal?,
    val status: OrderStatus,
    val filledQuantity: BigDecimal,
    val averageFillPrice: BigDecimal?,
    val timeInForce: TimeInForce,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class OrderStatus {
    PENDING, PARTIALLY_FILLED, FILLED, CANCELLED, REJECTED
}

data class WatchlistRequest(
    @field:NotBlank(message = "Watchlist name is required")
    @field:Size(max = 100, message = "Watchlist name must not exceed 100 characters")
    val name: String,
    
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null,
    
    val symbols: List<String> = emptyList()
)

data class WatchlistResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val description: String?,
    val symbols: List<String>,
    val marketData: List<MarketDataResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class InvestmentAlertRequest(
    @field:NotBlank(message = "Symbol is required")
    val symbol: String,
    
    @field:NotNull(message = "Alert type is required")
    val alertType: AlertType,
    
    @field:NotNull(message = "Trigger value is required")
    val triggerValue: BigDecimal,
    
    val message: String? = null,
    val enabled: Boolean = true
)

enum class AlertType {
    PRICE_ABOVE, PRICE_BELOW, PERCENTAGE_GAIN, PERCENTAGE_LOSS, VOLUME_SPIKE
}

data class InvestmentAlertResponse(
    val id: UUID,
    val userId: UUID,
    val symbol: String,
    val alertType: AlertType,
    val triggerValue: BigDecimal,
    val currentValue: BigDecimal?,
    val message: String?,
    val enabled: Boolean,
    val triggered: Boolean,
    val triggeredAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class PortfolioAnalyticsResponse(
    val userId: UUID,
    val period: String,
    val totalReturn: BigDecimal,
    val totalReturnPercentage: BigDecimal,
    val annualizedReturn: BigDecimal,
    val volatility: BigDecimal,
    val sharpeRatio: BigDecimal,
    val maxDrawdown: BigDecimal,
    val bestPerformingAsset: String?,
    val worstPerformingAsset: String?,
    val sectorAllocation: List<SectorAllocation>,
    val performanceHistory: List<PerformanceDataPoint>
)

data class SectorAllocation(
    val sector: String,
    val value: BigDecimal,
    val percentage: BigDecimal,
    val return: BigDecimal
)

data class PerformanceDataPoint(
    val date: String,
    val portfolioValue: BigDecimal,
    val return: BigDecimal,
    val returnPercentage: BigDecimal
)

data class InvestmentRecommendationResponse(
    val userId: UUID,
    val recommendations: List<InvestmentRecommendation>,
    val riskProfile: String,
    val recommendedAllocation: List<AssetAllocation>,
    val generatedAt: LocalDateTime
)

data class InvestmentRecommendation(
    val symbol: String,
    val name: String,
    val assetType: AssetType,
    val recommendationType: RecommendationType,
    val targetPrice: BigDecimal?,
    val confidence: String, // HIGH, MEDIUM, LOW
    val reasoning: String,
    val riskLevel: String, // LOW, MEDIUM, HIGH
    val timeHorizon: String // SHORT, MEDIUM, LONG
)

enum class RecommendationType {
    BUY, SELL, HOLD, STRONG_BUY, STRONG_SELL
}

data class DividendResponse(
    val investmentId: UUID,
    val symbol: String,
    val dividendAmount: BigDecimal,
    val dividendPerShare: BigDecimal,
    val exDividendDate: LocalDateTime,
    val paymentDate: LocalDateTime,
    val recordDate: LocalDateTime,
    val frequency: String, // QUARTERLY, MONTHLY, ANNUALLY
    val yield: BigDecimal
)

data class InvestmentNewsResponse(
    val symbol: String?,
    val headline: String,
    val summary: String,
    val source: String,
    val publishedAt: LocalDateTime,
    val sentiment: String, // POSITIVE, NEGATIVE, NEUTRAL
    val relevanceScore: BigDecimal,
    val url: String?
)

data class RiskAssessmentRequest(
    val investmentHorizon: Int, // Years
    val riskTolerance: RiskTolerance,
    val investmentGoals: List<InvestmentGoal>,
    val monthlyInvestmentAmount: BigDecimal,
    val currentAge: Int,
    val retirementAge: Int?
)

enum class RiskTolerance {
    CONSERVATIVE, MODERATE, AGGRESSIVE, VERY_AGGRESSIVE
}

enum class InvestmentGoal {
    RETIREMENT, WEALTH_BUILDING, INCOME_GENERATION, CAPITAL_PRESERVATION, EDUCATION, HOME_PURCHASE
}

data class RiskAssessmentResponse(
    val userId: UUID,
    val riskProfile: String,
    val riskScore: Int, // 1-100
    val recommendedAllocation: List<AssetAllocation>,
    val suitableInvestments: List<String>,
    val warnings: List<String>,
    val recommendations: List<String>,
    val assessmentDate: LocalDateTime
)

data class InvestmentEducationResponse(
    val topic: String,
    val title: String,
    val content: String,
    val difficulty: String, // BEGINNER, INTERMEDIATE, ADVANCED
    val estimatedReadTime: Int, // Minutes
    val tags: List<String>,
    val relatedTopics: List<String>
)

data class BacktestRequest(
    val symbols: List<String>,
    val allocation: List<BigDecimal>, // Percentages for each symbol
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val initialInvestment: BigDecimal,
    val rebalanceFrequency: RebalanceFrequency
)

enum class RebalanceFrequency {
    NEVER, MONTHLY, QUARTERLY, ANNUALLY
}

data class BacktestResponse(
    val symbols: List<String>,
    val allocation: List<BigDecimal>,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val initialInvestment: BigDecimal,
    val finalValue: BigDecimal,
    val totalReturn: BigDecimal,
    val totalReturnPercentage: BigDecimal,
    val annualizedReturn: BigDecimal,
    val volatility: BigDecimal,
    val maxDrawdown: BigDecimal,
    val sharpeRatio: BigDecimal,
    val performanceData: List<PerformanceDataPoint>
)