package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReferralRepositoryImpl @Inject constructor() : ReferralRepository {
    
    private val referralPrograms = mutableListOf<ReferralProgram>()
    private val referrals = mutableListOf<Referral>()
    private val referralCodes = mutableListOf<ReferralCode>()
    
    init {
        initializeMockData()
    }
    
    override suspend fun getActivePrograms(): Result<List<ReferralProgram>> {
        return try {
            val activePrograms = referralPrograms.filter { 
                it.isActive && (it.endDate == null || it.endDate.isAfter(LocalDateTime.now())) 
            }
            Result.success(activePrograms)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProgram(programId: String): Result<ReferralProgram?> {
        return try {
            val program = referralPrograms.find { it.id == programId }
            Result.success(program)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserReferrals(userId: String): Result<List<Referral>> {
        return try {
            val userReferrals = referrals.filter { it.referrerId == userId }
                .sortedByDescending { it.createdDate }
            Result.success(userReferrals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReferral(referralId: String): Result<Referral?> {
        return try {
            val referral = referrals.find { it.id == referralId }
            Result.success(referral)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createReferral(referral: Referral): Result<String> {
        return try {
            referrals.add(referral)
            Result.success(referral.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateReferralStatus(referralId: String, status: ReferralStatus): Result<Unit> {
        return try {
            val index = referrals.indexOfFirst { it.id == referralId }
            if (index != -1) {
                val referral = referrals[index]
                val updatedReferral = referral.copy(
                    status = status,
                    completedDate = if (status == ReferralStatus.COMPLETED) LocalDateTime.now() else referral.completedDate
                )
                referrals[index] = updatedReferral
                Result.success(Unit)
            } else {
                Result.failure(Exception("Referral not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateReferralCode(userId: String, programId: String): Result<ReferralCode> {
        return try {
            // Check if user already has an active code for this program
            val existingCode = referralCodes.find { 
                it.userId == userId && it.programId == programId && it.isActive 
            }
            
            if (existingCode != null) {
                return Result.success(existingCode)
            }
            
            val code = generateUniqueCode()
            val referralCode = ReferralCode(
                code = code,
                userId = userId,
                programId = programId,
                isActive = true,
                createdDate = LocalDateTime.now(),
                expiryDate = LocalDateTime.now().plusMonths(6),
                usageCount = 0,
                maxUsage = 10
            )
            
            referralCodes.add(referralCode)
            Result.success(referralCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validateReferralCode(code: String): Result<ReferralCode?> {
        return try {
            val referralCode = referralCodes.find { 
                it.code == code && 
                it.isActive && 
                (it.expiryDate == null || it.expiryDate.isAfter(LocalDateTime.now())) &&
                (it.maxUsage == null || it.usageCount < it.maxUsage)
            }
            Result.success(referralCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun useReferralCode(code: String, refereeId: String): Result<Referral> {
        return try {
            val referralCode = validateReferralCode(code).getOrNull()
                ?: return Result.failure(Exception("Invalid or expired referral code"))
            
            val program = referralPrograms.find { it.id == referralCode.programId }
                ?: return Result.failure(Exception("Program not found"))
            
            // Check if referee already used a referral code
            val existingReferral = referrals.find { it.refereeId == refereeId }
            if (existingReferral != null) {
                return Result.failure(Exception("User already referred"))
            }
            
            // Create new referral
            val referral = Referral(
                id = UUID.randomUUID().toString(),
                referrerId = referralCode.userId,
                refereeId = refereeId,
                programId = program.id,
                referralCode = code,
                status = ReferralStatus.PENDING,
                createdDate = LocalDateTime.now(),
                completedDate = null,
                referrerRewardAmount = program.referrerReward,
                refereeRewardAmount = program.refereeReward,
                referrerRewardStatus = ReferralRewardStatus.PENDING,
                refereeRewardStatus = ReferralRewardStatus.PENDING
            )
            
            referrals.add(referral)
            
            // Update code usage count
            val codeIndex = referralCodes.indexOfFirst { it.code == code }
            if (codeIndex != -1) {
                val updatedCode = referralCodes[codeIndex].copy(
                    usageCount = referralCodes[codeIndex].usageCount + 1
                )
                referralCodes[codeIndex] = updatedCode
            }
            
            Result.success(referral)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReferralStats(userId: String): Result<ReferralStats> {
        return try {
            val userReferrals = referrals.filter { it.referrerId == userId }
            
            val totalReferrals = userReferrals.size
            val successfulReferrals = userReferrals.count { it.status == ReferralStatus.COMPLETED }
            val pendingReferrals = userReferrals.count { it.status == ReferralStatus.PENDING }
            
            val totalRewardsEarned = userReferrals.filter { it.status == ReferralStatus.COMPLETED }
                .sumOf { it.referrerRewardAmount ?: BigDecimal.ZERO }
            val totalRewardsPaid = userReferrals.filter { it.referrerRewardStatus == ReferralRewardStatus.CREDITED }
                .sumOf { it.referrerRewardAmount ?: BigDecimal.ZERO }
            
            val currentMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            val currentMonthReferrals = userReferrals.count { it.createdDate.isAfter(currentMonth) }
            
            val lastReferralDate = userReferrals.maxByOrNull { it.createdDate }?.createdDate
            
            val stats = ReferralStats(
                userId = userId,
                totalReferrals = totalReferrals,
                successfulReferrals = successfulReferrals,
                pendingReferrals = pendingReferrals,
                totalRewardsEarned = totalRewardsEarned,
                totalRewardsPaid = totalRewardsPaid,
                currentMonthReferrals = currentMonthReferrals,
                lastReferralDate = lastReferralDate
            )
            
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processReferralRewards(referralId: String): Result<Unit> {
        return try {
            val index = referrals.indexOfFirst { it.id == referralId }
            if (index != -1) {
                val referral = referrals[index]
                if (referral.status == ReferralStatus.COMPLETED) {
                    val updatedReferral = referral.copy(
                        referrerRewardStatus = ReferralRewardStatus.CREDITED,
                        refereeRewardStatus = ReferralRewardStatus.CREDITED
                    )
                    referrals[index] = updatedReferral
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Referral not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReferralLeaderboard(limit: Int): Result<List<ReferralLeaderboard>> {
        return try {
            val leaderboard = referrals.filter { it.status == ReferralStatus.COMPLETED }
                .groupBy { it.referrerId }
                .map { (userId, userReferrals) ->
                    ReferralLeaderboard(
                        userId = userId,
                        userName = "User $userId", // Mock name
                        referralCount = userReferrals.size,
                        rewardsEarned = userReferrals.sumOf { it.referrerRewardAmount ?: BigDecimal.ZERO },
                        rank = 0 // Will be set after sorting
                    )
                }
                .sortedByDescending { it.referralCount }
                .take(limit)
                .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
            
            Result.success(leaderboard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExpiredReferrals(): Result<List<Referral>> {
        return try {
            val expiredReferrals = referrals.filter { referral ->
                referral.status == ReferralStatus.PENDING &&
                referral.createdDate.isBefore(LocalDateTime.now().minusDays(30)) // 30 days expiry
            }
            Result.success(expiredReferrals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserReferralCodes(userId: String): Result<List<ReferralCode>> {
        return try {
            val userCodes = referralCodes.filter { it.userId == userId }
                .sortedByDescending { it.createdDate }
            Result.success(userCodes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deactivateReferralCode(codeId: String): Result<Unit> {
        return try {
            val index = referralCodes.indexOfFirst { it.code == codeId }
            if (index != -1) {
                val updatedCode = referralCodes[index].copy(isActive = false)
                referralCodes[index] = updatedCode
                Result.success(Unit)
            } else {
                Result.failure(Exception("Referral code not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateUniqueCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var code: String
        do {
            code = (1..8).map { chars.random() }.joinToString("")
        } while (referralCodes.any { it.code == code })
        return code
    }
    
    private fun initializeMockData() {
        val mockPrograms = listOf(
            ReferralProgram(
                id = UUID.randomUUID().toString(),
                name = "Friend Referral Program",
                description = "Refer friends and earn rewards for both of you",
                referrerReward = BigDecimal("25.00"),
                refereeReward = BigDecimal("25.00"),
                isActive = true,
                startDate = LocalDateTime.now().minusMonths(6),
                endDate = LocalDateTime.now().plusMonths(6),
                maxReferrals = 10,
                minRefereeActivity = "Complete first transaction",
                rewardType = ReferralRewardType.CASH,
                terms = "Terms and conditions apply"
            ),
            ReferralProgram(
                id = UUID.randomUUID().toString(),
                name = "Premium Account Referral",
                description = "Refer friends to premium accounts",
                referrerReward = BigDecimal("50.00"),
                refereeReward = BigDecimal("30.00"),
                isActive = true,
                startDate = LocalDateTime.now().minusMonths(3),
                endDate = LocalDateTime.now().plusMonths(9),
                maxReferrals = 5,
                minRefereeActivity = "Upgrade to premium account",
                rewardType = ReferralRewardType.CASH,
                terms = "Premium account terms apply"
            )
        )
        
        referralPrograms.addAll(mockPrograms)
    }
}