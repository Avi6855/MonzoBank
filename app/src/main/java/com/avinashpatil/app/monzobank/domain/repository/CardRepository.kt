package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.DeliveryStatus
import com.avinashpatil.app.monzobank.domain.model.CardUsage
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface CardRepository {
    
    fun getCardsByUserId(userId: String): Flow<List<Card>>
    
    fun observeCardsByUserId(userId: String): Flow<List<Card>>
    
    suspend fun getCardById(cardId: String): Card?
    
    suspend fun getCardByNumber(cardNumber: String): Card?
    
    suspend fun getCardsByAccountId(accountId: String): List<Card>
    
    suspend fun getCardsByType(userId: String, cardType: CardType): List<Card>
    
    suspend fun getActiveCards(userId: String): List<Card>
    
    suspend fun getFrozenCards(userId: String): List<Card>
    
    suspend fun getCardsByDeliveryStatus(status: DeliveryStatus): List<Card>
    
    fun getCardsByDeliveryStatus(userId: String, statuses: List<DeliveryStatus>): Flow<List<Card>>
    
    suspend fun getPrimaryDebitCard(userId: String): Card?
    
    suspend fun createCard(card: Card): Result<Card>
    
    suspend fun createCard(userId: String, cardType: CardType): Result<Card>
    
    suspend fun createVirtualCard(
        userId: String,
        accountId: String,
        cardHolderName: String,
        dailyLimit: BigDecimal? = null
    ): Result<Card>
    
    suspend fun orderPhysicalCard(
        userId: String,
        accountId: String,
        cardHolderName: String,
        deliveryAddress: String,
        cardDesign: String? = null
    ): Result<Card>
    
    suspend fun updateCard(card: Card): Result<Card>
    
    suspend fun freezeCard(cardId: String): Result<Unit>
    
    suspend fun unfreezeCard(cardId: String): Result<Unit>
    
    suspend fun updateContactlessStatus(cardId: String, enabled: Boolean): Result<Unit>
    
    suspend fun updateInternationalStatus(cardId: String, enabled: Boolean): Result<Unit>
    
    suspend fun updateDailyLimit(cardId: String, limit: BigDecimal): Result<Unit>
    
    suspend fun updateMonthlyLimit(cardId: String, limit: BigDecimal): Result<Unit>
    
    suspend fun changeCardPin(cardId: String, newPin: String): Result<Unit>
    
    suspend fun updateLastUsedAt(cardId: String): Result<Unit>
    
    suspend fun updateDeliveryStatus(cardId: String, status: DeliveryStatus): Result<Unit>
    
    suspend fun deactivateCard(cardId: String): Result<Unit>
    
    suspend fun replaceCard(cardId: String, reason: String): Result<Card>
    
    suspend fun deleteCard(cardId: String): Result<Unit>
    
    suspend fun getActiveCardCount(userId: String): Int
    
    suspend fun getVirtualCardCount(userId: String): Int
    
    suspend fun syncCardsFromRemote(userId: String): Result<List<Card>>
    
    suspend fun trackCardDelivery(cardId: String): Result<DeliveryTrackingInfo>
    
    suspend fun updateCardDeliveryStatus(
        cardId: String,
        status: DeliveryStatus,
        trackingNumber: String? = null,
        estimatedDelivery: java.util.Date? = null
    ): Result<Unit>
    
    suspend fun updateCardDeliveryAddress(cardId: String, address: String): Result<Unit>
    
    suspend fun updateCardPin(cardId: String, pinHash: String): Result<Unit>
    
    suspend fun updateCardLockStatus(cardId: String, isLocked: Boolean, reason: String?): Result<Unit>
    
    suspend fun updateCardFreezeStatus(cardId: String, isFrozen: Boolean): Result<Unit>
    
    suspend fun updateCardOnlineEnabled(cardId: String, enabled: Boolean): Result<Unit>
    
    suspend fun updateCardAtmEnabled(cardId: String, enabled: Boolean): Result<Unit>
    
    suspend fun updateCardContactlessEnabled(cardId: String, enabled: Boolean): Result<Unit>
    
    suspend fun updateCardInternationalEnabled(cardId: String, enabled: Boolean): Result<Unit>
    
    suspend fun updateCardDailyLimit(cardId: String, limit: BigDecimal): Result<Unit>
    
    suspend fun updateCardMonthlyLimit(cardId: String, limit: BigDecimal): Result<Unit>
    
    suspend fun activateCard(cardId: String, activationCode: String): Result<Unit>
    
    suspend fun getCardUsage(cardId: String): Result<CardUsage>
}

data class DeliveryTrackingInfo(
    val cardId: String,
    val status: DeliveryStatus,
    val trackingNumber: String?,
    val estimatedDelivery: java.util.Date?,
    val courierName: String?,
    val trackingUrl: String?
)