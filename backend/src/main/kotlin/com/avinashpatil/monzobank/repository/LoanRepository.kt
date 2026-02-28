package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.Loan
import com.avinashpatil.monzobank.entity.LoanStatus
import com.avinashpatil.monzobank.entity.LoanType
import com.avinashpatil.monzobank.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface LoanRepository : JpaRepository<Loan, UUID> {
    
    // Find loans by user
    fun findByUser(user: User): List<Loan>
    fun findByUserId(userId: UUID): List<Loan>
    
    // Find loans by type
    fun findByLoanType(loanType: LoanType): List<Loan>
    fun findByUserAndLoanType(user: User, loanType: LoanType): List<Loan>
    
    // Find loans by status
    fun findByStatus(status: LoanStatus): List<Loan>
    fun findByUserAndStatus(user: User, status: LoanStatus): List<Loan>
    
    // Find loans by amount
    fun findByPrincipalAmountGreaterThan(principalAmount: BigDecimal): List<Loan>
    fun findByPrincipalAmountLessThan(principalAmount: BigDecimal): List<Loan>
    fun findByPrincipalAmountBetween(minAmount: BigDecimal, maxAmount: BigDecimal): List<Loan>
    
    // Find loans by outstanding balance
    fun findByOutstandingBalanceGreaterThan(outstandingBalance: BigDecimal): List<Loan>
    fun findByOutstandingBalanceLessThan(outstandingBalance: BigDecimal): List<Loan>
    fun findByOutstandingBalanceBetween(minBalance: BigDecimal, maxBalance: BigDecimal): List<Loan>
    
    // Find loans by interest rate
    fun findByInterestRateGreaterThan(interestRate: BigDecimal): List<Loan>
    fun findByInterestRateLessThan(interestRate: BigDecimal): List<Loan>
    fun findByInterestRateBetween(minRate: BigDecimal, maxRate: BigDecimal): List<Loan>
    
    // Find loans by term
    fun findByTermInMonthsGreaterThan(termInMonths: Int): List<Loan>
    fun findByTermInMonthsLessThan(termInMonths: Int): List<Loan>
    fun findByTermInMonthsBetween(minTerm: Int, maxTerm: Int): List<Loan>
    
    // Find loans by dates
    fun findByApplicationDateBefore(applicationDate: LocalDateTime): List<Loan>
    fun findByApplicationDateAfter(applicationDate: LocalDateTime): List<Loan>
    fun findByApplicationDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Loan>
    
    fun findByApprovalDateBefore(approvalDate: LocalDateTime): List<Loan>
    fun findByApprovalDateAfter(approvalDate: LocalDateTime): List<Loan>
    fun findByApprovalDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Loan>
    
    fun findByDisbursementDateBefore(disbursementDate: LocalDateTime): List<Loan>
    fun findByDisbursementDateAfter(disbursementDate: LocalDateTime): List<Loan>
    fun findByDisbursementDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Loan>
    
    fun findByMaturityDateBefore(maturityDate: LocalDateTime): List<Loan>
    fun findByMaturityDateAfter(maturityDate: LocalDateTime): List<Loan>
    fun findByMaturityDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Loan>
    
    // Find loans by next payment date
    fun findByNextPaymentDateBefore(nextPaymentDate: LocalDateTime): List<Loan>
    fun findByNextPaymentDateAfter(nextPaymentDate: LocalDateTime): List<Loan>
    fun findByNextPaymentDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Loan>
    
    // Custom queries
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.status IN ('ACTIVE', 'CURRENT') ORDER BY l.applicationDate DESC")
    fun findActiveLoansByUserIdOrderByApplicationDateDesc(@Param("userId") userId: UUID): List<Loan>
    
    @Query("SELECT SUM(l.outstandingBalance) FROM Loan l WHERE l.user.id = :userId AND l.status IN ('ACTIVE', 'CURRENT')")
    fun getTotalOutstandingBalanceByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT SUM(l.principalAmount) FROM Loan l WHERE l.user.id = :userId")
    fun getTotalLoanAmountByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status IN ('ACTIVE', 'CURRENT')")
    fun countActiveLoansByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT l FROM Loan l WHERE l.nextPaymentDate <= :date AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findLoansWithUpcomingPayments(@Param("date") date: LocalDateTime): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.nextPaymentDate < :date AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findOverdueLoans(@Param("date") date: LocalDateTime): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.nextPaymentDate <= :date AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findUpcomingPaymentsByUserId(@Param("userId") userId: UUID, @Param("date") date: LocalDateTime): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.maturityDate <= :date AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findLoansMaturingSoon(@Param("date") date: LocalDateTime): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.loanType = :loanType AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findActiveLoansByUserAndType(@Param("userId") userId: UUID, @Param("loanType") loanType: LoanType): List<Loan>
    
    @Query("SELECT AVG(l.interestRate) FROM Loan l WHERE l.loanType = :loanType AND l.status IN ('ACTIVE', 'CURRENT')")
    fun getAverageInterestRateByLoanType(@Param("loanType") loanType: LoanType): BigDecimal?
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.outstandingBalance > :minBalance AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findLoansByUserWithMinimumBalance(@Param("userId") userId: UUID, @Param("minBalance") minBalance: BigDecimal): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.outstandingBalance DESC")
    fun findLoansByUserOrderByOutstandingBalanceDesc(@Param("userId") userId: UUID): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.interestRate DESC")
    fun findLoansByUserOrderByInterestRateDesc(@Param("userId") userId: UUID): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.nextPaymentDate ASC")
    fun findLoansByUserOrderByNextPaymentDateAsc(@Param("userId") userId: UUID): List<Loan>
    
    @Query("SELECT l.loanType, COUNT(l), SUM(l.principalAmount), AVG(l.interestRate) FROM Loan l WHERE l.status IN ('ACTIVE', 'CURRENT') GROUP BY l.loanType")
    fun getLoanStatisticsByType(): List<Array<Any>>
    
    @Query("SELECT l FROM Loan l WHERE l.applicationDate >= :date")
    fun findRecentLoanApplications(@Param("date") date: LocalDateTime): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.applicationDate >= :date")
    fun findRecentLoanApplicationsByUserId(@Param("userId") userId: UUID, @Param("date") date: LocalDateTime): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'PENDING_APPROVAL' ORDER BY l.applicationDate ASC")
    fun findPendingApprovalLoansOrderByApplicationDate(): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'APPROVED' AND l.disbursementDate IS NULL ORDER BY l.approvalDate ASC")
    fun findApprovedLoansAwaitingDisbursement(): List<Loan>
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.purpose LIKE %:searchTerm%")
    fun searchLoansByPurpose(@Param("userId") userId: UUID, @Param("searchTerm") searchTerm: String): List<Loan>
    
    @Query("SELECT SUM(l.monthlyPayment) FROM Loan l WHERE l.user.id = :userId AND l.status IN ('ACTIVE', 'CURRENT')")
    fun getTotalMonthlyPaymentsByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.monthlyPayment >= :minPayment AND l.status IN ('ACTIVE', 'CURRENT')")
    fun findLoansByUserWithMinimumMonthlyPayment(@Param("userId") userId: UUID, @Param("minPayment") minPayment: BigDecimal): List<Loan>
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'")
    fun countActiveLoans(): Long
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'PENDING_APPROVAL'")
    fun countPendingApprovalLoans(): Long
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'APPROVED'")
    fun countApprovedLoans(): Long
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'REJECTED'")
    fun countRejectedLoans(): Long
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'PAID_OFF'")
    fun countPaidOffLoans(): Long
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'DEFAULTED'")
    fun countDefaultedLoans(): Long
    
    @Query("SELECT COUNT(DISTINCT l.user.id) FROM Loan l WHERE l.status IN ('ACTIVE', 'CURRENT')")
    fun countActiveBorrowers(): Long
    
    @Query("SELECT SUM(l.outstandingBalance) FROM Loan l WHERE l.status IN ('ACTIVE', 'CURRENT')")
    fun getTotalOutstandingBalance(): BigDecimal?
    
    @Query("SELECT AVG(l.principalAmount) FROM Loan l WHERE l.loanType = :loanType")
    fun getAverageLoanAmountByType(@Param("loanType") loanType: LoanType): BigDecimal?
}