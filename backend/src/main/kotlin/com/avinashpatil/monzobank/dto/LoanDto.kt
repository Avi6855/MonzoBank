package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.LoanStatus
import com.avinashpatil.monzobank.entity.LoanType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class LoanApplicationRequest(
    @field:NotNull(message = "Loan type is required")
    val loanType: LoanType,
    
    @field:NotNull(message = "Principal amount is required")
    @field:DecimalMin(value = "100.00", message = "Principal amount must be at least £100")
    @field:DecimalMax(value = "1000000.00", message = "Principal amount cannot exceed £1,000,000")
    @field:Digits(integer = 10, fraction = 2, message = "Principal amount must have at most 2 decimal places")
    val principalAmount: BigDecimal,
    
    @field:NotNull(message = "Term in months is required")
    @field:Min(value = 6, message = "Term must be at least 6 months")
    @field:Max(value = 360, message = "Term cannot exceed 360 months (30 years)")
    val termInMonths: Int,
    
    @field:NotBlank(message = "Purpose is required")
    @field:Size(max = 500, message = "Purpose must not exceed 500 characters")
    val purpose: String,
    
    @field:DecimalMin(value = "0.00", message = "Annual income cannot be negative")
    @field:Digits(integer = 10, fraction = 2, message = "Annual income must have at most 2 decimal places")
    val annualIncome: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Monthly expenses cannot be negative")
    @field:Digits(integer = 10, fraction = 2, message = "Monthly expenses must have at most 2 decimal places")
    val monthlyExpenses: BigDecimal? = null,
    
    @field:Size(max = 100, message = "Employment status must not exceed 100 characters")
    val employmentStatus: String? = null,
    
    @field:Size(max = 200, message = "Employer name must not exceed 200 characters")
    val employerName: String? = null,
    
    @field:Min(value = 0, message = "Employment duration cannot be negative")
    val employmentDurationMonths: Int? = null
)

