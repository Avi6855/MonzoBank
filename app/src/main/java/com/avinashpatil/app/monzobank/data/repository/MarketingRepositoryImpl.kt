package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.MarketingRepository
import com.avinashpatil.app.monzobank.domain.repository.MarketingCampaign
import com.avinashpatil.app.monzobank.domain.repository.MarketingSegment
import com.avinashpatil.app.monzobank.domain.repository.MarketingMessage
import com.avinashpatil.app.monzobank.domain.repository.CampaignGoal
import com.avinashpatil.app.monzobank.domain.repository.CampaignMetrics
import com.avinashpatil.app.monzobank.domain.repository.SegmentCriteria
import com.avinashpatil.app.monzobank.domain.repository.MarketingAnalytics
import com.avinashpatil.app.monzobank.domain.repository.CampaignType
import com.avinashpatil.app.monzobank.domain.repository.CampaignStatus
import com.avinashpatil.app.monzobank.domain.repository.MarketingChannel
import com.avinashpatil.app.monzobank.domain.repository.MessageStatus
import com.avinashpatil.app.monzobank.domain.repository.MarketingGoalType
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketingRepositoryImpl @Inject constructor() : MarketingRepository {
    
    private val campaigns = mutableListOf<MarketingCampaign>()
    private val segments = mutableListOf<MarketingSegment>()
    private val messages = mutableListOf<MarketingMessage>()
    private val segmentUsers = mutableMapOf<String, MutableList<String>>()
    private val messageInteractions = mutableMapOf<String, MutableList<String>>()
    
    init {
        initializeMockData()
    }
    
    override suspend fun getCampaigns(): Result<List<MarketingCampaign>> {
        return try {
            val sortedCampaigns = campaigns.sortedByDescending { it.createdDate }
            Result.success(sortedCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaign(campaignId: String): Result<MarketingCampaign?> {
        return try {
            val campaign = campaigns.find { it.id == campaignId }
            Result.success(campaign)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createCampaign(campaign: MarketingCampaign): Result<String> {
        return try {
            campaigns.add(campaign)
            Result.success(campaign.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCampaign(campaign: MarketingCampaign): Result<Unit> {
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
                // Also remove associated messages
                messages.removeIf { it.campaignId == campaignId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveCampaigns(): Result<List<MarketingCampaign>> {
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
    
    override suspend fun getCampaignsByType(type: CampaignType): Result<List<MarketingCampaign>> {
        return try {
            val typeCampaigns = campaigns.filter { it.type == type }
                .sortedByDescending { it.createdDate }
            Result.success(typeCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignsByStatus(status: CampaignStatus): Result<List<MarketingCampaign>> {
        return try {
            val statusCampaigns = campaigns.filter { it.status == status }
                .sortedByDescending { it.createdDate }
            Result.success(statusCampaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMarketingSegments(): Result<List<MarketingSegment>> {
        return try {
            val sortedSegments = segments.sortedByDescending { it.createdDate }
            Result.success(sortedSegments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSegment(segmentId: String): Result<MarketingSegment?> {
        return try {
            val segment = segments.find { it.id == segmentId }
            Result.success(segment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createSegment(segment: MarketingSegment): Result<String> {
        return try {
            segments.add(segment)
            segmentUsers[segment.id] = mutableListOf()
            Result.success(segment.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSegment(segment: MarketingSegment): Result<Unit> {
        return try {
            val index = segments.indexOfFirst { it.id == segment.id }
            if (index != -1) {
                segments[index] = segment.copy(updatedDate = LocalDateTime.now())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Segment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteSegment(segmentId: String): Result<Unit> {
        return try {
            val removed = segments.removeIf { it.id == segmentId }
            if (removed) {
                segmentUsers.remove(segmentId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Segment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignMessages(campaignId: String): Result<List<MarketingMessage>> {
        return try {
            val campaignMessages = messages.filter { it.campaignId == campaignId }
                .sortedByDescending { it.scheduledDate ?: it.sentDate }
            Result.success(campaignMessages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMessage(messageId: String): Result<MarketingMessage?> {
        return try {
            val message = messages.find { it.id == messageId }
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createMessage(message: MarketingMessage): Result<String> {
        return try {
            messages.add(message)
            Result.success(message.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMessage(message: MarketingMessage): Result<Unit> {
        return try {
            val index = messages.indexOfFirst { it.id == message.id }
            if (index != -1) {
                messages[index] = message
                Result.success(Unit)
            } else {
                Result.failure(Exception("Message not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            val removed = messages.removeIf { it.id == messageId }
            if (removed) {
                messageInteractions.remove(messageId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Message not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sendMessage(messageId: String): Result<Unit> {
        return try {
            val index = messages.indexOfFirst { it.id == messageId }
            if (index != -1) {
                val message = messages[index]
                if (message.status == MessageStatus.DRAFT || message.status == MessageStatus.SCHEDULED) {
                    val updatedMessage = message.copy(
                        status = MessageStatus.SENT,
                        sentDate = LocalDateTime.now()
                    )
                    messages[index] = updatedMessage
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Message cannot be sent in current status"))
                }
            } else {
                Result.failure(Exception("Message not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun scheduleMessage(messageId: String, scheduledDate: LocalDateTime): Result<Unit> {
        return try {
            val index = messages.indexOfFirst { it.id == messageId }
            if (index != -1) {
                val message = messages[index]
                val updatedMessage = message.copy(
                    status = MessageStatus.SCHEDULED,
                    scheduledDate = scheduledDate
                )
                messages[index] = updatedMessage
                Result.success(Unit)
            } else {
                Result.failure(Exception("Message not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCampaignMetrics(campaignId: String): Result<CampaignMetrics?> {
        return try {
            val campaign = campaigns.find { it.id == campaignId }
            Result.success(campaign?.metrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCampaignMetrics(campaignId: String, metrics: CampaignMetrics): Result<Unit> {
        return try {
            val index = campaigns.indexOfFirst { it.id == campaignId }
            if (index != -1) {
                val campaign = campaigns[index]
                val updatedCampaign = campaign.copy(
                    metrics = metrics,
                    updatedDate = LocalDateTime.now()
                )
                campaigns[index] = updatedCampaign
                Result.success(Unit)
            } else {
                Result.failure(Exception("Campaign not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMarketingAnalytics(): Result<MarketingAnalytics> {
        return try {
            val totalCampaigns = campaigns.size
            val activeCampaigns = campaigns.count { it.status == CampaignStatus.ACTIVE }
            val totalBudget = campaigns.sumOf { it.budget }
            val totalSpent = campaigns.sumOf { it.spentAmount }
            
            val allMetrics = campaigns.mapNotNull { it.metrics }
            val totalImpressions = allMetrics.sumOf { it.impressions }
            val totalClicks = allMetrics.sumOf { it.clicks }
            val totalConversions = allMetrics.sumOf { it.conversions }
            
            val averageCTR = if (totalImpressions > 0) {
                (totalClicks.toDouble() / totalImpressions) * 100
            } else 0.0
            
            val averageConversionRate = if (totalClicks > 0) {
                (totalConversions.toDouble() / totalClicks) * 100
            } else 0.0
            
            val totalRevenue = allMetrics.sumOf { it.revenue }
            val totalCost = allMetrics.sumOf { it.cost }
            val totalROI = if (totalCost > BigDecimal.ZERO) {
                ((totalRevenue - totalCost).divide(totalCost, 4, RoundingMode.HALF_UP).toDouble()) * 100
            } else 0.0
            
            val analytics = MarketingAnalytics(
                totalCampaigns = totalCampaigns,
                activeCampaigns = activeCampaigns,
                totalBudget = totalBudget,
                totalSpent = totalSpent,
                totalImpressions = totalImpressions,
                totalClicks = totalClicks,
                totalConversions = totalConversions,
                averageCTR = averageCTR,
                averageConversionRate = averageConversionRate,
                totalROI = totalROI
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSegmentUsers(segmentId: String): Result<List<String>> {
        return try {
            val users = segmentUsers[segmentId] ?: emptyList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addUserToSegment(userId: String, segmentId: String): Result<Unit> {
        return try {
            val users = segmentUsers.getOrPut(segmentId) { mutableListOf() }
            if (!users.contains(userId)) {
                users.add(userId)
                
                // Update segment user count
                val segmentIndex = segments.indexOfFirst { it.id == segmentId }
                if (segmentIndex != -1) {
                    val segment = segments[segmentIndex]
                    segments[segmentIndex] = segment.copy(
                        userCount = users.size,
                        updatedDate = LocalDateTime.now()
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeUserFromSegment(userId: String, segmentId: String): Result<Unit> {
        return try {
            val users = segmentUsers[segmentId]
            if (users != null && users.remove(userId)) {
                // Update segment user count
                val segmentIndex = segments.indexOfFirst { it.id == segmentId }
                if (segmentIndex != -1) {
                    val segment = segments[segmentIndex]
                    segments[segmentIndex] = segment.copy(
                        userCount = users.size,
                        updatedDate = LocalDateTime.now()
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPersonalizedContent(userId: String): Result<List<MarketingMessage>> {
        return try {
            // Find segments that contain this user
            val userSegments = segmentUsers.filter { it.value.contains(userId) }.keys
            
            // Get messages for those segments
            val personalizedMessages = messages.filter { message ->
                userSegments.contains(message.targetSegment) &&
                (message.status == MessageStatus.SENT || message.status == MessageStatus.DELIVERED)
            }.take(10) // Limit to 10 messages
            
            Result.success(personalizedMessages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun trackMessageInteraction(messageId: String, userId: String, interactionType: String): Result<Unit> {
        return try {
            val interactions = messageInteractions.getOrPut(messageId) { mutableListOf() }
            interactions.add("$userId:$interactionType:${System.currentTimeMillis()}")
            
            // Update message status based on interaction
            val messageIndex = messages.indexOfFirst { it.id == messageId }
            if (messageIndex != -1) {
                val message = messages[messageIndex]
                val newStatus = when (interactionType.lowercase()) {
                    "delivered" -> MessageStatus.DELIVERED
                    "opened" -> MessageStatus.OPENED
                    "clicked" -> MessageStatus.CLICKED
                    else -> message.status
                }
                
                if (newStatus != message.status) {
                    messages[messageIndex] = message.copy(status = newStatus)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun initializeMockData() {
        // Mock campaigns
        val mockCampaigns = listOf(
            MarketingCampaign(
                id = UUID.randomUUID().toString(),
                name = "Welcome Campaign",
                description = "Onboarding campaign for new users",
                type = CampaignType.ACQUISITION,
                status = CampaignStatus.ACTIVE,
                targetAudience = listOf("new_users"),
                channels = listOf(MarketingChannel.EMAIL, MarketingChannel.PUSH_NOTIFICATION),
                budget = BigDecimal("10000.00"),
                spentAmount = BigDecimal("3500.00"),
                startDate = LocalDateTime.now().minusDays(30),
                endDate = LocalDateTime.now().plusDays(30),
                createdBy = "marketing_team",
                createdDate = LocalDateTime.now().minusDays(35),
                updatedDate = LocalDateTime.now().minusDays(30),
                goals = listOf(
                    // CampaignGoal(MarketingGoalType.CONVERSIONS, BigDecimal("1000"), BigDecimal("350"), "users"),
                    // CampaignGoal(MarketingGoalType.REVENUE, BigDecimal("50000"), BigDecimal("17500"), "GBP")
                ),
                metrics = CampaignMetrics(
                    impressions = 25000,
                    clicks = 1250,
                    conversions = 350,
                    revenue = BigDecimal("17500.00"),
                    cost = BigDecimal("3500.00"),
                    clickThroughRate = 5.0,
                    conversionRate = 28.0,
                    returnOnInvestment = 400.0,
                    lastUpdated = LocalDateTime.now()
                )
            ),
            MarketingCampaign(
                id = UUID.randomUUID().toString(),
                name = "Holiday Promotion",
                description = "Special offers for holiday season",
                type = CampaignType.PROMOTIONAL,
                status = CampaignStatus.SCHEDULED,
                targetAudience = listOf("active_users", "premium_users"),
                channels = listOf(MarketingChannel.EMAIL, MarketingChannel.SMS, MarketingChannel.IN_APP_MESSAGE),
                budget = BigDecimal("25000.00"),
                spentAmount = BigDecimal.ZERO,
                startDate = LocalDateTime.now().plusDays(7),
                endDate = LocalDateTime.now().plusDays(37),
                createdBy = "marketing_team",
                createdDate = LocalDateTime.now().minusDays(10),
                updatedDate = LocalDateTime.now().minusDays(5),
                goals = listOf(
                    // CampaignGoal(MarketingGoalType.REVENUE, BigDecimal("100000"), BigDecimal.ZERO, "GBP"),
                    // CampaignGoal(MarketingGoalType.ENGAGEMENT_RATE, BigDecimal("15"), BigDecimal.ZERO, "percent")
                ),
                metrics = null
            )
        )
        
        campaigns.addAll(mockCampaigns)
        
        // Mock segments
        val mockSegments = listOf(
            MarketingSegment(
                id = UUID.randomUUID().toString(),
                name = "New Users",
                description = "Users who joined in the last 30 days",
                criteria = listOf(
                    SegmentCriteria("registration_date", ">", "30_days_ago"),
                    SegmentCriteria("account_status", "=", "active")
                ),
                userCount = 1250,
                isActive = true,
                createdDate = LocalDateTime.now().minusDays(60),
                updatedDate = LocalDateTime.now().minusDays(1)
            ),
            MarketingSegment(
                id = UUID.randomUUID().toString(),
                name = "Premium Users",
                description = "Users with premium account subscription",
                criteria = listOf(
                    SegmentCriteria("account_type", "=", "premium"),
                    SegmentCriteria("subscription_status", "=", "active")
                ),
                userCount = 850,
                isActive = true,
                createdDate = LocalDateTime.now().minusDays(90),
                updatedDate = LocalDateTime.now().minusDays(7)
            )
        )
        
        segments.addAll(mockSegments)
        
        // Initialize segment users
        mockSegments.forEach { segment ->
            segmentUsers[segment.id] = mutableListOf()
            // Add some mock users
            repeat(segment.userCount.coerceAtMost(10)) {
                segmentUsers[segment.id]?.add("user_${UUID.randomUUID().toString().take(8)}")
            }
        }
        
        // Mock messages
        val mockMessages = listOf(
            MarketingMessage(
                id = UUID.randomUUID().toString(),
                campaignId = mockCampaigns[0].id,
                title = "Welcome to MonzoBank!",
                content = "Thank you for joining MonzoBank. Discover all the amazing features we have for you.",
                channel = MarketingChannel.EMAIL,
                targetSegment = mockSegments[0].id,
                scheduledDate = null,
                sentDate = LocalDateTime.now().minusDays(25),
                status = MessageStatus.SENT
            ),
            MarketingMessage(
                id = UUID.randomUUID().toString(),
                campaignId = mockCampaigns[1].id,
                title = "Holiday Special Offers!",
                content = "Don't miss our exclusive holiday deals. Limited time only!",
                channel = MarketingChannel.PUSH_NOTIFICATION,
                targetSegment = mockSegments[1].id,
                scheduledDate = LocalDateTime.now().plusDays(7),
                sentDate = null,
                status = MessageStatus.SCHEDULED
            )
        )
        
        messages.addAll(mockMessages)
    }
}