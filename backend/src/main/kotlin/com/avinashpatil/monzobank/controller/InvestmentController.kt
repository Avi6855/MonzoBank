package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.AssetType
import com.avinashpatil.monzobank.service.InvestmentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/investments")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class InvestmentController(
    private val investmentService: InvestmentService
) {
    
    @PostMapping
    fun createInvestment(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: CreateInvestmentRequest
    ): ResponseEntity<ApiResponse<InvestmentResponse>> {
        val response = investmentService.createInvestment(UUID.fromString(userId), request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Investment created successfully", response))
    }
    
    @GetMapping("/{investmentId}")
    fun getInvestment(
        @PathVariable investmentId: UUID
    ): ResponseEntity<ApiResponse<InvestmentResponse>> {
        val investment = investmentService.getInvestmentById(investmentId)
        return ResponseEntity.ok(ApiResponse.success("Investment retrieved successfully", investment))
    }
    
    @GetMapping
    fun getInvestments(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<InvestmentResponse>>> {
        val investments = investmentService.getInvestmentsByUserId(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Investments retrieved successfully", investments))
    }
    
    @GetMapping("/by-asset-type/{assetType}")
    fun getInvestmentsByAssetType(
        @AuthenticationPrincipal userId: String,
        @PathVariable assetType: AssetType
    ): ResponseEntity<ApiResponse<List<InvestmentResponse>>> {
        val investments = investmentService.getInvestmentsByAssetType(UUID.fromString(userId), assetType)
        return ResponseEntity.ok(ApiResponse.success("Investments retrieved successfully", investments))
    }
    
    @PutMapping("/{investmentId}/price")
    fun updateInvestmentPrice(
        @PathVariable investmentId: UUID,
        @RequestParam newPrice: BigDecimal
    ): ResponseEntity<ApiResponse<InvestmentResponse>> {
        val investment = investmentService.updateInvestmentPrice(investmentId, newPrice)
        return ResponseEntity.ok(ApiResponse.success("Investment price updated successfully", investment))
    }
    
    @PostMapping("/{investmentId}/sell")
    fun sellInvestment(
        @PathVariable investmentId: UUID,
        @Valid @RequestBody request: SellInvestmentRequest
    ): ResponseEntity<ApiResponse<InvestmentResponse>> {
        val investment = investmentService.sellInvestment(investmentId, request)
        return ResponseEntity.ok(ApiResponse.success("Investment sold successfully", investment))
    }
    
    @GetMapping("/portfolio/summary")
    fun getPortfolioSummary(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<PortfolioSummaryResponse>> {
        val summary = investmentService.getPortfolioSummary(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Portfolio summary retrieved successfully", summary))
    }
    
    @GetMapping("/{investmentId}/performance")
    fun getInvestmentPerformance(
        @PathVariable investmentId: UUID
    ): ResponseEntity<ApiResponse<InvestmentPerformanceResponse>> {
        val performance = investmentService.getInvestmentPerformance(investmentId)
        return ResponseEntity.ok(ApiResponse.success("Investment performance retrieved successfully", performance))
    }
    
    @PostMapping("/search")
    fun searchInvestments(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: InvestmentSearchRequest
    ): ResponseEntity<ApiResponse<List<InvestmentResponse>>> {
        val investments = investmentService.searchInvestments(UUID.fromString(userId), request.query)
        return ResponseEntity.ok(ApiResponse.success("Investment search completed successfully", investments))
    }
    
    @GetMapping("/market-data/{symbol}")
    fun getMarketData(
        @PathVariable symbol: String
    ): ResponseEntity<ApiResponse<MarketDataResponse>> {
        // In a real implementation, this would fetch data from external market data providers
        val marketData = MarketDataResponse(
            symbol = symbol.uppercase(),
            name = "Sample Company",
            currentPrice = BigDecimal("150.25"),
            previousClose = BigDecimal("148.75"),
            dayChange = BigDecimal("1.50"),
            dayChangePercentage = BigDecimal("1.01"),
            volume = 1250000L,
            marketCap = BigDecimal("50000000000"),
            high52Week = BigDecimal("180.50"),
            low52Week = BigDecimal("120.25"),
            lastUpdated = LocalDateTime.now()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Market data retrieved successfully", marketData))
    }
    
    @PostMapping("/orders")
    fun placeOrder(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: InvestmentOrderRequest
    ): ResponseEntity<ApiResponse<InvestmentOrderResponse>> {
        // In a real implementation, this would place an order with a broker
        val order = InvestmentOrderResponse(
            orderId = UUID.randomUUID(),
            symbol = request.symbol.uppercase(),
            orderType = request.orderType,
            orderSide = request.orderSide,
            quantity = request.quantity,
            limitPrice = request.limitPrice,
            stopPrice = request.stopPrice,
            status = OrderStatus.PENDING,
            filledQuantity = BigDecimal.ZERO,
            averageFillPrice = null,
            timeInForce = request.timeInForce,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Order placed successfully", order))
    }
    
    @GetMapping("/orders")
    fun getOrders(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<InvestmentOrderResponse>>> {
        // In a real implementation, this would retrieve user's orders
        val orders = emptyList<InvestmentOrderResponse>()
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders))
    }
    
    @GetMapping("/orders/{orderId}")
    fun getOrder(
        @PathVariable orderId: UUID
    ): ResponseEntity<ApiResponse<InvestmentOrderResponse>> {
        // In a real implementation, this would retrieve a specific order
        val order = InvestmentOrderResponse(
            orderId = orderId,
            symbol = "AAPL",
            orderType = OrderType.MARKET,
            orderSide = OrderSide.BUY,
            quantity = BigDecimal("10"),
            limitPrice = null,
            stopPrice = null,
            status = OrderStatus.FILLED,
            filledQuantity = BigDecimal("10"),
            averageFillPrice = BigDecimal("150.25"),
            timeInForce = TimeInForce.DAY,
            createdAt = LocalDateTime.now().minusHours(1),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order))
    }
    
    @DeleteMapping("/orders/{orderId}")
    fun cancelOrder(
        @PathVariable orderId: UUID
    ): ResponseEntity<ApiResponse<InvestmentOrderResponse>> {
        // In a real implementation, this would cancel the order
        val order = InvestmentOrderResponse(
            orderId = orderId,
            symbol = "AAPL",
            orderType = OrderType.LIMIT,
            orderSide = OrderSide.BUY,
            quantity = BigDecimal("10"),
            limitPrice = BigDecimal("145.00"),
            stopPrice = null,
            status = OrderStatus.CANCELLED,
            filledQuantity = BigDecimal.ZERO,
            averageFillPrice = null,
            timeInForce = TimeInForce.GTC,
            createdAt = LocalDateTime.now().minusHours(2),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order))
    }
    
    @PostMapping("/watchlists")
    fun createWatchlist(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: WatchlistRequest
    ): ResponseEntity<ApiResponse<WatchlistResponse>> {
        // In a real implementation, this would create a watchlist
        val watchlist = WatchlistResponse(
            id = UUID.randomUUID(),
            userId = UUID.fromString(userId),
            name = request.name,
            description = request.description,
            symbols = request.symbols,
            marketData = emptyList(), // Would be populated with actual market data
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Watchlist created successfully", watchlist))
    }
    
    @GetMapping("/watchlists")
    fun getWatchlists(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<WatchlistResponse>>> {
        // In a real implementation, this would retrieve user's watchlists
        val watchlists = emptyList<WatchlistResponse>()
        return ResponseEntity.ok(ApiResponse.success("Watchlists retrieved successfully", watchlists))
    }
    
    @PostMapping("/alerts")
    fun createAlert(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: InvestmentAlertRequest
    ): ResponseEntity<ApiResponse<InvestmentAlertResponse>> {
        // In a real implementation, this would create a price alert
        val alert = InvestmentAlertResponse(
            id = UUID.randomUUID(),
            userId = UUID.fromString(userId),
            symbol = request.symbol.uppercase(),
            alertType = request.alertType,
            triggerValue = request.triggerValue,
            currentValue = null,
            message = request.message,
            enabled = request.enabled,
            triggered = false,
            triggeredAt = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Investment alert created successfully", alert))
    }
    
    @GetMapping("/alerts")
    fun getAlerts(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<InvestmentAlertResponse>>> {
        // In a real implementation, this would retrieve user's alerts
        val alerts = emptyList<InvestmentAlertResponse>()
        return ResponseEntity.ok(ApiResponse.success("Investment alerts retrieved successfully", alerts))
    }
    
    @GetMapping("/portfolio/analytics")
    fun getPortfolioAnalytics(
        @AuthenticationPrincipal userId: String,
        @RequestParam(defaultValue = "12") months: Int
    ): ResponseEntity<ApiResponse<PortfolioAnalyticsResponse>> {
        // In a real implementation, this would calculate actual portfolio analytics
        val analytics = PortfolioAnalyticsResponse(
            userId = UUID.fromString(userId),
            period = "Last $months months",
            totalReturn = BigDecimal.ZERO,
            totalReturnPercentage = BigDecimal.ZERO,
            annualizedReturn = BigDecimal.ZERO,
            volatility = BigDecimal.ZERO,
            sharpeRatio = BigDecimal.ZERO,
            maxDrawdown = BigDecimal.ZERO,
            bestPerformingAsset = null,
            worstPerformingAsset = null,
            sectorAllocation = emptyList(),
            performanceHistory = emptyList()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Portfolio analytics retrieved successfully", analytics))
    }
    
    @GetMapping("/recommendations")
    fun getInvestmentRecommendations(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<InvestmentRecommendationResponse>> {
        // In a real implementation, this would generate personalized recommendations
        val recommendations = InvestmentRecommendationResponse(
            userId = UUID.fromString(userId),
            recommendations = listOf(
                InvestmentRecommendation(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    assetType = AssetType.STOCK,
                    recommendationType = RecommendationType.BUY,
                    targetPrice = BigDecimal("160.00"),
                    confidence = "HIGH",
                    reasoning = "Strong fundamentals and growth prospects in services segment",
                    riskLevel = "MEDIUM",
                    timeHorizon = "LONG"
                )
            ),
            riskProfile = "MODERATE",
            recommendedAllocation = listOf(
                AssetAllocation(AssetType.STOCK, BigDecimal("60000"), BigDecimal("60")),
                AssetAllocation(AssetType.BOND, BigDecimal("30000"), BigDecimal("30")),
                AssetAllocation(AssetType.COMMODITY, BigDecimal("10000"), BigDecimal("10"))
            ),
            generatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Investment recommendations retrieved successfully", recommendations))
    }
    
    @GetMapping("/{investmentId}/dividends")
    fun getDividends(
        @PathVariable investmentId: UUID
    ): ResponseEntity<ApiResponse<List<DividendResponse>>> {
        // In a real implementation, this would retrieve dividend information
        val dividends = emptyList<DividendResponse>()
        return ResponseEntity.ok(ApiResponse.success("Dividends retrieved successfully", dividends))
    }
    
    @GetMapping("/news")
    fun getInvestmentNews(
        @RequestParam(required = false) symbol: String?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<InvestmentNewsResponse>>> {
        // In a real implementation, this would fetch news from financial news APIs
        val news = listOf(
            InvestmentNewsResponse(
                symbol = symbol,
                headline = "Market Update: Technology Stocks Rally",
                summary = "Technology stocks showed strong performance today...",
                source = "Financial Times",
                publishedAt = LocalDateTime.now().minusHours(2),
                sentiment = "POSITIVE",
                relevanceScore = BigDecimal("0.85"),
                url = "https://example.com/news/1"
            )
        )
        
        return ResponseEntity.ok(ApiResponse.success("Investment news retrieved successfully", news))
    }
    
    @PostMapping("/risk-assessment")
    fun assessRisk(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: RiskAssessmentRequest
    ): ResponseEntity<ApiResponse<RiskAssessmentResponse>> {
        // In a real implementation, this would perform a comprehensive risk assessment
        val assessment = RiskAssessmentResponse(
            userId = UUID.fromString(userId),
            riskProfile = "MODERATE",
            riskScore = 65,
            recommendedAllocation = listOf(
                AssetAllocation(AssetType.STOCK, BigDecimal("60000"), BigDecimal("60")),
                AssetAllocation(AssetType.BOND, BigDecimal("30000"), BigDecimal("30")),
                AssetAllocation(AssetType.COMMODITY, BigDecimal("10000"), BigDecimal("10"))
            ),
            suitableInvestments = listOf("Index Funds", "Blue Chip Stocks", "Government Bonds"),
            warnings = listOf("Consider diversifying across different sectors"),
            recommendations = listOf(
                "Start with low-cost index funds",
                "Gradually increase equity allocation as you gain experience",
                "Review and rebalance portfolio quarterly"
            ),
            assessmentDate = LocalDateTime.now()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Risk assessment completed successfully", assessment))
    }
    
    @GetMapping("/education")
    fun getInvestmentEducation(
        @RequestParam(required = false) topic: String?,
        @RequestParam(defaultValue = "BEGINNER") difficulty: String
    ): ResponseEntity<ApiResponse<List<InvestmentEducationResponse>>> {
        // In a real implementation, this would retrieve educational content
        val education = listOf(
            InvestmentEducationResponse(
                topic = topic ?: "Getting Started",
                title = "Introduction to Stock Market Investing",
                content = "Learn the basics of stock market investing...",
                difficulty = difficulty,
                estimatedReadTime = 10,
                tags = listOf("stocks", "beginner", "basics"),
                relatedTopics = listOf("Portfolio Diversification", "Risk Management")
            )
        )
        
        return ResponseEntity.ok(ApiResponse.success("Investment education content retrieved successfully", education))
    }
    
    @PostMapping("/backtest")
    fun runBacktest(
        @Valid @RequestBody request: BacktestRequest
    ): ResponseEntity<ApiResponse<BacktestResponse>> {
        // In a real implementation, this would run a historical backtest
        val backtest = BacktestResponse(
            symbols = request.symbols,
            allocation = request.allocation,
            startDate = request.startDate,
            endDate = request.endDate,
            initialInvestment = request.initialInvestment,
            finalValue = request.initialInvestment.multiply(BigDecimal("1.15")), // 15% return example
            totalReturn = request.initialInvestment.multiply(BigDecimal("0.15")),
            totalReturnPercentage = BigDecimal("15.00"),
            annualizedReturn = BigDecimal("7.50"),
            volatility = BigDecimal("12.50"),
            maxDrawdown = BigDecimal("-8.25"),
            sharpeRatio = BigDecimal("0.85"),
            performanceData = emptyList()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Backtest completed successfully", backtest))
    }
    
    @PostMapping("/update-prices")
    fun updateAllPrices(): ResponseEntity<ApiResponse<String>> {
        investmentService.updateAllInvestmentPrices()
        return ResponseEntity.ok(ApiResponse.success("Investment prices updated successfully", "Price update completed"))
    }
}