data class LoanResponse(
    val id: UUID,
    val userId: UUID,
    val loanType: LoanType,
    val principalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val termInMonths: Int,
    val monthlyPayment: BigDecimal,
    val outstandingBalance: BigDecimal,
    val status: LoanStatus,
    val purpose: String,
    val applicationDate: LocalDateTime,
    val approvalDate: LocalDateTime?,
    val disbursementDate: LocalDateTime?,
    val maturityDate: LocalDateTime,
    val nextPaymentDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class LoanApprovalRequest(
    @field:DecimalMin(value = "100.00", message = "Approved amount must be at least £100")
    @field:Digits(integer = 10, fraction = 2, message = "Approved amount must have at most 2 decimal places")
    val approvedAmount: BigDecimal? = null,
    
    @field:DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    @field:DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50%")
    @field:Digits(integer = 2, fraction = 2, message = "Interest rate must have at most 2 decimal places")
    val approvedInterestRate: BigDecimal? = null,
    
    @field:Min(value = 6, message = "Term must be at least 6 months")
    @field:Max(value = 360, message = "Term cannot exceed 360 months")
    val approvedTermInMonths: Int? = null,
    
    @field:Size(max = 500, message = "Approval notes must not exceed 500 characters")
    val approvalNotes: String? = null
)

data class LoanPaymentRequest(
    @field:NotNull(message = "Payment amount is required")
    @field:DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Payment amount must have at most 2 decimal places")
    val amount: BigDecimal,
    
    @field:Size(max = 255, message = "Payment reference must not exceed 255 characters")
    val paymentReference: String? = null
)

data class LoanPaymentResponse(
    val paymentId: UUID,
    val loanId: UUID,
    val amount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val remainingBalance: BigDecimal,
    val paymentDate: LocalDateTime,
    val nextPaymentDate: LocalDateTime?,
    val status: String
)

data class LoanSummaryResponse(
    val totalActiveLoans: Long,
    val totalOutstandingBalance: BigDecimal,
    val totalMonthlyPayments: BigDecimal,
    val totalLoanAmount: BigDecimal,
    val upcomingPayments: Long,
    val nextPaymentDate: LocalDateTime?,
    val averageInterestRate: BigDecimal
)

data class LoanPaymentScheduleResponse(
    val loanId: UUID,
    val loanType: LoanType,
    val paymentAmount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val paymentDate: LocalDateTime,
    val remainingBalance: BigDecimal,
    val paymentNumber: Int
)

data class LoanCalculatorRequest(
    @field:NotNull(message = "Principal amount is required")
    @field:DecimalMin(value = "100.00", message = "Principal amount must be at least £100")
    @field:Digits(integer = 10, fraction = 2, message = "Principal amount must have at most 2 decimal places")
    val principalAmount: BigDecimal,
    
    @field:NotNull(message = "Interest rate is required")
    @field:DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    @field:DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50%")
    @field:Digits(integer = 2, fraction = 2, message = "Interest rate must have at most 2 decimal places")
    val interestRate: BigDecimal,
    
    @field:NotNull(message = "Term in months is required")
    @field:Min(value = 1, message = "Term must be at least 1 month")
    @field:Max(value = 360, message = "Term cannot exceed 360 months")
    val termInMonths: Int
)

data class LoanCalculatorResponse(
    val principalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val termInMonths: Int,
    val monthlyPayment: BigDecimal,
    val totalPayment: BigDecimal,
    val totalInterest: BigDecimal,
    val paymentSchedule: List<PaymentScheduleItem>
)

data class PaymentScheduleItem(
    val paymentNumber: Int,
    val paymentDate: LocalDateTime,
    val paymentAmount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val remainingBalance: BigDecimal
)

data class LoanEligibilityRequest(
    @field:NotNull(message = "Loan type is required")
    val loanType: LoanType,
    
    @field:NotNull(message = "Requested amount is required")
    @field:DecimalMin(value = "100.00", message = "Requested amount must be at least £100")
    val requestedAmount: BigDecimal,
    
    @field:NotNull(message = "Annual income is required")
    @field:DecimalMin(value = "0.00", message = "Annual income cannot be negative")
    val annualIncome: BigDecimal,
    
    @field:NotNull(message = "Monthly expenses are required")
    @field:DecimalMin(value = "0.00", message = "Monthly expenses cannot be negative")
    val monthlyExpenses: BigDecimal,
    
    @field:NotBlank(message = "Employment status is required")
    val employmentStatus: String,
    
    @field:Min(value = 0, message = "Employment duration cannot be negative")
    val employmentDurationMonths: Int,
    
    @field:DecimalMin(value = "0.00", message = "Existing debt cannot be negative")
    val existingDebt: BigDecimal? = null
)

data class LoanEligibilityResponse(
    val isEligible: Boolean,
    val maxLoanAmount: BigDecimal?,
    val estimatedInterestRate: BigDecimal?,
    val recommendedTerm: Int?,
    val estimatedMonthlyPayment: BigDecimal?,
    val creditScore: Int?,
    val debtToIncomeRatio: BigDecimal,
    val reasons: List<String>,
    val requirements: List<String>
)

data class LoanRefinanceRequest(
    @field:NotNull(message = "Current loan ID is required")
    val currentLoanId: UUID,
    
    @field:NotNull(message = "New interest rate is required")
    @field:DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    val newInterestRate: BigDecimal,
    
    @field:Min(value = 6, message = "New term must be at least 6 months")
    val newTermInMonths: Int? = null,
    
    @field:Size(max = 500, message = "Reason must not exceed 500 characters")
    val reason: String? = null
)

data class LoanRefinanceResponse(
    val originalLoanId: UUID,
    val newLoanId: UUID,
    val originalInterestRate: BigDecimal,
    val newInterestRate: BigDecimal,
    val originalMonthlyPayment: BigDecimal,
    val newMonthlyPayment: BigDecimal,
    val monthlySavings: BigDecimal,
    val totalSavings: BigDecimal,
    val processingFee: BigDecimal,
    val netSavings: BigDecimal,
    val effectiveDate: LocalDateTime
)

data class LoanModificationRequest(
    @field:NotNull(message = "Loan ID is required")
    val loanId: UUID,
    
    @field:NotNull(message = "Modification type is required")
    val modificationType: ModificationType,
    
    @field:Size(max = 1000, message = "Reason must not exceed 1000 characters")
    val reason: String,
    
    val newPaymentAmount: BigDecimal? = null,
    val newTermInMonths: Int? = null,
    val defermentMonths: Int? = null
)

enum class ModificationType {
    PAYMENT_REDUCTION, TERM_EXTENSION, PAYMENT_DEFERMENT, INTEREST_RATE_REDUCTION
}

data class LoanModificationResponse(
    val modificationId: UUID,
    val loanId: UUID,
    val modificationType: ModificationType,
    val status: String, // PENDING, APPROVED, REJECTED
    val originalTerms: LoanTerms,
    val proposedTerms: LoanTerms,
    val reason: String,
    val applicationDate: LocalDateTime,
    val reviewDate: LocalDateTime?,
    val effectiveDate: LocalDateTime?
)

data class LoanTerms(
    val monthlyPayment: BigDecimal,
    val interestRate: BigDecimal,
    val termInMonths: Int,
    val outstandingBalance: BigDecimal
)

data class LoanInsuranceRequest(
    @field:NotNull(message = "Loan ID is required")
    val loanId: UUID,
    
    @field:NotNull(message = "Insurance type is required")
    val insuranceType: InsuranceType,
    
    val coverageAmount: BigDecimal? = null,
    val beneficiaryName: String? = null,
    val beneficiaryRelationship: String? = null
)

enum class InsuranceType {
    LIFE_INSURANCE, DISABILITY_INSURANCE, UNEMPLOYMENT_INSURANCE, COMPREHENSIVE
}

data class LoanInsuranceResponse(
    val insuranceId: UUID,
    val loanId: UUID,
    val insuranceType: InsuranceType,
    val coverageAmount: BigDecimal,
    val monthlyPremium: BigDecimal,
    val beneficiaryName: String?,
    val beneficiaryRelationship: String?,
    val isActive: Boolean,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?
)

data class LoanStatementRequest(
    @field:NotNull(message = "Loan ID is required")
    val loanId: UUID,
    
    @field:NotNull(message = "Start date is required")
    val startDate: LocalDateTime,
    
    @field:NotNull(message = "End date is required")
    val endDate: LocalDateTime,
    
    val format: StatementFormat = StatementFormat.PDF,
    
    @field:Email(message = "Valid email is required")
    val email: String? = null
)

data class LoanStatementResponse(
    val statementId: UUID,
    val loanId: UUID,
    val format: StatementFormat,
    val downloadUrl: String,
    val emailSent: Boolean,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
)

data class LoanAnalyticsResponse(
    val loanId: UUID,
    val totalPaid: BigDecimal,
    val principalPaid: BigDecimal,
    val interestPaid: BigDecimal,
    val remainingPrincipal: BigDecimal,
    val remainingInterest: BigDecimal,
    val paymentsRemaining: Int,
    val monthsRemaining: Int,
    val paymentHistory: List<PaymentHistoryItem>,
    val projectedPayoffDate: LocalDateTime,
    val earlyPayoffSavings: BigDecimal?
)

data class PaymentHistoryItem(
    val paymentDate: LocalDateTime,
    val amount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val balanceAfter: BigDecimal,
    val status: String
)

data class LoanComparisonRequest(
    val loanOffers: List<LoanOfferComparison>
)

data class LoanOfferComparison(
    val lenderName: String,
    val principalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val termInMonths: Int,
    val fees: BigDecimal,
    val features: List<String>
)

data class LoanComparisonResponse(
    val comparisons: List<LoanOfferAnalysis>,
    val bestOverallOffer: String,
    val lowestRateOffer: String,
    val lowestPaymentOffer: String,
    val lowestTotalCostOffer: String
)

data class LoanOfferAnalysis(
    val lenderName: String,
    val monthlyPayment: BigDecimal,
    val totalPayment: BigDecimal,
    val totalInterest: BigDecimal,
    val totalCost: BigDecimal, // Including fees
    val apr: BigDecimal, // Annual Percentage Rate
    val pros: List<String>,
    val cons: List<String>,
    val rating: String // EXCELLENT, GOOD, FAIR, POOR
)

data class EarlyPaymentRequest(
    @field:NotNull(message = "Loan ID is required")
    val loanId: UUID,
    
    @field:NotNull(message = "Payment amount is required")
    @field:DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    val additionalPayment: BigDecimal,
    
    val applyToPrincipal: Boolean = true
)

data class EarlyPaymentResponse(
    val loanId: UUID,
    val additionalPayment: BigDecimal,
    val newOutstandingBalance: BigDecimal,
    val interestSaved: BigDecimal,
    val timeSaved: String, // e.g., "2 years 3 months"
    val newPayoffDate: LocalDateTime,
    val totalSavings: BigDecimal
)