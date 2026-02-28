package com.avinashpatil.app.monzobank.domain.usecase.card

import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.DeliveryStatus
import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CreateCardUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(
        userId: String,
        accountId: String,
        cardType: CardType,
        cardHolderName: String,
        cardName: String? = null
    ): Result<Card> {
        return try {
            val cardNumber = generateCardNumber()
            val cvv = generateCVV()
            val expiryDate = generateExpiryDate()
            
            val card = Card(
                id = UUID.randomUUID().toString(),
                userId = userId,
                accountId = accountId,
                cardNumber = cardNumber,
                expiryDate = expiryDate,
                cvv = cvv,
                cardType = cardType,
                cardHolderName = cardHolderName,
                cardName = cardName ?: getDefaultCardName(cardType),
                isActive = true,
                isFrozen = false,
                dailyLimit = getDefaultDailyLimit(cardType),
                monthlyLimit = getDefaultMonthlyLimit(cardType),
                contactlessEnabled = true,
                internationalEnabled = cardType == CardType.DEBIT, // Virtual cards default to domestic only
            onlineEnabled = true,
            atmEnabled = cardType == CardType.DEBIT, // Only physical cards can be used at ATMs
            deliveryStatus = if (cardType == CardType.VIRTUAL) DeliveryStatus.DELIVERED else DeliveryStatus.ORDERED,
            cardDesign = "default",
            trackingNumber = if (cardType == CardType.DEBIT) generateTrackingNumber() else null,
                createdAt = Date(),
                updatedAt = Date()
            )
            
            cardRepository.createCard(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateCardNumber(): String {
        // Monzo cards start with 5555 (Mastercard BIN)
        val prefix = "5555"
        val middle = (100000000000L..999999999999L).random().toString().take(8)
        val cardNumberWithoutChecksum = prefix + middle
        val checksum = calculateLuhnChecksum(cardNumberWithoutChecksum)
        return cardNumberWithoutChecksum + checksum
    }
    
    private fun generateCVV(): String {
        return (100..999).random().toString()
    }
    
    private fun generateExpiryDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, 4) // Cards expire in 4 years
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calendar.time
    }
    
    private fun generateTrackingNumber(): String {
        return "MZ" + (100000000L..999999999L).random().toString()
    }
    
    private fun getDefaultCardName(cardType: CardType): String {
        return when (cardType) {
            CardType.DEBIT -> "Monzo Card"
            CardType.VIRTUAL -> "Virtual Card"
            CardType.CREDIT -> "Credit Card"
            CardType.PREPAID -> "Prepaid Card"
            CardType.BUSINESS_DEBIT -> "Business Debit Card"
            CardType.BUSINESS_CREDIT -> "Business Credit Card"
            CardType.PREMIUM -> "Premium Card"
        }
    }
    
    private fun getDefaultDailyLimit(cardType: CardType): BigDecimal {
        return when (cardType) {
            CardType.DEBIT -> BigDecimal("1000.00")
            CardType.VIRTUAL -> BigDecimal("500.00")
            CardType.CREDIT -> BigDecimal("1500.00")
            CardType.PREPAID -> BigDecimal("300.00")
            CardType.BUSINESS_DEBIT -> BigDecimal("2000.00")
            CardType.BUSINESS_CREDIT -> BigDecimal("3000.00")
            CardType.PREMIUM -> BigDecimal("5000.00")
        }
    }

    private fun getDefaultMonthlyLimit(cardType: CardType): BigDecimal {
        return when (cardType) {
            CardType.DEBIT -> BigDecimal("10000.00")
            CardType.VIRTUAL -> BigDecimal("2000.00")
            CardType.CREDIT -> BigDecimal("15000.00")
            CardType.PREPAID -> BigDecimal("1000.00")
            CardType.BUSINESS_DEBIT -> BigDecimal("20000.00")
            CardType.BUSINESS_CREDIT -> BigDecimal("30000.00")
            CardType.PREMIUM -> BigDecimal("50000.00")
        }
    }
    
    private fun calculateLuhnChecksum(cardNumber: String): String {
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
}