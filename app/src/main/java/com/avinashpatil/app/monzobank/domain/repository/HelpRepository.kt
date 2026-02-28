package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class HelpArticle(
    val id: String,
    val title: String,
    val content: String,
    val category: HelpCategory,
    val tags: List<String>,
    val isPublished: Boolean,
    val viewCount: Int,
    val helpfulCount: Int,
    val notHelpfulCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val authorId: String
)

data class HelpCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: String?,
    val parentId: String?,
    val sortOrder: Int,
    val isActive: Boolean
)

data class HelpSearchResult(
    val articles: List<HelpArticle>,
    val totalCount: Int,
    val suggestions: List<String>
)

interface HelpRepository {
    suspend fun getHelpArticles(): Result<List<HelpArticle>>
    suspend fun getHelpArticle(articleId: String): Result<HelpArticle?>
    suspend fun getHelpCategories(): Result<List<HelpCategory>>
    suspend fun getArticlesByCategory(categoryId: String): Result<List<HelpArticle>>
    suspend fun searchHelpArticles(query: String): Result<HelpSearchResult>
    suspend fun markArticleHelpful(articleId: String, isHelpful: Boolean): Result<Unit>
    suspend fun incrementViewCount(articleId: String): Result<Unit>
    suspend fun getPopularArticles(limit: Int): Result<List<HelpArticle>>
    suspend fun getRecentArticles(limit: Int): Result<List<HelpArticle>>
    suspend fun getRelatedArticles(articleId: String): Result<List<HelpArticle>>
}