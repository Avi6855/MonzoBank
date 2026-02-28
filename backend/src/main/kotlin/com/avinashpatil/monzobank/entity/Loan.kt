package com.avinashpatil.monzobank.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "loans")
data class Loan(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    val loanType: LoanType,
    
    @Column(precision = 15, scale = 2, nullable = false)
    val amount: BigDecimal,
    
    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    val outstandingBalance: BigDecimal,
    
    @Column(name = "interest_rate", precision = 5, scale = 4, nullable = false)
    val interestRate: BigDecimal,
    
    @Column(name = "term_months", nullable = false)
    val termMonths: Int,
    
    @Column(name = "monthly_payment", precision = 10, scale = 2)
    val monthlyPayment: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    val status: LoanStatus,
    
    @Column(name = "purpose")
    val purpose: String? = null,
    
    @Column(name = "collateral_description")
    val collateralDescription: String? = null,
    
    @Column(name = "collateral_value", precision = 15, scale = 2)
    val collateralValue: BigDecimal? = null,
    
    @Column(name = "credit_score")
    val creditScore: Int? = null,
    
    @Column(name = "employment_status")
    val employmentStatus: String? = null,
    
    @Column(name = "annual_income", precision = 15, scale = 2)
    val annualIncome: BigDecimal? = null,
    
    @Column(name = "debt_to_income_ratio", precision = 5, scale = 2)
    val debtToIncomeRatio: BigDecimal? = null,
    
    @Column(name = "application_date", nullable = false)
    val applicationDate: LocalDateTime,
    
    @Column(name = "approval_date")
    val approvalDate: LocalDateTime? = null,
    
    @Column(name = "disbursement_date")
    val disbursementDate: LocalDateTime? = null,
    
    @Column(name = "first_payment_date")
    val firstPaymentDate: LocalDateTime? = null,
    
    @Column(name = "maturity_date")
    val maturityDate: LocalDateTime? = null,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class LoanType {
    PERSONAL, MORTGAGE, AUTO, BUSINESS, STUDENT, CREDIT_LINE
}

enum class LoanStatus {
    PENDING, APPROVED, REJECTED, ACTIVE, PAID_OFF, DEFAULTED,