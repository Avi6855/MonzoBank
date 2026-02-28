package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.RewardRepository
import com.avinashpatil.app.monzobank.domain.repository.Reward
import com.avinashpatil.app.monzobank.domain.repository.UserReward
import com.avinashpatil.app.monzobank.domain.repository.RewardPoints
import com.avinashpatil.app.monzobank.domain.repository.PointsTransaction
import com.avinashpatil.app.monzobank.domain.repository.RewardCategory
import com.avinashpatil.app.monzobank.domain.repository.RewardStatus
import com.avinashpatil.app.monzobank.domain.repository.PointsTransactionType
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardRepositoryImpl @Inject constructor() : RewardRepository {
    
    private val rewards = mutableListOf<Reward>()
    private val userRewards = mutableListOf<UserReward>()
    private val userPoints = mutableMapOf<String, RewardPoints>()
    private val pointsTransactions = mutableListOf<PointsTransaction>()
    
    init {
        initializeMockRewards()
    }
    
    override suspend fun getAvailableRewards(): Result<List<Reward>> {
        return try {
            val availableRewards = rewards.filter { it.isAvailable }
            Result.success(availableRewards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRewardsByCategory(category: RewardCategory): Result<List<Reward>> {
        return try {
            val categoryRewards = rewards.filter { it.category == category && it.isAvailable }
            Result.success(categoryRewards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReward(rewardId: String): Result<Reward?> {
        return try {
            val reward = rewards.find { it.id == rewardId }
            Result.success(reward)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserRewards(userId: String): Result<List<UserReward>> {
        return try {
            val userRewardsList = userRewards.filter { it.userId == userId }
                .sortedByDescending { it.redeemedAt }
            Result.success(userRewardsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun redeemReward(userId: String, rewardId: String): Result<UserReward> {
        return try {
            val reward = rewards.find { it.id == rewardId }
                ?: return Result.failure(Exception("Reward not found"))
            
            val userPointsData = userPoints[userId]
                ?: return Result.failure(Exception("User points not found"))
            
            if (userPointsData.availablePoints < reward.pointsRequired) {
                return Result.failure(Exception("Insufficient points"))
            }
            
            // Deduct points
            deductPoints(userId, reward.pointsRequired, "Redeemed reward: ${reward.name}")
            
            // Create user reward
            val userReward = UserReward(
                id = UUID.randomUUID().toString(),
                userId = userId,
                rewardId = rewardId,
                pointsUsed = reward.pointsRequired,
status = RewardStatus.REDEEMED,
                redeemedAt = LocalDateTime.now(),
                expiresAt = reward.expiryDate,
                redemptionCode = generateRedemptionCode()
            )
            
            userRewards.add(userReward)
            Result.success(userReward)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserPoints(userId: String): Result<RewardPoints> {
        return try {
            val points = userPoints[userId] ?: RewardPoints(
                userId = userId,
                totalPoints = 0,
                availablePoints = 0,
                usedPoints = 0,
                expiringPoints = 0,
                lastUpdated = LocalDateTime.now()
            )
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addPoints(userId: String, points: Int, description: String, relatedTransactionId: String?): Result<Unit> {
        return try {
            val transaction = PointsTransaction(
                id = UUID.randomUUID().toString(),
                userId = userId,
                points = points,
                transactionType = PointsTransactionType.EARNED,
                description = description,
                relatedTransactionId = relatedTransactionId,
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plusYears(1) // Points expire in 1 year
            )
            
            pointsTransactions.add(transaction)
            
            // Update user points
            val currentPoints = userPoints[userId] ?: RewardPoints(
                userId = userId,
                totalPoints = 0,
                availablePoints = 0,
                usedPoints = 0,
                expiringPoints = 0,
                lastUpdated = LocalDateTime.now()
            )
            
            val updatedPoints = currentPoints.copy(
                totalPoints = currentPoints.totalPoints + points,
                availablePoints = currentPoints.availablePoints + points,
                lastUpdated = LocalDateTime.now()
            )
            
            userPoints[userId] = updatedPoints
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deductPoints(userId: String, points: Int, description: String): Result<Unit> {
        return try {
            val currentPoints = userPoints[userId]
                ?: return Result.failure(Exception("User points not found"))
            
            if (currentPoints.availablePoints < points) {
                return Result.failure(Exception("Insufficient points"))
            }
            
            val transaction = PointsTransaction(
                id = UUID.randomUUID().toString(),
                userId = userId,
                points = -points,
                transactionType = PointsTransactionType.REDEEMED,
                description = description,
                createdAt = LocalDateTime.now()
            )
            
            pointsTransactions.add(transaction)
            
            // Update user points
            val updatedPoints = currentPoints.copy(
                availablePoints = currentPoints.availablePoints - points,
                usedPoints = currentPoints.usedPoints + points,
                lastUpdated = LocalDateTime.now()
            )
            
            userPoints[userId] = updatedPoints
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPointsHistory(userId: String): Result<List<PointsTransaction>> {
        return try {
            val userTransactions = pointsTransactions.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(userTransactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExpiringPoints(userId: String): Result<List<PointsTransaction>> {
        return try {
            val expiringDate = LocalDateTime.now().plusDays(30) // Points expiring in 30 days
            val expiringTransactions = pointsTransactions.filter { transaction ->
                transaction.userId == userId &&
                transaction.transactionType == PointsTransactionType.EARNED &&
                transaction.expiresAt != null &&
                transaction.expiresAt.isBefore(expiringDate)
            }.sortedBy { it.expiresAt }
            
            Result.success(expiringTransactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculatePointsForTransaction(transactionAmount: BigDecimal): Result<Int> {
        return try {
            // 1 point per dollar spent
            val points = transactionAmount.toInt()
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRewardRecommendations(userId: String): Result<List<Reward>> {
        return try {
            val userPointsData = userPoints[userId]
                ?: return Result.success(emptyList())
            
            // Recommend rewards that user can afford
            val affordableRewards = rewards.filter { 
                it.isAvailable && it.pointsRequired <= userPointsData.availablePoints 
            }.take(5)
            
            Result.success(affordableRewards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateRedemptionCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }
    
    private fun initializeMockRewards() {
        val mockRewards = listOf(
            Reward(
                id = UUID.randomUUID().toString(),
                name = "$10 Amazon Gift Card",
                description = "Redeem for a $10 Amazon gift card",
                pointsRequired = 1000,
                category = RewardCategory.GIFT_CARDS,
                value = BigDecimal("10.00")
            ),
            Reward(
                id = UUID.randomUUID().toString(),
                name = "$25 Starbucks Gift Card",
                description = "Enjoy your favorite coffee with this gift card",
                pointsRequired = 2500,
                category = RewardCategory.DINING,
                value = BigDecimal("25.00")
            ),
            Reward(
                id = UUID.randomUUID().toString(),
                name = "$50 Cashback",
                description = "Direct cashback to your account",
                pointsRequired = 5000,
                category = RewardCategory.CASHBACK,
                value = BigDecimal("50.00")
            ),
            Reward(
                id = UUID.randomUUID().toString(),
                name = "Movie Theater Tickets",
                description = "Two tickets to any participating theater",
                pointsRequired = 1500,
                category = RewardCategory.ENTERTAINMENT,
                value = BigDecimal("30.00")
            ),
            Reward(
                id = UUID.randomUUID().toString(),
                name = "$100 Travel Credit",
                description = "Credit towards your next travel booking",
                pointsRequired = 10000,
                category = RewardCategory.TRAVEL,
                value = BigDecimal("100.00")
            )
        )
        
        rewards.addAll(mockRewards)
    }
}