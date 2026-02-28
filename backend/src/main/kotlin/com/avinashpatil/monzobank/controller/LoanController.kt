package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.LoanType
import com.avinashpatil.monzobank.service.LoanService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/loans")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class LoanController(
    private val loanService: LoanService
) {
    
    @PostMapping("/apply")
    fun applyForLoan(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: LoanApplicationRequest
    ): ResponseEntity<ApiResponse<LoanResponse>> {
        val response = loanService.applyForLoan(UUID.fromString(userId), request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Loan application submitted successfully", response))
    }
    
    @GetMapping("/{loanId}")
    fun getLoan(
        @PathVariable loanId: UUID
    ): ResponseEntity<ApiResponse<LoanResponse>> {
        val loan = loanService.getLoanById(loanId)
        return ResponseEntity.ok(ApiResponse.success("Loan retrieved successfully", loan))
    }
    
    @GetMapping
    fun getLoans(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<LoanResponse>>> {
        val loans = loanService.getLoansByUserId(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans))
    }
    
    @PostMapping("/{loanId}/approve")
    fun approveLoan(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: LoanApprovalRequest
    ): ResponseEntity<ApiResponse<LoanResponse>> {
        val loan = loanService.approveLoan(loanId, request)
        return ResponseEntity.ok(ApiResponse.success("Loan approved successfully", loan))
    }
    
    @PostMapping("/{loanId}/reject")
    fun rejectLoan(
        @PathVariable loanId: UUID,
        @RequestParam reason: String
    ): ResponseEntity<ApiResponse<LoanResponse>> {
        val loan = loanService.rejectLoan(loanId, reason)
        return ResponseEntity.ok(ApiResponse.success("Loan rejected successfully", loan))
    }
    
    @PostMapping("/{loanId}/disburse")
    fun disburseLoan(
        @PathVariable loanId: UUID
    ): ResponseEntity<ApiResponse<LoanResponse>> {
        val loan = loanService.disburseLoan(loanId)
        return ResponseEntity.ok(ApiResponse.success("Loan disbursed successfully", loan))
    }
    
    @PostMapping("/{loanId}/payment")
    fun makePayment(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: LoanPaymentRequest
    ): ResponseEntity<ApiResponse<LoanPaymentResponse>> {
        val payment = loanService.makePayment(loanId, request)
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", payment))
    }
    
    @GetMapping("/summary")
    fun getLoanSummary(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<LoanSummaryResponse>> {
        val summary = loanService.getLoanSummary(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Loan summary retrieved successfully", summary))
    }
    
    @GetMapping("/upcoming-payments")
    fun getUpcomingPayments(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<ApiResponse<List<LoanPaymentScheduleResponse>>> {
        val payments = loanService.getUpcomingPayments(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Upcoming payments retrieved successfully", payments))
    }
    
    @PostMapping("/calculator")
    fun calculateLoan(
        @Valid @RequestBody request: LoanCalculatorRequest
    ): ResponseEntity<ApiResponse<LoanCalculatorResponse>> {
        // Calculate loan details
        val monthlyRate = request.interestRate.divide(BigDecimal("1200"), 10, RoundingMode.HALF_UP)
        val factor = BigDecimal.ONE.add(monthlyRate).pow(request.termInMonths)
        
        val monthlyPayment = if (request.interestRate == BigDecimal.ZERO) {
            request.principalAmount.divide(BigDecimal(request.termInMonths), 2, RoundingMode.HALF_UP)
        } else {
            request.principalAmount.multiply(monthlyRate).multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP)
        }
        
        val totalPayment = monthlyPayment.multiply(BigDecimal(request.termInMonths))
        val totalInterest = totalPayment.subtract(request.principalAmount)
        
        // Generate payment schedule
        val paymentSchedule = mutableListOf<PaymentScheduleItem>()
        var remainingBalance = request.principalAmount
        
        for (i in 1..request.termInMonths) {
            val interestPayment = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP)
            val principalPayment = monthlyPayment.subtract(interestPayment)
            remainingBalance = remainingBalance.subtract(principalPayment)
            
            paymentSchedule.add(
                PaymentScheduleItem(
                    paymentNumber = i,
                    paymentDate = LocalDateTime.now().plusMonths(i.toLong()),
                    paymentAmount = monthlyPayment,
                    principalAmount = principalPayment,
                    interestAmount = interestPayment,
                    remainingBalance = remainingBalance.max(BigDecimal.ZERO)
                )
            )
        }
        
        val response = LoanCalculatorResponse(
            principalAmount = request.principalAmount,
            interestRate = request.interestRate,
            termInMonths = request.termInMonths,
            monthlyPayment = monthlyPayment,
            totalPayment = totalPayment,
            totalInterest = totalInterest,
            paymentSchedule = paymentSchedule
        )
        
        return ResponseEntity.ok(ApiResponse.success("Loan calculation completed successfully", response))
    }
    
    @PostMapping("/eligibility")
    fun checkEligibility(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: LoanEligibilityRequest
    ): ResponseEntity<ApiResponse<LoanEligibilityResponse>> {
        // Simplified eligibility check
        val monthlyIncome = request.annualIncome.divide(BigDecimal("12"), 2, RoundingMode.HALF_UP)
        val debtToIncomeRatio = request.monthlyExpenses.divide(monthlyIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal("100"))
        
        val isEligible = debtToIncomeRatio <= BigDecimal("40") && // DTI ratio <= 40%
                request.employmentDurationMonths >= 6 && // At least 6 months employment
                request.annualIncome >= BigDecimal("20000") // Minimum income
        
        val maxLoanAmount = if (isEligible) {
            monthlyIncome.multiply(BigDecimal("5")).min(request.requestedAmount) // 5x monthly income or requested amount, whichever is lower
        } else null
        
        val estimatedInterestRate = when (request.loanType) {
            LoanType.PERSONAL -> BigDecimal("8.5")
            LoanType.MORTGAGE -> BigDecimal("3.5")
            LoanType.AUTO -> BigDecimal("5.5")
            LoanType.BUSINESS -> BigDecimal("7.0")
            LoanType.STUDENT -> BigDecimal("4.5")
        }
        
        val reasons = mutableListOf<String>()
        val requirements = mutableListOf<String>()
        
        if (!isEligible) {
            if (debtToIncomeRatio > BigDecimal("40")) {
                reasons.add("Debt-to-income ratio too high (${debtToIncomeRatio}%)")
                requirements.add("Reduce monthly expenses or increase income")
            }
            if (request.employmentDurationMonths < 6) {
                reasons.add("Insufficient employment history")
                requirements.add("Minimum 6 months of employment required")
            }
            if (request.annualIncome < BigDecimal("20000")) {
                reasons.add("Income below minimum threshold")
                requirements.add("Minimum annual income of £20,000 required")
            }
        }
        
        val response = LoanEligibilityResponse(
            isEligible = isEligible,
            maxLoanAmount = maxLoanAmount,
            estimatedInterestRate = if (isEligible) estimatedInterestRate else null,
            recommendedTerm = if (isEligible) 60 else null, // 5 years
            estimatedMonthlyPayment = if (isEligible && maxLoanAmount != null) {
                // Simple calculation for estimation
                val monthlyRate = estimatedInterestRate.divide(BigDecimal("1200"), 10, RoundingMode.HALF_UP)
                val factor = BigDecimal.ONE.add(monthlyRate).pow(60)
                maxLoanAmount.multiply(monthlyRate).multiply(factor)
                    .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP)
            } else null,
            creditScore = (650..850).random(), // Simulated credit score
            debtToIncomeRatio = debtToIncomeRatio,
            reasons = reasons,
            requirements = requirements
        )
        
        return ResponseEntity.ok(ApiResponse.success("Eligibility check completed successfully", response))
    }
    
    @PostMapping("/{loanId}/refinance")
    fun refinanceLoan(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: LoanRefinanceRequest
    ): ResponseEntity<ApiResponse<LoanRefinanceResponse>> {
        // In a real implementation, this would process the refinancing
        val originalLoan = loanService.getLoanById(loanId)
        
        val newTermInMonths = request.newTermInMonths ?: originalLoan.termInMonths
        val newMonthlyPayment = calculateMonthlyPayment(
            originalLoan.outstandingBalance,
            request.newInterestRate,
            newTermInMonths
        )
        
        val monthlySavings = originalLoan.monthlyPayment.subtract(newMonthlyPayment)
        val totalSavings = monthlySavings.multiply(BigDecimal(newTermInMonths))
        val processingFee = originalLoan.outstandingBalance.multiply(BigDecimal("0.01")) // 1% processing fee
        val netSavings = totalSavings.subtract(processingFee)
        
        val response = LoanRefinanceResponse(
            originalLoanId = loanId,
            newLoanId = UUID.randomUUID(),
            originalInterestRate = originalLoan.interestRate,
            newInterestRate = request.newInterestRate,
            originalMonthlyPayment = originalLoan.monthlyPayment,
            newMonthlyPayment = newMonthlyPayment,
            monthlySavings = monthlySavings,
            totalSavings = totalSavings,
            processingFee = processingFee,
            netSavings = netSavings,
            effectiveDate = LocalDateTime.now().plusDays(7)
        )
        
        return ResponseEntity.ok(ApiResponse.success("Loan refinance processed successfully", response))
    }
    
    @PostMapping("/{loanId}/modification")
    fun requestModification(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: LoanModificationRequest
    ): ResponseEntity<ApiResponse<LoanModificationResponse>> {
        // In a real implementation, this would create a modification request
        val originalLoan = loanService.getLoanById(loanId)
        
        val originalTerms = LoanTerms(
            monthlyPayment = originalLoan.monthlyPayment,
            interestRate = originalLoan.interestRate,
            termInMonths = originalLoan.termInMonths,
            outstandingBalance = originalLoan.outstandingBalance
        )
        
        val proposedTerms = when (request.modificationType) {
            ModificationType.PAYMENT_REDUCTION -> {
                val newPayment = request.newPaymentAmount ?: originalLoan.monthlyPayment.multiply(BigDecimal("0.8"))
                LoanTerms(
                    monthlyPayment = newPayment,
                    interestRate = originalLoan.interestRate,
                    termInMonths = calculateNewTerm(originalLoan.outstandingBalance, originalLoan.interestRate, newPayment),
                    outstandingBalance = originalLoan.outstandingBalance
                )
            }
            ModificationType.TERM_EXTENSION -> {
                val newTerm = request.newTermInMonths ?: (originalLoan.termInMonths + 12)
                val newPayment = calculateMonthlyPayment(originalLoan.outstandingBalance, originalLoan.interestRate, newTerm)
                LoanTerms(
                    monthlyPayment = newPayment,
                    interestRate = originalLoan.interestRate,
                    termInMonths = newTerm,
                    outstandingBalance = originalLoan.outstandingBalance
                )
            }
            else -> originalTerms
        }
        
        val response = LoanModificationResponse(
            modificationId = UUID.randomUUID(),
            loanId = loanId,
            modificationType = request.modificationType,
            status = "PENDING",
            originalTerms = originalTerms,
            proposedTerms = proposedTerms,
            reason = request.reason,
            applicationDate = LocalDateTime.now(),
            reviewDate = null,
            effectiveDate = null
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Loan modification request submitted successfully", response))
    }
    
    @PostMapping("/{loanId}/insurance")
    fun addInsurance(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: LoanInsuranceRequest
    ): ResponseEntity<ApiResponse<LoanInsuranceResponse>> {
        // In a real implementation, this would add insurance to the loan
        val loan = loanService.getLoanById(loanId)
        val coverageAmount = request.coverageAmount ?: loan.outstandingBalance
        
        val monthlyPremium = when (request.insuranceType) {
            InsuranceType.LIFE_INSURANCE -> coverageAmount.multiply(BigDecimal("0.001")) // 0.1% of coverage
            InsuranceType.DISABILITY_INSURANCE -> coverageAmount.multiply(BigDecimal("0.002")) // 0.2% of coverage
            InsuranceType.UNEMPLOYMENT_INSURANCE -> coverageAmount.multiply(BigDecimal("0.0015")) // 0.15% of coverage
            InsuranceType.COMPREHENSIVE -> coverageAmount.multiply(BigDecimal("0.003")) // 0.3% of coverage
        }
        
        val response = LoanInsuranceResponse(
            insuranceId = UUID.randomUUID(),
            loanId = loanId,
            insuranceType = request.insuranceType,
            coverageAmount = coverageAmount,
            monthlyPremium = monthlyPremium,
            beneficiaryName = request.beneficiaryName,
            beneficiaryRelationship = request.beneficiaryRelationship,
            isActive = true,
            startDate = LocalDateTime.now(),
            endDate = loan.maturityDate
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Loan insurance added successfully", response))
    }
    
    @PostMapping("/{loanId}/statement")
    fun generateStatement(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: LoanStatementRequest
    ): ResponseEntity<ApiResponse<LoanStatementResponse>> {
        // In a real implementation, this would generate and return a statement
        val statement = LoanStatementResponse(
            statementId = UUID.randomUUID(),
            loanId = loanId,
            format = request.format,
            downloadUrl = "/api/loans/$loanId/statement/download",
            emailSent = request.email != null,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(7)
        )
        
        return ResponseEntity.ok(ApiResponse.success("Loan statement generated successfully", statement))
    }
    
    @GetMapping("/{loanId}/analytics")
    fun getLoanAnalytics(
        @PathVariable loanId: UUID
    ): ResponseEntity<ApiResponse<LoanAnalyticsResponse>> {
        // In a real implementation, this would calculate actual loan analytics
        val loan = loanService.getLoanById(loanId)
        
        val analytics = LoanAnalyticsResponse(
            loanId = loanId,
            totalPaid = BigDecimal.ZERO, // Would be calculated from payment history
            principalPaid = BigDecimal.ZERO,
            interestPaid = BigDecimal.ZERO,
            remainingPrincipal = loan.outstandingBalance,
            remainingInterest = BigDecimal.ZERO, // Would be calculated
            paymentsRemaining = loan.termInMonths, // Simplified
            monthsRemaining = loan.termInMonths,
            paymentHistory = emptyList(),
            projectedPayoffDate = loan.maturityDate,
            earlyPayoffSavings = null
        )
        
        return ResponseEntity.ok(ApiResponse.success("Loan analytics retrieved successfully", analytics))
    }
    
    @PostMapping("/compare")
    fun compareLoans(
        @Valid @RequestBody request: LoanComparisonRequest
    ): ResponseEntity<ApiResponse<LoanComparisonResponse>> {
        // In a real implementation, this would analyze and compare loan offers
        val comparisons = request.loanOffers.map { offer ->
            val monthlyPayment = calculateMonthlyPayment(offer.principalAmount, offer.interestRate, offer.termInMonths)
            val totalPayment = monthlyPayment.multiply(BigDecimal(offer.termInMonths))
            val totalInterest = totalPayment.subtract(offer.principalAmount)
            val totalCost = totalPayment.add(offer.fees)
            
            // Calculate APR (simplified)
            val apr = offer.interestRate.add(offer.fees.divide(offer.principalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal("100")))
            
            LoanOfferAnalysis(
                lenderName = offer.lenderName,
                monthlyPayment = monthlyPayment,
                totalPayment = totalPayment,
                totalInterest = totalInterest,
                totalCost = totalCost,
                apr = apr,
                pros = offer.features,
                cons = emptyList(), // Would be determined based on analysis
                rating = if (apr <= BigDecimal("5")) "EXCELLENT" 
                        else if (apr <= BigDecimal("8")) "GOOD" 
                        else if (apr <= BigDecimal("12")) "FAIR" 
                        else "POOR"
            )
        }
        
        val response = LoanComparisonResponse(
            comparisons = comparisons,
            bestOverallOffer = comparisons.minByOrNull { it.totalCost }?.lenderName ?: "",
            lowestRateOffer = comparisons.minByOrNull { it.apr }?.lenderName ?: "",
            lowestPaymentOffer = comparisons.minByOrNull { it.monthlyPayment }?.lenderName ?: "",
            lowestTotalCostOffer = comparisons.minByOrNull { it.totalCost }?.lenderName ?: ""
        )
        
        return ResponseEntity.ok(ApiResponse.success("Loan comparison completed successfully", response))
    }
    
    @PostMapping("/{loanId}/early-payment")
    fun calculateEarlyPayment(
        @PathVariable loanId: UUID,
        @Valid @RequestBody request: EarlyPaymentRequest
    ): ResponseEntity<ApiResponse<EarlyPaymentResponse>> {
        // In a real implementation, this would calculate the impact of early payments
        val loan = loanService.getLoanById(loanId)
        
        val newBalance = if (request.applyToPrincipal) {
            loan.outstandingBalance.subtract(request.additionalPayment)
        } else {
            loan.outstandingBalance // If not applied to principal, balance remains same
        }
        
        // Simplified calculation - in reality would need complex amortization calculations
        val interestSaved = request.additionalPayment.multiply(loan.interestRate.divide(BigDecimal("100"), 4, RoundingMode.HALF_UP))
        val monthsSaved = (request.additionalPayment.divide(loan.monthlyPayment, 0, RoundingMode.DOWN)).toInt()
        
        val response = EarlyPaymentResponse(
            loanId = loanId,
            additionalPayment = request.additionalPayment,
            newOutstandingBalance = newBalance,
            interestSaved = interestSaved,
            timeSaved = "$monthsSaved months",
            newPayoffDate = loan.maturityDate.minusMonths(monthsSaved.toLong()),
            totalSavings = interestSaved
        )
        
        return ResponseEntity.ok(ApiResponse.success("Early payment calculation completed successfully", response))
    }
    
    private fun calculateMonthlyPayment(principal: BigDecimal, annualRate: BigDecimal, termInMonths: Int): BigDecimal {
        if (annualRate == BigDecimal.ZERO) {
            return principal.divide(BigDecimal(termInMonths), 2, RoundingMode.HALF_UP)
        }
        
        val monthlyRate = annualRate.divide(BigDecimal("1200"), 10, RoundingMode.HALF_UP)
        val factor = BigDecimal.ONE.add(monthlyRate).pow(termInMonths)
        
        return principal.multiply(monthlyRate).multiply(factor)
            .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP)
    }
    
    private fun calculateNewTerm(principal: BigDecimal, annualRate: BigDecimal, monthlyPayment: BigDecimal): Int {
        // Simplified calculation - in reality would use logarithmic formula
        val monthlyRate = annualRate.divide(BigDecimal("1200"), 10, RoundingMode.HALF_UP)
        val ratio = monthlyPayment.divide(principal.multiply(monthlyRate), 10, RoundingMode.HALF_UP)
        
        // Approximate calculation
        return (principal.divide(monthlyPayment, 0, RoundingMode.UP)).toInt().coerceAtMost(360)
    }
}