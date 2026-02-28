package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TipsRepositoryImpl @Inject constructor() : TipsRepository {
    
    private val tips = mutableListOf(
        Tip(
            id = "tip1",
            title = "Set up spending notifications",
            content = "Enable notifications to stay on top of your spending and avoid surprises.",
            category = TipCategory.BUDGETING,
            difficulty = TipDifficulty.BEGINNER,
            estimatedTime = 2,
            tags = listOf("notifications", "spending", "budgeting"),
            isPublished = true,
            viewCount = 1250,
            likeCount = 89,
            shareCount = 23,
            createdAt = LocalDateTime.now().minusDays(30),
            updatedAt = LocalDateTime.now().minusDays(5)
        ),
        Tip(
            id = "tip2",
            title = "Use spending categories",
            content = "Categorize your transactions to better understand where your money goes.",
            category = TipCategory.BUDGETING,
            difficulty = TipDifficulty.BEGINNER,
            estimatedTime = 5,
            tags = listOf("categories", "tracking", "analysis"),
            isPublished = true,
            viewCount = 890,
            likeCount = 67,
            shareCount = 15,
            createdAt = LocalDateTime.now().minusDays(20),
            updatedAt = LocalDateTime.now().minusDays(2)
        )
    )
    
    override suspend fun getTips(): Result<List<Tip>> {
        return try {
            val publishedTips = tips.filter { it.isPublished }
                .sortedByDescending { it.updatedAt }
            Result.success(publishedTips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTip(tipId: String): Result<Tip?> {
        return try {
            val tip = tips.find { it.id == tipId && it.isPublished }
            Result.success(tip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTipsByCategory(category: TipCategory): Result<List<Tip>> {
        return try {
            val categoryTips = tips.filter { 
                it.category == category && it.isPublished 
            }.sortedByDescending { it.viewCount }
            Result.success(categoryTips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTipOfTheDay(): Result<TipOfTheDay?> {
        return try {
            val publishedTips = tips.filter { it.isPublished }
            if (publishedTips.isNotEmpty()) {
                // Simple logic: rotate tips based on day of year
                val dayOfYear = LocalDateTime.now().dayOfYear
                val tipIndex = dayOfYear % publishedTips.size
                val tip = publishedTips[tipIndex]
                
                val tipOfTheDay = TipOfTheDay(
                    tip = tip,
                    date = LocalDateTime.now()
                )
                Result.success(tipOfTheDay)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchTips(query: String): Result<List<Tip>> {
        return try {
            val searchResults = tips.filter { tip ->
                tip.isPublished && (
                    tip.title.contains(query, ignoreCase = true) ||
                    tip.content.contains(query, ignoreCase = true) ||
                    tip.tags.any { it.contains(query, ignoreCase = true) }
                )
            }.sortedByDescending { it.viewCount }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun likeTip(tipId: String): Result<Unit> {
        return try {
            val index = tips.indexOfFirst { it.id == tipId }
            if (index != -1) {
                val tip = tips[index]
                val updated = tip.copy(likeCount = tip.likeCount + 1)
                tips[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tip not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun shareTip(tipId: String): Result<Unit> {
        return try {
            val index = tips.indexOfFirst { it.id == tipId }
            if (index != -1) {
                val tip = tips[index]
                val updated = tip.copy(shareCount = tip.shareCount + 1)
                tips[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tip not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun incrementViewCount(tipId: String): Result<Unit> {
        return try {
            val index = tips.indexOfFirst { it.id == tipId }
            if (index != -1) {
                val tip = tips[index]
                val updated = tip.copy(viewCount = tip.viewCount + 1)
                tips[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tip not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPopularTips(limit: Int): Result<List<Tip>> {
        return try {
            val popularTips = tips.filter { it.isPublished }
                .sortedByDescending { it.viewCount }
                .take(limit)
            Result.success(popularTips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecentTips(limit: Int): Result<List<Tip>> {
        return try {
            val recentTips = tips.filter { it.isPublished }
                .sortedByDescending { it.updatedAt }
                .take(limit)
            Result.success(recentTips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPersonalizedTips(userId: String): Result<List<Tip>> {
        return try {
            // Mock personalization - return tips based on user preferences
            // In a real implementation, this would use user data and ML algorithms
            val personalizedTips = tips.filter { it.isPublished }
                .sortedBy { it.difficulty } // Start with beginner tips
                .take(5)
            Result.success(personalizedTips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}