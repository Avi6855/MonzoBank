package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepositoryImpl @Inject constructor() : FeedbackRepository {
    
    private val feedbackList = mutableListOf<Feedback>()
    
    override suspend fun submitFeedback(feedback: Feedback): Result<String> {
        return try {
            feedbackList.add(feedback)
            Result.success(feedback.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeedback(userId: String): Result<List<Feedback>> {
        return try {
            val userFeedback = feedbackList.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(userFeedback)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeedbackById(feedbackId: String): Result<Feedback?> {
        return try {
            val feedback = feedbackList.find { it.id == feedbackId }
            Result.success(feedback)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateFeedbackStatus(feedbackId: String, status: FeedbackStatus): Result<Unit> {
        return try {
            val index = feedbackList.indexOfFirst { it.id == feedbackId }
            if (index != -1) {
                val updated = feedbackList[index].copy(
                    status = status,
                    updatedAt = LocalDateTime.now()
                )
                feedbackList[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Feedback not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun respondToFeedback(feedbackId: String, response: String, respondedBy: String): Result<Unit> {
        return try {
            val index = feedbackList.indexOfFirst { it.id == feedbackId }
            if (index != -1) {
                val updated = feedbackList[index].copy(
                    response = response,
                    respondedBy = respondedBy,
                    respondedAt = LocalDateTime.now(),
                    status = FeedbackStatus.RESOLVED,
                    updatedAt = LocalDateTime.now()
                )
                feedbackList[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Feedback not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeedbackByCategory(category: FeedbackCategory): Result<List<Feedback>> {
        return try {
            val categoryFeedback = feedbackList.filter { it.category == category }
                .sortedByDescending { it.createdAt }
            Result.success(categoryFeedback)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeedbackByStatus(status: FeedbackStatus): Result<List<Feedback>> {
        return try {
            val statusFeedback = feedbackList.filter { it.status == status }
                .sortedByDescending { it.createdAt }
            Result.success(statusFeedback)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeedbackSummary(startDate: LocalDateTime, endDate: LocalDateTime): Result<FeedbackSummary> {
        return try {
            val periodFeedback = feedbackList.filter { 
                it.createdAt.isAfter(startDate) && it.createdAt.isBefore(endDate) 
            }
            
            val totalFeedback = periodFeedback.size
            val averageRating = if (periodFeedback.isNotEmpty()) {
                periodFeedback.mapNotNull { it.rating }.average()
            } else 0.0
            
            val categoryBreakdown = periodFeedback.groupBy { it.category }
                .mapValues { it.value.size }
            
            val statusBreakdown = periodFeedback.groupBy { it.status }
                .mapValues { it.value.size }
            
            val recentFeedback = periodFeedback.sortedByDescending { it.createdAt }.take(10)
            
            // Mock trend data
            val trendData = listOf(
                FeedbackTrend(LocalDateTime.now().minusDays(7), 15, 4.2),
                FeedbackTrend(LocalDateTime.now().minusDays(6), 12, 4.1),
                FeedbackTrend(LocalDateTime.now().minusDays(5), 18, 4.3)
            )
            
            val summary = FeedbackSummary(
                totalFeedback = totalFeedback,
                averageRating = averageRating,
                categoryBreakdown = categoryBreakdown,
                statusBreakdown = statusBreakdown,
                recentFeedback = recentFeedback,
                trendData = trendData
            )
            
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchFeedback(query: String): Result<List<Feedback>> {
        return try {
            val searchResults = feedbackList.filter { feedback ->
                feedback.subject.contains(query, ignoreCase = true) ||
                feedback.message.contains(query, ignoreCase = true) ||
                feedback.response?.contains(query, ignoreCase = true) == true
            }.sortedByDescending { it.createdAt }
            
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteFeedback(feedbackId: String): Result<Unit> {
        return try {
            val removed = feedbackList.removeIf { it.id == feedbackId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Feedback not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeedbackAnalytics(period: String): Result<Map<String, Any>> {
        return try {
            val analytics = mapOf<String, Any>(
                "total_feedback" to feedbackList.size,
                "average_rating" to if (feedbackList.isNotEmpty()) feedbackList.mapNotNull { it.rating }.average() else 0.0,
                "response_rate" to if (feedbackList.isNotEmpty()) (feedbackList.count { it.response != null }.toDouble() / feedbackList.size * 100) else 0.0,
                "most_common_category" to (feedbackList.groupBy { it.category }
                    .maxByOrNull { it.value.size }?.key?.name ?: "None"),
                "resolution_time_avg" to "2.5 days" // Mock data
            )
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportFeedback(format: String, filters: Map<String, Any>): Result<String> {
        return try {
            // Mock export functionality
            val exportId = UUID.randomUUID().toString()
            val exportUrl = "https://api.monzobank.com/exports/feedback/$exportId.$format"
            Result.success(exportUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}