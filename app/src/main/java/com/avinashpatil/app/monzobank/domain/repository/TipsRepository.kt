package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class Tip(
    val id: String,
    val title: String,
    val content: String,
    val category: TipCategory,
    val difficulty: TipDifficulty,
    val estimatedTime: Int, // in minutes
    val tags: List<String>,
    val isPublished: Boolean,
    val viewCount: Int,
    val likeCount: Int,
    val shareCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class TipOfTheDay(
    val tip: Tip,
    val date: LocalDateTime
)

enum class TipCategory {
    BUDGETING,
    SAVING,
    INVESTING,
    SECURITY,
    FEATURES,
    GENERAL
}

enum class TipDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

interface TipsRepository {
    suspend fun getTips(): Result<List<Tip>>
    suspend fun getTip(tipId: String): Result<Tip?>
    suspend fun getTipsByCategory(category: TipCategory): Result<List<Tip>>
    suspend fun getTipOfTheDay(): Result<TipOfTheDay?>
    suspend fun searchTips(query: String): Result<List<Tip>>
    suspend fun likeTip(tipId: String): Result<Unit>
    suspend fun shareTip(tipId: String): Result<Unit>
    suspend fun incrementViewCount(tipId: String): Result<Unit>
    suspend fun getPopularTips(limit: Int): Result<List<Tip>>
    suspend fun getRecentTips(limit: Int): Result<List<Tip>>
    suspend fun getPersonalizedTips(userId: String): Result<List<Tip>>
}