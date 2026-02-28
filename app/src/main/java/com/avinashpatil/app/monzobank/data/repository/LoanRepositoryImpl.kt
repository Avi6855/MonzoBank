package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import com.avinashpatil.app.monzobank.domain.model.PaymentStatus
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class LoanRepositoryImpl @Inject constructor() : LoanRepository {
    
    private val applications = mutableListOf<LoanApplication>()
    private val loans = mutableListOf<Loan>()
    private val payments = mutableListOf<LoanPayment>()
    
    override suspend fun submitLoanApplication(application: LoanApplication): Result<String> {
        return try {
            applications.add(application)
            Result.success(application.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoanApplications(userId: String): Result<List<LoanApplication>> {
        return try {
            val userApplications = applications.filter { it.userId == userId }
            Result.success(userApplications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateApplicationStatus(applicationId: String, status: LoanApplicationStatus): Result<Unit> {
        return try {
            val index = applications.indexOfFirst { it.id == applicationId }
            if (index != -1) {
                val application = applications[index]
                val updated = application.copy(
                    status = status,
                    processedAt = LocalDateTime.now(),
                    approvedAmount = if (status == LoanApplicationStatus.APPROVED) 
                        application.requestedAmount else null,
                    approvedRate = if (status == LoanApplicationStatus.APPROVED) 
                        BigDecimal("8.5") else null
                )
                applications[index] = updated
                
                // Create loan if approved
                if (status == LoanApplicationStatus.APPROVED && updated.approvedAmount != null) {
                    val monthlyPayment = calculateMonthlyPayment(
                        updated.approvedAmount,
                        updated.approvedRate ?: BigDecimal("8.5"),
                        updated.termMonths
                    ).getOrNull() ?: BigDecimal.ZERO
                    
                    val loan = Loan(
                        id = UUID.randomUUID().toString(),
                        userId = updated.userId,
                        loanType = updated.loanType,
                        principalAmount = updated.approvedAmount,
                        currentBalance = updated.approvedAmount,
                        interestRate = updated.approvedRate ?: BigDecimal("8.5"),
                        termMonths = updated.termMonths,
                        monthlyPayment = monthlyPayment,
                        startDate = LocalDateTime.now(),
                        maturityDate = LocalDateTime.now().plusMonths(updated.termMonths.toLong()),
                        status = LoanStatus.ACTIVE,
                        nextPaymentDate = LocalDateTime.now().plusMonths(1)
                    )
                    loans.add(loan)
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Application not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createLoan(loan: Loan): Result<String> {
        return try {
            loans.add(loan)
            Result.success(loan.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoans(userId: String): Result<List<Loan>> {
        return try {
            val userLoans = loans.filter { it.userId == userId }
            Result.success(userLoans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoan(loanId: String): Result<Loan?> {
        return try {
            val loan = loans.find { it.id == loanId }
            Result.success(loan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun makePayment(payment: LoanPayment): Result<Unit> {
        return try {
            payments.add(payment)
            
            // Update loan balance
            val loanIndex = loans.indexOfFirst { it.id == payment.loanId }
            if (loanIndex != -1) {
                val loan = loans[loanIndex]
                val newBalance = loan.currentBalance - payment.principalAmount
                val nextPaymentDate = if (newBalance <= BigDecimal.ZERO) {
                    null
                } else {
                    loan.nextPaymentDate?.plusMonths(1)
                }
                
                val updatedLoan = loan.copy(
                    currentBalance = newBalance.max(BigDecimal.ZERO),
                    status = if (newBalance <= BigDecimal.ZERO) LoanStatus.PAID_OFF else loan.status,
                    nextPaymentDate = nextPaymentDate
                )
                loans[loanIndex] = updatedLoan
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPaymentHistory(loanId: String): Result<List<LoanPayment>> {
        return try {
            val loanPayments = payments.filter { it.loanId == loanId }
                .sortedByDescending { it.paymentDate }
            Result.success(loanPayments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generatePaymentSchedule(loanId: String): Result<LoanSchedule> {
        return try {
            val loan = loans.find { it.id == loanId }
                ?: return Result.failure(Exception("Loan not found"))
            
            val monthlyRate = loan.interestRate.divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
            
            val scheduledPayments = mutableListOf<ScheduledPayment>()
            var remainingBalance = loan.principalAmount
            var currentDate = loan.startDate.plusMonths(1)
            
            for (paymentNumber in 1..loan.termMonths) {
                val interestPayment = remainingBalance.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP)
                val principalPayment = loan.monthlyPayment.subtract(interestPayment)
                    .setScale(2, RoundingMode.HALF_UP)
                
                remainingBalance = remainingBalance.subtract(principalPayment)
                if (remainingBalance < BigDecimal.ZERO) {
                    remainingBalance = BigDecimal.ZERO
                }
                
                val scheduledPayment = ScheduledPayment(
                    paymentNumber = paymentNumber,
                    dueDate = currentDate,
                    paymentAmount = loan.monthlyPayment,
                    principalAmount = principalPayment,
                    interestAmount = interestPayment,
                    remainingBalance = remainingBalance,
                    isPaid = false // Would check against actual payments
                )
                
                scheduledPayments.add(scheduledPayment)
                currentDate = currentDate.plusMonths(1)
            }
            
            val schedule = LoanSchedule(
                loanId = loanId,
                payments = scheduledPayments
            )
            
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateMonthlyPayment(principal: BigDecimal, rate: BigDecimal, termMonths: Int): Result<BigDecimal> {
        return try {
            if (rate == BigDecimal.ZERO) {
                // No interest loan
                val payment = principal.divide(BigDecimal(termMonths), 2, RoundingMode.HALF_UP)
                Result.success(payment)
            } else {
                val monthlyRate = rate.divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
                    .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
                
                val rateDouble = monthlyRate.toDouble()
                val termDouble = termMonths.toDouble()
                
                // PMT formula: P * [r(1+r)^n] / [(1+r)^n - 1]
                val numerator = rateDouble * (1 + rateDouble).pow(termDouble)
                val denominator = (1 + rateDouble).pow(termDouble) - 1
                val paymentFactor = numerator / denominator
                
                val monthlyPayment = principal.multiply(BigDecimal(paymentFactor))
                    .setScale(2, RoundingMode.HALF_UP)
                
                Result.success(monthlyPayment)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOverdueLoans(): Result<List<Loan>> {
        return try {
            val now = LocalDateTime.now()
            val overdueLoans = loans.filter { loan ->
                loan.status == LoanStatus.ACTIVE && 
                loan.nextPaymentDate != null && 
                loan.nextPaymentDate.isBefore(now)
            }
            Result.success(overdueLoans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateLoanStatus(loanId: String, status: LoanStatus): Result<Unit> {
        return try {
            val index = loans.indexOfFirst { it.id == loanId }
            if (index != -1) {
                val updated = loans[index].copy(status = status)
                loans[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Loan not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLoanOffers(userId: String): Result<List<LoanOffer>> {
        return try {
            // Mock loan offers based on user profile
            val offers = listOf(
                LoanOffer(
                    id = UUID.randomUUID().toString(),
                    loanType = LoanType.PERSONAL,
                    maxAmount = BigDecimal("50000"),
                    minInterestRate = BigDecimal("6.99"),
                    maxInterestRate = BigDecimal("12.99"),
                    minTermMonths = 12,
                    maxTermMonths = 60,
                    requirements = listOf("Credit score 650+", "Stable income"),
                    validUntil = LocalDateTime.now().plusDays(30)
                ),
                LoanOffer(
                    id = UUID.randomUUID().toString(),
                    loanType = LoanType.AUTO,
                    maxAmount = BigDecimal("75000"),
                    minInterestRate = BigDecimal("4.99"),
                    maxInterestRate = BigDecimal("8.99"),
                    minTermMonths = 24,
                    maxTermMonths = 72,
                    requirements = listOf("Vehicle as collateral", "Insurance required"),
                    validUntil = LocalDateTime.now().plusDays(30)
                )
            )
            Result.success(offers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateEarlyPayoffAmount(loanId: String): Result<BigDecimal> {
        return try {
            val loan = loans.find { it.id == loanId }
                ?: return Result.failure(Exception("Loan not found"))
            
            // Simple early payoff calculation (current balance + small penalty)
            val penalty = loan.currentBalance.multiply(BigDecimal("0.02")) // 2% penalty
            val payoffAmount = loan.currentBalance.add(penalty)
            
            Result.success(payoffAmount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processEarlyPayoff(loanId: String, amount: BigDecimal): Result<Unit> {
        return try {
            val index = loans.indexOfFirst { it.id == loanId }
            if (index != -1) {
                val loan = loans[index]
                val payoffAmount = calculateEarlyPayoffAmount(loanId).getOrNull()
                    ?: return Result.failure(Exception("Could not calculate payoff amount"))
                
                if (amount >= payoffAmount) {
                    // Process early payoff
                    val payment = LoanPayment(
                        id = UUID.randomUUID().toString(),
                        loanId = loanId,
                        amount = amount,
                        principalAmount = loan.currentBalance,
                        interestAmount = amount.subtract(loan.currentBalance),
                        paymentDate = LocalDateTime.now(),
                        dueDate = LocalDateTime.now(),
                        status = PaymentStatus.COMPLETED,
                        remainingBalance = BigDecimal.ZERO
                    )
                    payments.add(payment)
                    
                    val updatedLoan = loan.copy(
                        currentBalance = BigDecimal.ZERO,
                        status = LoanStatus.PAID_OFF,
                        nextPaymentDate = null
                    )
                    loans[index] = updatedLoan
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Insufficient amount for early payoff"))
                }
            } else {
                Result.failure(Exception("Loan not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}