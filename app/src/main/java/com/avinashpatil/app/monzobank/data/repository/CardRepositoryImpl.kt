package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.data.local.dao.CardDao
import com.avinashpatil.app.monzobank.data.local.entity.CardEntity
import com.avinashpatil.app.monzobank.data.local.entity.CardType as EntityCardType
import com.avinashpatil.app.monzobank.data.local.entity.DeliveryStatus as EntityDeliveryStatus
import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.CardUsage
import com.avinashpatil.app.monzobank.domain.model.DeliveryStatus
import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import com.avinashpatil.app.monzobank.domain.repository.DeliveryTrackingInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.security.MessageDigest
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {
    
    override fun getCardsByUserId(userId: String): Flow<List<Card>> {
        return cardDao.getCardsByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun observeCardsByUserId(userId: String): Flow<List<Card>> {
        return cardDao.getCardsByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getCardById(cardId: String): Card? {
        return cardDao.getCardById(cardId)?.toDomainModel()
    }
    
    override suspend fun getCardByNumber(cardNumber: String): Card? {
        return cardDao.getCardByNumber(cardNumber)?.toDomainModel()
    }
    
    override suspend fun getCardsByAccountId(accountId: String): List<Card> {
        return cardDao.getCardsByAccountId(accountId).map { it.toDomainModel() }
    }
    
    override suspend fun getCardsByType(userId: String, cardType: CardType): List<Card> {
        return cardDao.getCardsByType(userId, cardType.toEntityType()).map { it.toDomainModel() }
    }
    
    override suspend fun getActiveCards(userId: String): List<Card> {
        return cardDao.getActiveCards(userId).map { it.toDomainModel() }
    }
    
    override suspend fun getFrozenCards(userId: String): List<Card> {
        return cardDao.getFrozenCards(userId).map { it.toDomainModel() }
    }
    
    override suspend fun getCardsByDeliveryStatus(status: DeliveryStatus): List<Card> {
        return cardDao.getCardsByDeliveryStatus(status.toEntityType()).map { it.toDomainModel() }
    }
    
    override suspend fun getPrimaryDebitCard(userId: String): Card? {
        return cardDao.getPrimaryDebitCard(userId)?.toDomainModel()
    }
    
    override suspend fun createCard(card: Card): Result<Card> {
        return try {
            val entity = card.toEntity()
            cardDao.insertCard(entity)
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createCard(userId: String, cardType: CardType): Result<Card> {
        return try {
            val cardNumber = when (cardType) {
                CardType.VIRTUAL -> generateVirtualCardNumber()
                else -> generateCardNumber()
            }
            val expiryDate = generateExpiryDate()
            val cvv = generateCVV()
            
            val card = Card(
                id = UUID.randomUUID().toString(),
                userId = userId,
                accountId = "", // Will be set by the caller or use default account
                cardNumber = cardNumber,
                cardType = cardType,
                expiryDate = expiryDate,
                cvv = cvv,
                isActive = true,
                isFrozen = false,
                contactlessEnabled = cardType != CardType.VIRTUAL,
                dailyLimit = when (cardType) {
                    CardType.VIRTUAL -> BigDecimal("1000.00")
                    else -> BigDecimal("500.00")
                },
                monthlyLimit = when (cardType) {
                    CardType.VIRTUAL -> BigDecimal("5000.00")
                    else -> BigDecimal("2000.00")
                },
                internationalEnabled = cardType == CardType.VIRTUAL,
                cardHolderName = "", // Will be set by the caller
                deliveryStatus = if (cardType == CardType.VIRTUAL) DeliveryStatus.DELIVERED else DeliveryStatus.PENDING
            )
            
            val entity = card.toEntity().copy(cvvHash = hashCVV(cvv))
            cardDao.insertCard(entity)
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createVirtualCard(
        userId: String,
        accountId: String,
        cardHolderName: String,
        dailyLimit: BigDecimal?
    ): Result<Card> {
        return try {
            val cardNumber = generateVirtualCardNumber()
            val expiryDate = generateExpiryDate()
            val cvv = generateCVV()
            
            val card = Card(
                id = UUID.randomUUID().toString(),
                userId = userId,
                accountId = accountId,
                cardNumber = cardNumber,
                cardType = CardType.VIRTUAL,
                expiryDate = expiryDate,
                cvv = cvv,
                isActive = true,
                isFrozen = false,
                contactlessEnabled = false, // Virtual cards don't have contactless
                dailyLimit = dailyLimit ?: BigDecimal("1000.00"),
                monthlyLimit = BigDecimal("5000.00"),
                internationalEnabled = true,
                cardHolderName = cardHolderName,
                deliveryStatus = DeliveryStatus.DELIVERED // Virtual cards are instantly available
            )
            
            val entity = card.toEntity().copy(cvvHash = hashCVV(cvv))
            cardDao.insertCard(entity)
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun orderPhysicalCard(
        userId: String,
        accountId: String,
        cardHolderName: String,
        deliveryAddress: String,
        cardDesign: String?
    ): Result<Card> {
        return try {
            val cardNumber = generateCardNumber()
            val expiryDate = generateExpiryDate()
            val cvv = generateCVV()
            
            val card = Card(
                id = UUID.randomUUID().toString(),
                userId = userId,
                accountId = accountId,
                cardNumber = cardNumber,
                cardType = CardType.DEBIT,
                expiryDate = expiryDate,
                cvv = cvv,
                isActive = true,
                isFrozen = false,
                contactlessEnabled = true,
                dailyLimit = BigDecimal("500.00"),
                monthlyLimit = BigDecimal("2000.00"),
                internationalEnabled = false,
                cardHolderName = cardHolderName,
                deliveryStatus = DeliveryStatus.PENDING,
                cardDesign = cardDesign
            )
            
            val entity = card.toEntity().copy(cvvHash = hashCVV(cvv))
            cardDao.insertCard(entity)
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCard(card: Card): Result<Card> {
        return try {
            val entity = card.toEntity()
            cardDao.updateCard(entity)
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun freezeCard(cardId: String): Result<Unit> {
        return try {
            cardDao.updateCardFreezeStatus(cardId, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unfreezeCard(cardId: String): Result<Unit> {
        return try {
            cardDao.updateCardFreezeStatus(cardId, false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateContactlessStatus(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardDao.updateContactlessStatus(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateInternationalStatus(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardDao.updateInternationalStatus(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDailyLimit(cardId: String, limit: BigDecimal): Result<Unit> {
        return try {
            cardDao.updateDailyLimit(cardId, limit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMonthlyLimit(cardId: String, limit: BigDecimal): Result<Unit> {
        return try {
            cardDao.updateMonthlyLimit(cardId, limit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changeCardPin(cardId: String, newPin: String): Result<Unit> {
        return try {
            val hashedPin = hashPin(newPin)
            cardDao.updateCardPin(cardId, hashedPin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateLastUsedAt(cardId: String): Result<Unit> {
        return try {
            cardDao.updateLastUsedAt(cardId, Date())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDeliveryStatus(cardId: String, status: DeliveryStatus): Result<Unit> {
        return try {
            cardDao.updateDeliveryStatus(cardId, status.toEntityType())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deactivateCard(cardId: String): Result<Unit> {
        return try {
            cardDao.deactivateCard(cardId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun activateCard(cardId: String, activationCode: String): Result<Unit> {
        return try {
            // Validate activation code (in a real implementation, this would be validated against a secure backend)
            if (activationCode.length < 6) {
                return Result.failure(Exception("Invalid activation code"))
            }
            
            val card = cardDao.getCardById(cardId)
            if (card != null) {
                // Update card to active status
                cardDao.updateCardActiveStatus(cardId, true)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Card not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun replaceCard(cardId: String, reason: String): Result<Card> {
        return try {
            val existingCard = cardDao.getCardById(cardId)
            if (existingCard != null) {
                // Deactivate old card
                cardDao.deactivateCard(cardId)
                
                // Create new card with same details but new number
                val newCardNumber = if (existingCard.cardType == EntityCardType.VIRTUAL) {
                    generateVirtualCardNumber()
                } else {
                    generateCardNumber()
                }
                
                val newCard = existingCard.copy(
                    id = UUID.randomUUID().toString(),
                    cardNumber = newCardNumber,
                    expiryDate = generateExpiryDate(),
                    cvvHash = hashCVV(generateCVV()),
                    deliveryStatus = if (existingCard.cardType == EntityCardType.VIRTUAL) 
                        EntityDeliveryStatus.DELIVERED else EntityDeliveryStatus.PENDING,
                    createdAt = Date()
                )
                
                cardDao.insertCard(newCard)
                Result.success(newCard.toDomainModel())
            } else {
                Result.failure(Exception("Card not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCard(cardId: String): Result<Unit> {
        return try {
            val card = cardDao.getCardById(cardId)
            if (card != null) {
                cardDao.deleteCard(card)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveCardCount(userId: String): Int {
        return cardDao.getActiveCardCount(userId)
    }
    
    override suspend fun getVirtualCardCount(userId: String): Int {
        return cardDao.getVirtualCardCount(userId)
    }
    
    override suspend fun syncCardsFromRemote(userId: String): Result<List<Card>> {
        // TODO: Implement remote sync when backend is ready
        return try {
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun trackCardDelivery(cardId: String): Result<DeliveryTrackingInfo> {
        return try {
            val card = cardDao.getCardById(cardId)
            if (card != null) {
                val trackingInfo = DeliveryTrackingInfo(
                    cardId = cardId,
                    status = card.deliveryStatus.toDomainType(),
                    trackingNumber = card.trackingNumber ?: "TRK${cardId.takeLast(8).uppercase()}",
                    estimatedDelivery = card.estimatedDelivery ?: if (card.deliveryStatus == EntityDeliveryStatus.DISPATCHED) {
                        Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 3) }.time
                    } else null,
                    courierName = "Royal Mail",
                    trackingUrl = "https://www.royalmail.com/track-your-item"
                )
                Result.success(trackingInfo)
            } else {
                Result.failure(Exception("Card not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getCardsByDeliveryStatus(userId: String, statuses: List<DeliveryStatus>): Flow<List<Card>> {
        val entityStatuses = statuses.map { it.toEntityType() }
        return cardDao.getCardsByDeliveryStatus(userId, entityStatuses).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun updateCardDeliveryStatus(
        cardId: String,
        status: DeliveryStatus,
        trackingNumber: String?,
        estimatedDelivery: Date?
    ): Result<Unit> {
        return try {
            cardDao.updateCardDeliveryStatus(cardId, status.toEntityType(), trackingNumber, estimatedDelivery)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardDeliveryAddress(cardId: String, address: String): Result<Unit> {
        return try {
            cardDao.updateCardDeliveryAddress(cardId, address)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardPin(cardId: String, pinHash: String): Result<Unit> {
        return try {
            cardDao.updateCardPin(cardId, pinHash)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardLockStatus(cardId: String, isLocked: Boolean, reason: String?): Result<Unit> {
        return try {
            cardDao.updateCardLockStatus(cardId, isLocked, reason)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardFreezeStatus(cardId: String, isFrozen: Boolean): Result<Unit> {
        return try {
            cardDao.updateCardFreezeStatus(cardId, isFrozen)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardOnlineEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardDao.updateCardOnlineEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardAtmEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardDao.updateCardAtmEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardContactlessEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardDao.updateCardContactlessEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardInternationalEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardDao.updateCardInternationalEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardDailyLimit(cardId: String, limit: BigDecimal): Result<Unit> {
        return try {
            cardDao.updateCardDailyLimit(cardId, limit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCardMonthlyLimit(cardId: String, limit: BigDecimal): Result<Unit> {
        return try {
            cardDao.updateCardMonthlyLimit(cardId, limit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCardUsage(cardId: String): Result<CardUsage> {
        return try {
            val card = cardDao.getCardById(cardId)
            if (card != null) {
                // For now, return mock usage data since we don't have transaction integration yet
                // In a real implementation, this would query transactions for the card
                val usage = CardUsage(
                    cardId = cardId,
                    dailySpent = BigDecimal("45.67"),
                    monthlySpent = BigDecimal("1234.56"),
                    transactionCount = 23,
                    lastTransactionDate = Date()
                )
                Result.success(usage)
            } else {
                Result.failure(Exception("Card not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper functions
    private fun generateCardNumber(): String {
        // Generate a valid Luhn algorithm card number starting with 4532 (Visa)
        val prefix = "4532"
        val middle = (1..8).map { (0..9).random() }.joinToString("")
        val partial = prefix + middle
        val checkDigit = calculateLuhnCheckDigit(partial)
        return partial + checkDigit
    }
    
    private fun generateVirtualCardNumber(): String {
        // Generate virtual card number starting with 4000
        val prefix = "4000"
        val middle = (1..8).map { (0..9).random() }.joinToString("")
        val partial = prefix + middle
        val checkDigit = calculateLuhnCheckDigit(partial)
        return partial + checkDigit
    }
    
    private fun generateExpiryDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, 4) // Card expires in 4 years
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calendar.time
    }
    
    private fun generateCVV(): String {
        return (100..999).random().toString()
    }
    
    private fun calculateLuhnCheckDigit(cardNumber: String): String {
        var sum = 0
        var alternate = false
        
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        
        return ((10 - (sum % 10)) % 10).toString()
    }
    
    private fun hashCVV(cvv: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(cvv.toByteArray()).joinToString("") { "%02x".format(it) }
    }
    
    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
    }
    
    // Type conversion functions
    private fun CardType.toEntityType(): EntityCardType {
        return when (this) {
            CardType.DEBIT -> EntityCardType.DEBIT
            CardType.CREDIT -> EntityCardType.CREDIT
            CardType.VIRTUAL -> EntityCardType.VIRTUAL
            CardType.PREPAID -> EntityCardType.PREPAID
            CardType.BUSINESS_DEBIT -> EntityCardType.BUSINESS_DEBIT
            CardType.BUSINESS_CREDIT -> EntityCardType.BUSINESS_CREDIT
            CardType.PREMIUM -> EntityCardType.PREMIUM
        }
    }
    
    private fun EntityCardType.toDomainType(): CardType {
        return when (this) {
            EntityCardType.DEBIT -> CardType.DEBIT
            EntityCardType.CREDIT -> CardType.CREDIT
            EntityCardType.VIRTUAL -> CardType.VIRTUAL
            EntityCardType.PREPAID -> CardType.PREPAID
            EntityCardType.BUSINESS_DEBIT -> CardType.BUSINESS_DEBIT
            EntityCardType.BUSINESS_CREDIT -> CardType.BUSINESS_CREDIT
            EntityCardType.PREMIUM -> CardType.PREMIUM
        }
    }
    
    private fun DeliveryStatus.toEntityType(): EntityDeliveryStatus {
        return when (this) {
            DeliveryStatus.PENDING -> EntityDeliveryStatus.PENDING
            DeliveryStatus.ORDERED -> EntityDeliveryStatus.ORDERED
            DeliveryStatus.PROCESSING -> EntityDeliveryStatus.PROCESSING
            DeliveryStatus.DISPATCHED -> EntityDeliveryStatus.DISPATCHED
            DeliveryStatus.SHIPPED -> EntityDeliveryStatus.SHIPPED
            DeliveryStatus.OUT_FOR_DELIVERY -> EntityDeliveryStatus.OUT_FOR_DELIVERY
            DeliveryStatus.DELIVERED -> EntityDeliveryStatus.DELIVERED
            DeliveryStatus.FAILED_DELIVERY -> EntityDeliveryStatus.FAILED_DELIVERY
            DeliveryStatus.DELIVERY_FAILED -> EntityDeliveryStatus.FAILED_DELIVERY // Alias
            DeliveryStatus.RETURNED -> EntityDeliveryStatus.RETURNED
            DeliveryStatus.CANCELLED -> EntityDeliveryStatus.CANCELLED
        }
    }
    
    private fun EntityDeliveryStatus.toDomainType(): DeliveryStatus {
        return when (this) {
            EntityDeliveryStatus.PENDING -> DeliveryStatus.PENDING
            EntityDeliveryStatus.ORDERED -> DeliveryStatus.ORDERED
            EntityDeliveryStatus.PROCESSING -> DeliveryStatus.PROCESSING
            EntityDeliveryStatus.DISPATCHED -> DeliveryStatus.DISPATCHED
            EntityDeliveryStatus.SHIPPED -> DeliveryStatus.SHIPPED
            EntityDeliveryStatus.OUT_FOR_DELIVERY -> DeliveryStatus.OUT_FOR_DELIVERY
            EntityDeliveryStatus.DELIVERED -> DeliveryStatus.DELIVERED
            EntityDeliveryStatus.FAILED_DELIVERY -> DeliveryStatus.FAILED_DELIVERY
            EntityDeliveryStatus.RETURNED -> DeliveryStatus.RETURNED
            EntityDeliveryStatus.CANCELLED -> DeliveryStatus.CANCELLED
        }
    }

    // Extension functions for mapping
    private fun CardEntity.toDomainModel(): Card {
        return Card(
            id = id,
            userId = userId,
            accountId = accountId,
            cardNumber = cardNumber,
            cardType = cardType.toDomainType(),
            expiryDate = expiryDate,
            cvv = "", // CVV is not exposed in domain model for security
            isActive = isActive,
            isFrozen = isFrozen,
            contactlessEnabled = contactlessEnabled,
            dailyLimit = dailyLimit,
            monthlyLimit = monthlyLimit,
            internationalEnabled = internationalEnabled,
            onlineEnabled = onlineEnabled,
            atmEnabled = atmEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt,
            cardHolderName = cardHolderName,
            cardName = cardName,
            lastUsedAt = lastUsedAt,
            deliveryStatus = deliveryStatus.toDomainType(),
            cardDesign = cardDesign,
            pinHash = pinHash,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            deliveryAddress = deliveryAddress,
            isLocked = isLocked,
            lockReason = lockReason
        )
    }
    
    private fun Card.toEntity(): CardEntity {
        return CardEntity(
            id = id.ifEmpty { UUID.randomUUID().toString() },
            userId = userId,
            accountId = accountId,
            cardNumber = cardNumber,
            cardType = cardType.toEntityType(),
            expiryDate = expiryDate,
            cvvHash = if (cvv.isNotEmpty()) hashCVV(cvv) else "",
            isActive = isActive,
            isFrozen = isFrozen,
            contactlessEnabled = contactlessEnabled,
            dailyLimit = dailyLimit,
            monthlyLimit = monthlyLimit,
            internationalEnabled = internationalEnabled,
            onlineEnabled = onlineEnabled,
            atmEnabled = atmEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt,
            cardHolderName = cardHolderName,
            cardName = cardName,
            pinHash = pinHash,
            lastUsedAt = lastUsedAt,
            deliveryStatus = deliveryStatus.toEntityType(),
            cardDesign = cardDesign,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            deliveryAddress = deliveryAddress,
            isLocked = isLocked,
            lockReason = lockReason
        )
    }
}