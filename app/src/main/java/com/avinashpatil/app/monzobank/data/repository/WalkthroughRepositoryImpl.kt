package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkthroughRepositoryImpl @Inject constructor() : WalkthroughRepository {
    
    private val walkthroughs = listOf(
        Walkthrough(
            id = "wt1",
            title = "Dashboard Overview",
            description = "Learn about your dashboard features",
            feature = "dashboard",
            steps = listOf(
                WalkthroughStep(
                    id = "step1",
                    title = "Your Balance",
                    description = "This shows your current account balance",
                    targetElement = "balance_card",
                    position = WalkthroughPosition.BOTTOM,
                    order = 1
                ),
                WalkthroughStep(
                    id = "step2",
                    title = "Recent Transactions",
                    description = "View your latest transactions here",
                    targetElement = "transactions_list",
                    position = WalkthroughPosition.TOP,
                    order = 2
                )
            ),
            isActive = true,
            targetVersion = "1.0.0",
            createdAt = LocalDateTime.now().minusDays(10)
        )
    )
    
    private val progressList = mutableListOf<WalkthroughProgress>()
    
    override suspend fun getWalkthroughs(): Result<List<Walkthrough>> {
        return try {
            val activeWalkthroughs = walkthroughs.filter { it.isActive }
            Result.success(activeWalkthroughs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWalkthrough(walkthroughId: String): Result<Walkthrough?> {
        return try {
            val walkthrough = walkthroughs.find { it.id == walkthroughId && it.isActive }
            Result.success(walkthrough)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWalkthroughsForFeature(feature: String): Result<List<Walkthrough>> {
        return try {
            val featureWalkthroughs = walkthroughs.filter { 
                it.feature == feature && it.isActive 
            }
            Result.success(featureWalkthroughs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markWalkthroughCompleted(userId: String, walkthroughId: String): Result<Unit> {
        return try {
            val existingProgress = progressList.find { 
                it.userId == userId && it.walkthroughId == walkthroughId 
            }
            
            if (existingProgress != null) {
                val index = progressList.indexOfFirst { it.id == existingProgress.id }
                val updated = existingProgress.copy(
                    isCompleted = true,
                    completedAt = LocalDateTime.now()
                )
                progressList[index] = updated
            } else {
                val progress = WalkthroughProgress(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    walkthroughId = walkthroughId,
                    isCompleted = true,
                    completedAt = LocalDateTime.now(),
                    skippedAt = null
                )
                progressList.add(progress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun skipWalkthrough(userId: String, walkthroughId: String): Result<Unit> {
        return try {
            val existingProgress = progressList.find { 
                it.userId == userId && it.walkthroughId == walkthroughId 
            }
            
            if (existingProgress != null) {
                val index = progressList.indexOfFirst { it.id == existingProgress.id }
                val updated = existingProgress.copy(
                    skippedAt = LocalDateTime.now()
                )
                progressList[index] = updated
            } else {
                val progress = WalkthroughProgress(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    walkthroughId = walkthroughId,
                    isCompleted = false,
                    completedAt = null,
                    skippedAt = LocalDateTime.now()
                )
                progressList.add(progress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserProgress(userId: String): Result<List<WalkthroughProgress>> {
        return try {
            val userProgress = progressList.filter { it.userId == userId }
            Result.success(userProgress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun shouldShowWalkthrough(userId: String, walkthroughId: String): Result<Boolean> {
        return try {
            val progress = progressList.find { 
                it.userId == userId && it.walkthroughId == walkthroughId 
            }
            
            val shouldShow = progress == null || (!progress.isCompleted && progress.skippedAt == null)
            Result.success(shouldShow)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}