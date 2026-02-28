package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class MarketingCampaign(
    val id: String,
    val name: String,
    val description: String,
    val type: CampaignType,
    val status: CampaignStatus,
    val targetAudience: List<String>,
    val channels: List<MarketingChannel>,
    val budget: BigDecimal,
    val spentAmount: BigDecimal,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdBy: String,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime,
    val goals: List<CampaignGoal>,
    val metrics: CampaignMetrics?
)

data class MarketingSegment(
    val id: String,
    val name: String,
    val description: String,
    val criteria: List<SegmentCriteria>,
    val userCount: Int,
    val isActive: Boolean,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime
)

data class MarketingMessage(
    val id: String,
    val campaignId: String,
    val title: String,
    val content: String,
    val channel: MarketingChannel,
    val targetSegment: String,
    val scheduledDate: LocalDateTime?,
    val sentDate: LocalDateTime?,
    val status: MessageStatus,
    val personalizationData: Map<String, String> = emptyMap()
)

data class CampaignMetrics(
    val impressions: Long,
    val clicks: Long,
    val conversions: Long,
    val revenue: BigDecimal,
    val cost: BigDecimal,
    val clickThroughRate: Double,
    val conversionRate: Double,
    val returnOnInvestment: Double,
    val lastUpdated: LocalDateTime
)

data class CampaignGoal(
    val type: MarketingGoalType,
    val targetValue: BigDecimal,
    val currentValue: BigDecimal,
    val unit: String
)

data class SegmentCriteria(
    val field: String,
    val operator: String,
    val value: String
)

data class MarketingAnalytics(
    val totalCampaigns: Int,
    val activeCampaigns: Int,
    val totalBudget: BigDecimal,
    val totalSpent: BigDecimal,
    val totalImpressions: Long,
    val totalClicks: Long,
    val totalConversions: Long,
    val averageCTR: Double,
    val averageConversionRate: Double,
    val totalROI: Double
)

enum class CampaignType {
    ACQUISITION,
    RETENTION,
    ENGAGEMENT,
    PRODUCT_LAUNCH,
    SEASONAL,
    PROMOTIONAL,
    EDUCATIONAL,
    BRAND_AWARENESS
}

enum class CampaignStatus {
    DRAFT,
    SCHEDULED,
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED
}

enum class MarketingChannel {
    EMAIL,
    SMS,
    PUSH_NOTIFICATION,
    IN_APP_MESSAGE,
    SOCIAL_MEDIA,
    DISPLAY_ADS,
    SEARCH_ADS,
    DIRECT_MAIL,
    WEBSITE_BANNER
}

enum class MessageStatus {
    DRAFT,
    SCHEDULED,
    SENT,
    DELIVERED,
    OPENED,
    CLICKED,
    FAILED,
    CANCELLED
}

enum class MarketingGoalType {
    IMPRESSIONS,
    CLICKS,
    CONVERSIONS,
    REVENUE,
    SIGN_UPS,
    APP_DOWNLOADS,
    ENGAGEMENT_RATE
}

interface MarketingRepository {
    suspend fun getCampaigns(): Result<List<MarketingCampaign>>
    suspend fun getCampaign(campaignId: String): Result<MarketingCampaign?>
    suspend fun createCampaign(campaign: MarketingCampaign): Result<String>
    suspend fun updateCampaign(campaign: MarketingCampaign): Result<Unit>
    suspend fun deleteCampaign(campaignId: String): Result<Unit>
    suspend fun getActiveCampaigns(): Result<List<MarketingCampaign>>
    suspend fun getCampaignsByType(type: CampaignType): Result<List<MarketingCampaign>>
    suspend fun getCampaignsByStatus(status: CampaignStatus): Result<List<MarketingCampaign>>
    suspend fun getMarketingSegments(): Result<List<MarketingSegment>>
    suspend fun getSegment(segmentId: String): Result<MarketingSegment?>
    suspend fun createSegment(segment: MarketingSegment): Result<String>
    suspend fun updateSegment(segment: MarketingSegment): Result<Unit>
    suspend fun deleteSegment(segmentId: String): Result<Unit>
    suspend fun getCampaignMessages(campaignId: String): Result<List<MarketingMessage>>
    suspend fun getMessage(messageId: String): Result<MarketingMessage?>
    suspend fun createMessage(message: MarketingMessage): Result<String>
    suspend fun updateMessage(message: MarketingMessage): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun sendMessage(messageId: String): Result<Unit>
    suspend fun scheduleMessage(messageId: String, scheduledDate: LocalDateTime): Result<Unit>
    suspend fun getCampaignMetrics(campaignId: String): Result<CampaignMetrics?>
    suspend fun updateCampaignMetrics(campaignId: String, metrics: CampaignMetrics): Result<Unit>
    suspend fun getMarketingAnalytics(): Result<MarketingAnalytics>
    suspend fun getSegmentUsers(segmentId: String): Result<List<String>>
    suspend fun addUserToSegment(userId: String, segmentId: String): Result<Unit>
    suspend fun removeUserFromSegment(userId: String, segmentId: String): Result<Unit>
    suspend fun getPersonalizedContent(userId: String): Result<List<MarketingMessage>>
    suspend fun trackMessageInteraction(messageId: String, userId: String, interactionType: String): Result<Unit>
}