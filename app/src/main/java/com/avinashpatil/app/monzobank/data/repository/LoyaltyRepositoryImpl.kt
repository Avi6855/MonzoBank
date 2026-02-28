package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyRepositoryImpl @Inject constructor() : LoyaltyRepository {
    
    private val loyaltyPrograms = mutableListOf<LoyaltyProgram>()
    private val userLoyaltyStatuses = mutableListOf<UserLoyaltyStatus>()
    private val loyaltyActivities = mutableListOf<LoyaltyActivity>()
    
    init {
        initializeMockPrograms()
    }
    
    override suspend fun getLoyaltyPrograms(): Result<List<LoyaltyProgram>> {
        return try {
            val activePrograms = loyaltyPrograms.filter { it.isActive }
            Result.success(activePrograms)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoyaltyProgram(programId: String): Result<LoyaltyProgram?> {
        return try {
            val program = loyaltyPrograms.find { it.id == programId }
            Result.success(program)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserLoyaltyStatus(userId: String): Result<List<UserLoyaltyStatus>> {
        return try {
            val userStatuses = userLoyaltyStatuses.filter { it.userId == userId }
            Result.success(userStatuses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserLoyaltyStatus(userId: String, programId: String): Result<UserLoyaltyStatus?> {
        return try {
            val status = userLoyaltyStatuses.find { 
                it.userId == userId && it.programId == programId 
            }
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun joinLoyaltyProgram(userId: String, programId: String): Result<UserLoyaltyStatus> {
        return try {
            val program = loyaltyPrograms.find { it.id == programId }
                ?: return Result.failure(Exception("Loyalty program not found"))
            
            val existingStatus = userLoyaltyStatuses.find { 
                it.userId == userId && it.programId == programId 
            }
            
            if (existingStatus != null) {
                return Result.failure(Exception("User already enrolled in this program"))
            }
            
            val status = UserLoyaltyStatus(
                userId = userId,
                programId = programId,
                tier = LoyaltyTier.BRONZE,
                points = 0,
                nextTierPoints = getTierRequirement(LoyaltyTier.SILVER),
                joinedDate = LocalDateTime.now(),
                lastActivityDate = LocalDateTime.now(),
                benefits = getBenefitsForTier(programId, LoyaltyTier.BRONZE)
            )
            
            userLoyaltyStatuses.add(status)
            
            // Record activity
            val activity = LoyaltyActivity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                programId = programId,
                activityType = ActivityType.POINTS_EARNED,
                points = 0,
                description = "Joined loyalty program",
                createdAt = LocalDateTime.now()
            )
            loyaltyActivities.add(activity)
            
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun leaveLoyaltyProgram(userId: String, programId: String): Result<Unit> {
        return try {
            val removed = userLoyaltyStatuses.removeIf { 
                it.userId == userId && it.programId == programId 
            }
            
            if (removed) {
                // Remove related activities
                loyaltyActivities.removeIf { 
                    it.userId == userId && it.programId == programId 
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not enrolled in this program"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addLoyaltyPoints(userId: String, programId: String, points: Int, description: String, relatedTransactionId: String?): Result<Unit> {
        return try {
            val statusIndex = userLoyaltyStatuses.indexOfFirst { 
                it.userId == userId && it.programId == programId 
            }
            
            if (statusIndex == -1) {
                return Result.failure(Exception("User not enrolled in this program"))
            }
            
            val currentStatus = userLoyaltyStatuses[statusIndex]
            val newPoints = currentStatus.points + points
            
            // Check for tier upgrade
            val newTier = calculateTierForPoints(newPoints)
            val nextTierPoints = if (newTier == LoyaltyTier.DIAMOND) {
                0 // Already at highest tier
            } else {
                getTierRequirement(getNextTier(newTier)) - newPoints
            }
            
            val updatedStatus = currentStatus.copy(
                points = newPoints,
                tier = newTier,
                nextTierPoints = nextTierPoints,
                lastActivityDate = LocalDateTime.now(),
                benefits = getBenefitsForTier(programId, newTier)
            )
            
            userLoyaltyStatuses[statusIndex] = updatedStatus
            
            // Record activity
            val activity = LoyaltyActivity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                programId = programId,
                activityType = ActivityType.POINTS_EARNED,
                points = points,
                description = description,
                relatedTransactionId = relatedTransactionId,
                createdAt = LocalDateTime.now()
            )
            loyaltyActivities.add(activity)
            
            // Record tier upgrade if applicable
            if (newTier != currentStatus.tier) {
                val upgradeActivity = LoyaltyActivity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    programId = programId,
                    activityType = ActivityType.TIER_UPGRADE,
                    points = 0,
                    description = "Upgraded to ${newTier.name} tier",
                    createdAt = LocalDateTime.now()
                )
                loyaltyActivities.add(upgradeActivity)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun redeemLoyaltyPoints(userId: String, programId: String, points: Int, description: String): Result<Unit> {
        return try {
            val statusIndex = userLoyaltyStatuses.indexOfFirst { 
                it.userId == userId && it.programId == programId 
            }
            
            if (statusIndex == -1) {
                return Result.failure(Exception("User not enrolled in this program"))
            }
            
            val currentStatus = userLoyaltyStatuses[statusIndex]
            
            if (currentStatus.points < points) {
                return Result.failure(Exception("Insufficient points"))
            }
            
            val newPoints = currentStatus.points - points
            val newTier = calculateTierForPoints(newPoints)
            val nextTierPoints = if (newTier == LoyaltyTier.DIAMOND) {
                0
            } else {
                getTierRequirement(getNextTier(newTier)) - newPoints
            }
            
            val updatedStatus = currentStatus.copy(
                points = newPoints,
                tier = newTier,
                nextTierPoints = nextTierPoints,
                lastActivityDate = LocalDateTime.now(),
                benefits = getBenefitsForTier(programId, newTier)
            )
            
            userLoyaltyStatuses[statusIndex] = updatedStatus
            
            // Record activity
            val activity = LoyaltyActivity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                programId = programId,
                activityType = ActivityType.POINTS_REDEEMED,
                points = -points,
                description = description,
                createdAt = LocalDateTime.now()
            )
            loyaltyActivities.add(activity)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoyaltyActivity(userId: String): Result<List<LoyaltyActivity>> {
        return try {
            val userActivities = loyaltyActivities.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(userActivities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoyaltyActivity(userId: String, programId: String): Result<List<LoyaltyActivity>> {
        return try {
            val userProgramActivities = loyaltyActivities.filter { 
                it.userId == userId && it.programId == programId 
            }.sortedByDescending { it.createdAt }
            Result.success(userProgramActivities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkTierUpgrade(userId: String, programId: String): Result<LoyaltyTier?> {
        return try {
            val status = userLoyaltyStatuses.find { 
                it.userId == userId && it.programId == programId 
            } ?: return Result.success(null)
            
            val newTier = calculateTierForPoints(status.points)
            
            if (newTier != status.tier) {
                Result.success(newTier)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculatePointsForTransaction(transactionAmount: BigDecimal, programId: String): Result<Int> {
        return try {
            // Default: 1 point per dollar spent
            val points = transactionAmount.toInt()
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAvailableBenefits(userId: String, programId: String): Result<List<LoyaltyBenefit>> {
        return try {
            val status = userLoyaltyStatuses.find { 
                it.userId == userId && it.programId == programId 
            } ?: return Result.success(emptyList())
            
            val program = loyaltyPrograms.find { it.id == programId }
                ?: return Result.success(emptyList())
            
            // Return benefits available for user's current tier
            val availableBenefits = program.benefits.filter { benefit ->
                status.benefits.contains(benefit.id)
            }
            
            Result.success(availableBenefits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getTierRequirement(tier: LoyaltyTier): Int {
        return when (tier) {
            LoyaltyTier.BRONZE -> 0
            LoyaltyTier.SILVER -> 1000
            LoyaltyTier.GOLD -> 5000
            LoyaltyTier.PLATINUM -> 15000
            LoyaltyTier.DIAMOND -> 50000
        }
    }
    
    private fun calculateTierForPoints(points: Int): LoyaltyTier {
        return when {
            points >= 50000 -> LoyaltyTier.DIAMOND
            points >= 15000 -> LoyaltyTier.PLATINUM
            points >= 5000 -> LoyaltyTier.GOLD
            points >= 1000 -> LoyaltyTier.SILVER
            else -> LoyaltyTier.BRONZE
        }
    }
    
    private fun getNextTier(currentTier: LoyaltyTier): LoyaltyTier {
        return when (currentTier) {
            LoyaltyTier.BRONZE -> LoyaltyTier.SILVER
            LoyaltyTier.SILVER -> LoyaltyTier.GOLD
            LoyaltyTier.GOLD -> LoyaltyTier.PLATINUM
            LoyaltyTier.PLATINUM -> LoyaltyTier.DIAMOND
            LoyaltyTier.DIAMOND -> LoyaltyTier.DIAMOND
        }
    }
    
    private fun getBenefitsForTier(programId: String, tier: LoyaltyTier): List<String> {
        // Mock benefits based on tier
        return when (tier) {
            LoyaltyTier.BRONZE -> listOf("basic_support")
            LoyaltyTier.SILVER -> listOf("basic_support", "fee_waiver_1")
            LoyaltyTier.GOLD -> listOf("basic_support", "fee_waiver_1", "cashback_bonus_1", "priority_support")
            LoyaltyTier.PLATINUM -> listOf("basic_support", "fee_waiver_2", "cashback_bonus_2", "priority_support", "travel_benefits")
            LoyaltyTier.DIAMOND -> listOf("basic_support", "fee_waiver_3", "cashback_bonus_3", "priority_support", "travel_benefits", "insurance_coverage")
        }
    }
    
    private fun initializeMockPrograms() {
        val program = LoyaltyProgram(
            id = UUID.randomUUID().toString(),
            name = "MonzoBank Rewards",
            description = "Earn points for every transaction and unlock exclusive benefits",
            programType = LoyaltyProgramType.TIER_BASED,
            startDate = LocalDateTime.now().minusYears(1),
            benefits = listOf(
                LoyaltyBenefit(
                    id = "basic_support",
                    programId = "",
                    benefitType = BenefitType.PRIORITY_SUPPORT,
                    title = "Basic Support",
                    description = "Access to customer support",
                    value = BigDecimal.ZERO
                ),
                LoyaltyBenefit(
                    id = "fee_waiver_1",
                    programId = "",
                    benefitType = BenefitType.FEE_WAIVER,
                    title = "Monthly Fee Waiver",
                    description = "Waive monthly account fees",
                    value = BigDecimal("10.00")
                )
            )
        )
        
        loyaltyPrograms.add(program)
    }
}