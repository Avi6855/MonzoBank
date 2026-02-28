package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

// Note: CampaignRepository is closely related to MarketingRepository
// This provides a more focused interface for campaign-specific operations

data class Campaign(
    val id: String,
    val name: String,
    val description: String,
    val type: CampaignType,
    val status: CampaignStatus,
    val targetAudience: List<String>,
    val channels: List<CampaignChannel>,
    val budget: BigDecimal,
    val spentAmount: BigDecimal,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdBy: String,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime,
    val objectives: List<CampaignObjective>,
    val performance: CampaignPerformance?
)

data class CampaignObjective(
    val id: String,
    val type: ObjectiveType,
    val targetValue: BigDecimal,
    val currentValue: BigDecimal,
    val unit: String,
    val deadline: LocalDateTime?
)

data class CampaignPerformance(
    val campaignId: String,
    val reach: Long,
    val impressions: Long,
    val clicks: Long,
    val conversions: Long,
    val revenue: BigDecimal,
    val cost: BigDecimal,
    val engagementRate: Double,
    val clickThroughRate: Double,
    val conversionRate: Double,
    val costPerClick: BigDecimal,
    val costPerConversion: BigDecimal,
    val returnOnAdSpend: Double,
    val lastUpdated: LocalDateTime
)

data class CampaignAudience(
    val id: String,
    val campaignId: String,
    val segmentId: String,
    val segmentName: String,
    val userCount: Int,
    val targetedCount: Int,
    val reachedCount: Int,
    val engagedCount: Int,
    val convertedCount: Int
)

data class CampaignContent(
    val id: String,
    val campaignId: String,
    val title: String,
    val content: String,
    val contentType: ContentType,
    val channel: CampaignChannel,
    val language: String,
    val version: Int,
    val isActive: Boolean,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime
)

data class CampaignSchedule(
    val id: String,
    val campaignId: String,
    val channel: CampaignChannel,
    val scheduledDate: LocalDateTime,
    val actualSentDate: LocalDateTime?,
    val status: ScheduleStatus,
    val targetAudience: String,
    val contentId: String
)

enum class CampaignChannel {
    EMAIL,
    SMS,
    PUSH_NOTIFICATION,
    IN_APP_MESSAGE,
    SOCIAL_MEDIA,
    DISPLAY_ADS,
    SEARCH_ADS,
    VIDEO_ADS,
    NATIVE_ADS,
    DIRECT_MAIL,
    WEBSITE_BANNER,
    MOBILE_BANNER
}

enum class ContentType {
    TEXT,
    HTML,
    IMAGE,
    VIDEO,
    AUDIO,
    INTERACTIVE,
    RICH_MEDIA
}

enum class ObjectiveType {
    AWARENESS,
    REACH,
    TRAFFIC,
    ENGAGEMENT,
    LEADS,
    CONVERSIONS,
    SALES,
    RETENTION,
    LOYALTY
}

enum class ScheduleStatus {
    SCHEDULED,
    SENT,
    DELIVERED,
    FAILED,
    CANCELLED,
    PAUSED
}

interface CampaignRepository {
    suspend fun getCampaigns(): Result<List<Campaign>>
    suspend fun getCampaign(campaignId: String): Result<Campaign?>
    suspend fun createCampaign(campaign: Campaign): Result<String>
    suspend fun updateCampaign(campaign: Campaign): Result<Unit>
    suspend fun deleteCampaign(campaignId: String): Result<Unit>
    suspend fun getActiveCampaigns(): Result<List<Campaign>>
    suspend fun getCampaignsByType(type: CampaignType): Result<List<Campaign>>
    suspend fun getCampaignsByStatus(status: CampaignStatus): Result<List<Campaign>>
    suspend fun getCampaignsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Result<List<Campaign>>
    suspend fun getCampaignPerformance(campaignId: String): Result<CampaignPerformance?>
    suspend fun updateCampaignPerformance(performance: CampaignPerformance): Result<Unit>
    suspend fun getCampaignAudiences(campaignId: String): Result<List<CampaignAudience>>
    suspend fun addAudienceToCampaign(campaignId: String, audience: CampaignAudience): Result<Unit>
    suspend fun removeAudienceFromCampaign(campaignId: String, audienceId: String): Result<Unit>
    suspend fun getCampaignContent(campaignId: String): Result<List<CampaignContent>>
    suspend fun addContentToCampaign(content: CampaignContent): Result<String>
    suspend fun updateCampaignContent(content: CampaignContent): Result<Unit>
    suspend fun deleteCampaignContent(contentId: String): Result<Unit>
    suspend fun getCampaignSchedules(campaignId: String): Result<List<CampaignSchedule>>
    suspend fun scheduleCampaignContent(schedule: CampaignSchedule): Result<String>
    suspend fun updateCampaignSchedule(schedule: CampaignSchedule): Result<Unit>
    suspend fun cancelCampaignSchedule(scheduleId: String): Result<Unit>
    suspend fun launchCampaign(campaignId: String): Result<Unit>
    suspend fun pauseCampaign(campaignId: String): Result<Unit>
    suspend fun resumeCampaign(campaignId: String): Result<Unit>
    suspend fun stopCampaign(campaignId: String): Result<Unit>
    suspend fun duplicateCampaign(campaignId: String, newName: String): Result<String>
    suspend fun getCampaignAnalytics(campaignId: String, startDate: LocalDateTime, endDate: LocalDateTime): Result<CampaignPerformance>
    suspend fun getTopPerformingCampaigns(limit: Int): Result<List<Campaign>>
    suspend fun getCampaignsByBudgetRange(minBudget: BigDecimal, maxBudget: BigDecimal): Result<List<Campaign>>
}