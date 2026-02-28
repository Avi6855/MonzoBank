package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepositoryImpl @Inject constructor() : PromotionRepository {
    
    private val promotions = mutableListOf<Promotion>()
    private val userPromotions = mutableListOf<UserPromotion>()
    private val promotionCodes = mutableListOf<PromotionCode>()
    private val promotionViews = mutableMapOf<String, Int>()
    private val promotionClicks = mutableMapOf<String, Int>()
    
    init {
        initializeMockData()
    }
    
    override suspend fun getActivePromotions(): Result<List<Promotion>> {
        return try {
            val now = LocalDateTime.now()
            val activePromotions = promotions.filter { 
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now) &&
                (it.usageLimit == null || it.usageCount < it.usageLimit)
            }.sortedBy { it.priority }
            
            Result.success(activePromotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPromotionsByCategory(category: PromotionCategory): Result<List<Promotion>> {
        return try {
            val now = LocalDateTime.now()
            val categoryPromotions = promotions.filter { 
                it.category == category &&
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now)
            }.sortedBy { it.priority }
            
            Result.success(categoryPromotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPromotion(promotionId: String): Result<Promotion?> {
        return try {
            val promotion = promotions.find { it.id == promotionId }
            Result.success(promotion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserPromotions(userId: String): Result<List<UserPromotion>> {
        return try {
            val userPromos = userPromotions.filter { it.userId == userId }
                .sortedByDescending { it.expiryDate }
            Result.success(userPromos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAvailablePromotions(userId: String): Result<List<Promotion>> {
        return try {
            val now = LocalDateTime.now()
            val userPromoIds = userPromotions.filter { 
                it.userId == userId && it.status == UserPromotionStatus.AVAILABLE 
            }.map { it.promotionId }
            
            val availablePromotions = promotions.filter { promotion ->
                promotion.isActive &&
                promotion.startDate.isBefore(now) &&
                promotion.endDate.isAfter(now) &&
                (promotion.usageLimit == null || promotion.usageCount < promotion.usageLimit) &&
                (userPromoIds.contains(promotion.id) || promotion.targetAudience.isEmpty())
            }.sortedBy { it.priority }
            
            Result.success(availablePromotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPromotion(promotion: Promotion): Result<String> {
        return try {
            promotions.add(promotion)
            Result.success(promotion.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePromotion(promotion: Promotion): Result<Unit> {
        return try {
            val index = promotions.indexOfFirst { it.id == promotion.id }
            if (index != -1) {
                promotions[index] = promotion
                Result.success(Unit)
            } else {
                Result.failure(Exception("Promotion not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deletePromotion(promotionId: String): Result<Unit> {
        return try {
            val removed = promotions.removeIf { it.id == promotionId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Promotion not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun assignPromotionToUser(userId: String, promotionId: String): Result<UserPromotion> {
        return try {
            val promotion = promotions.find { it.id == promotionId }
                ?: return Result.failure(Exception("Promotion not found"))
            
            // Check if user already has this promotion
            val existingUserPromo = userPromotions.find { 
                it.userId == userId && it.promotionId == promotionId 
            }
            if (existingUserPromo != null) {
                return Result.success(existingUserPromo)
            }
            
            val userPromotion = UserPromotion(
                id = UUID.randomUUID().toString(),
                userId = userId,
                promotionId = promotionId,
                status = UserPromotionStatus.AVAILABLE,
                usedDate = null,
                discountAmount = null,
                transactionId = null,
                expiryDate = promotion.endDate,
                notificationSent = false
            )
            
            userPromotions.add(userPromotion)
            Result.success(userPromotion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun usePromotion(userId: String, promotionId: String, transactionAmount: BigDecimal): Result<BigDecimal> {
        return try {
            val userPromotion = userPromotions.find { 
                it.userId == userId && 
                it.promotionId == promotionId && 
                it.status == UserPromotionStatus.AVAILABLE 
            } ?: return Result.failure(Exception("Promotion not available for user"))
            
            val promotion = promotions.find { it.id == promotionId }
                ?: return Result.failure(Exception("Promotion not found"))
            
            // Check minimum purchase amount
            if (promotion.minPurchaseAmount != null && transactionAmount < promotion.minPurchaseAmount) {
                return Result.failure(Exception("Minimum purchase amount not met"))
            }
            
            // Calculate discount
            val discountAmount = calculateDiscountAmount(promotion, transactionAmount)
            
            // Update user promotion status
            val index = userPromotions.indexOfFirst { it.id == userPromotion.id }
            if (index != -1) {
                val updatedUserPromotion = userPromotion.copy(
                    status = UserPromotionStatus.USED,
                    usedDate = LocalDateTime.now(),
                    discountAmount = discountAmount
                )
                userPromotions[index] = updatedUserPromotion
            }
            
            // Update promotion usage count
            val promoIndex = promotions.indexOfFirst { it.id == promotionId }
            if (promoIndex != -1) {
                val updatedPromotion = promotions[promoIndex].copy(
                    usageCount = promotions[promoIndex].usageCount + 1
                )
                promotions[promoIndex] = updatedPromotion
            }
            
            Result.success(discountAmount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validatePromotionCode(code: String): Result<PromotionCode?> {
        return try {
            val now = LocalDateTime.now()
            val promotionCode = promotionCodes.find { 
                it.code == code && 
                it.isActive && 
                (it.expiryDate == null || it.expiryDate.isAfter(now)) &&
                (it.usageLimit == null || it.usageCount < it.usageLimit)
            }
            Result.success(promotionCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun applyPromotionCode(userId: String, code: String): Result<UserPromotion> {
        return try {
            val promotionCode = validatePromotionCode(code).getOrNull()
                ?: return Result.failure(Exception("Invalid or expired promotion code"))
            
            val userPromotion = assignPromotionToUser(userId, promotionCode.promotionId).getOrThrow()
            
            // Update code usage count
            val codeIndex = promotionCodes.indexOfFirst { it.code == code }
            if (codeIndex != -1) {
                val updatedCode = promotionCodes[codeIndex].copy(
                    usageCount = promotionCodes[codeIndex].usageCount + 1
                )
                promotionCodes[codeIndex] = updatedCode
            }
            
            Result.success(userPromotion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPromotionAnalytics(promotionId: String): Result<PromotionAnalytics> {
        return try {
            val promotion = promotions.find { it.id == promotionId }
                ?: return Result.failure(Exception("Promotion not found"))
            
            val views = promotionViews[promotionId] ?: 0
            val clicks = promotionClicks[promotionId] ?: 0
            val redemptions = userPromotions.count { 
                it.promotionId == promotionId && it.status == UserPromotionStatus.USED 
            }
            
            val totalRevenue = userPromotions.filter { 
                it.promotionId == promotionId && it.status == UserPromotionStatus.USED 
            }.sumOf { it.discountAmount ?: BigDecimal.ZERO }
            
            val conversionRate = if (views > 0) redemptions.toDouble() / views else 0.0
            val averageOrderValue = if (redemptions > 0) totalRevenue.divide(BigDecimal(redemptions), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO
            
            val analytics = PromotionAnalytics(
                promotionId = promotionId,
                totalViews = views,
                totalClicks = clicks,
                totalRedemptions = redemptions,
                totalRevenue = totalRevenue,
                conversionRate = conversionRate,
                averageOrderValue = averageOrderValue,
                topUserSegments = listOf("Premium Users", "New Users") // Mock data
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExpiringPromotions(userId: String): Result<List<UserPromotion>> {
        return try {
            val expiringDate = LocalDateTime.now().plusDays(7) // Expiring in 7 days
            val expiringPromotions = userPromotions.filter { 
                it.userId == userId &&
                it.status == UserPromotionStatus.AVAILABLE &&
                it.expiryDate.isBefore(expiringDate)
            }.sortedBy { it.expiryDate }
            
            Result.success(expiringPromotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeaturedPromotions(limit: Int): Result<List<Promotion>> {
        return try {
            val now = LocalDateTime.now()
            val featuredPromotions = promotions.filter { 
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now) &&
                it.priority <= 3 // Top priority promotions
            }.sortedBy { it.priority }
                .take(limit)
            
            Result.success(featuredPromotions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchPromotions(query: String): Result<List<Promotion>> {
        return try {
            val now = LocalDateTime.now()
            val searchResults = promotions.filter { promotion ->
                promotion.isActive &&
                promotion.startDate.isBefore(now) &&
                promotion.endDate.isAfter(now) &&
                (promotion.title.contains(query, ignoreCase = true) ||
                 promotion.description.contains(query, ignoreCase = true))
            }.sortedBy { it.priority }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPromotionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Result<List<Promotion>> {
        return try {
            val promotionsInRange = promotions.filter { promotion ->
                promotion.startDate.isBefore(endDate) && promotion.endDate.isAfter(startDate)
            }.sortedBy { it.startDate }
            
            Result.success(promotionsInRange)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateDiscount(promotionId: String, amount: BigDecimal): Result<BigDecimal> {
        return try {
            val promotion = promotions.find { it.id == promotionId }
                ?: return Result.failure(Exception("Promotion not found"))
            
            val discountAmount = calculateDiscountAmount(promotion, amount)
            Result.success(discountAmount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun trackPromotionView(promotionId: String, userId: String): Result<Unit> {
        return try {
            promotionViews[promotionId] = (promotionViews[promotionId] ?: 0) + 1
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun trackPromotionClick(promotionId: String, userId: String): Result<Unit> {
        return try {
            promotionClicks[promotionId] = (promotionClicks[promotionId] ?: 0) + 1
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateDiscountAmount(promotion: Promotion, amount: BigDecimal): BigDecimal {
        val discount = when (promotion.discountType) {
            DiscountType.PERCENTAGE -> {
                amount.multiply(promotion.discountValue)
                    .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
            }
            DiscountType.FIXED_AMOUNT -> promotion.discountValue
            DiscountType.FREE_ITEM -> BigDecimal.ZERO // Handle separately
            DiscountType.POINTS_MULTIPLIER -> BigDecimal.ZERO // Handle separately
        }
        
        return if (promotion.maxDiscountAmount != null) {
            discount.min(promotion.maxDiscountAmount)
        } else {
            discount
        }
    }
    
    private fun initializeMockData() {
        val mockPromotions = listOf(
            Promotion(
                id = UUID.randomUUID().toString(),
                title = "Welcome Bonus",
                description = "Get 20% off your first purchase",
                type = PromotionType.DISCOUNT,
                category = PromotionCategory.NEW_USER,
                discountType = DiscountType.PERCENTAGE,
                discountValue = BigDecimal("20"),
                minPurchaseAmount = BigDecimal("50"),
                maxDiscountAmount = BigDecimal("100"),
                startDate = LocalDateTime.now().minusDays(30),
                endDate = LocalDateTime.now().plusDays(30),
                isActive = true,
                usageLimit = 1000,
                usageCount = 150,
                targetAudience = listOf("new_users"),
                terms = "Valid for new users only",
                imageUrl = null,
                priority = 1,
                createdDate = LocalDateTime.now().minusDays(30)
            ),
            Promotion(
                id = UUID.randomUUID().toString(),
                title = "Flash Sale",
                description = "Limited time 50% off selected items",
                type = PromotionType.DISCOUNT,
                category = PromotionCategory.FLASH_SALE,
                discountType = DiscountType.PERCENTAGE,
                discountValue = BigDecimal("50"),
                minPurchaseAmount = null,
                maxDiscountAmount = BigDecimal("200"),
                startDate = LocalDateTime.now().minusHours(2),
                endDate = LocalDateTime.now().plusHours(22),
                isActive = true,
                usageLimit = 500,
                usageCount = 75,
                targetAudience = emptyList(),
                terms = "Limited time offer",
                imageUrl = null,
                priority = 2,
                createdDate = LocalDateTime.now().minusHours(2)
            ),
            Promotion(
                id = UUID.randomUUID().toString(),
                title = "Loyalty Cashback",
                description = "Earn 5% cashback on all purchases",
                type = PromotionType.CASHBACK,
                category = PromotionCategory.LOYALTY,
                discountType = DiscountType.PERCENTAGE,
                discountValue = BigDecimal("5"),
                minPurchaseAmount = BigDecimal("25"),
                maxDiscountAmount = BigDecimal("50"),
                startDate = LocalDateTime.now().minusMonths(1),
                endDate = LocalDateTime.now().plusMonths(2),
                isActive = true,
                usageLimit = null,
                usageCount = 320,
                targetAudience = listOf("loyalty_members"),
                terms = "For loyalty members only",
                imageUrl = null,
                priority = 3,
                createdDate = LocalDateTime.now().minusMonths(1)
            )
        )
        
        promotions.addAll(mockPromotions)
        
        // Add some promotion codes
        val mockCodes = listOf(
            PromotionCode(
                code = "WELCOME20",
                promotionId = mockPromotions[0].id,
                isActive = true,
                usageLimit = 100,
                usageCount = 25,
                createdDate = LocalDateTime.now().minusDays(30),
                expiryDate = LocalDateTime.now().plusDays(30)
            ),
            PromotionCode(
                code = "FLASH50",
                promotionId = mockPromotions[1].id,
                isActive = true,
                usageLimit = 50,
                usageCount = 12,
                createdDate = LocalDateTime.now().minusHours(2),
                expiryDate = LocalDateTime.now().plusHours(22)
            )
        )
        
        promotionCodes.addAll(mockCodes)
    }
}