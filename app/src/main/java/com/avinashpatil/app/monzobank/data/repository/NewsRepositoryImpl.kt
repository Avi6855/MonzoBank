package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor() : NewsRepository {
    
    private val articles = mutableListOf(
        NewsArticle(
            id = "news1",
            title = "New Security Features Available",
            summary = "Enhanced security measures to protect your account",
            content = "We've introduced new security features including biometric authentication and advanced fraud detection...",
            category = NewsCategory.SECURITY_ALERTS,
            author = "Monzo Security Team",
            imageUrl = null,
            isPublished = true,
            isFeatured = true,
            viewCount = 2500,
            publishedAt = LocalDateTime.now().minusDays(2),
            createdAt = LocalDateTime.now().minusDays(5),
            updatedAt = LocalDateTime.now().minusDays(1)
        ),
        NewsArticle(
            id = "news2",
            title = "App Update: New Dashboard Design",
            summary = "Experience our refreshed dashboard with improved usability",
            content = "Our latest app update includes a completely redesigned dashboard that makes managing your finances easier...",
            category = NewsCategory.PRODUCT_UPDATES,
            author = "Monzo Product Team",
            imageUrl = null,
            isPublished = true,
            isFeatured = false,
            viewCount = 1800,
            publishedAt = LocalDateTime.now().minusDays(7),
            createdAt = LocalDateTime.now().minusDays(10),
            updatedAt = LocalDateTime.now().minusDays(6)
        )
    )
    
    override suspend fun getNews(): Result<List<NewsArticle>> {
        return try {
            val publishedArticles = articles.filter { it.isPublished }
                .sortedByDescending { it.publishedAt }
            Result.success(publishedArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getNewsArticle(articleId: String): Result<NewsArticle?> {
        return try {
            val article = articles.find { it.id == articleId && it.isPublished }
            Result.success(article)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getNewsByCategory(category: NewsCategory): Result<List<NewsArticle>> {
        return try {
            val categoryArticles = articles.filter { 
                it.category == category && it.isPublished 
            }.sortedByDescending { it.publishedAt }
            Result.success(categoryArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeaturedNews(): Result<List<NewsArticle>> {
        return try {
            val featuredArticles = articles.filter { 
                it.isFeatured && it.isPublished 
            }.sortedByDescending { it.publishedAt }
            Result.success(featuredArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchNews(query: String): Result<List<NewsArticle>> {
        return try {
            val searchResults = articles.filter { article ->
                article.isPublished && (
                    article.title.contains(query, ignoreCase = true) ||
                    article.summary.contains(query, ignoreCase = true) ||
                    article.content.contains(query, ignoreCase = true)
                )
            }.sortedByDescending { it.publishedAt }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun incrementViewCount(articleId: String): Result<Unit> {
        return try {
            val index = articles.indexOfFirst { it.id == articleId }
            if (index != -1) {
                val article = articles[index]
                val updated = article.copy(viewCount = article.viewCount + 1)
                articles[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Article not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecentNews(limit: Int): Result<List<NewsArticle>> {
        return try {
            val recentArticles = articles.filter { it.isPublished }
                .sortedByDescending { it.publishedAt }
                .take(limit)
            Result.success(recentArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}