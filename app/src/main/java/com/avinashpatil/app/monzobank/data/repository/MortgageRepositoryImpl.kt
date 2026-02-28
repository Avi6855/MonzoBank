package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class MortgageRepositoryImpl @Inject constructor() : MortgageRepository {
    
    private val applications = mutableListOf<MortgageApplication>()
    private val mortgages = mutableListOf<Mortgage>()
    private val payments = mutableListOf<MortgagePayment>()
    private val valuations = mutableMapOf<String, PropertyValuation>()
    
    override suspend fun submitApplication(application: MortgageApplication): Result<String> {
        return try {
            applications.add(application)
            Result.success(application.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getApplications(userId: String): Result<List<MortgageApplication>> {
        return try {
            val userApplications = applications.filter { it.userId == userId }
            Result.success(userApplications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateApplicationStatus(applicationId: String, status: ApplicationStatus): Result<Unit> {
        return try {
            val index = applications.indexOfFirst { it.id == applicationId }
            if (index != -1) {
                val application = applications[index]
                val updated = application.copy(
                    status = status,
                    approvalDate = if (status == ApplicationStatus.APPROVED) LocalDateTime.now() else null
                )
                applications[index] = updated
                
                // Create mortgage if approved
                if (status == ApplicationStatus.APPROVED) {
                    val mortgage = Mortgage(
                        id = UUID.randomUUID().toString(),
                        userId = updated.userId,
                        applicationId = applicationId,
                        propertyAddress = updated.propertyAddress,
                        originalAmount = updated.loanAmount,
                        currentBalance = updated.loanAmount,
                        interestRate = updated.interestRate,
                        monthlyPayment = updated.monthlyPayment,
                        termYears = updated.termYears,
                        remainingPayments = updated.termYears * 12,
                        nextPaymentDate = LocalDateTime.now().plusMonths(1),
                        maturityDate = LocalDateTime.now().plusYears(updated.termYears.toLong()),
                        mortgageType = updated.mortgageType,
                        status = MortgageStatus.ACTIVE,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                    mortgages.add(mortgage)
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Application not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMortgages(userId: String): Result<List<Mortgage>> {
        return try {
            val userMortgages = mortgages.filter { it.userId == userId }
            Result.success(userMortgages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMortgage(mortgageId: String): Result<Mortgage?> {
        return try {
            val mortgage = mortgages.find { it.id == mortgageId }
            Result.success(mortgage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createMortgage(mortgage: Mortgage): Result<String> {
        return try {
            mortgages.add(mortgage)
            Result.success(mortgage.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMortgage(mortgage: Mortgage): Result<Unit> {
        return try {
            val index = mortgages.indexOfFirst { it.id == mortgage.id }
            if (index != -1) {
                mortgages[index] = mortgage
                Result.success(Unit)
            } else {
                Result.failure(Exception("Mortgage not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun makePayment(payment: MortgagePayment): Result<Unit> {
        return try {
            payments.add(payment)
            
            // Update mortgage balance and remaining payments
            val mortgageIndex = mortgages.indexOfFirst { it.id == payment.mortgageId }
            if (mortgageIndex != -1) {
                val mortgage = mortgages[mortgageIndex]
                val newBalance = payment.remainingBalance
                val newRemainingPayments = mortgage.remainingPayments - 1
                
                val updatedMortgage = mortgage.copy(
                    currentBalance = newBalance,
                    remainingPayments = newRemainingPayments,
                    nextPaymentDate = if (newRemainingPayments > 0) mortgage.nextPaymentDate.plusMonths(1) else mortgage.nextPaymentDate,
                    status = if (newBalance <= BigDecimal.ZERO) MortgageStatus.PAID_OFF else mortgage.status,
                    updatedAt = LocalDateTime.now()
                )
                mortgages[mortgageIndex] = updatedMortgage
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPaymentHistory(mortgageId: String): Result<List<MortgagePayment>> {
        return try {
            val mortgagePayments = payments.filter { it.mortgageId == mortgageId }
                .sortedByDescending { it.paymentDate }
            Result.success(mortgagePayments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUpcomingPayments(userId: String): Result<List<MortgagePayment>> {
        return try {
            val userMortgages = mortgages.filter { it.userId == userId && it.status == MortgageStatus.ACTIVE }
            val upcomingPayments = userMortgages.map { mortgage ->
                val monthlyRate = mortgage.interestRate.divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
                    .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
                val interestAmount = mortgage.currentBalance.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP)
                val principalAmount = mortgage.monthlyPayment.subtract(interestAmount)
                    .setScale(2, RoundingMode.HALF_UP)
                
                MortgagePayment(
                    id = UUID.randomUUID().toString(),
                    mortgageId = mortgage.id,
                    paymentAmount = mortgage.monthlyPayment,
                    principalAmount = principalAmount,
                    interestAmount = interestAmount,
                    paymentDate = mortgage.nextPaymentDate,
                    dueDate = mortgage.nextPaymentDate,
                    status = MortgagePaymentStatus.ON_TIME,
                    remainingBalance = mortgage.currentBalance.subtract(principalAmount)
                )
            }
            Result.success(upcomingPayments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateMonthlyPayment(loanAmount: BigDecimal, interestRate: BigDecimal, termYears: Int): Result<BigDecimal> {
        return try {
            if (interestRate == BigDecimal.ZERO) {
                // No interest loan
                val payment = loanAmount.divide(BigDecimal(termYears * 12), 2, RoundingMode.HALF_UP)
                Result.success(payment)
            } else {
                val monthlyRate = interestRate.divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
                    .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
                
                val numPayments = termYears * 12
                val rateDouble = monthlyRate.toDouble()
                
                // PMT formula: P * [r(1+r)^n] / [(1+r)^n - 1]
                val numerator = rateDouble * (1 + rateDouble).pow(numPayments)
                val denominator = (1 + rateDouble).pow(numPayments) - 1
                val paymentFactor = numerator / denominator
                
                val monthlyPayment = loanAmount.multiply(BigDecimal(paymentFactor))
                    .setScale(2, RoundingMode.HALF_UP)
                
                Result.success(monthlyPayment)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAmortizationSchedule(mortgageId: String): Result<List<MortgagePayment>> {
        return try {
            val mortgage = mortgages.find { it.id == mortgageId }
                ?: return Result.failure(Exception("Mortgage not found"))
            
            val schedule = mutableListOf<MortgagePayment>()
            var remainingBalance = mortgage.originalAmount
            var paymentDate = mortgage.createdAt.plusMonths(1)
            
            val monthlyRate = mortgage.interestRate.divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
            
            for (paymentNumber in 1..(mortgage.termYears * 12)) {
                val interestAmount = remainingBalance.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP)
                val principalAmount = mortgage.monthlyPayment.subtract(interestAmount)
                    .setScale(2, RoundingMode.HALF_UP)
                
                remainingBalance = remainingBalance.subtract(principalAmount)
                if (remainingBalance < BigDecimal.ZERO) {
                    remainingBalance = BigDecimal.ZERO
                }
                
                val payment = MortgagePayment(
                    id = UUID.randomUUID().toString(),
                    mortgageId = mortgageId,
                    paymentAmount = mortgage.monthlyPayment,
                    principalAmount = principalAmount,
                    interestAmount = interestAmount,
                    paymentDate = paymentDate,
                    dueDate = paymentDate,
                    status = MortgagePaymentStatus.ON_TIME,
                    remainingBalance = remainingBalance
                )
                
                schedule.add(payment)
                paymentDate = paymentDate.plusMonths(1)
            }
            
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPropertyValuation(propertyAddress: String): Result<PropertyValuation?> {
        return try {
            val valuation = valuations[propertyAddress]
            Result.success(valuation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun requestPropertyValuation(propertyAddress: String): Result<PropertyValuation> {
        return try {
            // Mock property valuation
            val baseValue = BigDecimal("300000")
            val randomFactor = (0.8 + Math.random() * 0.4) // 80% to 120% of base value
            val estimatedValue = baseValue.multiply(BigDecimal(randomFactor))
                .setScale(0, RoundingMode.HALF_UP)
            
            val valuation = PropertyValuation(
                id = UUID.randomUUID().toString(),
                propertyAddress = propertyAddress,
                currentValue = estimatedValue,
                valuationDate = LocalDateTime.now(),
                valuationMethod = "Automated Valuation Model",
                appraiser = "AI Valuation System"
            )
            
            valuations[propertyAddress] = valuation
            Result.success(valuation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRefinanceOptions(mortgageId: String): Result<List<MortgageApplication>> {
        return try {
            val mortgage = mortgages.find { it.id == mortgageId }
                ?: return Result.failure(Exception("Mortgage not found"))
            
            // Generate mock refinance options with different rates
            val refinanceOptions = listOf(
                BigDecimal("3.25"),
                BigDecimal("3.50"),
                BigDecimal("3.75")
            ).map { newRate ->
                val newMonthlyPayment = calculateMonthlyPayment(
                    mortgage.currentBalance, newRate, mortgage.remainingPayments / 12
                ).getOrNull() ?: BigDecimal.ZERO
                
                MortgageApplication(
                    id = UUID.randomUUID().toString(),
                    userId = mortgage.userId,
                    propertyAddress = mortgage.propertyAddress,
                    propertyValue = mortgage.currentBalance.multiply(BigDecimal("1.2")), // Assume 20% equity
                    loanAmount = mortgage.currentBalance,
                    downPayment = BigDecimal.ZERO,
                    interestRate = newRate,
                    termYears = mortgage.remainingPayments / 12,
                    mortgageType = mortgage.mortgageType,
                    status = ApplicationStatus.SUBMITTED,
                    monthlyPayment = newMonthlyPayment,
                    applicationDate = LocalDateTime.now()
                )
            }
            
            Result.success(refinanceOptions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateRefinanceSavings(mortgageId: String, newRate: BigDecimal): Result<BigDecimal> {
        return try {
            val mortgage = mortgages.find { it.id == mortgageId }
                ?: return Result.failure(Exception("Mortgage not found"))
            
            val newMonthlyPayment = calculateMonthlyPayment(
                mortgage.currentBalance, newRate, mortgage.remainingPayments / 12
            ).getOrNull() ?: return Result.failure(Exception("Could not calculate new payment"))
            
            val monthlySavings = mortgage.monthlyPayment.subtract(newMonthlyPayment)
            val totalSavings = monthlySavings.multiply(BigDecimal(mortgage.remainingPayments))
            
            Result.success(totalSavings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}