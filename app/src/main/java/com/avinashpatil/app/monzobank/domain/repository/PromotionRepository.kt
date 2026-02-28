package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val type: PromotionType,
    val category: PromotionCategory,
    val discountType: DiscountType,
    val discountValue: BigDecimal,
    val minPurchaseAmount: BigDecimal?,
    val maxDiscountAmount: BigDecimal?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isActive: Boolean,
    val usageLimit: Int?,
    val usageCount: Int,
    val targetAudience: List<String>,
    val terms: String,
    val imageUrl: String?,
    val priority: Int,
    val createdDate: LocalDateTime
)

data class UserPromotion(
    val id: String,
    val userId: String,
    val promotionId: String,
    val status: UserPromotionStatus,
    val usedDate: LocalDateTime?,
    val discountAmount: BigDecimal?,
    val transactionId: String?,
    val expiryDate: LocalDateTime,
    val notificationSent: Boolean
)

data class PromotionCode(
    val code: String,
    val promotionId: String,
    val isActive: Boolean,
    val usageLimit: Int?,
    val usageCount: Int,
    val createdDate: LocalDateTime,
    val expiryDate: LocalDateTime?
)

data class PromotionAnalytics(
    val promotionId: String,
    val totalViews: Int,
    val totalClicks: Int,
    val totalRedemptions: Int,
    val totalRevenue: BigDecimal,
    val conversionRate: Double,
    val averageOrderValue: BigDecimal,
    val topUserSegments: List<String>
)

enum class PromotionType {
    DISCOUNT,
    CASHBACK,
    FREE_SHIPPING,
    BUY_ONE_GET_ONE,
    LOYALTY_POINTS,
    REFERRAL_BONUS
}

enum class PromotionCategory {
    GENERAL,
    NEW_USER,
    LOYALTY,
    SEASONAL,
    FLASH_SALE,
    CATEGORY_SPECIFIC,
    PAYMENT_METHOD
}

enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_ITEM,
    POINTS_MULTIPLIER
}

enum class UserPromotionStatus {
    AVAILABLE,
    USED,
    EXPIRED,
    CANCELLED
}

interface PromotionRepository {
    suspend fun getActivePromotions(): Result<List<Promotion>>
    suspend fun getPromotionsByCategory(category: PromotionCategory): Result<List<Promotion>>
    suspend fun getPromotion(promotionId: String): Result<Promotion?>
    suspend fun getUserPromotions(userId: String): Result<List<UserPromotion>>
    suspend fun getAvailablePromotions(userId: String): Result<List<Promotion>>
    suspend fun createPromotion(promotion: Promotion): Result<String>
    suspend fun updatePromotion(promotion: Promotion): Result<Unit>
    suspend fun deletePromotion(promotionId: String): Result<Unit>
    suspend fun assignPromotionToUser(userId: String, promotionId: String): Result<UserPromotion>
    suspend fun usePromotion(userId: String, promotionId: String, transactionAmount: BigDecimal): Result<BigDecimal>
    suspend fun validatePromotionCode(code: String): Result<PromotionCode?>
    suspend fun applyPromotionCode(userId: String, code: String): Result<UserPromotion>
    suspend fun getPromotionAnalytics(promotionId: String): Result<PromotionAnalytics>
    suspend fun getExpiringPromotions(userId: String): Result<List<UserPromotion>>
    suspend fun getFeaturedPromotions(limit: Int): Result<List<Promotion>>
    suspend fun searchPromotions(query: String): Result<List<Promotion>>
    suspend fun getPromotionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Result<List<Promotion>>
    suspend fun calculateDiscount(promotionId: String, amount: BigDecimal): Result<BigDecimal>
    suspend fun trackPromotionView(promotionId: String, userId: String): Result<Unit>
    suspend fun trackPromotionClick(promotionId: String, userId: String): Result<Unit>
}