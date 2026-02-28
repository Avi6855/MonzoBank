package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.service.PotService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/pots")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class PotController(
    private val potService: PotService
) {
    
    @PostMapping
    fun createPot(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: CreatePotRequest
    ): ResponseEntity<ApiResponse<PotResponse>> {
        val response = potService.createPot(UUID.fromString(userId), request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Pot created successfully", response))
    }
    
    @GetMapping("/{potId}")
    fun getPot(
        @PathVariable potId: UUID
    ): ResponseEntity<ApiResponse<PotResponse>> {
        val pot = potService.getPotById(potId)
        return ResponseEntity.ok(ApiResponse.success("Pot retrieved successfully", pot))
    }
    
    @GetMapping
    fun getPots(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<PotResponse>>> {
        val pots = potService.getPotsByUserId(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Pots retrieved successfully", pots))
    }
    
    @PutMapping("/{potId}")
    fun updatePot(
        @PathVariable potId: UUID,
        @Valid @RequestBody request: UpdatePotRequest
    ): ResponseEntity<ApiResponse<PotResponse>> {
        val pot = potService.updatePot(potId, request)
        return ResponseEntity.ok(ApiResponse.success("Pot updated successfully", pot))
    }
    
    @PostMapping("/{potId}/add-money")
    fun addMoney(
        @PathVariable potId: UUID,
        @Valid @RequestBody request: AddMoneyToPotRequest
    ): ResponseEntity<ApiResponse<PotResponse>> {
        val pot = potService.addMoney(potId, request)
        return ResponseEntity.ok(ApiResponse.success("Money added to pot successfully", pot))
    }
    
    @PostMapping("/{potId}/withdraw-money")
    fun withdrawMoney(
        @PathVariable potId: UUID,
        @Valid @RequestBody request: WithdrawMoneyFromPotRequest
    ): ResponseEntity<ApiResponse<PotResponse>> {
        val pot = potService.withdrawMoney(potId, request)
        return ResponseEntity.ok(ApiResponse.success("Money withdrawn from pot successfully", pot))
    }
    
    @DeleteMapping("/{potId}")
    fun closePot(
        @PathVariable potId: UUID
    ): ResponseEntity<ApiResponse<PotResponse>> {
        val pot = potService.closePot(potId)
        return ResponseEntity.ok(ApiResponse.success("Pot closed successfully", pot))
    }
    
    @GetMapping("/summary")
    fun getPotSummary(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<PotSummaryResponse>> {
        val summary = potService.getPotSummary(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Pot summary retrieved successfully", summary))
    }
    
    @GetMapping("/completed")
    fun getCompletedPots(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<PotResponse>>> {
        val completedPots = potService.getCompletedPots(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Completed pots retrieved successfully", completedPots))
    }
    
    @GetMapping("/{potId}/transactions")
    fun getPotTransactions(
        @PathVariable potId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<PotTransactionResponse>>> {
        // In a real implementation, this would retrieve actual pot transactions
        val transactions = emptyList<PotTransactionResponse>()
        return ResponseEntity.ok(ApiResponse.success("Pot transactions retrieved successfully", transactions))
    }
    
    @PostMapping("/{potId}/transactions/history")
    fun getPotTransactionHistory(
        @PathVariable potId: UUID,
        @Valid @RequestBody request: PotTransactionHistoryRequest
    ): ResponseEntity<ApiResponse<List<PotTransactionResponse>>> {
        // In a real implementation, this would retrieve filtered transaction history
        val transactions = emptyList<PotTransactionResponse>()
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved successfully", transactions))
    }
    
    @GetMapping("/{potId}/goal")
    fun getPotGoal(
        @PathVariable potId: UUID
    ): ResponseEntity<ApiResponse<PotGoalResponse>> {
        val pot = potService.getPotById(potId)
        
        // Calculate goal metrics
        val daysRemaining = pot.targetDate?.let {
            java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), it)
        }
        
        val remainingAmount = pot.targetAmount.subtract(pot.currentBalance)
        val recommendedDailyDeposit = if (daysRemaining != null && daysRemaining > 0) {
            remainingAmount.divide(java.math.BigDecimal(daysRemaining), 2, java.math.RoundingMode.HALF_UP)
        } else null
        
        val goalResponse = PotGoalResponse(
            potId = pot.id,
            name = pot.name,
            targetAmount = pot.targetAmount,
            currentBalance = pot.currentBalance,
            progressPercentage = pot.progressPercentage,
            targetDate = pot.targetDate,
            daysRemaining = daysRemaining,
            recommendedDailyDeposit = recommendedDailyDeposit,
            recommendedWeeklyDeposit = recommendedDailyDeposit?.multiply(java.math.BigDecimal(7)),
            recommendedMonthlyDeposit = recommendedDailyDeposit?.multiply(java.math.BigDecimal(30)),
            onTrack = pot.progressPercentage >= java.math.BigDecimal(50), // Simplified logic
            projectedCompletionDate = pot.targetDate?.plusMonths(1) // Simplified projection
        )
        
        return ResponseEntity.ok(ApiResponse.success("Pot goal information retrieved successfully", goalResponse))
    }
    
    @GetMapping("/{potId}/analytics")
    fun getPotAnalytics(
        @PathVariable potId: UUID,
        @RequestParam(defaultValue = "12") months: Int
    ): ResponseEntity<ApiResponse<PotAnalyticsResponse>> {
        // In a real implementation, this would calculate actual analytics
        val analytics = PotAnalyticsResponse(
            potId = potId,
            period = "Last $months months",
            totalDeposits = java.math.BigDecimal.ZERO,
            totalWithdrawals = java.math.BigDecimal.ZERO,
            netSavings = java.math.BigDecimal.ZERO,
            depositCount = 0L,
            withdrawalCount = 0L,
            averageDepositAmount = java.math.BigDecimal.ZERO,
            averageWithdrawalAmount = java.math.BigDecimal.ZERO,
            savingsRate = java.math.BigDecimal.ZERO,
            monthlyTrend = emptyList()
        )
        
        return ResponseEntity.ok(ApiResponse.success("Pot analytics retrieved successfully", analytics))
    }
    
    @PostMapping("/{potId}/reminders")
    fun createPotReminder(
        @PathVariable potId: UUID,
        @Valid @RequestBody request: PotReminderRequest
    ): ResponseEntity<ApiResponse<PotReminderResponse>> {
        // In a real implementation, this would create a reminder
        val reminder = PotReminderResponse(
            id = UUID.randomUUID(),
            potId = potId,
            reminderType = request.reminderType,
            frequency = request.frequency,
            customMessage = request.customMessage,
            enabled = request.enabled,
            nextReminderDate = LocalDateTime.now().plusDays(1),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Pot reminder created successfully", reminder))
    }
    
    @GetMapping("/{potId}/reminders")
    fun getPotReminders(
        @PathVariable potId: UUID
    ): ResponseEntity<ApiResponse<List<PotReminderResponse>>> {
        // In a real implementation, this would retrieve actual reminders
        val reminders = emptyList<PotReminderResponse>()
        return ResponseEntity.ok(ApiResponse.success("Pot reminders retrieved successfully", reminders))
    }
    
    @PostMapping("/{potId}/challenges")
    fun createPotChallenge(
        @PathVariable potId: UUID,
        @Valid @RequestBody request: PotChallengeRequest
    ): ResponseEntity<ApiResponse<PotChallengeResponse>> {
        // In a real implementation, this would create a savings challenge
        val challenge = PotChallengeResponse(
            id = UUID.randomUUID(),
            potId = potId,
            challengeName = request.challengeName,
            challengeType = request.challengeType,
            targetAmount = request.targetAmount,
            currentAmount = java.math.BigDecimal.ZERO,
            progressPercentage = java.math.BigDecimal.ZERO,
            durationInDays = request.durationInDays,
            daysRemaining = request.durationInDays,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusDays(request.durationInDays.toLong()),
            isCompleted = false,
            isActive = true,
            description = request.description,
            createdAt = LocalDateTime.now()
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Pot challenge created successfully", challenge))
    }
    
    @GetMapping("/{potId}/challenges")
    fun getPotChallenges(
        @PathVariable potId: UUID
    ): ResponseEntity<ApiResponse<List<PotChallengeResponse>>> {
        // In a real implementation, this would retrieve actual challenges
        val challenges = emptyList<PotChallengeResponse>()
        return ResponseEntity.ok(ApiResponse.success("Pot challenges retrieved successfully", challenges))
    }
    
    @GetMapping("/{potId}/insights")
    fun getPotInsights(
        @PathVariable potId: UUID
    ): ResponseEntity<ApiResponse<PotInsightsResponse>> {
        // In a real implementation, this would calculate actual insights
        val insights = PotInsightsResponse(
            potId = potId,
            savingsVelocity = java.math.BigDecimal.ZERO,
            consistencyScore = java.math.BigDecimal(75), // Example score
            timeToGoal = null,
            bestSavingsMonth = null,
            worstSavingsMonth = null,
            averageMonthlyDeposit = java.math.BigDecimal.ZERO,
            longestSavingsStreak = 0,
            currentSavingsStreak = 0,
            recommendations = listOf(
                SavingsRecommendation(
                    type = "INCREASE_FREQUENCY",
                    title = "Increase Deposit Frequency",
                    description = "Consider making smaller, more frequent deposits to build a consistent savings habit.",
                    impact = "MEDIUM",
                    actionRequired = "Set up weekly auto-deposits"
                )
            )
        )
        
        return ResponseEntity.ok(ApiResponse.success("Pot insights retrieved successfully", insights))
    }
    
    @GetMapping("/comparison")
    fun getPotComparison(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<PotComparisonResponse>> {
        // In a real implementation, this would compare user's pots with aggregated data
        val comparison = PotComparisonResponse(
            userPots = emptyList(),
            averageUserSavings = java.math.BigDecimal("500.00"),
            userRanking = 65, // 65th percentile
            topPerformingPot = null,
            improvementSuggestions = listOf(
                "Consider setting up automatic deposits to maintain consistency",
                "Try the 52-week savings challenge to boost your savings",
                "Review your spending categories to find more savings opportunities"
            )
        )
        
        return ResponseEntity.ok(ApiResponse.success("Pot comparison retrieved successfully", comparison))
    }
    
    @PostMapping("/export")
    fun exportPots(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: PotExportRequest
    ): ResponseEntity<ApiResponse<PotExportResponse>> {
        // In a real implementation, this would generate and return an export file
        val export = PotExportResponse(
            exportId = UUID.randomUUID(),
            status = "PROCESSING",
            downloadUrl = null,
            format = request.format,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(7)
        )
        
        return ResponseEntity.ok(ApiResponse.success("Pot export initiated successfully", export))
    }
    
    @GetMapping("/templates")
    fun getPotTemplates(): ResponseEntity<ApiResponse<List<PotTemplateResponse>>> {
        // In a real implementation, this would retrieve actual templates
        val templates = listOf(
            PotTemplateResponse(
                id = UUID.randomUUID(),
                templateName = "Emergency Fund",
                templateType = PotTemplateType.EMERGENCY_FUND,
                suggestedTargetAmount = java.math.BigDecimal("3000.00"),
                suggestedDurationInMonths = 12,
                suggestedAutoDepositAmount = java.math.BigDecimal("250.00"),
                suggestedDepositFrequency = com.avinashpatil.monzobank.entity.DepositFrequency.MONTHLY,
                description = "Build an emergency fund to cover 3-6 months of expenses",
                tips = listOf(
                    "Start with a small target and increase gradually",
                    "Keep emergency funds in a separate, easily accessible account",
                    "Aim for 3-6 months of living expenses"
                ),
                isPopular = true
            ),
            PotTemplateResponse(
                id = UUID.randomUUID(),
                templateName = "Holiday Fund",
                templateType = PotTemplateType.VACATION,
                suggestedTargetAmount = java.math.BigDecimal("1500.00"),
                suggestedDurationInMonths = 8,
                suggestedAutoDepositAmount = java.math.BigDecimal("187.50"),
                suggestedDepositFrequency = com.avinashpatil.monzobank.entity.DepositFrequency.MONTHLY,
                description = "Save for your dream vacation",
                tips = listOf(
                    "Research your destination to set a realistic target",
                    "Book flights and accommodation early for better deals",
                    "Consider travel insurance"
                ),
                isPopular = true
            )
        )
        
        return ResponseEntity.ok(ApiResponse.success("Pot templates retrieved successfully", templates))
    }
    
    @PostMapping("/templates/{templateId}/create")
    fun createPotFromTemplate(
        @AuthenticationPrincipal userId: String,
        @PathVariable templateId: UUID,
        @Valid @RequestBody request: PotTemplateRequest
    ): ResponseEntity<ApiResponse<PotResponse>> {
        // In a real implementation, this would create a pot based on the template
        val createRequest = CreatePotRequest(
            name = request.templateName,
            description = "Created from ${request.templateType} template",
            targetAmount = request.targetAmount ?: java.math.BigDecimal("1000.00"),
            targetDate = request.durationInMonths?.let { LocalDateTime.now().plusMonths(it.toLong()) },
            autoDepositEnabled = request.autoDepositAmount != null,
            autoDepositAmount = request.autoDepositAmount,
            depositFrequency = request.depositFrequency
        )
        
        val pot = potService.createPot(UUID.fromString(userId), createRequest)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Pot created from template successfully", pot))
    }
}