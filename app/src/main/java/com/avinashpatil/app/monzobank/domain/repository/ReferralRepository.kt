package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class ReferralProgram(
    val id: String,
    val name: String,
    val description: String,
    val referrerReward: BigDecimal,
    val refereeReward: BigDecimal,
    val isActive: Boolean,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val maxReferrals: Int?,
    val minRefereeActivity: String?,
    val rewardType: ReferralRewardType,
    val terms: String
)

data class Referral(
    val id: String,
    val referrerId: String,
    val refereeId: String,
    val programId: String,
    val referralCode: String,
    val status: ReferralStatus,
    val createdDate: LocalDateTime,
    val completedDate: LocalDateTime?,
    val referrerRewardAmount: BigDecimal?,
    val refereeRewardAmount: BigDecimal?,
    val referrerRewardStatus: ReferralRewardStatus,
    val refereeRewardStatus: ReferralRewardStatus,
    val metadata: Map<String, String> = emptyMap()
)

data class ReferralCode(
    val code: String,
    val userId: String,
    val programId: String,
    val isActive: Boolean,
    val createdDate: LocalDateTime,
    val expiryDate: LocalDateTime?,
    val usageCount: Int,
    val maxUsage: Int?
)

data class ReferralStats(
    val userId: String,
    val totalReferrals: Int,
    val successfulReferrals: Int,
    val pendingReferrals: Int,
    val totalRewardsEarned: BigDecimal,
    val totalRewardsPaid: BigDecimal,
    val currentMonthReferrals: Int,
    val lastReferralDate: LocalDateTime?
)

data class ReferralLeaderboard(
    val userId: String,
    val userName: String,
    val referralCount: Int,
    val rewardsEarned: BigDecimal,
    val rank: Int
)

enum class ReferralStatus {
    PENDING,
    COMPLETED,
    EXPIRED,
    CANCELLED
}

enum class ReferralRewardType {
    CASH,
    POINTS,
    CREDIT,
    BONUS_INTEREST
}

enum class ReferralRewardStatus {
    PENDING,
    CREDITED,
    EXPIRED,
    CANCELLED
}

interface ReferralRepository {
    suspend fun getActivePrograms(): Result<List<ReferralProgram>>
    suspend fun getProgram(programId: String): Result<ReferralProgram?>
    suspend fun getUserReferrals(userId: String): Result<List<Referral>>
    suspend fun getReferral(referralId: String): Result<Referral?>
    suspend fun createReferral(referral: Referral): Result<String>
    suspend fun updateReferralStatus(referralId: String, status: ReferralStatus): Result<Unit>
    suspend fun generateReferralCode(userId: String, programId: String): Result<ReferralCode>
    suspend fun validateReferralCode(code: String): Result<ReferralCode?>
    suspend fun useReferralCode(code: String, refereeId: String): Result<Referral>
    suspend fun getReferralStats(userId: String): Result<ReferralStats>
    suspend fun processReferralRewards(referralId: String): Result<Unit>
    suspend fun getReferralLeaderboard(limit: Int): Result<List<ReferralLeaderboard>>
    suspend fun getExpiredReferrals(): Result<List<Referral>>
    suspend fun getUserReferralCodes(userId: String): Result<List<ReferralCode>>
    suspend fun deactivateReferralCode(codeId: String): Result<Unit>
}