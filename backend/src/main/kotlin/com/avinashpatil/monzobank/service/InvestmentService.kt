package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.*
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.InvestmentRepository
import com.avinashpatil.monzobank.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class InvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val smsService: SmsService
) {
    
    private val logger = LoggerFactory.getLogger(InvestmentService::class.java)
    
    fun createInvestment(userId: UUID, request: CreateInvestmentRequest): InvestmentResponse {
        logger.info("Creating investment for user: $userId")
        
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        // Validate investment parameters
        if (request.quantity <= BigDecimal.ZERO) {
            throw ValidationException("Quantity must be positive")
        }
        
        if (request.purchasePrice <= BigDecimal.ZERO) {
            throw ValidationException("Purchase price must be positive")
        }
        
        // Check if user already has this investment
        val existingInvestment = investmentRepository.findByUserAndSymbol(user, request.symbol)
        if (existingInvestment.isPresent && existingInvestment.get().status == InvestmentStatus.ACTIVE) {
            // Update existing investment (average cost)
            return updateExistingInvestment(existingInvestment.get(), request)
        }
        
        val investment = Investment(
            id = UUID.randomUUID(),
            user = user,
            symbol = request.symbol.uppercase(),
            name = request.name,
            assetType = request.assetType,
            quantity = request.quantity,
            purchasePrice = request.purchasePrice,
            currentPrice = request.purchasePrice, // Initially same as purchase price
            purchaseDate = LocalDateTime.now(),
            status = InvestmentStatus.ACTIVE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedInvestment = investmentRepository.save(investment)
        
        // Send investment creation notification
        sendInvestmentCreationNotification(user, savedInvestment)
        
        logger.info("Investment created successfully: ${savedInvestment.id}")
        return mapToInvestmentResponse(savedInvestment)
    }
    
    @Transactional(readOnly = true)
    fun getInvestmentById(investmentId: UUID): InvestmentResponse {
        val investment = investmentRepository.findById(investmentId)
            .orElseThrow { InvestmentNotFoundException("Investment not found with ID: $investmentId") }
        
        return mapToInvestmentResponse(investment)
    }
    
    @Transactional(readOnly = true)
    fun getInvestmentsByUserId(userId: UUID): List<InvestmentResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return investmentRepository.findActiveInvestmentsByUserIdOrderByPurchaseDateDesc(userId)
            .map { mapToInvestmentResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun getInvestmentsByAssetType(userId: UUID, assetType: AssetType): List<InvestmentResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return investmentRepository.findInvestmentsByUserAndAssetTypeOrderByValueDesc(userId, assetType)
            .map { mapToInvestmentResponse(it) }
    }
    
    fun updateInvestmentPrice(investmentId: UUID, newPrice: BigDecimal): InvestmentResponse {
        logger.info("Updating investment price: $investmentId to $newPrice")
        
        val investment = investmentRepository.findById(investmentId)
            .orElseThrow { InvestmentNotFoundException("Investment not found with ID: $investmentId") }
        
        if (newPrice <= BigDecimal.ZERO) {
            throw ValidationException("Price must be positive")
        }
        
        val oldPrice = investment.currentPrice
        investment.currentPrice = newPrice
        investment.updatedAt = LocalDateTime.now()
        
        val updatedInvestment = investmentRepository.save(investment)
        
        // Check for significant price changes and send alerts
        val priceChangePercentage = calculatePriceChangePercentage(oldPrice, newPrice)
        if (priceChangePercentage.abs() >= BigDecimal("10")) { // 10% threshold
            sendPriceAlertNotification(investment.user, updatedInvestment, priceChangePercentage)
        }
        
        logger.info("Investment price updated successfully: $investmentId")
        return mapToInvestmentResponse(updatedInvestment)
    }
    
    fun sellInvestment(investmentId: UUID, request: SellInvestmentRequest): InvestmentResponse {
        logger.info("Selling investment: $investmentId, quantity: ${request.quantity}")
        
        val investment = investmentRepository.findById(investmentId)
            .orElseThrow { InvestmentNotFoundException("Investment not found with ID: $investmentId") }
        
        if (investment.status != InvestmentStatus.ACTIVE) {
            throw BusinessRuleException("Cannot sell inactive investment")
        }
        
        if (request.quantity <= BigDecimal.ZERO) {
            throw ValidationException("Quantity must be positive")
        }
        
        if (request.quantity > investment.quantity) {
            throw ValidationException("Cannot sell more than owned quantity")
        }
        
        if (request.salePrice <= BigDecimal.ZERO) {
            throw ValidationException("Sale price must be positive")
        }
        
        // Calculate gain/loss
        val totalSaleValue = request.quantity.multiply(request.salePrice)
        val totalPurchaseValue = request.quantity.multiply(investment.purchasePrice)
        val gainLoss = totalSaleValue.subtract(totalPurchaseValue)
        
        if (request.quantity == investment.quantity) {
            // Selling entire position
            investment.status = InvestmentStatus.SOLD
            investment.quantity = BigDecimal.ZERO
        } else {
            // Partial sale
            investment.quantity = investment.quantity.subtract(request.quantity)
        }
        
        investment.updatedAt = LocalDateTime.now()
        
        val updatedInvestment = investmentRepository.save(investment)
        
        // Send sale notification
        sendInvestmentSaleNotification(investment.user, updatedInvestment, request.quantity, request.salePrice, gainLoss)
        
        logger.info("Investment sold successfully: $investmentId")
        return mapToInvestmentResponse(updatedInvestment)
    }
    
    @Transactional(readOnly = true)
    fun getPortfolioSummary(userId: UUID): PortfolioSummaryResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        val activeInvestments = investmentRepository.findActiveInvestmentsByUserIdOrderByPurchaseDateDesc(userId)
        
        val totalInvestmentValue = investmentRepository.getTotalInvestmentValueByUserId(userId) ?: BigDecimal.ZERO
        val currentPortfolioValue = investmentRepository.getCurrentPortfolioValueByUserId(userId) ?: BigDecimal.ZERO
        val totalGainLoss = investmentRepository.getTotalGainLossByUserId(userId) ?: BigDecimal.ZERO
        
        val gainLossPercentage = if (totalInvestmentValue > BigDecimal.ZERO) {
            totalGainLoss.divide(totalInvestmentValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100))
        } else {
            BigDecimal.ZERO
        }
        
        // Get portfolio allocation
        val allocation = investmentRepository.getPortfolioAllocationByUserId(userId)
            .map { result ->
                AssetAllocation(
                    assetType = result[0] as AssetType,
                    value = result[1] as BigDecimal,
                    percentage = if (currentPortfolioValue > BigDecimal.ZERO) {
                        (result[1] as BigDecimal).divide(currentPortfolioValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100))
                    } else BigDecimal.ZERO
                )
            }
        
        return PortfolioSummaryResponse(
            totalInvestments = activeInvestments.size.toLong(),
            totalInvestmentValue = totalInvestmentValue,
            currentPortfolioValue = currentPortfolioValue,
            totalGainLoss = totalGainLoss,
            gainLossPercentage = gainLossPercentage,
            topPerformingInvestment = findTopPerformingInvestment(activeInvestments),
            worstPerformingInvestment = findWorstPerformingInvestment(activeInvestments),
            assetAllocation = allocation
        )
    }
    
    @Transactional(readOnly = true)
    fun getInvestmentPerformance(investmentId: UUID): InvestmentPerformanceResponse {
        val investment = investmentRepository.findById(investmentId)
            .orElseThrow { InvestmentNotFoundException("Investment not found with ID: $investmentId") }
        
        val currentValue = investment.quantity.multiply(investment.currentPrice)
        val purchaseValue = investment.quantity.multiply(investment.purchasePrice)
        val gainLoss = currentValue.subtract(purchaseValue)
        val gainLossPercentage = if (purchaseValue > BigDecimal.ZERO) {
            gainLoss.divide(purchaseValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100))
        } else BigDecimal.ZERO
        
        return InvestmentPerformanceResponse(
            investmentId = investment.id,
            symbol = investment.symbol,
            name = investment.name,
            quantity = investment.quantity,
            purchasePrice = investment.purchasePrice,
            currentPrice = investment.currentPrice,
            purchaseValue = purchaseValue,
            currentValue = currentValue,
            gainLoss = gainLoss,
            gainLossPercentage = gainLossPercentage,
            daysSincePurchase = java.time.temporal.ChronoUnit.DAYS.between(investment.purchaseDate, LocalDateTime.now()),
            annualizedReturn = calculateAnnualizedReturn(gainLossPercentage, investment.purchaseDate)
        )
    }
    
    fun updateAllInvestmentPrices() {
        logger.info("Updating all investment prices")
        
        val activeInvestments = investmentRepository.findByStatus(InvestmentStatus.ACTIVE)
        
        activeInvestments.forEach { investment ->
            try {
                // In a real implementation, this would fetch prices from external APIs
                val newPrice = simulateMarketPrice(investment.currentPrice)
                
                val oldPrice = investment.currentPrice
                investment.currentPrice = newPrice
                investment.updatedAt = LocalDateTime.now()
                
                investmentRepository.save(investment)
                
                // Check for significant price changes
                val priceChangePercentage = calculatePriceChangePercentage(oldPrice, newPrice)
                if (priceChangePercentage.abs() >= BigDecimal("15")) { // 15% threshold for batch updates
                    sendPriceAlertNotification(investment.user, investment, priceChangePercentage)
                }
                
            } catch (e: Exception) {
                logger.error("Failed to update price for investment: ${investment.id}", e)
            }
        }
        
        logger.info("Investment prices updated for ${activeInvestments.size} investments")
    }
    
    @Transactional(readOnly = true)
    fun searchInvestments(userId: UUID, searchTerm: String): List<InvestmentResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return investmentRepository.searchUserInvestmentsBySymbolOrName(userId, searchTerm)
            .map { mapToInvestmentResponse(it) }
    }
    
    private fun updateExistingInvestment(existingInvestment: Investment, request: CreateInvestmentRequest): InvestmentResponse {
        // Calculate new average cost
        val totalQuantity = existingInvestment.quantity.add(request.quantity)
        val totalValue = existingInvestment.quantity.multiply(existingInvestment.purchasePrice)
            .add(request.quantity.multiply(request.purchasePrice))
        val averagePrice = totalValue.divide(totalQuantity, 4, RoundingMode.HALF_UP)
        
        existingInvestment.quantity = totalQuantity
        existingInvestment.purchasePrice = averagePrice
        existingInvestment.updatedAt = LocalDateTime.now()
        
        val updatedInvestment = investmentRepository.save(existingInvestment)
        
        // Send update notification
        sendInvestmentUpdateNotification(existingInvestment.user, updatedInvestment, request.quantity)
        
        return mapToInvestmentResponse(updatedInvestment)
    }
    
    private fun calculatePriceChangePercentage(oldPrice: BigDecimal, newPrice: BigDecimal): BigDecimal {
        if (oldPrice == BigDecimal.ZERO) return BigDecimal.ZERO
        return newPrice.subtract(oldPrice).divide(oldPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100))
    }
    
    private fun calculateAnnualizedReturn(totalReturn: BigDecimal, purchaseDate: LocalDateTime): BigDecimal {
        val daysSincePurchase = java.time.temporal.ChronoUnit.DAYS.between(purchaseDate, LocalDateTime.now())
        if (daysSincePurchase <= 0) return BigDecimal.ZERO
        
        val yearsHeld = BigDecimal(daysSincePurchase).divide(BigDecimal(365), 4, RoundingMode.HALF_UP)
        if (yearsHeld == BigDecimal.ZERO) return BigDecimal.ZERO
        
        return totalReturn.divide(yearsHeld, 4, RoundingMode.HALF_UP)
    }
    
    private fun simulateMarketPrice(currentPrice: BigDecimal): BigDecimal {
        // Simulate market price changes (-5% to +5%)
        val changePercentage = (Math.random() - 0.5) * 0.1 // -5% to +5%
        val change = currentPrice.multiply(BigDecimal(changePercentage))
        return currentPrice.add(change).max(BigDecimal("0.01")) // Minimum price of 0.01
    }
    
    private fun findTopPerformingInvestment(investments: List<Investment>): String? {
        return investments.maxByOrNull { investment ->
            val gainLoss = investment.currentPrice.subtract(investment.purchasePrice)
            gainLoss.divide(investment.purchasePrice, 4, RoundingMode.HALF_UP)
        }?.symbol
    }
    
    private fun findWorstPerformingInvestment(investments: List<Investment>): String? {
        return investments.minByOrNull { investment ->
            val gainLoss = investment.currentPrice.subtract(investment.purchasePrice)
            gainLoss.divide(investment.purchasePrice, 4, RoundingMode.HALF_UP)
        }?.symbol
    }
    
    private fun sendInvestmentCreationNotification(user: User, investment: Investment) {
        try {
            val totalValue = investment.quantity.multiply(investment.purchasePrice)
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Investment Purchase",
                totalValue,
                "GBP",
                "You have purchased ${investment.quantity} shares of ${investment.symbol} at £${investment.purchasePrice} per share."
            )
        } catch (e: Exception) {
            logger.error("Failed to send investment creation notification", e)
        }
    }
    
    private fun sendInvestmentUpdateNotification(user: User, investment: Investment, additionalQuantity: BigDecimal) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Investment Updated",
                BigDecimal.ZERO,
                "GBP",
                "You have added $additionalQuantity shares to your ${investment.symbol} position. Total quantity: ${investment.quantity}."
            )
        } catch (e: Exception) {
            logger.error("Failed to send investment update notification", e)
        }
    }
    
    private fun sendInvestmentSaleNotification(user: User, investment: Investment, quantity: BigDecimal, salePrice: BigDecimal, gainLoss: BigDecimal) {
        try {
            val totalSaleValue = quantity.multiply(salePrice)
            val gainLossText = if (gainLoss >= BigDecimal.ZERO) "gain of £$gainLoss" else "loss of £${gainLoss.abs()}"
            
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Investment Sale",
                totalSaleValue,
                "GBP",
                "You have sold $quantity shares of ${investment.symbol} at £$salePrice per share for a $gainLossText."
            )
        } catch (e: Exception) {
            logger.error("Failed to send investment sale notification", e)
        }
    }
    
    private fun sendPriceAlertNotification(user: User, investment: Investment, priceChangePercentage: BigDecimal) {
        try {
            val direction = if (priceChangePercentage >= BigDecimal.ZERO) "increased" else "decreased"
            val message = "${investment.symbol} has $direction by ${priceChangePercentage.abs()}% to £${investment.currentPrice}."
            
            emailService.sendSecurityAlert(
                user.email,
                user.firstName,
                "Investment Price Alert",
                message
            )
            
            // Send SMS for significant changes (>20%)
            if (priceChangePercentage.abs() >= BigDecimal("20")) {
                smsService.sendSecurityAlert(
                    user.phoneNumber,
                    "Investment Alert: $message"
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to send price alert notification", e)
        }
    }
    
    private fun mapToInvestmentResponse(investment: Investment): InvestmentResponse {
        val currentValue = investment.quantity.multiply(investment.currentPrice)
        val purchaseValue = investment.quantity.multiply(investment.purchasePrice)
        val gainLoss = currentValue.subtract(purchaseValue)
        val gainLossPercentage = if (purchaseValue > BigDecimal.ZERO) {
            gainLoss.divide(purchaseValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100))
        } else BigDecimal.ZERO
        
        return InvestmentResponse(
            id = investment.id,
            userId = investment.user.id,
            symbol = investment.symbol,
            name = investment.name,
            assetType = investment.assetType,
            quantity = investment.quantity,
            purchasePrice = investment.purchasePrice,
            currentPrice = investment.currentPrice,
            purchaseValue = purchaseValue,
            currentValue = currentValue,
            gainLoss = gainLoss,
            gainLossPercentage = gainLossPercentage,
            status = investment.status,
            purchaseDate = investment.purchaseDate,
            createdAt = investment.createdAt,
            updatedAt = investment.updatedAt
        )
    }
}