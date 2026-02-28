package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class MortgageApplication(
    val id: String,
    val userId: String,
    val propertyAddress: String,
    val propertyValue: BigDecimal,
    val loanAmount: BigDecimal,
    val downPayment: BigDecimal,
    val interestRate: BigDecimal,
    val termYears: Int,
    val mortgageType: MortgageType,
    val status: ApplicationStatus,
    val monthlyPayment: BigDecimal,
    val applicationDate: LocalDateTime,
    val approvalDate: LocalDateTime? = null,
    val documents: List<String> = emptyList()
)

data class Mortgage(
    val id: String,
    val userId: String,
    val applicationId: String,
    val propertyAddress: String,
    val originalAmount: BigDecimal,
    val currentBalance: BigDecimal,
    val interestRate: BigDecimal,
    val monthlyPayment: BigDecimal,
    val termYears: Int,
    val remainingPayments: Int,
    val nextPaymentDate: LocalDateTime,
    val maturityDate: LocalDateTime,
    val mortgageType: MortgageType,
    val status: MortgageStatus,
    val escrowBalance: BigDecimal = BigDecimal.ZERO,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class MortgagePayment(
    val id: String,
    val mortgageId: String,
    val paymentAmount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val escrowAmount: BigDecimal = BigDecimal.ZERO,
    val paymentDate: LocalDateTime,
    val dueDate: LocalDateTime,
    val status: MortgagePaymentStatus,
    val remainingBalance: BigDecimal
)

data class PropertyValuation(
    val id: String,
    val propertyAddress: String,
    val currentValue: BigDecimal,
    val valuationDate: LocalDateTime,
    val valuationMethod: String,
    val appraiser: String? = null
)

enum class MortgageType {
    FIXED_RATE,
    ADJUSTABLE_RATE,
    FHA,
    VA,
    USDA,
    JUMBO,
    CONVENTIONAL
}

enum class ApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    DENIED,
    PENDING_DOCUMENTS,
    CLOSED
}

enum class MortgageStatus {
    ACTIVE,
    PAID_OFF,
    IN_DEFAULT,
    FORECLOSURE,
    REFINANCED
}

enum class MortgagePaymentStatus {
    ON_TIME,
    LATE,
    MISSED,
    PARTIAL
}

interface MortgageRepository {
    suspend fun submitApplication(application: MortgageApplication): Result<String>
    suspend fun getApplications(userId: String): Result<List<MortgageApplication>>
    suspend fun updateApplicationStatus(applicationId: String, status: ApplicationStatus): Result<Unit>
    
    suspend fun getMortgages(userId: String): Result<List<Mortgage>>
    suspend fun getMortgage(mortgageId: String): Result<Mortgage?>
    suspend fun createMortgage(mortgage: Mortgage): Result<String>
    suspend fun updateMortgage(mortgage: Mortgage): Result<Unit>
    
    suspend fun makePayment(payment: MortgagePayment): Result<Unit>
    suspend fun getPaymentHistory(mortgageId: String): Result<List<MortgagePayment>>
    suspend fun getUpcomingPayments(userId: String): Result<List<MortgagePayment>>
    
    suspend fun calculateMonthlyPayment(loanAmount: BigDecimal, interestRate: BigDecimal, termYears: Int): Result<BigDecimal>
    suspend fun getAmortizationSchedule(mortgageId: String): Result<List<MortgagePayment>>
    
    suspend fun getPropertyValuation(propertyAddress: String): Result<PropertyValuation?>
    suspend fun requestPropertyValuation(propertyAddress: String): Result<PropertyValuation>
    
    suspend fun getRefinanceOptions(mortgageId: String): Result<List<MortgageApplication>>
    suspend fun calculateRefinanceSavings(mortgageId: String, newRate: BigDecimal): Result<BigDecimal>
}