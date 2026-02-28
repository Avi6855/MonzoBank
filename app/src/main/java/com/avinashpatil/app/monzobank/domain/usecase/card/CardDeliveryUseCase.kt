package com.avinashpatil.app.monzobank.domain.usecase.card

import com.avinashpatil.app.monzobank.domain.model.DeliveryStatus
import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class CardDeliveryUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    suspend fun updateDeliveryStatus(
        cardId: String,
        status: DeliveryStatus,
        trackingNumber: String? = null,
        estimatedDelivery: Date? = null
    ): Result<Unit> {
        return try {
            cardRepository.updateCardDeliveryStatus(
                cardId = cardId,
                status = status,
                trackingNumber = trackingNumber,
                estimatedDelivery = estimatedDelivery
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDeliveryStatus(cardId: String): Result<DeliveryStatus> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            Result.success(card.deliveryStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTrackingInfo(cardId: String): Result<TrackingInfo> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            val trackingInfo = TrackingInfo(
                cardId = card.id,
                trackingNumber = card.trackingNumber,
                status = card.deliveryStatus,
                estimatedDelivery = card.estimatedDelivery,
                deliveryAddress = card.deliveryAddress,
                trackingHistory = getTrackingHistory(card.id)
            )
            
            Result.success(trackingInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCardsAwaitingDelivery(userId: String): Flow<List<Card>> {
        return cardRepository.getCardsByDeliveryStatus(
            userId = userId,
            statuses = listOf(
                DeliveryStatus.PENDING,
                DeliveryStatus.DISPATCHED
            )
        )
    }
    
    suspend fun reportDeliveryIssue(
        cardId: String,
        issueType: DeliveryIssueType,
        description: String
    ): Result<Unit> {
        return try {
            // In real app, this would create a support ticket
            cardRepository.updateCardDeliveryStatus(
                cardId = cardId,
                status = DeliveryStatus.DELIVERY_FAILED,
                trackingNumber = null,
                estimatedDelivery = null
            )
            
            // Log the issue for customer support
            logDeliveryIssue(cardId, issueType, description)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun requestRedelivery(cardId: String, newAddress: String?): Result<Unit> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            if (card.deliveryStatus != DeliveryStatus.DELIVERY_FAILED) {
                return Result.failure(Exception("Card is not eligible for redelivery"))
            }
            
            // Update delivery address if provided
            if (newAddress != null) {
                cardRepository.updateCardDeliveryAddress(cardId, newAddress)
            }
            
            // Reset delivery status
            cardRepository.updateCardDeliveryStatus(
                cardId = cardId,
                status = DeliveryStatus.PENDING,
                trackingNumber = generateNewTrackingNumber(),
                estimatedDelivery = calculateEstimatedDelivery()
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun getTrackingHistory(cardId: String): List<TrackingEvent> {
        // In real app, this would fetch from delivery service API
        return emptyList()
    }
    
    private fun logDeliveryIssue(
        cardId: String,
        issueType: DeliveryIssueType,
        description: String
    ) {
        // Log to analytics/support system
    }
    
    private fun generateNewTrackingNumber(): String {
        return "MZ" + (100000000L..999999999L).random().toString()
    }
    
    private fun calculateEstimatedDelivery(): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 3) // 3 business days
        return calendar.time
    }
}

data class TrackingInfo(
    val cardId: String,
    val trackingNumber: String?,
    val status: DeliveryStatus,
    val estimatedDelivery: Date?,
    val deliveryAddress: String?,
    val trackingHistory: List<TrackingEvent>
)

data class TrackingEvent(
    val timestamp: Date,
    val status: String,
    val location: String?,
    val description: String
)

enum class DeliveryIssueType {
    NOT_DELIVERED,
    DAMAGED_PACKAGE,
    WRONG_ADDRESS,
    LOST_IN_TRANSIT,
    OTHER
}