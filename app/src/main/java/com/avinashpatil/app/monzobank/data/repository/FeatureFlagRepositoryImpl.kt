package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.FeatureFlag
import com.avinashpatil.app.monzobank.domain.repository.FeatureFlagOverride
import com.avinashpatil.app.monzobank.domain.repository.FeatureFlagRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FeatureFlagRepositoryImpl @Inject constructor() : FeatureFlagRepository {
    
    private val flags = mutableMapOf<String, FeatureFlag>()
    private val userOverrides = mutableMapOf<String, MutableList<FeatureFlagOverride>>()
    
    init {
        initializeDefaultFlags()
    }
    
    override suspend fun getAllFlags(): Result<List<FeatureFlag>> {
        return try {
            Result.success(flags.values.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFlag(flagName: String): Result<FeatureFlag?> {
        return try {
            Result.success(flags[flagName])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isFlagEnabled(flagName: String, userId: String?): Result<Boolean> {
        return try {
            val flag = flags[flagName]
            if (flag == null) {
                Result.success(false)
            } else {
                // Check user override first
                if (userId != null) {
                    val override = userOverrides[userId]?.find { it.flagName == flagName }
                    if (override != null && (override.expiresAt == null || override.expiresAt.isAfter(LocalDateTime.now()))) {
                        return Result.success(override.isEnabled)
                    }
                }
                
                // Check rollout percentage
                if (flag.rolloutPercentage < 100.0) {
                    val userHash = userId?.hashCode()?.rem(100) ?: Random.nextInt(100)
                    val isInRollout = userHash < flag.rolloutPercentage
                    Result.success(flag.isEnabled && isInRollout)
                } else {
                    Result.success(flag.isEnabled)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createFlag(flag: FeatureFlag): Result<String> {
        return try {
            flags[flag.name] = flag
            Result.success(flag.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateFlag(flag: FeatureFlag): Result<Unit> {
        return try {
            flags[flag.name] = flag
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteFlag(flagName: String): Result<Unit> {
        return try {
            flags.remove(flagName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun enableFlag(flagName: String, userId: String): Result<Unit> {
        return try {
            val flag = flags[flagName]
            if (flag != null) {
                flags[flagName] = flag.copy(isEnabled = true, updatedAt = LocalDateTime.now(), lastModifiedBy = userId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun disableFlag(flagName: String, userId: String): Result<Unit> {
        return try {
            val flag = flags[flagName]
            if (flag != null) {
                flags[flagName] = flag.copy(isEnabled = false, updatedAt = LocalDateTime.now(), lastModifiedBy = userId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setRolloutPercentage(flagName: String, percentage: Double): Result<Unit> {
        return try {
            val flag = flags[flagName]
            if (flag != null) {
                flags[flagName] = flag.copy(rolloutPercentage = percentage, updatedAt = LocalDateTime.now())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserOverrides(userId: String): Result<List<FeatureFlagOverride>> {
        return try {
            val overrides = userOverrides[userId] ?: emptyList()
            Result.success(overrides)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setUserOverride(
        userId: String,
        flagName: String,
        isEnabled: Boolean,
        reason: String?
    ): Result<Unit> {
        return try {
            val override = FeatureFlagOverride(
                id = UUID.randomUUID().toString(),
                userId = userId,
                flagName = flagName,
                isEnabled = isEnabled,
                reason = reason,
                expiresAt = null,
                createdAt = LocalDateTime.now()
            )
            
            val userOverrideList = userOverrides.getOrPut(userId) { mutableListOf() }
            userOverrideList.removeIf { it.flagName == flagName }
            userOverrideList.add(override)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeUserOverride(userId: String, flagName: String): Result<Unit> {
        return try {
            userOverrides[userId]?.removeIf { it.flagName == flagName }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFlagUsageStats(flagName: String): Result<Map<String, Any>> {
        return try {
            val stats = mapOf(
                "totalUsers" to 1000,
                "enabledUsers" to 750,
                "disabledUsers" to 250,
                "rolloutPercentage" to (flags[flagName]?.rolloutPercentage ?: 0.0)
            )
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun initializeDefaultFlags() {
        val defaultFlags = listOf(
            FeatureFlag(
                id = UUID.randomUUID().toString(),
                name = "dark_mode",
                description = "Enable dark mode theme",
                isEnabled = true,
                rolloutPercentage = 100.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "system",
                lastModifiedBy = "system"
            ),
            FeatureFlag(
                id = UUID.randomUUID().toString(),
                name = "biometric_auth",
                description = "Enable biometric authentication",
                isEnabled = true,
                rolloutPercentage = 80.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "system",
                lastModifiedBy = "system"
            )
        )
        
        defaultFlags.forEach { flag ->
            flags[flag.name] = flag
        }
    }
}