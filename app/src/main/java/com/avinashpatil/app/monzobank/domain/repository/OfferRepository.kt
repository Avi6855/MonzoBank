package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val type: OfferType,
    val category: OfferCategory,
    val value: BigDecimal,
    val valueType: OfferValueType,
    val minRequirement: BigDecimal?,
    val maxBenefit: BigDecimal?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isActive: Boolean,
    val targetAudience: List<String>,
    val eligibilityCriteria: List<String>,
    val terms: String,
    val imageUrl: String?,
    val priority: Int,
    val usageLimit: Int?,
    val usageCount: Int,
    val merchantId: String?,
    val merchantName: String?,
    val locations: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdDate: LocalDateTime
)

data class UserOffer(
    val id: String,
    val userId: String,
    val offerId: String,
    val status: UserOfferStatus,
    val assignedDate: LocalDateTime,
    val usedDate: LocalDateTime?,
    val expiryDate: LocalDateTime,
    val benefitAmount: BigDecimal?,
    val transactionId: String?,
    val notificationSent: Boolean
)

data class OfferRedemption(
    val id: String,
    val userId: String,
    val offerId: String,
    val transactionId: String,
    val redemptionDate: LocalDateTime,
    val benefitAmount: BigDecimal,
    val location: String?,
    val merchantId: String?
)

data class OfferAnalytics(
    val offerId: String,
    val totalViews: Int,
    val totalAssignments: Int,
    val totalRedemptions: Int,
    val totalBenefitPaid: BigDecimal,
    val conversionRate: Double,
    val averageBenefitAmount: BigDecimal,
    val topLocations: List<String>,
    val topUserSegments: List<String>
)

enum class OfferType {
    CASHBACK,
    DISCOUNT,
    REWARD_POINTS,
    FREE_ITEM,
    UPGRADE,
    BONUS_INTEREST,
    FEE_WAIVER,
    GIFT_CARD
}

enum class OfferCategory {
    BANKING,
    SHOPPING,
    DINING,
    TRAVEL,
    ENTERTAINMENT,
    HEALTH,
    EDUCATION,
    UTILITIES,
    FUEL,
    GROCERIES,
    GENERAL
}

enum class OfferValueType {
    PERCENTAGE,
    FIXED_AMOUNT,
    POINTS,
    MULTIPLIER
}

enum class UserOfferStatus {
    ASSIGNED,
    VIEWED,
    REDEEMED,
    EXPIRED,
    CANCELLED
}

interface OfferRepository {
    suspend fun getActiveOffers(): Result<List<Offer>>
    suspend fun getOffersByCategory(category: OfferCategory): Result<List<Offer>>
    suspend fun getOffersByType(type: OfferType): Result<List<Offer>>
    suspend fun getOffer(offerId: String): Result<Offer?>
    suspend fun getUserOffers(userId: String): Result<List<UserOffer>>
    suspend fun getAvailableOffers(userId: String): Result<List<Offer>>
    suspend fun createOffer(offer: Offer): Result<String>
    suspend fun updateOffer(offer: Offer): Result<Unit>
    suspend fun deleteOffer(offerId: String): Result<Unit>
    suspend fun assignOfferToUser(userId: String, offerId: String): Result<UserOffer>
    suspend fun redeemOffer(userId: String, offerId: String, transactionId: String): Result<OfferRedemption>
    suspend fun getOfferRedemptions(offerId: String): Result<List<OfferRedemption>>
    suspend fun getUserRedemptions(userId: String): Result<List<OfferRedemption>>
    suspend fun getOfferAnalytics(offerId: String): Result<OfferAnalytics>
    suspend fun getExpiringOffers(userId: String): Result<List<UserOffer>>
    suspend fun getFeaturedOffers(limit: Int): Result<List<Offer>>
    suspend fun searchOffers(query: String): Result<List<Offer>>
    suspend fun getOffersByMerchant(merchantId: String): Result<List<Offer>>
    suspend fun getOffersByLocation(location: String): Result<List<Offer>>
    suspend fun trackOfferView(offerId: String, userId: String): Result<Unit>
    suspend fun getPersonalizedOffers(userId: String): Result<List<Offer>>
    suspend fun validateOfferEligibility(userId: String, offerId: String): Result<Boolean>
}