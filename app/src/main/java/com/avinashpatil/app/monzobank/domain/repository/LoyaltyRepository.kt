package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class LoyaltyProgram(
    val id: String,
    val name: String,
    val description: String,
    val programType: LoyaltyProgramType,
    val isActive: Boolean = true,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val benefits: List<LoyaltyBenefit> = emptyList()
)

data class LoyaltyBenefit(
    val id: String,
    val programId: String,
    val benefitType: BenefitType,
    val title: String,
    val description: String,
    val value: BigDecimal,
    val requirements: String? = null
)

data class UserLoyaltyStatus(
    val userId: String,
    val programId: String,
    val tier: LoyaltyTier,
    val points: Int,
    val nextTierPoints: Int,
    val joinedDate: LocalDateTime,
    val lastActivityDate: LocalDateTime,
    val benefits: List<String> = emptyList()
)

data class LoyaltyActivity(
    val id: String,
    val userId: String,
    val programId: String,
    val activityType: ActivityType,
    val points: Int,
    val description: String,
    val relatedTransactionId: String? = null,
    val createdAt: LocalDateTime
)

enum class LoyaltyProgramType {
    POINTS_BASED,
    TIER_BASED,
    CASHBACK,
    HYBRID
}

enum class BenefitType {
    CASHBACK_BONUS,
    FEE_WAIVER,
    INTEREST_BONUS,
    PRIORITY_SUPPORT,
    EXCLUSIVE_OFFERS,
    TRAVEL_BENEFITS,
    INSURANCE_COVERAGE
}

enum class LoyaltyTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND
}

enum class ActivityType {
    POINTS_EARNED,
    POINTS_REDEEMED,
    TIER_UPGRADE,
    TIER_DOWNGRADE,
    BENEFIT_CLAIMED,
    BONUS_AWARDED
}

interface LoyaltyRepository {
    suspend fun getLoyaltyPrograms(): Result<List<LoyaltyProgram>>
    suspend fun getLoyaltyProgram(programId: String): Result<LoyaltyProgram?>
    
    suspend fun getUserLoyaltyStatus(userId: String): Result<List<UserLoyaltyStatus>>
    suspend fun getUserLoyaltyStatus(userId: String, programId: String): Result<UserLoyaltyStatus?>
    suspend fun joinLoyaltyProgram(userId: String, programId: String): Result<UserLoyaltyStatus>
    suspend fun leaveLoyaltyProgram(userId: String, programId: String): Result<Unit>
    
    suspend fun addLoyaltyPoints(userId: String, programId: String, points: Int, description: String, relatedTransactionId: String? = null): Result<Unit>
    suspend fun redeemLoyaltyPoints(userId: String, programId: String, points: Int, description: String): Result<Unit>
    
    suspend fun getLoyaltyActivity(userId: String): Result<List<LoyaltyActivity>>
    suspend fun getLoyaltyActivity(userId: String, programId: String): Result<List<LoyaltyActivity>>
    
    suspend fun checkTierUpgrade(userId: String, programId: String): Result<LoyaltyTier?>
    suspend fun calculatePointsForTransaction(transactionAmount: BigDecimal, programId: String): Result<Int>
    suspend fun getAvailableBenefits(userId: String, programId: String): Result<List<LoyaltyBenefit>>
}