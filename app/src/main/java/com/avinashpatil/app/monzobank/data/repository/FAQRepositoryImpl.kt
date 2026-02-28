package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FAQRepositoryImpl @Inject constructor() : FAQRepository {
    
    private val faqs = mutableListOf(
        FAQ(
            id = "faq1",
            question = "How do I reset my password?",
            answer = "You can reset your password by clicking 'Forgot Password' on the login screen and following the instructions sent to your email.",
            category = "Account",
            tags = listOf("password", "reset", "login"),
            isPublished = true,
            viewCount = 1500,
            helpfulCount = 120,
            sortOrder = 1,
            createdAt = LocalDateTime.now().minusDays(60),
            updatedAt = LocalDateTime.now().minusDays(10)
        ),
        FAQ(
            id = "faq2",
            question = "What are the transaction limits?",
            answer = "Daily transaction limits vary by account type. Standard accounts have a £1000 daily limit, while premium accounts have £5000.",
            category = "Transactions",
            tags = listOf("limits", "transactions", "daily"),
            isPublished = true,
            viewCount = 890,
            helpfulCount = 75,
            sortOrder = 2,
            createdAt = LocalDateTime.now().minusDays(45),
            updatedAt = LocalDateTime.now().minusDays(5)
        )
    )
    
    override suspend fun getFAQs(): Result<List<FAQ>> {
        return try {
            val publishedFAQs = faqs.filter { it.isPublished }
                .sortedBy { it.sortOrder }
            Result.success(publishedFAQs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFAQ(faqId: String): Result<FAQ?> {
        return try {
            val faq = faqs.find { it.id == faqId && it.isPublished }
            Result.success(faq)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFAQsByCategory(category: String): Result<List<FAQ>> {
        return try {
            val categoryFAQs = faqs.filter { 
                it.category.equals(category, ignoreCase = true) && it.isPublished 
            }.sortedBy { it.sortOrder }
            Result.success(categoryFAQs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchFAQs(query: String): Result<List<FAQ>> {
        return try {
            val searchResults = faqs.filter { faq ->
                faq.isPublished && (
                    faq.question.contains(query, ignoreCase = true) ||
                    faq.answer.contains(query, ignoreCase = true) ||
                    faq.tags.any { it.contains(query, ignoreCase = true) }
                )
            }.sortedByDescending { it.viewCount }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markFAQHelpful(faqId: String): Result<Unit> {
        return try {
            val index = faqs.indexOfFirst { it.id == faqId }
            if (index != -1) {
                val faq = faqs[index]
                val updated = faq.copy(helpfulCount = faq.helpfulCount + 1)
                faqs[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("FAQ not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun incrementViewCount(faqId: String): Result<Unit> {
        return try {
            val index = faqs.indexOfFirst { it.id == faqId }
            if (index != -1) {
                val faq = faqs[index]
                val updated = faq.copy(viewCount = faq.viewCount + 1)
                faqs[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("FAQ not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPopularFAQs(limit: Int): Result<List<FAQ>> {
        return try {
            val popularFAQs = faqs.filter { it.isPublished }
                .sortedByDescending { it.viewCount }
                .take(limit)
            Result.success(popularFAQs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}