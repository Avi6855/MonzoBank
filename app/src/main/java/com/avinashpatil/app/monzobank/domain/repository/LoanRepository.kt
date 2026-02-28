package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.PaymentStatus
import java.time.LocalDateTime
import java.math.BigDecimal

data class Loan(
    val id: String,
    val userId: String,
    val loanType: LoanType,
    val principalAmount: BigDecimal,
    val currentBalance: BigDecimal,
    val interestRate: BigDecimal,
    val termMonths: Int,
    val monthlyPayment: BigDecimal,
    val startDate: LocalDateTime,
    val maturityDate: LocalDateTime,
    val status: LoanStatus,
    val nextPaymentDate: LocalDateTime?
)

enum class LoanType {
    PERSONAL, AUTO, MORTGAGE, STUDENT, BUSINESS, PAYDAY, INSTALLMENT
}

enum class LoanStatus {
    ACTIVE, PAID_OFF, DEFAULTED, DELINQUENT, SUSPENDED
}

data class LoanApplication(
    val id: String,
    val userId: String,
    val loanType: LoanType,
    val requestedAmount: BigDecimal,
    val purpose: String,
    val termMonths: Int,
    val status: LoanApplicationStatus,
    val submittedAt: LocalDateTime,
    val processedAt: LocalDateTime?,
    val approvedAmount: BigDecimal?,
    val approvedRate: BigDecimal?
)

enum class LoanApplicationStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, CANCELLED, PENDING_DOCUMENTS
}

data class LoanPayment(
    val id: String,
    val loanId: String,
    val amount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val paymentDate: LocalDateTime,
    val dueDate: LocalDateTime,
    val status: PaymentStatus,
    val remainingBalance: BigDecimal
)

data class LoanSchedule(
    val loanId: String,
    val payments: List<ScheduledPayment>
)

data class ScheduledPayment(
    val paymentNumber: Int,
    val dueDate: LocalDateTime,
    val paymentAmount: BigDecimal,
    val principalAmount: BigDecimal,
    val interestAmount: BigDecimal,
    val remainingBalance: BigDecimal,
    val isPaid: Boolean = false
)

data class LoanOffer(
    val id: String,
    val loanType: LoanType,
    val maxAmount: BigDecimal,
    val minInterestRate: BigDecimal,
    val maxInterestRate: BigDecimal,
    val minTermMonths: Int,
    val maxTermMonths: Int,
    val requirements: List<String>,
    val validUntil: LocalDateTime
)

interface LoanRepository {
    suspend fun submitLoanApplication(application: LoanApplication): Result<String>
    suspend fun getLoanApplications(userId: String): Result<List<LoanApplication>>
    suspend fun updateApplicationStatus(applicationId: String, status: LoanApplicationStatus): Result<Unit>
    suspend fun createLoan(loan: Loan): Result<String>
    suspend fun getLoans(userId: String): Result<List<Loan>>
    suspend fun getLoan(loanId: String): Result<Loan?>
    suspend fun makePayment(payment: LoanPayment): Result<Unit>
    suspend fun getPaymentHistory(loanId: String): Result<List<LoanPayment>>
    suspend fun generatePaymentSchedule(loanId: String): Result<LoanSchedule>
    suspend fun calculateMonthlyPayment(principal: BigDecimal, rate: BigDecimal, termMonths: Int): Result<BigDecimal>
    suspend fun getOverdueLoans(): Result<List<Loan>>
    suspend fun updateLoanStatus(loanId: String, status: LoanStatus): Result<Unit>
    suspend fun getLoanOffers(userId: String): Result<List<LoanOffer>>
    suspend fun calculateEarlyPayoffAmount(loanId: String): Result<BigDecimal>
    suspend fun processEarlyPayoff(loanId: String, amount: BigDecimal): Result<Unit>
}