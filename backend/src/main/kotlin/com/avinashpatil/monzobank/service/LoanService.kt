package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.*
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.LoanRepository
import com.avinashpatil.monzobank.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class LoanService(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val smsService: SmsService
) {
    
    private val logger = LoggerFactory.getLogger(LoanService::class.java)
    
    fun applyForLoan(userId: UUID, request: LoanApplicationRequest): LoanResponse {
        logger.info("Processing loan application for user: $userId")
        
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        // Validate loan application
        if (request.principalAmount <= BigDecimal.ZERO) {
            throw ValidationException("Principal amount must be positive")
        }
        
        if (request.termInMonths <= 0) {
            throw ValidationException("Term must be positive")
        }
        
        // Check user eligibility
        val eligibilityResult = checkLoanEligibility(user, request)
        if (!eligibilityResult.isEligible) {
            throw BusinessRuleException("Loan application rejected: ${eligibilityResult.reason}")
        }
        
        // Calculate interest rate based on credit score and loan type
        val interestRate = calculateInterestRate(request.loanType, eligibilityResult.creditScore)
        
        // Calculate monthly payment
        val monthlyPayment = calculateMonthlyPayment(
            request.principalAmount,
            interestRate,
            request.termInMonths
        )
        
        val loan = Loan(
            id = UUID.randomUUID(),
            user = user,
            loanType = request.loanType,
            principalAmount = request.principalAmount,
            interestRate = interestRate,
            termInMonths = request.termInMonths,
            monthlyPayment = monthlyPayment,
            outstandingBalance = request.principalAmount,
            status = LoanStatus.PENDING_APPROVAL,
            purpose = request.purpose,
            applicationDate = LocalDateTime.now(),
            maturityDate = LocalDateTime.now().plusMonths(request.termInMonths.toLong()),
            nextPaymentDate = null, // Set after approval
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedLoan = loanRepository.save(loan)
        
        // Send application confirmation
        sendLoanApplicationNotification(user, savedLoan)
        
        logger.info("Loan application submitted successfully: ${savedLoan.id}")
        return mapToLoanResponse(savedLoan)
    }
    
    @Transactional(readOnly = true)
    fun getLoanById(loanId: UUID): LoanResponse {
        val loan = loanRepository.findById(loanId)
            .orElseThrow { LoanNotFoundException("Loan not found with ID: $loanId") }
        
        return mapToLoanResponse(loan)
    }
    
    @Transactional(readOnly = true)
    fun getLoansByUserId(userId: UUID): List<LoanResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return loanRepository.findActiveLoansByUserIdOrderByApplicationDateDesc(userId)
            .map { mapToLoanResponse(it) }
    }
    
    fun approveLoan(loanId: UUID, request: LoanApprovalRequest): LoanResponse {
        logger.info("Approving loan: $loanId")
        
        val loan = loanRepository.findById(loanId)
            .orElseThrow { LoanNotFoundException("Loan not found with ID: $loanId") }
        
        if (loan.status != LoanStatus.PENDING_APPROVAL) {
            throw BusinessRuleException("Loan is not pending approval")
        }
        
        // Validate approved amount
        request.approvedAmount?.let { 
            if (it <= BigDecimal.ZERO) {
                throw ValidationException("Approved amount must be positive")
            }
        }
        
        // Validate approved interest rate
        request.approvedInterestRate?.let { 
            if (it < BigDecimal.ZERO) {
                throw ValidationException("Interest rate cannot be negative")
            }
        }
        
        // Validate approved term
        request.approvedTermInMonths?.let {
            if (it <= 0) {
                throw ValidationException("Term must be positive")
            }
        }
        
        val approvedAmount = request.approvedAmount ?: loan.amount
        val approvedRate = request.approvedInterestRate ?: loan.interestRate
        val approvedTerm = request.approvedTermInMonths ?: loan.termMonths
        
        // Recalculate monthly payment
        val monthlyPayment = calculateMonthlyPayment(approvedAmount, approvedRate, approvedTerm)
        
        val updatedLoan = loan.copy(
            amount = approvedAmount,
            outstandingBalance = approvedAmount,
            interestRate = approvedRate,
            termMonths = approvedTerm,
            monthlyPayment = monthlyPayment,
            status = LoanStatus.APPROVED,
            approvalDate = LocalDateTime.now(),
            firstPaymentDate = LocalDateTime.now().plusMonths(1),
            maturityDate = loan.applicationDate.plusMonths(approvedTerm.toLong())
        )
        
        val savedLoan = loanRepository.save(updatedLoan)
        
        // Send approval notification
        sendLoanApprovalNotification(loan.user, savedLoan)
        
        logger.info("Loan approved successfully: $loanId")
        return mapToLoanResponse(savedLoan)
    }
    
    fun rejectLoan(loanId: UUID, reason: String): LoanResponse {
        logger.info("Rejecting loan: $loanId, reason: $reason")
        
        val loan = loanRepository.findById(loanId)
            .orElseThrow { LoanNotFoundException("Loan not found with ID: $loanId") }
        
        if (loan.status != LoanStatus.PENDING_APPROVAL) {
            throw BusinessRuleException("Loan is not pending approval")
        }
        
        val updatedLoan = loan.copy(status = LoanStatus.REJECTED)
        val savedLoan = loanRepository.save(updatedLoan)
        
        // Send rejection notification
        sendLoanRejectionNotification(loan.user, savedLoan, reason)
        
        logger.info("Loan rejected successfully: $loanId")
        return mapToLoanResponse(savedLoan)
    }
    
    fun disburseLoan(loanId: UUID): LoanResponse {
        logger.info("Disbursing loan: $loanId")
        
        val loan = loanRepository.findById(loanId)
            .orElseThrow { LoanNotFoundException("Loan not found with ID: $loanId") }
        
        if (loan.status != LoanStatus.APPROVED) {
            throw BusinessRuleException("Loan must be approved before disbursement")
        }
        
        val updatedLoan = loan.copy(
            status = LoanStatus.ACTIVE,
            disbursementDate = LocalDateTime.now()
        )
        val savedLoan = loanRepository.save(updatedLoan)
        
        // Send disbursement notification
        sendLoanDisbursementNotification(loan.user, savedLoan)
        
        logger.info("Loan disbursed successfully: $loanId")
        return mapToLoanResponse(savedLoan)
    }
    
    fun makePayment(loanId: UUID, request: LoanPaymentRequest): LoanPaymentResponse {
        logger.info("Processing loan payment: $loanId, amount: ${request.amount}")
        
        val loan = loanRepository.findById(loanId)
            .orElseThrow { LoanNotFoundException("Loan not found with ID: $loanId") }
        
        if (loan.status != LoanStatus.ACTIVE && loan.status != LoanStatus.CURRENT) {
            throw BusinessRuleException("Cannot make payment on inactive loan")
        }
        
        if (request.amount <= BigDecimal.ZERO) {
            throw ValidationException("Payment amount must be positive")
        }
        
        if (request.amount > loan.outstandingBalance) {
            throw ValidationException("Payment amount cannot exceed outstanding balance")
        }
        
        // Calculate interest and principal portions
        val interestPayment = calculateInterestPayment(loan)
        val principalPayment = request.amount.subtract(interestPayment).max(BigDecimal.ZERO)
        
        // Calculate new outstanding balance
        val newOutstandingBalance = loan.outstandingBalance.subtract(request.amount)
        
        // Determine new status and next payment date
        val (newStatus, newNextPaymentDate) = if (newOutstandingBalance <= BigDecimal.ZERO) {
            LoanStatus.PAID_OFF to null
        } else {
            LoanStatus.CURRENT to loan.firstPaymentDate?.plusMonths(1)
        }
        
        val updatedLoan = loan.copy(
            outstandingBalance = newOutstandingBalance,
            status = newStatus,
            firstPaymentDate = newNextPaymentDate
        )
        
        val savedLoan = loanRepository.save(updatedLoan)
        
        val paymentResponse = LoanPaymentResponse(
            paymentId = UUID.randomUUID(),
            loanId = loanId,
            amount = request.amount,
            principalAmount = principalPayment,
            interestAmount = interestPayment,
            remainingBalance = savedLoan.outstandingBalance,
            paymentDate = LocalDateTime.now(),
            nextPaymentDate = savedLoan.firstPaymentDate,
            status = "COMPLETED"
        )
        
        // Send payment confirmation
        sendLoanPaymentNotification(loan.user, savedLoan, paymentResponse)
        
        logger.info("Loan payment processed successfully: $loanId")
        return paymentResponse
    }
    
    @Transactional(readOnly = true)
    fun getLoanSummary(userId: UUID): LoanSummaryResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        val activeLoans = loanRepository.findActiveLoansByUserIdOrderByApplicationDateDesc(userId)
        val totalOutstanding = loanRepository.getTotalOutstandingBalanceByUserId(userId) ?: BigDecimal.ZERO
        val totalMonthlyPayments = loanRepository.getTotalMonthlyPaymentsByUserId(userId) ?: BigDecimal.ZERO
        val totalLoanAmount = loanRepository.getTotalLoanAmountByUserId(userId) ?: BigDecimal.ZERO
        
        val upcomingPayments = loanRepository.findUpcomingPaymentsByUserId(
            userId, 
            LocalDateTime.now().plusDays(30)
        )
        
        return LoanSummaryResponse(
            totalActiveLoans = activeLoans.size.toLong(),
            totalOutstandingBalance = totalOutstanding,
            totalMonthlyPayments = totalMonthlyPayments,
            totalLoanAmount = totalLoanAmount,
            upcomingPayments = upcomingPayments.size.toLong(),
            nextPaymentDate = upcomingPayments.minByOrNull { it.firstPaymentDate ?: LocalDateTime.MAX }?.firstPaymentDate,
            averageInterestRate = if (activeLoans.isNotEmpty()) {
                activeLoans.map { it.interestRate }.reduce { acc, rate -> acc.add(rate) }
                    .divide(BigDecimal(activeLoans.size), 4, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO
        )
    }
    
    @Transactional(readOnly = true)
    fun getUpcomingPayments(userId: UUID): List<LoanPaymentScheduleResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        val upcomingPayments = loanRepository.findUpcomingPaymentsByUserId(
            userId,
            LocalDateTime.now().plusDays(90) // Next 3 months
        )
        
        return upcomingPayments.map { loan ->
            LoanPaymentScheduleResponse(
                loanId = loan.id,
                loanType = loan.loanType,
                paymentAmount = loan.monthlyPayment,
                principalAmount = calculatePrincipalPayment(loan),
                interestAmount = calculateInterestPayment(loan),
                paymentDate = loan.firstPaymentDate ?: LocalDateTime.now(),
                remainingBalance = loan.outstandingBalance,
                paymentNumber = calculatePaymentNumber(loan)
            )
        }.sortedBy { it.paymentDate }
    }
    
    fun processOverdueLoans() {
        logger.info("Processing overdue loans")
        
        val overdueLoans = loanRepository.findOverdueLoans(LocalDateTime.now())
        
        overdueLoans.forEach { loan ->
            try {
                // Update loan status
                val updatedLoan = loan.copy(status = LoanStatus.OVERDUE)
                val savedLoan = loanRepository.save(updatedLoan)
                
                // Send overdue notification
                sendOverdueNotification(loan.user, savedLoan)
                
            } catch (e: Exception) {
                logger.error("Failed to process overdue loan: ${loan.id}", e)
            }
        }
        
        logger.info("Processed ${overdueLoans.size} overdue loans")
    }
    
    private fun checkLoanEligibility(user: User, request: LoanApplicationRequest): EligibilityResult {
        // Simplified eligibility check
        val creditScore = simulateCreditScore(user)
        
        if (creditScore < 600) {
            return EligibilityResult(false, "Credit score too low", creditScore)
        }
        
        if (request.principalAmount > BigDecimal("100000")) {
            return EligibilityResult(false, "Loan amount exceeds maximum limit", creditScore)
        }
        
        // Check existing loan burden
        val existingLoans = loanRepository.countActiveLoansByUserId(user.id)
        if (existingLoans >= 3) {
            return EligibilityResult(false, "Too many existing loans", creditScore)
        }
        
        return EligibilityResult(true, "Eligible", creditScore)
    }
    
    private fun calculateInterestRate(loanType: LoanType, creditScore: Int): BigDecimal {
        val baseRate = when (loanType) {
            LoanType.PERSONAL -> BigDecimal("8.5")
            LoanType.MORTGAGE -> BigDecimal("3.5")
            LoanType.AUTO -> BigDecimal("5.5")
            LoanType.BUSINESS -> BigDecimal("7.0")
            LoanType.STUDENT -> BigDecimal("4.5")
        }
        
        // Adjust based on credit score
        val adjustment = when {
            creditScore >= 800 -> BigDecimal("-1.0")
            creditScore >= 750 -> BigDecimal("-0.5")
            creditScore >= 700 -> BigDecimal("0.0")
            creditScore >= 650 -> BigDecimal("0.5")
            else -> BigDecimal("1.0")
        }
        
        return baseRate.add(adjustment).max(BigDecimal("2.0")) // Minimum 2%
    }
    
    private fun calculateMonthlyPayment(principal: BigDecimal, annualRate: BigDecimal, termInMonths: Int): BigDecimal {
        if (annualRate == BigDecimal.ZERO) {
            return principal.divide(BigDecimal(termInMonths), 2, RoundingMode.HALF_UP)
        }
        
        val monthlyRate = annualRate.divide(BigDecimal("1200"), 10, RoundingMode.HALF_UP) // Annual to monthly percentage
        val factor = BigDecimal.ONE.add(monthlyRate).pow(termInMonths)
        
        return principal.multiply(monthlyRate).multiply(factor)
            .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP)
    }
    
    private fun calculateInterestPayment(loan: Loan): BigDecimal {
        val monthlyRate = loan.interestRate.divide(BigDecimal("1200"), 10, RoundingMode.HALF_UP)
        return loan.outstandingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP)
    }
    
    private fun calculatePrincipalPayment(loan: Loan): BigDecimal {
        val interestPayment = calculateInterestPayment(loan)
        return loan.monthlyPayment.subtract(interestPayment).max(BigDecimal.ZERO)
    }
    
    private fun calculatePaymentNumber(loan: Loan): Int {
        if (loan.disbursementDate == null || loan.firstPaymentDate == null) return 1
        
        val monthsSinceDisbursement = java.time.temporal.ChronoUnit.MONTHS.between(
            loan.disbursementDate,
            loan.firstPaymentDate
        )
        
        return monthsSinceDisbursement.toInt().coerceAtLeast(1)
    }
    
    private fun simulateCreditScore(user: User): Int {
        // Simulate credit score based on user data
        return (650..850).random()
    }
    
    private fun sendLoanApplicationNotification(user: User, loan: Loan) {
        try {
            val details = "Your ${loan.loanType} loan application for £${loan.amount} has been received and is under review."
            emailService.sendTransactionNotification(user.email, details)
        } catch (e: Exception) {
            logger.error("Failed to send loan application notification", e)
        }
    }
    
    private fun sendLoanApprovalNotification(user: User, loan: Loan) {
        try {
            val details = "Congratulations! Your ${loan.loanType} loan for £${loan.amount} has been approved at ${loan.interestRate}% interest."
            emailService.sendTransactionNotification(user.email, details)
            
            smsService.sendTransactionAlert(
                user.phone,
                "£${loan.amount}",
                "Loan Approved"
            )
        } catch (e: Exception) {
            logger.error("Failed to send loan approval notification", e)
        }
    }
    
    private fun sendLoanRejectionNotification(user: User, loan: Loan, reason: String) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Loan Application Declined",
                BigDecimal.ZERO,
                "GBP",
                "We regret to inform you that your ${loan.loanType} loan application has been declined. Reason: $reason"
            )
        } catch (e: Exception) {
            logger.error("Failed to send loan rejection notification", e)
        }
    }
    
    private fun sendLoanDisbursementNotification(user: User, loan: Loan) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Loan Disbursed",
                loan.amount,
                "GBP",
                "Your ${loan.loanType} loan of £${loan.amount} has been disbursed to your account."
            )
        } catch (e: Exception) {
            logger.error("Failed to send loan disbursement notification", e)
        }
    }
    
    private fun sendLoanPaymentNotification(user: User, loan: Loan, payment: LoanPaymentResponse) {
        try {
            emailService.sendTransactionNotification(
                user.email,
                user.firstName,
                "Loan Payment Received",
                payment.amount,
                "GBP",
                "Payment of £${payment.amount} received for your ${loan.loanType} loan. Remaining balance: £${payment.remainingBalance}."
            )
        } catch (e: Exception) {
            logger.error("Failed to send loan payment notification", e)
        }
    }
    
    private fun sendOverdueNotification(user: User, loan: Loan) {
        try {
            emailService.sendSecurityAlert(
                user.email,
                user.firstName,
                "Loan Payment Overdue",
                "Your ${loan.loanType} loan payment of £${loan.monthlyPayment} is overdue. Please make payment immediately to avoid penalties."
            )
            
            smsService.sendSecurityAlert(
                user.phoneNumber,
                "Loan payment overdue. Please pay £${loan.monthlyPayment} immediately."
            )
        } catch (e: Exception) {
            logger.error("Failed to send overdue notification", e)
        }
    }
    
    private fun mapToLoanResponse(loan: Loan): LoanResponse {
        return LoanResponse(
            id = loan.id,
            userId = loan.user.id,
            loanType = loan.loanType,
            principalAmount = loan.amount,
            interestRate = loan.interestRate,
            termInMonths = loan.termMonths,
            monthlyPayment = loan.monthlyPayment,
            outstandingBalance = loan.outstandingBalance,
            status = loan.status,
            purpose = loan.purpose ?: "",
            applicationDate = loan.applicationDate,
            approvalDate = loan.approvalDate,
            disbursementDate = loan.disbursementDate,
            maturityDate = loan.maturityDate,
            nextPaymentDate = loan.firstPaymentDate,
            createdAt = loan.createdAt,
            updatedAt = loan.createdAt
        )
    }
    
    private data class EligibilityResult(
        val isEligible: Boolean,
        val reason: String,
        val creditScore: Int
    )
}