package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.Card
import com.avinashpatil.monzobank.entity.CardType
import com.avinashpatil.monzobank.entity.DeliveryStatus
import com.avinashpatil.monzobank.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface CardRepository : JpaRepository<Card, UUID> {
    
    // Find cards by user
    fun findByUser(user: User): List<Card>
    fun findByUserId(userId: UUID): List<Card>
    
    // Find cards by card number (masked for security)
    fun findByCardNumber(cardNumber: String): Optional<Card>
    
    // Find cards by type
    fun findByCardType(cardType: CardType): List<Card>
    fun findByUserAndCardType(user: User, cardType: CardType): List<Card>
    
    // Find active cards
    fun findByIsActiveTrue(): List<Card>
    fun findByUserAndIsActiveTrue(user: User): List<Card>
    
    // Find cards by status
    fun findByIsBlocked(isBlocked: Boolean): List<Card>
    fun findByUserAndIsBlocked(user: User, isBlocked: Boolean): List<Card>
    
    // Find cards by delivery status
    fun findByDeliveryStatus(deliveryStatus: DeliveryStatus): List<Card>
    fun findByUserAndDeliveryStatus(user: User, deliveryStatus: DeliveryStatus): List<Card>
    
    // Find cards expiring soon
    fun findByExpiryDateBefore(expiryDate: LocalDateTime): List<Card>
    fun findByExpiryDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Card>
    
    // Find cards by account
    fun findByAccountId(accountId: UUID): List<Card>
    
    // Custom queries
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.isActive = true AND c.isBlocked = false")
    fun findActiveUnblockedCardsByUserId(@Param("userId") userId: UUID): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.cardType = :cardType AND c.isActive = true")
    fun findActiveCardsByType(@Param("cardType") cardType: CardType): List<Card>
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.user.id = :userId")
    fun countCardsByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.cardType = :cardType")
    fun countCardsByType(@Param("cardType") cardType: CardType): Long
    
    @Query("SELECT c FROM Card c WHERE c.expiryDate <= :date AND c.isActive = true")
    fun findExpiringCards(@Param("date") date: LocalDateTime): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.createdAt >= :date")
    fun findRecentlyCreatedCards(@Param("date") date: LocalDateTime): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.cardType = :cardType AND c.isActive = true")
    fun findActiveCardByUserAndType(@Param("userId") userId: UUID, @Param("cardType") cardType: CardType): Optional<Card>
    
    @Query("SELECT c FROM Card c WHERE c.isBlocked = true AND c.blockedAt >= :date")
    fun findRecentlyBlockedCards(@Param("date") date: LocalDateTime): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.deliveryStatus = 'DELIVERED' AND c.activatedAt IS NULL")
    fun findDeliveredButNotActivatedCards(): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    fun findCardsByUserIdOrderByCreatedAtDesc(@Param("userId") userId: UUID): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.cardNumber LIKE :lastFourDigits AND c.user.id = :userId")
    fun findByLastFourDigitsAndUserId(@Param("lastFourDigits") lastFourDigits: String, @Param("userId") userId: UUID): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.contactlessEnabled = true AND c.isActive = true")
    fun findContactlessEnabledCards(): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.onlinePaymentsEnabled = false AND c.isActive = true")
    fun findCardsWithOnlinePaymentsDisabled(): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.atmWithdrawalsEnabled = false AND c.isActive = true")
    fun findCardsWithAtmWithdrawalsDisabled(): List<Card>
    
    @Query("SELECT c FROM Card c WHERE c.magneticStripeEnabled = false AND c.isActive = true")
    fun findCardsWithMagneticStripeDisabled(): List<Card>
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.isActive = true")
    fun countActiveCards(): Long
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.isBlocked = true")
    fun countBlockedCards(): Long
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.deliveryStatus = 'PENDING'")
    fun countPendingDeliveryCards(): Long
}