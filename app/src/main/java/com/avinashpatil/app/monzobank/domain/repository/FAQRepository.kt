package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class FAQ(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val tags: List<String>,
    val isPublished: Boolean,
    val viewCount: Int,
    val helpfulCount: Int,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

interface FAQRepository {
    suspend fun getFAQs(): Result<List<FAQ>>
    suspend fun getFAQ(faqId: String): Result<FAQ?>
    suspend fun getFAQsByCategory(category: String): Result<List<FAQ>>
    suspend fun searchFAQs(query: String): Result<List<FAQ>>
    suspend fun markFAQHelpful(faqId: String): Result<Unit>
    suspend fun incrementViewCount(faqId: String): Result<Unit>
    suspend fun getPopularFAQs(limit: Int): Result<List<FAQ>>
}