package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: NewsCategory,
    val author: String,
    val imageUrl: String?,
    val isPublished: Boolean,
    val isFeatured: Boolean,
    val viewCount: Int,
    val publishedAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class NewsCategory {
    COMPANY_NEWS,
    PRODUCT_UPDATES,
    FINANCIAL_TIPS,
    INDUSTRY_NEWS,
    REGULATORY_UPDATES,
    SECURITY_ALERTS
}

interface NewsRepository {
    suspend fun getNews(): Result<List<NewsArticle>>
    suspend fun getNewsArticle(articleId: String): Result<NewsArticle?>
    suspend fun getNewsByCategory(category: NewsCategory): Result<List<NewsArticle>>
    suspend fun getFeaturedNews(): Result<List<NewsArticle>>
    suspend fun searchNews(query: String): Result<List<NewsArticle>>
    suspend fun incrementViewCount(articleId: String): Result<Unit>
    suspend fun getRecentNews(limit: Int): Result<List<NewsArticle>>
}