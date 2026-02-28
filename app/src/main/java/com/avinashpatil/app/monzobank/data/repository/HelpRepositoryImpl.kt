package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpRepositoryImpl @Inject constructor() : HelpRepository {
    
    private val categories = listOf(
        HelpCategory(
            id = "cat1",
            name = "Account Management",
            description = "Managing your account settings and preferences",
            icon = "account",
            parentId = null,
            sortOrder = 1,
            isActive = true
        ),
        HelpCategory(
            id = "cat2",
            name = "Transactions",
            description = "Understanding and managing your transactions",
            icon = "transaction",
            parentId = null,
            sortOrder = 2,
            isActive = true
        )
    )
    
    private val articles = mutableListOf(
        HelpArticle(
            id = "art1",
            title = "How to change your PIN",
            content = "To change your PIN, go to Settings > Security > Change PIN...",
            category = categories[0],
            tags = listOf("PIN", "Security", "Settings"),
            isPublished = true,
            viewCount = 1250,
            helpfulCount = 45,
            notHelpfulCount = 3,
            createdAt = LocalDateTime.now().minusDays(30),
            updatedAt = LocalDateTime.now().minusDays(5),
            authorId = "admin1"
        ),
        HelpArticle(
            id = "art2",
            title = "Understanding transaction fees",
            content = "Transaction fees may apply for certain types of transactions...",
            category = categories[1],
            tags = listOf("Fees", "Transactions", "Costs"),
            isPublished = true,
            viewCount = 890,
            helpfulCount = 32,
            notHelpfulCount = 8,
            createdAt = LocalDateTime.now().minusDays(20),
            updatedAt = LocalDateTime.now().minusDays(2),
            authorId = "admin2"
        )
    )
    
    override suspend fun getHelpArticles(): Result<List<HelpArticle>> {
        return try {
            val publishedArticles = articles.filter { it.isPublished }
                .sortedByDescending { it.updatedAt }
            Result.success(publishedArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getHelpArticle(articleId: String): Result<HelpArticle?> {
        return try {
            val article = articles.find { it.id == articleId && it.isPublished }
            Result.success(article)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getHelpCategories(): Result<List<HelpCategory>> {
        return try {
            val activeCategories = categories.filter { it.isActive }
                .sortedBy { it.sortOrder }
            Result.success(activeCategories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getArticlesByCategory(categoryId: String): Result<List<HelpArticle>> {
        return try {
            val categoryArticles = articles.filter { 
                it.category.id == categoryId && it.isPublished 
            }.sortedByDescending { it.viewCount }
            Result.success(categoryArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchHelpArticles(query: String): Result<HelpSearchResult> {
        return try {
            val searchResults = articles.filter { article ->
                article.isPublished && (
                    article.title.contains(query, ignoreCase = true) ||
                    article.content.contains(query, ignoreCase = true) ||
                    article.tags.any { it.contains(query, ignoreCase = true) }
                )
            }.sortedByDescending { it.viewCount }
            
            val suggestions = if (searchResults.isEmpty()) {
                listOf("PIN", "Transaction", "Account", "Security")
            } else {
                emptyList()
            }
            
            val result = HelpSearchResult(
                articles = searchResults,
                totalCount = searchResults.size,
                suggestions = suggestions
            )
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markArticleHelpful(articleId: String, isHelpful: Boolean): Result<Unit> {
        return try {
            val index = articles.indexOfFirst { it.id == articleId }
            if (index != -1) {
                val article = articles[index]
                val updated = if (isHelpful) {
                    article.copy(helpfulCount = article.helpfulCount + 1)
                } else {
                    article.copy(notHelpfulCount = article.notHelpfulCount + 1)
                }
                articles[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Article not found"))
            }
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
    
    override suspend fun getPopularArticles(limit: Int): Result<List<HelpArticle>> {
        return try {
            val popularArticles = articles.filter { it.isPublished }
                .sortedByDescending { it.viewCount }
                .take(limit)
            Result.success(popularArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecentArticles(limit: Int): Result<List<HelpArticle>> {
        return try {
            val recentArticles = articles.filter { it.isPublished }
                .sortedByDescending { it.updatedAt }
                .take(limit)
            Result.success(recentArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRelatedArticles(articleId: String): Result<List<HelpArticle>> {
        return try {
            val article = articles.find { it.id == articleId }
            if (article != null) {
                val relatedArticles = articles.filter { otherArticle ->
                    otherArticle.id != articleId &&
                    otherArticle.isPublished &&
                    (otherArticle.category.id == article.category.id ||
                     otherArticle.tags.any { tag -> article.tags.contains(tag) })
                }.sortedByDescending { it.viewCount }
                .take(5)
                
                Result.success(relatedArticles)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}