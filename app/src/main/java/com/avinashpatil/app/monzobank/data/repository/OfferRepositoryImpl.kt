package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfferRepositoryImpl @Inject constructor() : OfferRepository {
    
    private val offers = mutableListOf<Offer>()
    private val userOffers = mutableListOf<UserOffer>()
    private val offerRedemptions = mutableListOf<OfferRedemption>()
    private val offerViews = mutableMapOf<String, Int>()
    
    init {
        initializeMockData()
    }
    
    override suspend fun getActiveOffers(): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val activeOffers = offers.filter { 
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now) &&
                (it.usageLimit == null || it.usageCount < it.usageLimit)
            }.sortedBy { it.priority }
            
            Result.success(activeOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffersByCategory(category: OfferCategory): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val categoryOffers = offers.filter { 
                it.category == category &&
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now)
            }.sortedBy { it.priority }
            
            Result.success(categoryOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffersByType(type: OfferType): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val typeOffers = offers.filter { 
                it.type == type &&
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now)
            }.sortedBy { it.priority }
            
            Result.success(typeOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffer(offerId: String): Result<Offer?> {
        return try {
            val offer = offers.find { it.id == offerId }
            Result.success(offer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserOffers(userId: String): Result<List<UserOffer>> {
        return try {
            val userOffersList = userOffers.filter { it.userId == userId }
                .sortedByDescending { it.assignedDate }
            Result.success(userOffersList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAvailableOffers(userId: String): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val userOfferIds = userOffers.filter { 
                it.userId == userId && it.status == UserOfferStatus.ASSIGNED 
            }.map { it.offerId }
            
            val availableOffers = offers.filter { offer ->
                offer.isActive &&
                offer.startDate.isBefore(now) &&
                offer.endDate.isAfter(now) &&
                (offer.usageLimit == null || offer.usageCount < offer.usageLimit) &&
                (userOfferIds.contains(offer.id) || offer.targetAudience.isEmpty())
            }.sortedBy { it.priority }
            
            Result.success(availableOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createOffer(offer: Offer): Result<String> {
        return try {
            offers.add(offer)
            Result.success(offer.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateOffer(offer: Offer): Result<Unit> {
        return try {
            val index = offers.indexOfFirst { it.id == offer.id }
            if (index != -1) {
                offers[index] = offer
                Result.success(Unit)
            } else {
                Result.failure(Exception("Offer not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteOffer(offerId: String): Result<Unit> {
        return try {
            val removed = offers.removeIf { it.id == offerId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Offer not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun assignOfferToUser(userId: String, offerId: String): Result<UserOffer> {
        return try {
            val offer = offers.find { it.id == offerId }
                ?: return Result.failure(Exception("Offer not found"))
            
            // Check if user already has this offer
            val existingUserOffer = userOffers.find { 
                it.userId == userId && it.offerId == offerId 
            }
            if (existingUserOffer != null) {
                return Result.success(existingUserOffer)
            }
            
            val userOffer = UserOffer(
                id = UUID.randomUUID().toString(),
                userId = userId,
                offerId = offerId,
                status = UserOfferStatus.ASSIGNED,
                assignedDate = LocalDateTime.now(),
                usedDate = null,
                expiryDate = offer.endDate,
                benefitAmount = null,
                transactionId = null,
                notificationSent = false
            )
            
            userOffers.add(userOffer)
            Result.success(userOffer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun redeemOffer(userId: String, offerId: String, transactionId: String): Result<OfferRedemption> {
        return try {
            val userOffer = userOffers.find { 
                it.userId == userId && 
                it.offerId == offerId && 
                it.status == UserOfferStatus.ASSIGNED 
            } ?: return Result.failure(Exception("Offer not available for user"))
            
            val offer = offers.find { it.id == offerId }
                ?: return Result.failure(Exception("Offer not found"))
            
            // Calculate benefit amount
            val benefitAmount = calculateBenefitAmount(offer, BigDecimal("100.00")) // Mock transaction amount
            
            // Create redemption record
            val redemption = OfferRedemption(
                id = UUID.randomUUID().toString(),
                userId = userId,
                offerId = offerId,
                transactionId = transactionId,
                redemptionDate = LocalDateTime.now(),
                benefitAmount = benefitAmount,
                location = null, // Mock location
                merchantId = offer.merchantId
            )
            
            offerRedemptions.add(redemption)
            
            // Update user offer status
            val index = userOffers.indexOfFirst { it.id == userOffer.id }
            if (index != -1) {
                val updatedUserOffer = userOffer.copy(
                    status = UserOfferStatus.REDEEMED,
                    usedDate = LocalDateTime.now(),
                    benefitAmount = benefitAmount,
                    transactionId = transactionId
                )
                userOffers[index] = updatedUserOffer
            }
            
            // Update offer usage count
            val offerIndex = offers.indexOfFirst { it.id == offerId }
            if (offerIndex != -1) {
                val updatedOffer = offers[offerIndex].copy(
                    usageCount = offers[offerIndex].usageCount + 1
                )
                offers[offerIndex] = updatedOffer
            }
            
            Result.success(redemption)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOfferRedemptions(offerId: String): Result<List<OfferRedemption>> {
        return try {
            val redemptions = offerRedemptions.filter { it.offerId == offerId }
                .sortedByDescending { it.redemptionDate }
            Result.success(redemptions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserRedemptions(userId: String): Result<List<OfferRedemption>> {
        return try {
            val redemptions = offerRedemptions.filter { it.userId == userId }
                .sortedByDescending { it.redemptionDate }
            Result.success(redemptions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOfferAnalytics(offerId: String): Result<OfferAnalytics> {
        return try {
            val offer = offers.find { it.id == offerId }
                ?: return Result.failure(Exception("Offer not found"))
            
            val views = offerViews[offerId] ?: 0
            val assignments = userOffers.count { it.offerId == offerId }
            val redemptions = offerRedemptions.filter { it.offerId == offerId }
            
            val totalBenefitPaid = redemptions.sumOf { it.benefitAmount }
            val conversionRate = if (assignments > 0) redemptions.size.toDouble() / assignments else 0.0
            val averageBenefitAmount = if (redemptions.isNotEmpty()) {
                totalBenefitPaid.divide(BigDecimal(redemptions.size), 2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO
            
            val analytics = OfferAnalytics(
                offerId = offerId,
                totalViews = views,
                totalAssignments = assignments,
                totalRedemptions = redemptions.size,
                totalBenefitPaid = totalBenefitPaid,
                conversionRate = conversionRate,
                averageBenefitAmount = averageBenefitAmount,
                topLocations = listOf("London", "Manchester", "Birmingham"), // Mock data
                topUserSegments = listOf("Premium Users", "Young Adults") // Mock data
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExpiringOffers(userId: String): Result<List<UserOffer>> {
        return try {
            val expiringDate = LocalDateTime.now().plusDays(7) // Expiring in 7 days
            val expiringOffers = userOffers.filter { 
                it.userId == userId &&
                it.status == UserOfferStatus.ASSIGNED &&
                it.expiryDate.isBefore(expiringDate)
            }.sortedBy { it.expiryDate }
            
            Result.success(expiringOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeaturedOffers(limit: Int): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val featuredOffers = offers.filter { 
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now) &&
                it.priority <= 3 // Top priority offers
            }.sortedBy { it.priority }
                .take(limit)
            
            Result.success(featuredOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchOffers(query: String): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val searchResults = offers.filter { offer ->
                offer.isActive &&
                offer.startDate.isBefore(now) &&
                offer.endDate.isAfter(now) &&
                (offer.title.contains(query, ignoreCase = true) ||
                 offer.description.contains(query, ignoreCase = true) ||
                 offer.merchantName?.contains(query, ignoreCase = true) == true)
            }.sortedBy { it.priority }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffersByMerchant(merchantId: String): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val merchantOffers = offers.filter { 
                it.merchantId == merchantId &&
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now)
            }.sortedBy { it.priority }
            
            Result.success(merchantOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOffersByLocation(location: String): Result<List<Offer>> {
        return try {
            val now = LocalDateTime.now()
            val locationOffers = offers.filter { offer ->
                offer.isActive &&
                offer.startDate.isBefore(now) &&
                offer.endDate.isAfter(now) &&
                (offer.locations.isEmpty() || offer.locations.contains(location))
            }.sortedBy { it.priority }
            
            Result.success(locationOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun trackOfferView(offerId: String, userId: String): Result<Unit> {
        return try {
            offerViews[offerId] = (offerViews[offerId] ?: 0) + 1
            
            // Update user offer status to viewed if exists
            val userOfferIndex = userOffers.indexOfFirst { 
                it.userId == userId && it.offerId == offerId && it.status == UserOfferStatus.ASSIGNED 
            }
            if (userOfferIndex != -1) {
                val updatedUserOffer = userOffers[userOfferIndex].copy(
                    status = UserOfferStatus.VIEWED
                )
                userOffers[userOfferIndex] = updatedUserOffer
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPersonalizedOffers(userId: String): Result<List<Offer>> {
        return try {
            // Mock personalization logic - in real app, this would use ML/AI
            val now = LocalDateTime.now()
            val personalizedOffers = offers.filter { 
                it.isActive && 
                it.startDate.isBefore(now) && 
                it.endDate.isAfter(now) &&
                (it.category == OfferCategory.BANKING || it.category == OfferCategory.SHOPPING)
            }.sortedBy { it.priority }
                .take(5)
            
            Result.success(personalizedOffers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validateOfferEligibility(userId: String, offerId: String): Result<Boolean> {
        return try {
            val offer = offers.find { it.id == offerId }
                ?: return Result.failure(Exception("Offer not found"))
            
            val now = LocalDateTime.now()
            
            // Check basic eligibility
            val isEligible = offer.isActive &&
                    offer.startDate.isBefore(now) &&
                    offer.endDate.isAfter(now) &&
                    (offer.usageLimit == null || offer.usageCount < offer.usageLimit) &&
                    !userOffers.any { it.userId == userId && it.offerId == offerId && it.status == UserOfferStatus.REDEEMED }
            
            Result.success(isEligible)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateBenefitAmount(offer: Offer, transactionAmount: BigDecimal): BigDecimal {
        return when (offer.valueType) {
            OfferValueType.PERCENTAGE -> {
                val benefit = transactionAmount.multiply(offer.value)
                    .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
                if (offer.maxBenefit != null) benefit.min(offer.maxBenefit) else benefit
            }
            OfferValueType.FIXED_AMOUNT -> offer.value
            OfferValueType.POINTS -> offer.value // Points as benefit
            OfferValueType.MULTIPLIER -> transactionAmount.multiply(offer.value)
        }
    }
    
    private fun initializeMockData() {
        val mockOffers = listOf(
            Offer(
                id = UUID.randomUUID().toString(),
                title = "Welcome Cashback",
                description = "Get 5% cashback on your first purchase",
                type = OfferType.CASHBACK,
                category = OfferCategory.BANKING,
                value = BigDecimal("5.0"),
                valueType = OfferValueType.PERCENTAGE,
                minRequirement = BigDecimal("20.00"),
                maxBenefit = BigDecimal("50.00"),
                startDate = LocalDateTime.now().minusDays(30),
                endDate = LocalDateTime.now().plusDays(30),
                isActive = true,
                targetAudience = listOf("new_users"),
                eligibilityCriteria = listOf("First-time users"),
                terms = "Valid for new users only",
                imageUrl = null,
                priority = 1,
                usageLimit = 1000,
                usageCount = 150,
                merchantId = null,
                merchantName = null,
                createdDate = LocalDateTime.now().minusDays(30)
            ),
            Offer(
                id = UUID.randomUUID().toString(),
                title = "Shopping Discount",
                description = "20% off on all shopping purchases",
                type = OfferType.DISCOUNT,
                category = OfferCategory.SHOPPING,
                value = BigDecimal("20.0"),
                valueType = OfferValueType.PERCENTAGE,
                minRequirement = BigDecimal("50.00"),
                maxBenefit = BigDecimal("100.00"),
                startDate = LocalDateTime.now().minusHours(2),
                endDate = LocalDateTime.now().plusHours(22),
                isActive = true,
                targetAudience = emptyList(),
                eligibilityCriteria = listOf("Minimum purchase £50"),
                terms = "Limited time offer",
                imageUrl = null,
                priority = 2,
                usageLimit = 500,
                usageCount = 75,
                merchantId = "merchant_001",
                merchantName = "Fashion Store",
                createdDate = LocalDateTime.now().minusHours(2)
            ),
            Offer(
                id = UUID.randomUUID().toString(),
                title = "Dining Rewards",
                description = "Earn 3x points on dining purchases",
                type = OfferType.REWARD_POINTS,
                category = OfferCategory.DINING,
                value = BigDecimal("3.0"),
                valueType = OfferValueType.MULTIPLIER,
                minRequirement = BigDecimal("25.00"),
                maxBenefit = null,
                startDate = LocalDateTime.now().minusMonths(1),
                endDate = LocalDateTime.now().plusMonths(2),
                isActive = true,
                targetAudience = listOf("premium_users"),
                eligibilityCriteria = listOf("Premium account holders"),
                terms = "For premium members only",
                imageUrl = null,
                priority = 3,
                usageLimit = null,
                usageCount = 320,
                merchantId = "merchant_002",
                merchantName = "Restaurant Chain",
                createdDate = LocalDateTime.now().minusMonths(1)
            )
        )
        
        offers.addAll(mockOffers)
    }
}