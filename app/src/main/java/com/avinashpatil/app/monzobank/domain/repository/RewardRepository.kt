package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Reward(
    val id: String,
    val name: String,
    val description: String,
    val pointsRequired: Int,
    val category: RewardCategory,
    val value: BigDecimal,
    val isAvailable: Boolean = true,
    val expiryDate: LocalDateTime? = null,
    val imageUrl: String? = null,
    val termsAndConditions: String? = null
)

data class UserReward(
    val id: String,
    val userId: String,
    val rewardId: String,
    val pointsUsed: Int,
    val status: RewardStatus,
    val redeemedAt: LocalDateTime,
    val expiresAt: LocalDateTime? = null,
    val redemptionCode: String? = null
)

data class RewardPoints(
    val userId: String,
    val totalPoints: Int,
    val availablePoints: Int,
    val usedPoints: Int,
    val expiringPoints: Int,
    val nextExpiryDate: LocalDateTime? = null,
    val lastUpdated: LocalDateTime
)

data class PointsTransaction(
    val id: String,
    val userId: String,
    val points: Int,
    val transactionType: PointsTransactionType,
    val description: String,
    val relatedTransactionId: String? = null,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime? = null
)

enum class RewardCategory {
    CASHBACK,
    GIFT_CARDS,
    TRAVEL,
    SHOPPING,
    DINING,
    ENTERTAINMENT,
    CHARITY,
    EXPERIENCES,
    MERCHANDISE
}

enum class RewardStatus {
    REDEEMED,
    USED,
    EXPIRED,
    CANCELLED
}

enum class PointsTransactionType {
    EARNED,
    REDEEMED,
    EXPIRED,
    BONUS,
    ADJUSTMENT
}

interface RewardRepository {
    suspend fun getAvailableRewards(): Result<List<Reward>>
    suspend fun getRewardsByCategory(category: RewardCategory): Result<List<Reward>>
    suspend fun getReward(rewardId: String): Result<Reward?>
    
    suspend fun getUserRewards(userId: String): Result<List<UserReward>>
    suspend fun redeemReward(userId: String, rewardId: String): Result<UserReward>
    
    suspend fun getUserPoints(userId: String): Result<RewardPoints>
    suspend fun addPoints(userId: String, points: Int, description: String, relatedTransactionId: String? = null): Result<Unit>
    suspend fun deductPoints(userId: String, points: Int, description: String): Result<Unit>
    
    suspend fun getPointsHistory(userId: String): Result<List<PointsTransaction>>
    suspend fun getExpiringPoints(userId: String): Result<List<PointsTransaction>>
    
    suspend fun calculatePointsForTransaction(transactionAmount: BigDecimal): Result<Int>
    suspend fun getRewardRecommendations(userId: String): Result<List<Reward>>
}