package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class InsurancePolicy(
    val id: String,
    val userId: String,
    val policyNumber: String,
    val insuranceType: InsuranceType,
    val provider: String,
    val coverageAmount: BigDecimal,
    val premium: BigDecimal,
    val premiumFrequency: PremiumFrequency,
    val deductible: BigDecimal,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: PolicyStatus,
    val beneficiaries: List<String> = emptyList(),
    val coverageDetails: Map<String, Any> = emptyMap(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class InsuranceClaim(
    val id: String,
    val policyId: String,
    val claimNumber: String,
    val claimType: ClaimType,
    val claimAmount: BigDecimal,
    val approvedAmount: BigDecimal?,
    val description: String,
    val incidentDate: LocalDateTime,
    val filedDate: LocalDateTime,
    val status: ClaimStatus,
    val documents: List<String> = emptyList(),
    val notes: String? = null,
    val processedAt: LocalDateTime? = null
)

data class InsuranceQuote(
    val id: String,
    val userId: String,
    val insuranceType: InsuranceType,
    val provider: String,
    val coverageAmount: BigDecimal,
    val premium: BigDecimal,
    val premiumFrequency: PremiumFrequency,
    val deductible: BigDecimal,
    val validUntil: LocalDateTime,
    val quoteDetails: Map<String, Any> = emptyMap(),
    val createdAt: LocalDateTime
)

enum class InsuranceType {
    LIFE,
    HEALTH,
    AUTO,
    HOME,
    RENTERS,
    TRAVEL,
    DISABILITY,
    UMBRELLA,
    BUSINESS,
    PET
}

enum class PremiumFrequency {
    MONTHLY,
    QUARTERLY,
    SEMI_ANNUALLY,
    ANNUALLY
}

enum class PolicyStatus {
    ACTIVE,
    INACTIVE,
    EXPIRED,
    CANCELLED,
    PENDING,
    SUSPENDED
}

enum class ClaimType {
    ACCIDENT,
    ILLNESS,
    PROPERTY_DAMAGE,
    THEFT,
    NATURAL_DISASTER,
    LIABILITY,
    OTHER
}

enum class ClaimStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    DENIED,
    PAID,
    CLOSED
}

interface InsuranceRepository {
    suspend fun getPolicies(userId: String): Result<List<InsurancePolicy>>
    suspend fun getPolicy(policyId: String): Result<InsurancePolicy?>
    suspend fun createPolicy(policy: InsurancePolicy): Result<String>
    suspend fun updatePolicy(policy: InsurancePolicy): Result<Unit>
    suspend fun cancelPolicy(policyId: String): Result<Unit>
    
    suspend fun getClaims(userId: String): Result<List<InsuranceClaim>>
    suspend fun getClaim(claimId: String): Result<InsuranceClaim?>
    suspend fun submitClaim(claim: InsuranceClaim): Result<String>
    suspend fun updateClaim(claim: InsuranceClaim): Result<Unit>
    
    suspend fun getQuotes(userId: String, insuranceType: InsuranceType): Result<List<InsuranceQuote>>
    suspend fun requestQuote(userId: String, insuranceType: InsuranceType, details: Map<String, Any>): Result<InsuranceQuote>
    
    suspend fun calculatePremium(insuranceType: InsuranceType, coverageAmount: BigDecimal, userDetails: Map<String, Any>): Result<BigDecimal>
    suspend fun getProviders(insuranceType: InsuranceType): Result<List<String>>
    suspend fun renewPolicy(policyId: String): Result<Unit>
}