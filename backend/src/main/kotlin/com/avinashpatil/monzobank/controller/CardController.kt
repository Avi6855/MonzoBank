package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.DeliveryStatus
import com.avinashpatil.monzobank.service.CardService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class CardController(
    private val cardService: CardService
) {
    
    @PostMapping
    fun createCard(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: CreateCardRequest
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val response = cardService.createCard(UUID.fromString(userId), request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Card created successfully", response))
    }
    
    @GetMapping("/{cardId}")
    fun getCard(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.getCardById(cardId)
        return ResponseEntity.ok(ApiResponse.success("Card retrieved successfully", card))
    }
    
    @GetMapping
    fun getCards(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<CardResponse>>> {
        val cards = cardService.getCardsByUserId(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Cards retrieved successfully", cards))
    }
    
    @GetMapping("/number/{cardNumber}")
    fun getCardByNumber(
        @PathVariable cardNumber: String
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.getCardByCardNumber(cardNumber)
        return ResponseEntity.ok(ApiResponse.success("Card retrieved successfully", card))
    }
    
    @PostMapping("/{cardId}/activate")
    fun activateCard(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: ActivateCardRequest
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.activateCard(cardId, request)
        return ResponseEntity.ok(ApiResponse.success("Card activated successfully", card))
    }
    
    @PostMapping("/{cardId}/block")
    fun blockCard(
        @PathVariable cardId: UUID,
        @RequestParam reason: String
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.blockCard(cardId, reason)
        return ResponseEntity.ok(ApiResponse.success("Card blocked successfully", card))
    }
    
    @PostMapping("/{cardId}/unblock")
    fun unblockCard(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.unblockCard(cardId)
        return ResponseEntity.ok(ApiResponse.success("Card unblocked successfully", card))
    }
    
    @PutMapping("/{cardId}/settings")
    fun updateCardSettings(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: UpdateCardSettingsRequest
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.updateCardSettings(cardId, request)
        return ResponseEntity.ok(ApiResponse.success("Card settings updated successfully", card))
    }
    
    @PostMapping("/{cardId}/change-pin")
    fun changePIN(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: ChangePINRequest
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.changePIN(cardId, request)
        return ResponseEntity.ok(ApiResponse.success("PIN changed successfully", card))
    }
    
    @PostMapping("/{cardId}/replace")
    fun replaceCard(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: ReplaceCardRequest
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.replaceCard(cardId, request)
        return ResponseEntity.ok(ApiResponse.success("Card replacement initiated successfully", card))
    }
    
    @PutMapping("/{cardId}/delivery-status")
    fun updateDeliveryStatus(
        @PathVariable cardId: UUID,
        @RequestParam status: DeliveryStatus
    ): ResponseEntity<ApiResponse<CardResponse>> {
        val card = cardService.updateDeliveryStatus(cardId, status)
        return ResponseEntity.ok(ApiResponse.success("Delivery status updated successfully", card))
    }
    
    @GetMapping("/{cardId}/transactions")
    fun getCardTransactions(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<List<TransactionResponse>>> {
        val transactions = cardService.getCardTransactions(cardId)
        return ResponseEntity.ok(ApiResponse.success("Card transactions retrieved successfully", transactions))
    }
    
    @GetMapping("/{cardId}/limits")
    fun getCardLimits(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<CardLimitsResponse>> {
        // In a real implementation, these would be stored in the database
        val limits = CardLimitsResponse(
            dailySpendingLimit = java.math.BigDecimal("2000.00"),
            monthlySpendingLimit = java.math.BigDecimal("10000.00"),
            dailyWithdrawalLimit = java.math.BigDecimal("500.00"),
            monthlyWithdrawalLimit = java.math.BigDecimal("2000.00"),
            singleTransactionLimit = java.math.BigDecimal("1000.00"),
            onlineTransactionLimit = java.math.BigDecimal("1500.00"),
            contactlessLimit = java.math.BigDecimal("100.00")
        )
        return ResponseEntity.ok(ApiResponse.success("Card limits retrieved successfully", limits))
    }
    
    @PutMapping("/{cardId}/limits")
    fun updateCardLimits(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: UpdateCardLimitsRequest
    ): ResponseEntity<ApiResponse<CardLimitsResponse>> {
        // In a real implementation, this would update the limits in the database
        val limits = CardLimitsResponse(
            dailySpendingLimit = request.dailySpendingLimit ?: java.math.BigDecimal("2000.00"),
            monthlySpendingLimit = request.monthlySpendingLimit ?: java.math.BigDecimal("10000.00"),
            dailyWithdrawalLimit = request.dailyWithdrawalLimit ?: java.math.BigDecimal("500.00"),
            monthlyWithdrawalLimit = request.monthlyWithdrawalLimit ?: java.math.BigDecimal("2000.00"),
            singleTransactionLimit = request.singleTransactionLimit ?: java.math.BigDecimal("1000.00"),
            onlineTransactionLimit = request.onlineTransactionLimit ?: java.math.BigDecimal("1500.00"),
            contactlessLimit = request.contactlessLimit ?: java.math.BigDecimal("100.00")
        )
        return ResponseEntity.ok(ApiResponse.success("Card limits updated successfully", limits))
    }
    
    @GetMapping("/{cardId}/usage")
    fun getCardUsage(
        @PathVariable cardId: UUID,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<ApiResponse<CardUsageResponse>> {
        // In a real implementation, this would calculate actual usage statistics
        val usage = CardUsageResponse(
            cardId = cardId,
            totalTransactions = 0L,
            totalSpent = java.math.BigDecimal.ZERO,
            totalWithdrawn = java.math.BigDecimal.ZERO,
            averageTransactionAmount = java.math.BigDecimal.ZERO,
            largestTransaction = java.math.BigDecimal.ZERO,
            mostUsedMerchant = null,
            mostUsedCategory = null,
            contactlessTransactions = 0L,
            onlineTransactions = 0L,
            atmTransactions = 0L,
            period = "Last $days days"
        )
        return ResponseEntity.ok(ApiResponse.success("Card usage retrieved successfully", usage))
    }
    
    @GetMapping("/{cardId}/security")
    fun getCardSecurity(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<CardSecurityResponse>> {
        // In a real implementation, this would retrieve actual security information
        val security = CardSecurityResponse(
            cardId = cardId,
            isActive = true,
            isBlocked = false,
            blockReason = null,
            lastUsed = null,
            failedAttempts = 0,
            securityAlerts = emptyList()
        )
        return ResponseEntity.ok(ApiResponse.success("Card security information retrieved successfully", security))
    }
    
    @PostMapping("/{cardId}/statement")
    fun generateStatement(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: CardStatementRequest
    ): ResponseEntity<ApiResponse<CardStatementResponse>> {
        // In a real implementation, this would generate and return a statement
        val statement = CardStatementResponse(
            statementId = UUID.randomUUID(),
            cardId = cardId,
            format = request.format,
            downloadUrl = "/api/cards/$cardId/statement/download",
            emailSent = request.email != null,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(7)
        )
        return ResponseEntity.ok(ApiResponse.success("Statement generated successfully", statement))
    }
    
    @GetMapping("/{cardId}/rewards")
    fun getCardRewards(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<CardRewardsResponse>> {
        // In a real implementation, this would retrieve actual rewards information
        val rewards = CardRewardsResponse(
            cardId = cardId,
            totalPoints = 0L,
            availablePoints = 0L,
            redeemedPoints = 0L,
            cashbackEarned = java.math.BigDecimal.ZERO,
            cashbackRedeemed = java.math.BigDecimal.ZERO,
            currentTier = "Bronze",
            nextTierRequirement = 1000L,
            recentRewards = emptyList()
        )
        return ResponseEntity.ok(ApiResponse.success("Card rewards retrieved successfully", rewards))
    }
    
    @GetMapping("/{cardId}/fraud-alerts")
    fun getFraudAlerts(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<List<CardFraudAlertResponse>>> {
        // In a real implementation, this would retrieve actual fraud alerts
        val alerts = emptyList<CardFraudAlertResponse>()
        return ResponseEntity.ok(ApiResponse.success("Fraud alerts retrieved successfully", alerts))
    }
    
    @PostMapping("/fraud-alerts/{alertId}/confirm")
    fun confirmFraud(
        @PathVariable alertId: UUID,
        @Valid @RequestBody request: ConfirmFraudRequest
    ): ResponseEntity<ApiResponse<String>> {
        // In a real implementation, this would process the fraud confirmation
        val message = if (request.isFraud) {
            "Fraud confirmed. Card has been blocked and replacement will be issued."
        } else {
            "Transaction confirmed as legitimate. Alert has been dismissed."
        }
        return ResponseEntity.ok(ApiResponse.success(message, "Alert processed successfully"))
    }
    
    @GetMapping("/{cardId}/delivery-tracking")
    fun getDeliveryTracking(
        @PathVariable cardId: UUID
    ): ResponseEntity<ApiResponse<CardDeliveryTrackingResponse>> {
        // In a real implementation, this would retrieve actual delivery tracking information
        val tracking = CardDeliveryTrackingResponse(
            cardId = cardId,
            deliveryStatus = DeliveryStatus.PENDING,
            trackingNumber = null,
            estimatedDeliveryDate = LocalDateTime.now().plusDays(3),
            deliveryAddress = "123 Main St, London, UK",
            courierService = null,
            deliveryUpdates = emptyList()
        )
        return ResponseEntity.ok(ApiResponse.success("Delivery tracking retrieved successfully", tracking))
    }
    
    @PostMapping("/{cardId}/renew")
    fun renewCard(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: CardRenewalRequest
    ): ResponseEntity<ApiResponse<CardRenewalResponse>> {
        // In a real implementation, this would process card renewal
        val renewal = CardRenewalResponse(
            oldCardId = cardId,
            newCardId = UUID.randomUUID(),
            newCardNumber = "**** **** **** 1234",
            expiryDate = LocalDateTime.now().plusYears(3),
            deliveryStatus = DeliveryStatus.PENDING,
            estimatedDeliveryDate = LocalDateTime.now().plusDays(5),
            createdAt = LocalDateTime.now()
        )
        return ResponseEntity.ok(ApiResponse.success("Card renewal initiated successfully", renewal))
    }
    
    @PutMapping("/{cardId}/preferences")
    fun updateCardPreferences(
        @PathVariable cardId: UUID,
        @Valid @RequestBody request: CardPreferencesRequest
    ): ResponseEntity<ApiResponse<String>> {
        // In a real implementation, this would update card preferences
        return ResponseEntity.ok(ApiResponse.success("Card preferences updated successfully", "Preferences saved"))
    }
    
    @GetMapping("/{cardId}/analytics")
    fun getCardAnalytics(
        @PathVariable cardId: UUID,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<ApiResponse<CardAnalyticsResponse>> {
        // In a real implementation, this would calculate actual analytics
        val analytics = CardAnalyticsResponse(
            cardId = cardId,
            period = "Last $days days",
            totalSpending = java.math.BigDecimal.ZERO,
            transactionCount = 0L,
            averageTransactionSize = java.math.BigDecimal.ZERO,
            topMerchants = emptyList(),
            topCategories = emptyList(),
            spendingTrend = emptyList(),
            comparisonToPreviousPeriod = SpendingComparison(
                percentageChange = java.math.BigDecimal.ZERO,
                absoluteChange = java.math.BigDecimal.ZERO,
                trend = "STABLE"
            )
        )
        return ResponseEntity.ok(ApiResponse.success("Card analytics retrieved successfully", analytics))
    }
}