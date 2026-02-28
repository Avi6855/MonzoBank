package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampaignRepositoryImpl @Inject constructor() : CampaignRepository {
    
    private val campaigns = mutableListOf<Campaign>()
    private val campaignAudiences = mutableListOf<CampaignAudience>()
    private val campaignContent = mutableListOf<CampaignContent>()
    private val campaignSchedules = mutableListOf<CampaignSchedule>()
    
    init {
        initializeMockData()
    }
    
    override suspend fun getCampaigns(): Result<List<Campaign>> {
        return try {
            val sortedCampaigns = campaigns.sortedByDescending { it.createdDate }
            Result.success(sortedCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaign(campaignId: String): Result<Campaign?> {
        return try {
            val campaign = campaigns.find { it.id == campaignId }
            Result.success(campaign)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createCampaign(campaign: Campaign): Result<String> {
        return try {
            campaigns.add(campaign)
            Result.success(campaign.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCampaign(campaign: Campaign): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == campaign.id }
            if (index != -1) {
                campaigns[index] = campaign.copy(updatedDate = LocalDateTime.now())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCampaign(campaignId: String): Result<Unit> {
        return try {
            val removed = campaigns.removeIf { it.id == campaignId }
            if (removed) {
                // Clean up related data
                campaignAudiences.removeIf { it.campaignId == campaignId }
                campaignContent.removeIf { it.campaignId == campaignId }
                campaignSchedules.removeIf { it.campaignId == campaignId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveCampaigns(): Result<List<Campaign>> {
        return try {
            val now = LocalDateTime.now()
            val activeCampaigns = campaigns.filter { 
                it.status == CampaignStatus.ACTIVE &&
                it.startDate.isBefore(now) &&
                it.endDate.isAfter(now)
            }.sortedBy { it.startDate }
            
            Result.success(activeCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignsByType(type: CampaignType): Result<List<Campaign>> {
        return try {
            val typeCampaigns = campaigns.filter { it.type == type }
                .sortedByDescending { it.createdDate }
            Result.success(typeCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignsByStatus(status: CampaignStatus): Result<List<Campaign>> {
        return try {
            val statusCampaigns = campaigns.filter { it.status == status }
                .sortedByDescending { it.createdDate }
            Result.success(statusCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Result<List<Campaign>> {
        return try {
            val rangeCampaigns = campaigns.filter { campaign ->
                campaign.startDate.isBefore(endDate) && campaign.endDate.isAfter(startDate)
            }.sortedBy { it.startDate }
            
            Result.success(rangeCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignPerformance(campaignId: String): Result<CampaignPerformance?> {
        return try {
            val campaign = campaigns.find { it.id == campaignId }
            Result.success(campaign?.performance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCampaignPerformance(performance: CampaignPerformance): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == performance.campaignId }
            if (index != -1) {
                val campaign = campaigns[index]
                campaigns[index] = campaign.copy(
                    performance = performance,
                    updatedDate = LocalDateTime.now()
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignAudiences(campaignId: String): Result<List<CampaignAudience>> {
        return try {
            val audiences = campaignAudiences.filter { it.campaignId == campaignId }
            Result.success(audiences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addAudienceToCampaign(campaignId: String, audience: CampaignAudience): Result<Unit> {
        return try {
            val audienceWithCampaign = audience.copy(campaignId = campaignId)
            campaignAudiences.add(audienceWithCampaign)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeAudienceFromCampaign(campaignId: String, audienceId: String): Result<Unit> {
        return try {
            val removed = campaignAudiences.removeIf { 
                it.campaignId == campaignId && it.id == audienceId 
            }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Audience not found in campaign"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignContent(campaignId: String): Result<List<CampaignContent>> {
        return try {
            val content = campaignContent.filter { it.campaignId == campaignId }
                .sortedByDescending { it.updatedDate }
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addContentToCampaign(content: CampaignContent): Result<String> {
        return try {
            campaignContent.add(content)
            Result.success(content.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCampaignContent(content: CampaignContent): Result<Unit> {
        return try {
            val index = campaignContent.indexOfFirst { it.id == content.id }
            if (index != -1) {
                campaignContent[index] = content.copy(updatedDate = LocalDateTime.now())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Content not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCampaignContent(contentId: String): Result<Unit> {
        return try {
            val removed = campaignContent.removeIf { it.id == contentId }
            if (removed) {
                // Also remove related schedules
                campaignSchedules.removeIf { it.contentId == contentId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Content not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignSchedules(campaignId: String): Result<List<CampaignSchedule>> {
        return try {
            val schedules = campaignSchedules.filter { it.campaignId == campaignId }
                .sortedBy { it.scheduledDate }
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun scheduleCampaignContent(schedule: CampaignSchedule): Result<String> {
        return try {
            campaignSchedules.add(schedule)
            Result.success(schedule.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCampaignSchedule(schedule: CampaignSchedule): Result<Unit> {
        return try {
            val index = campaignSchedules.indexOfFirst { it.id == schedule.id }
            if (index != -1) {
                campaignSchedules[index] = schedule
                Result.success(Unit)
            } else {
                Result.failure(Exception("Schedule not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelCampaignSchedule(scheduleId: String): Result<Unit> {
        return try {
            val index = campaignSchedules.indexOfFirst { it.id == scheduleId }
            if (index != -1) {
                val schedule = campaignSchedules[index]
                campaignSchedules[index] = schedule.copy(status = ScheduleStatus.CANCELLED)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Schedule not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun launchCampaign(campaignId: String): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == campaignId }
            if (index != -1) {
                val campaign = campaigns[index]
                if (campaign.status == CampaignStatus.DRAFT || campaign.status == CampaignStatus.SCHEDULED) {
                    campaigns[index] = campaign.copy(
                        status = CampaignStatus.ACTIVE,
                        updatedDate = LocalDateTime.now()
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Campaign cannot be launched in current status"))
                }
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pauseCampaign(campaignId: String): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == campaignId }
            if (index != -1) {
                val campaign = campaigns[index]
                if (campaign.status == CampaignStatus.ACTIVE) {
                    campaigns[index] = campaign.copy(
                        status = CampaignStatus.PAUSED,
                        updatedDate = LocalDateTime.now()
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Only active campaigns can be paused"))
                }
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resumeCampaign(campaignId: String): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == campaignId }
            if (index != -1) {
                val campaign = campaigns[index]
                if (campaign.status == CampaignStatus.PAUSED) {
                    campaigns[index] = campaign.copy(
                        status = CampaignStatus.ACTIVE,
                        updatedDate = LocalDateTime.now()
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Only paused campaigns can be resumed"))
                }
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun stopCampaign(campaignId: String): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == campaignId }
            if (index != -1) {
                val campaign = campaigns[index]
                campaigns[index] = campaign.copy(
                    status = CampaignStatus.COMPLETED,
                    updatedDate = LocalDateTime.now()
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun duplicateCampaign(campaignId: String, newName: String): Result<String> {
        return try {
            val originalCampaign = campaigns.find { it.id == campaignId }
                ?: return Result.failure(Exception("Campaign not found"))
            
            val newCampaignId = UUID.randomUUID().toString()
            val duplicatedCampaign = originalCampaign.copy(
                id = newCampaignId,
                name = newName,
                status = CampaignStatus.DRAFT,
                spentAmount = BigDecimal.ZERO,
                createdDate = LocalDateTime.now(),
                updatedDate = LocalDateTime.now(),
                performance = null
            )
            
            campaigns.add(duplicatedCampaign)
            
            // Duplicate content
            val originalContent = campaignContent.filter { it.campaignId == campaignId }
            originalContent.forEach { content ->
                val newContent = content.copy(
                    id = UUID.randomUUID().toString(),
                    campaignId = newCampaignId,
                    createdDate = LocalDateTime.now(),
                    updatedDate = LocalDateTime.now()
                )
                campaignContent.add(newContent)
            }
            
            Result.success(newCampaignId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignAnalytics(campaignId: String, startDate: LocalDateTime, endDate: LocalDateTime): Result<CampaignPerformance> {
        return try {
            val campaign = campaigns.find { it.id == campaignId }
                ?: return Result.failure(Exception("Campaign not found"))
            
            // Mock analytics calculation - in real app, this would aggregate actual data
            val performance = campaign.performance ?: CampaignPerformance(
                campaignId = campaignId,
                reach = 0,
                impressions = 0,
                clicks = 0,
                conversions = 0,
                revenue = BigDecimal.ZERO,
                cost = BigDecimal.ZERO,
                engagementRate = 0.0,
                clickThroughRate = 0.0,
                conversionRate = 0.0,
                costPerClick = BigDecimal.ZERO,
                costPerConversion = BigDecimal.ZERO,
                returnOnAdSpend = 0.0,
                lastUpdated = LocalDateTime.now()
            )
            
            Result.success(performance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTopPerformingCampaigns(limit: Int): Result<List<Campaign>> {
        return try {
            val topCampaigns = campaigns.filter { it.performance != null }
                .sortedByDescending { it.performance?.returnOnAdSpend ?: 0.0 }
                .take(limit)
            
            Result.success(topCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignsByBudgetRange(minBudget: BigDecimal, maxBudget: BigDecimal): Result<List<Campaign>> {
        return try {
            val budgetCampaigns = campaigns.filter { campaign ->
                campaign.budget >= minBudget && campaign.budget <= maxBudget
            }.sortedByDescending { it.budget }
            
            Result.success(budgetCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun initializeMockData() {
        val mockCampaigns = listOf(
            Campaign(
                id = UUID.randomUUID().toString(),
                name = "Summer Promotion 2024",
                description = "Promote summer banking offers and new account sign-ups",
                type = CampaignType.PROMOTIONAL,
                status = CampaignStatus.ACTIVE,
                targetAudience = listOf("young_adults", "students"),
                channels = listOf(CampaignChannel.EMAIL, CampaignChannel.SOCIAL_MEDIA, CampaignChannel.PUSH_NOTIFICATION),
                budget = BigDecimal("15000.00"),
                spentAmount = BigDecimal("8500.00"),
                startDate = LocalDateTime.now().minusDays(15),
                endDate = LocalDateTime.now().plusDays(45),
                createdBy = "marketing_manager",
                createdDate = LocalDateTime.now().minusDays(20),
                updatedDate = LocalDateTime.now().minusDays(15),
                objectives = listOf(
                    CampaignObjective(
                        id = UUID.randomUUID().toString(),
                        type = ObjectiveType.CONVERSIONS,
                        targetValue = BigDecimal("500"),
                        currentValue = BigDecimal("287"),
                        unit = "sign-ups",
                        deadline = LocalDateTime.now().plusDays(45)
                    ),
                    CampaignObjective(
                        id = UUID.randomUUID().toString(),
                        type = ObjectiveType.REACH,
                        targetValue = BigDecimal("50000"),
                        currentValue = BigDecimal("32000"),
                        unit = "users",
                        deadline = LocalDateTime.now().plusDays(45)
                    )
                ),
                performance = CampaignPerformance(
                    campaignId = "", // Will be set to campaign ID
                    reach = 32000,
                    impressions = 125000,
                    clicks = 3750,
                    conversions = 287,
                    revenue = BigDecimal("28700.00"),
                    cost = BigDecimal("8500.00"),
                    engagementRate = 12.5,
                    clickThroughRate = 3.0,
                    conversionRate = 7.65,
                    costPerClick = BigDecimal("2.27"),
                    costPerConversion = BigDecimal("29.62"),
                    returnOnAdSpend = 237.6,
                    lastUpdated = LocalDateTime.now()
                )
            ),
            Campaign(
                id = UUID.randomUUID().toString(),
                name = "Premium Account Upgrade",
                description = "Encourage existing customers to upgrade to premium accounts",
                type = CampaignType.RETENTION,
                status = CampaignStatus.SCHEDULED,
                targetAudience = listOf("existing_customers", "high_value_users"),
                channels = listOf(CampaignChannel.EMAIL, CampaignChannel.IN_APP_MESSAGE),
                budget = BigDecimal("8000.00"),
                spentAmount = BigDecimal.ZERO,
                startDate = LocalDateTime.now().plusDays(5),
                endDate = LocalDateTime.now().plusDays(35),
                createdBy = "product_manager",
                createdDate = LocalDateTime.now().minusDays(10),
                updatedDate = LocalDateTime.now().minusDays(5),
                objectives = listOf(
                    CampaignObjective(
                        id = UUID.randomUUID().toString(),
                        type = ObjectiveType.CONVERSIONS,
                        targetValue = BigDecimal("200"),
                        currentValue = BigDecimal.ZERO,
                        unit = "upgrades",
                        deadline = LocalDateTime.now().plusDays(35)
                    )
                ),
                performance = null
            )
        )
        
        campaigns.addAll(mockCampaigns)
        
        // Set campaign IDs in performance data
        campaigns.forEach { campaign ->
            campaign.performance?.let { performance ->
                val index = campaigns.indexOfFirst { it.id == campaign.id }
                if (index != -1) {
                    campaigns[index] = campaign.copy(
                        performance = performance.copy(campaignId = campaign.id)
                    )
                }
            }
        }
        
        // Mock content
        val mockContent = listOf(
            CampaignContent(
                id = UUID.randomUUID().toString(),
                campaignId = campaigns[0].id,
                title = "Summer Banking Made Easy!",
                content = "Discover our amazing summer offers and start your banking journey with us.",
                contentType = ContentType.HTML,
                channel = CampaignChannel.EMAIL,
                language = "en",
                version = 1,
                isActive = true,
                createdDate = LocalDateTime.now().minusDays(18),
                updatedDate = LocalDateTime.now().minusDays(15)
            ),
            CampaignContent(
                id = UUID.randomUUID().toString(),
                campaignId = campaigns[1].id,
                title = "Upgrade to Premium Today!",
                content = "Unlock exclusive benefits with our premium account. Limited time offer!",
                contentType = ContentType.TEXT,
                channel = CampaignChannel.IN_APP_MESSAGE,
                language = "en",
                version = 1,
                isActive = true,
                createdDate = LocalDateTime.now().minusDays(8),
                updatedDate = LocalDateTime.now().minusDays(5)
            )
        )
        
        campaignContent.addAll(mockContent)
    }
}