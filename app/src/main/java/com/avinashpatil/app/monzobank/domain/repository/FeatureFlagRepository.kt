package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class FeatureFlag(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean,
    val rolloutPercentage: Double = 100.0,
    val targetAudience: List<String> = emptyList(),
    val conditions: Map<String, String> = emptyMap(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val createdBy: String,
    val lastModifiedBy: String
)

data class FeatureFlagOverride(
    val id: String,
    val userId: String,
    val flagName: String,
    val isEnabled: Boolean,
    val reason: String?,
    val expiresAt: LocalDateTime?,
    val createdAt: LocalDateTime
)

interface FeatureFlagRepository {
    suspend fun getAllFlags(): Result<List<FeatureFlag>>
    suspend fun getFlag(flagName: String): Result<FeatureFlag?>
    suspend fun isFlagEnabled(flagName: String, userId: String? = null): Result<Boolean>
    suspend fun createFlag(flag: FeatureFlag): Result<String>
    suspend fun updateFlag(flag: FeatureFlag): Result<Unit>
    suspend fun deleteFlag(flagName: String): Result<Unit>
    suspend fun enableFlag(flagName: String, userId: String): Result<Unit>
    suspend fun disableFlag(flagName: String, userId: String): Result<Unit>
    suspend fun setRolloutPercentage(flagName: String, percentage: Double): Result<Unit>
    suspend fun getUserOverrides(userId: String): Result<List<FeatureFlagOverride>>
    suspend fun setUserOverride(userId: String, flagName: String, isEnabled: Boolean, reason: String?): Result<Unit>
    suspend fun removeUserOverride(userId: String, flagName: String): Result<Unit>
    suspend fun getFlagUsageStats(flagName: String): Result<Map<String, Any>>
}