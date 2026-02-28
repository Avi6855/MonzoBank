package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Feedback(
    val id: String,
    val userId: String,
    val type: FeedbackType,
    val category: FeedbackCategory,
    val subject: String,
    val message: String,
    val rating: Int? = null, // 1-5 stars
    val status: FeedbackStatus,
    val priority: FeedbackPriority,
    val attachments: List<String> = emptyList(),
    val response: String? = null,
    val respondedBy: String? = null,
    val respondedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val metadata: Map<String, Any> = emptyMap()
)

data class FeedbackSummary(
    val totalFeedback: Int,
    val averageRating: Double,
    val categoryBreakdown: Map<FeedbackCategory, Int>,
    val statusBreakdown: Map<FeedbackStatus, Int>,
    val recentFeedback: List<Feedback>,
    val trendData: List<FeedbackTrend>
)

data class FeedbackTrend(
    val date: LocalDateTime,
    val count: Int,
    val averageRating: Double
)

enum class FeedbackType {
    COMPLAINT,
    SUGGESTION,
    COMPLIMENT,
    BUG_REPORT,
    FEATURE_REQUEST,
    GENERAL_INQUIRY,
    TECHNICAL_ISSUE
}

enum class FeedbackCategory {
    APP_FUNCTIONALITY,
    USER_INTERFACE,
    PERFORMANCE,
    SECURITY,
    CUSTOMER_SERVICE,
    ACCOUNT_MANAGEMENT,
    TRANSACTIONS,
    CARDS,
    LOANS,
    INVESTMENTS,
    GENERAL
}

enum class FeedbackStatus {
    SUBMITTED,
    ACKNOWLEDGED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    ESCALATED
}

enum class FeedbackPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

interface FeedbackRepository {
    suspend fun submitFeedback(feedback: Feedback): Result<String>
    suspend fun getFeedback(userId: String): Result<List<Feedback>>
    suspend fun getFeedbackById(feedbackId: String): Result<Feedback?>
    suspend fun updateFeedbackStatus(feedbackId: String, status: FeedbackStatus): Result<Unit>
    suspend fun respondToFeedback(feedbackId: String, response: String, respondedBy: String): Result<Unit>
    suspend fun getFeedbackByCategory(category: FeedbackCategory): Result<List<Feedback>>
    suspend fun getFeedbackByStatus(status: FeedbackStatus): Result<List<Feedback>>
    suspend fun getFeedbackSummary(startDate: LocalDateTime, endDate: LocalDateTime): Result<FeedbackSummary>
    suspend fun searchFeedback(query: String): Result<List<Feedback>>
    suspend fun deleteFeedback(feedbackId: String): Result<Unit>
    suspend fun getFeedbackAnalytics(period: String): Result<Map<String, Any>>
    suspend fun exportFeedback(format: String, filters: Map<String, Any>): Result<String>
}