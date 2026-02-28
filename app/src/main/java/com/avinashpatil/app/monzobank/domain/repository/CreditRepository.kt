package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class CreditApplication(
    val id: String,
    val userId: String,
    val creditType: CreditType,
    val requestedAmount: BigDecimal,
    val purpose: String,
    val status: CreditApplicationStatus,
    val submittedAt: LocalDateTime,
    val processedAt: LocalDateTime?,
    val approvedAmount: BigDecimal?,
    val interestRate: BigDecimal?,
    val termMonths: Int?
)

enum class CreditType {
    PERSONAL_LOAN, CREDIT_CARD, LINE_OF_CREDIT, MORTGAGE, AUTO_LOAN, BUSINESS_LOAN
}

enum class CreditApplicationStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, CANCELLED, PENDING_DOCUMENTS
}

data class CreditScore(
    val userId: String,
    val score: Int,
    val provider: String,
    val lastUpdated: LocalDateTime,
    val factors: List<CreditFactor>
)

data class CreditFactor(
    val factor: String,
    val impact: CreditImpact,
    val description: String
)

enum class CreditImpact {
    VERY_POSITIVE, POSITIVE, NEUTRAL, NEGATIVE, VERY_NEGATIVE
}

data class CreditLimit(
    val userId: String,
    val creditType: CreditType,
    val currentLimit: BigDecimal,
    val availableCredit: BigDecimal,
    val utilizationRate: Double,
    val lastUpdated: LocalDateTime
)

data class CreditHistory(
    val userId: String,
    val accounts: List<CreditAccount>,
    val paymentHistory: List<PaymentRecord>,
    val inquiries: List<CreditInquiry>
)

data class CreditAccount(
    val accountId: String,
    val accountType: CreditType,
    val balance: BigDecimal,
    val limit: BigDecimal,
    val openedDate: LocalDateTime,
    val status: CreditAccountStatus
)

enum class CreditAccountStatus {
    ACTIVE, CLOSED, DELINQUENT, CHARGED_OFF
}

data class PaymentRecord(
    val accountId: String,
    val amount: BigDecimal,
    val dueDate: LocalDateTime,
    val paidDate: LocalDateTime?,
    val status: CreditPaymentStatus
)

enum class CreditPaymentStatus {
    ON_TIME, LATE, MISSED, PARTIAL
}

data class CreditInquiry(
    val id: String,
    val inquiryType: InquiryType,
    val inquirer: String,
    val date: LocalDateTime,
    val purpose: String
)

enum class InquiryType {
    HARD, SOFT
}

interface CreditRepository {
    suspend fun submitCreditApplication(application: CreditApplication): Result<String>
    suspend fun getCreditApplications(userId: String): Result<List<CreditApplication>>
    suspend fun updateApplicationStatus(applicationId: String, status: CreditApplicationStatus): Result<Unit>
    suspend fun getCreditScore(userId: String): Result<CreditScore>
    suspend fun updateCreditScore(userId: String, score: CreditScore): Result<Unit>
    suspend fun getCreditLimits(userId: String): Result<List<CreditLimit>>
    suspend fun updateCreditLimit(userId: String, creditType: CreditType, newLimit: BigDecimal): Result<Unit>
    suspend fun getCreditHistory(userId: String): Result<CreditHistory>
    suspend fun addPaymentRecord(record: PaymentRecord): Result<Unit>
    suspend fun performCreditCheck(userId: String): Result<CreditScore>
    suspend fun calculateCreditUtilization(userId: String): Result<Double>
    suspend fun getPrequalifiedOffers(userId: String): Result<List<CreditApplication>>
}