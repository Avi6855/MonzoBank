package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.*
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.AccountRepository
import com.avinashpatil.monzobank.repository.CardRepository
import com.avinashpatil.monzobank.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@Service
@Transactional
class CardService(
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val emailService: EmailService,
    private val smsService: SmsService
) {
    
    private val logger = LoggerFactory.getLogger(CardService::class.java)
    
    fun createCard(userId: UUID, request: CreateCardRequest): CardResponse {
        logger.info("Creating card for user: $userId")
        
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        val account = accountRepository.findById(request.accountId)
            .orElseThrow { AccountNotFoundException("Account not found with ID: ${request.accountId}") }
        
        // Validate that the account belongs to the user
        if (account.user.id != userId) {
            throw AccessDeniedException("Account does not belong to the user")
        }
        
        // Check if user already has an active card of the same type
        val existingCard = cardRepository.findActiveCardByUserAndType(userId, request.cardType)
        if (existingCard.isPresent) {
            throw BusinessRuleException("User already has an active ${request.cardType} card")
        }
        
        val card = Card(
            id = UUID.randomUUID(),
            user = user,
            account = account,
            cardNumber = generateCardNumber(),
            cardType = request.cardType,
            expiryDate = LocalDateTime.now().plusYears(3),
            cvv = generateCVV(),
            pin = generatePIN(),
            isActive = false, // Card needs to be activated
            isBlocked = false,
            deliveryStatus = DeliveryStatus.PENDING,
            deliveryAddress = request.deliveryAddress,
            contactlessEnabled = true,
            onlinePaymentsEnabled = true,
            atmWithdrawalsEnabled = true,
            magneticStripeEnabled = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedCard = cardRepository.save(card)
        
        // Send card creation notification
        sendCardCreationNotification(user, savedCard)
        
        logger.info("Card created successfully: ${savedCard.id}")
        return mapToCardResponse(savedCard)
    }
    
    @Transactional(readOnly = true)
    fun getCardById(cardId: UUID): CardResponse {
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        return mapToCardResponse(card)
    }
    
    @Transactional(readOnly = true)
    fun getCardsByUserId(userId: UUID): List<CardResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return cardRepository.findByUserAndIsActiveTrue(user)
            .map { mapToCardResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun getCardByCardNumber(cardNumber: String): CardResponse {
        val card = cardRepository.findByCardNumber(cardNumber)
            .orElseThrow { CardNotFoundException("Card not found with number: ${maskCardNumber(cardNumber)}") }
        
        return mapToCardResponse(card)
    }
    
    fun activateCard(cardId: UUID, request: ActivateCardRequest): CardResponse {
        logger.info("Activating card: $cardId")
        
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        if (card.isActive) {
            throw BusinessRuleException("Card is already active")
        }
        
        if (card.deliveryStatus != DeliveryStatus.DELIVERED) {
            throw BusinessRuleException("Card must be delivered before activation")
        }
        
        // Validate PIN if provided
        if (request.pin != null && request.pin != card.pin) {
            throw InvalidCredentialsException("Invalid PIN provided")
        }
        
        card.isActive = true
        card.activatedAt = LocalDateTime.now()
        card.updatedAt = LocalDateTime.now()
        
        val updatedCard = cardRepository.save(card)
        
        // Send activation notification
        sendCardActivationNotification(card.user, updatedCard)
        
        logger.info("Card activated successfully: $cardId")
        return mapToCardResponse(updatedCard)
    }
    
    fun blockCard(cardId: UUID, reason: String): CardResponse {
        logger.info("Blocking card: $cardId, reason: $reason")
        
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        if (card.isBlocked) {
            throw BusinessRuleException("Card is already blocked")
        }
        
        card.isBlocked = true
        card.blockedAt = LocalDateTime.now()
        card.blockReason = reason
        card.updatedAt = LocalDateTime.now()
        
        val updatedCard = cardRepository.save(card)
        
        // Send block notification
        sendCardBlockNotification(card.user, updatedCard, reason)
        
        logger.info("Card blocked successfully: $cardId")
        return mapToCardResponse(updatedCard)
    }
    
    fun unblockCard(cardId: UUID): CardResponse {
        logger.info("Unblocking card: $cardId")
        
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        if (!card.isBlocked) {
            throw BusinessRuleException("Card is not blocked")
        }
        
        card.isBlocked = false
        card.blockedAt = null
        card.blockReason = null
        card.updatedAt = LocalDateTime.now()
        
        val updatedCard = cardRepository.save(card)
        
        // Send unblock notification
        sendCardUnblockNotification(card.user, updatedCard)
        
        logger.info("Card unblocked successfully: $cardId")
        return mapToCardResponse(updatedCard)
    }
    
    fun updateCardSettings(cardId: UUID, request: UpdateCardSettingsRequest): CardResponse {
        logger.info("Updating card settings: $cardId")
        
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        request.contactlessEnabled?.let { card.contactlessEnabled = it }
        request.onlinePaymentsEnabled?.let { card.onlinePaymentsEnabled = it }
        request.atmWithdrawalsEnabled?.let { card.atmWithdrawalsEnabled = it }
        request.magneticStripeEnabled?.let { card.magneticStripeEnabled = it }
        
        card.updatedAt = LocalDateTime.now()
        
        val updatedCard = cardRepository.save(card)
        
        logger.info("Card settings updated successfully: $cardId")
        return mapToCardResponse(updatedCard)
    }
    
    fun changePIN(cardId: UUID, request: ChangePINRequest): CardResponse {
        logger.info("Changing PIN for card: $cardId")
        
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        // Validate current PIN
        if (request.currentPin != card.pin) {
            throw InvalidCredentialsException("Current PIN is incorrect")
        }
        
        // Validate new PIN format
        if (!isValidPIN(request.newPin)) {
            throw ValidationException("New PIN must be 4 digits")
        }
        
        card.pin = request.newPin
        card.updatedAt = LocalDateTime.now()
        
        val updatedCard = cardRepository.save(card)
        
        // Send PIN change notification
        sendPINChangeNotification(card.user, updatedCard)
        
        logger.info("PIN changed successfully for card: $cardId")
        return mapToCardResponse(updatedCard)
    }
    
    fun replaceCard(cardId: UUID, request: ReplaceCardRequest): CardResponse {
        logger.info("Replacing card: $cardId, reason: ${request.reason}")
        
        val oldCard = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        // Deactivate old card
        oldCard.isActive = false
        oldCard.isBlocked = true
        oldCard.blockReason = "Replaced - ${request.reason}"
        oldCard.updatedAt = LocalDateTime.now()
        cardRepository.save(oldCard)
        
        // Create new card
        val newCard = Card(
            id = UUID.randomUUID(),
            user = oldCard.user,
            account = oldCard.account,
            cardNumber = generateCardNumber(),
            cardType = oldCard.cardType,
            expiryDate = LocalDateTime.now().plusYears(3),
            cvv = generateCVV(),
            pin = oldCard.pin, // Keep same PIN
            isActive = false, // Needs activation
            isBlocked = false,
            deliveryStatus = DeliveryStatus.PENDING,
            deliveryAddress = request.deliveryAddress ?: oldCard.deliveryAddress,
            contactlessEnabled = oldCard.contactlessEnabled,
            onlinePaymentsEnabled = oldCard.onlinePaymentsEnabled,
            atmWithdrawalsEnabled = oldCard.atmWithdrawalsEnabled,
            magneticStripeEnabled = oldCard.magneticStripeEnabled,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedNewCard = cardRepository.save(newCard)
        
        // Send replacement notification
        sendCardReplacementNotification(oldCard.user, savedNewCard, request.reason)
        
        logger.info("Card replaced successfully. Old card: $cardId, New card: ${savedNewCard.id}")
        return mapToCardResponse(savedNewCard)
    }
    
    fun updateDeliveryStatus(cardId: UUID, status: DeliveryStatus): CardResponse {
        logger.info("Updating delivery status for card: $cardId to $status")
        
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        card.deliveryStatus = status
        card.updatedAt = LocalDateTime.now()
        
        if (status == DeliveryStatus.DELIVERED) {
            card.deliveredAt = LocalDateTime.now()
        }
        
        val updatedCard = cardRepository.save(card)
        
        // Send delivery notification
        if (status == DeliveryStatus.DELIVERED) {
            sendCardDeliveryNotification(card.user, updatedCard)
        }
        
        logger.info("Delivery status updated successfully for card: $cardId")
        return mapToCardResponse(updatedCard)
    }
    
    @Transactional(readOnly = true)
    fun getCardTransactions(cardId: UUID): List<TransactionResponse> {
        val card = cardRepository.findById(cardId)
            .orElseThrow { CardNotFoundException("Card not found with ID: $cardId") }
        
        // In a real implementation, this would fetch transactions associated with the card
        // For now, return empty list
        return emptyList()
    }
    
    private fun generateCardNumber(): String {
        // Generate a 16-digit card number (simplified)
        val prefix = "4532" // Visa prefix
        val middle = (100000000000L..999999999999L).random().toString()
        return prefix + middle.substring(0, 12)
    }
    
    private fun generateCVV(): String {
        return (100..999).random().toString()
    }
    
    private fun generatePIN(): String {
        return (1000..9999).random().toString()
    }
    
    private fun isValidPIN(pin: String): Boolean {
        return pin.matches(Regex("^\\d{4}$"))
    }
    
    private fun maskCardNumber(cardNumber: String): String {
        if (cardNumber.length < 4) return "****"
        return "**** **** **** ${cardNumber.takeLast(4)}"
    }
    
    private fun sendCardCreationNotification(user: User, card: Card) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Card Creation",
                java.math.BigDecimal.ZERO,
                "GBP",
                "Your new ${card.cardType} card has been created and will be delivered soon."
            )
        } catch (e: Exception) {
            logger.error("Failed to send card creation notification", e)
        }
    }
    
    private fun sendCardActivationNotification(user: User, card: Card) {
        try {
            emailService.sendSecurityAlert(
                user.email,
                user.firstName,
                "Card Activated",
                "Your ${card.cardType} card ending in ${card.cardNumber.takeLast(4)} has been activated."
            )
            
            smsService.sendSecurityAlert(
                user.phoneNumber,
                "Your ${card.cardType} card has been activated."
            )
        } catch (e: Exception) {
            logger.error("Failed to send card activation notification", e)
        }
    }
    
    private fun sendCardBlockNotification(user: User, card: Card, reason: String) {
        try {
            emailService.sendSecurityAlert(
                user.email,
                user.firstName,
                "Card Blocked",
                "Your ${card.cardType} card ending in ${card.cardNumber.takeLast(4)} has been blocked. Reason: $reason"
            )
            
            smsService.sendCardBlockedAlert(
                user.phoneNumber,
                card.cardNumber.takeLast(4),
                reason
            )
        } catch (e: Exception) {
            logger.error("Failed to send card block notification", e)
        }
    }
    
    private fun sendCardUnblockNotification(user: User, card: Card) {
        try {
            emailService.sendSecurityAlert(
                user.email,
                user.firstName,
                "Card Unblocked",
                "Your ${card.cardType} card ending in ${card.cardNumber.takeLast(4)} has been unblocked."
            )
        } catch (e: Exception) {
            logger.error("Failed to send card unblock notification", e)
        }
    }
    
    private fun sendPINChangeNotification(user: User, card: Card) {
        try {
            emailService.sendSecurityAlert(
                user.email,
                user.firstName,
                "PIN Changed",
                "The PIN for your ${card.cardType} card ending in ${card.cardNumber.takeLast(4)} has been changed."
            )
        } catch (e: Exception) {
            logger.error("Failed to send PIN change notification", e)
        }
    }
    
    private fun sendCardReplacementNotification(user: User, card: Card, reason: String) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Card Replacement",
                java.math.BigDecimal.ZERO,
                "GBP",
                "A replacement ${card.cardType} card has been ordered. Reason: $reason. It will be delivered soon."
            )
        } catch (e: Exception) {
            logger.error("Failed to send card replacement notification", e)
        }
    }
    
    private fun sendCardDeliveryNotification(user: User, card: Card) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Card Delivered",
                java.math.BigDecimal.ZERO,
                "GBP",
                "Your ${card.cardType} card has been delivered. Please activate it to start using."
            )
        } catch (e: Exception) {
            logger.error("Failed to send card delivery notification", e)
        }
    }
    
    private fun mapToCardResponse(card: Card): CardResponse {
        return CardResponse(
            id = card.id,
            userId = card.user.id,
            accountId = card.account.id,
            cardNumber = maskCardNumber(card.cardNumber),
            cardType = card.cardType,
            expiryDate = card.expiryDate,
            isActive = card.isActive,
            isBlocked = card.isBlocked,
            deliveryStatus = card.deliveryStatus,
            contactlessEnabled = card.contactlessEnabled,
            onlinePaymentsEnabled = card.onlinePaymentsEnabled,
            atmWithdrawalsEnabled = card.atmWithdrawalsEnabled,
            magneticStripeEnabled = card.magneticStripeEnabled,
            createdAt = card.createdAt,
            activatedAt = card.activatedAt,
            blockedAt = card.blockedAt,
            deliveredAt = card.deliveredAt
        )
    }
}