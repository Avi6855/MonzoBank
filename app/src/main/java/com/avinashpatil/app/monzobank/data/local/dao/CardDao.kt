package com.avinashpatil.app.monzobank.data.local.dao

import androidx.room.*
import com.avinashpatil.app.monzobank.data.local.entity.CardEntity
import com.avinashpatil.app.monzobank.data.local.entity.CardType
import com.avinashpatil.app.monzobank.data.local.entity.DeliveryStatus
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

@Dao
interface CardDao {
    
    @Query("SELECT * FROM cards WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getCardsByUserId(userId: String): Flow<List<CardEntity>>
    
    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: String): CardEntity?
    
    @Query("SELECT * FROM cards WHERE cardNumber = :cardNumber")
    suspend fun getCardByNumber(cardNumber: String): CardEntity?
    
    @Query("SELECT * FROM cards WHERE accountId = :accountId AND isActive = 1")
    suspend fun getCardsByAccountId(accountId: String): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE userId = :userId AND cardType = :cardType AND isActive = 1")
    suspend fun getCardsByType(userId: String, cardType: CardType): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE userId = :userId AND isFrozen = 0 AND isActive = 1")
    suspend fun getActiveCards(userId: String): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE userId = :userId AND isFrozen = 1 AND isActive = 1")
    suspend fun getFrozenCards(userId: String): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE deliveryStatus = :status ORDER BY createdAt DESC")
    suspend fun getCardsByDeliveryStatus(status: DeliveryStatus): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE userId = :userId AND deliveryStatus IN (:statuses) ORDER BY createdAt DESC")
    fun getCardsByDeliveryStatus(userId: String, statuses: List<DeliveryStatus>): Flow<List<CardEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)
    
    @Update
    suspend fun updateCard(card: CardEntity)
    
    @Query("UPDATE cards SET isFrozen = :isFrozen WHERE id = :cardId")
    suspend fun updateCardFreezeStatus(cardId: String, isFrozen: Boolean)
    
    @Query("UPDATE cards SET contactlessEnabled = :enabled WHERE id = :cardId")
    suspend fun updateContactlessStatus(cardId: String, enabled: Boolean)
    
    @Query("UPDATE cards SET internationalEnabled = :enabled WHERE id = :cardId")
    suspend fun updateInternationalStatus(cardId: String, enabled: Boolean)
    
    @Query("UPDATE cards SET dailyLimit = :limit WHERE id = :cardId")
    suspend fun updateDailyLimit(cardId: String, limit: BigDecimal)
    
    @Query("UPDATE cards SET monthlyLimit = :limit WHERE id = :cardId")
    suspend fun updateMonthlyLimit(cardId: String, limit: BigDecimal)
    
    @Query("UPDATE cards SET pinHash = :pinHash WHERE id = :cardId")
    suspend fun updateCardPin(cardId: String, pinHash: String)
    
    @Query("UPDATE cards SET lastUsedAt = :lastUsedAt WHERE id = :cardId")
    suspend fun updateLastUsedAt(cardId: String, lastUsedAt: Date)
    
    @Query("UPDATE cards SET deliveryStatus = :status WHERE id = :cardId")
    suspend fun updateDeliveryStatus(cardId: String, status: DeliveryStatus)
    
    @Query("UPDATE cards SET deliveryStatus = :status, trackingNumber = :trackingNumber, estimatedDelivery = :estimatedDelivery WHERE id = :cardId")
    suspend fun updateCardDeliveryStatus(cardId: String, status: DeliveryStatus, trackingNumber: String?, estimatedDelivery: Date?)
    
    @Query("UPDATE cards SET deliveryAddress = :address WHERE id = :cardId")
    suspend fun updateCardDeliveryAddress(cardId: String, address: String)
    
    @Query("UPDATE cards SET isLocked = :isLocked, lockReason = :reason WHERE id = :cardId")
    suspend fun updateCardLockStatus(cardId: String, isLocked: Boolean, reason: String?)
    
    @Query("UPDATE cards SET onlineEnabled = :enabled WHERE id = :cardId")
    suspend fun updateCardOnlineEnabled(cardId: String, enabled: Boolean)
    
    @Query("UPDATE cards SET atmEnabled = :enabled WHERE id = :cardId")
    suspend fun updateCardAtmEnabled(cardId: String, enabled: Boolean)
    
    @Query("UPDATE cards SET contactlessEnabled = :enabled WHERE id = :cardId")
    suspend fun updateCardContactlessEnabled(cardId: String, enabled: Boolean)
    
    @Query("UPDATE cards SET internationalEnabled = :enabled WHERE id = :cardId")
    suspend fun updateCardInternationalEnabled(cardId: String, enabled: Boolean)
    
    @Query("UPDATE cards SET dailyLimit = :limit WHERE id = :cardId")
    suspend fun updateCardDailyLimit(cardId: String, limit: BigDecimal)
    
    @Query("UPDATE cards SET monthlyLimit = :limit WHERE id = :cardId")
    suspend fun updateCardMonthlyLimit(cardId: String, limit: BigDecimal)
    
    @Query("UPDATE cards SET isActive = 0 WHERE id = :cardId")
    suspend fun deactivateCard(cardId: String)
    
    @Query("UPDATE cards SET isActive = :isActive WHERE id = :cardId")
    suspend fun updateCardActiveStatus(cardId: String, isActive: Boolean)
    
    @Delete
    suspend fun deleteCard(card: CardEntity)
    
    @Query("DELETE FROM cards WHERE userId = :userId")
    suspend fun deleteCardsByUserId(userId: String)
    
    @Query("SELECT COUNT(*) FROM cards WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveCardCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM cards WHERE userId = :userId AND cardType = 'VIRTUAL' AND isActive = 1")
    suspend fun getVirtualCardCount(userId: String): Int
    
    @Query("""SELECT * FROM cards 
             WHERE userId = :userId 
             AND cardType = 'DEBIT' 
             AND isActive = 1 
             AND isFrozen = 0 
             ORDER BY createdAt DESC 
             LIMIT 1""")
    suspend fun getPrimaryDebitCard(userId: String): CardEntity?
}