package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsuranceRepositoryImpl @Inject constructor() : InsuranceRepository {
    
    private val policies = mutableListOf<InsurancePolicy>()
    private val claims = mutableListOf<InsuranceClaim>()
    private val quotes = mutableListOf<InsuranceQuote>()
    
    override suspend fun getPolicies(userId: String): Result<List<InsurancePolicy>> {
        return try {
            val userPolicies = policies.filter { it.userId == userId }
            Result.success(userPolicies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPolicy(policyId: String): Result<InsurancePolicy?> {
        return try {
            val policy = policies.find { it.id == policyId }
            Result.success(policy)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPolicy(policy: InsurancePolicy): Result<String> {
        return try {
            policies.add(policy)
            Result.success(policy.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePolicy(policy: InsurancePolicy): Result<Unit> {
        return try {
            val index = policies.indexOfFirst { it.id == policy.id }
            if (index != -1) {
                policies[index] = policy
                Result.success(Unit)
            } else {
                Result.failure(Exception("Policy not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelPolicy(policyId: String): Result<Unit> {
        return try {
            val index = policies.indexOfFirst { it.id == policyId }
            if (index != -1) {
                val policy = policies[index]
                val cancelledPolicy = policy.copy(
                    status = PolicyStatus.CANCELLED,
                    updatedAt = LocalDateTime.now()
                )
                policies[index] = cancelledPolicy
                Result.success(Unit)
            } else {
                Result.failure(Exception("Policy not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getClaims(userId: String): Result<List<InsuranceClaim>> {
        return try {
            val userClaims = claims.filter { claim ->
                val policy = policies.find { it.id == claim.policyId }
                policy?.userId == userId
            }
            Result.success(userClaims)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getClaim(claimId: String): Result<InsuranceClaim?> {
        return try {
            val claim = claims.find { it.id == claimId }
            Result.success(claim)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun submitClaim(claim: InsuranceClaim): Result<String> {
        return try {
            claims.add(claim)
            Result.success(claim.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateClaim(claim: InsuranceClaim): Result<Unit> {
        return try {
            val index = claims.indexOfFirst { it.id == claim.id }
            if (index != -1) {
                claims[index] = claim
                Result.success(Unit)
            } else {
                Result.failure(Exception("Claim not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getQuotes(userId: String, insuranceType: InsuranceType): Result<List<InsuranceQuote>> {
        return try {
            val userQuotes = quotes.filter { 
                it.userId == userId && it.insuranceType == insuranceType 
            }
            Result.success(userQuotes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun requestQuote(userId: String, insuranceType: InsuranceType, details: Map<String, Any>): Result<InsuranceQuote> {
        return try {
            val coverageAmount = BigDecimal(details["coverageAmount"]?.toString() ?: "100000")
            val premium = calculatePremium(insuranceType, coverageAmount, details).getOrNull() ?: BigDecimal("100")
            
            val quote = InsuranceQuote(
                id = UUID.randomUUID().toString(),
                userId = userId,
                insuranceType = insuranceType,
                provider = "Mock Insurance Co.",
                coverageAmount = coverageAmount,
                premium = premium,
                premiumFrequency = PremiumFrequency.MONTHLY,
                deductible = BigDecimal("500"),
                validUntil = LocalDateTime.now().plusDays(30),
                quoteDetails = details,
                createdAt = LocalDateTime.now()
            )
            
            quotes.add(quote)
            Result.success(quote)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculatePremium(insuranceType: InsuranceType, coverageAmount: BigDecimal, userDetails: Map<String, Any>): Result<BigDecimal> {
        return try {
            // Mock premium calculation based on insurance type and coverage amount
            val basePremium = when (insuranceType) {
                InsuranceType.LIFE -> coverageAmount.multiply(BigDecimal("0.001"))
                InsuranceType.HEALTH -> BigDecimal("300")
                InsuranceType.AUTO -> BigDecimal("150")
                InsuranceType.HOME -> coverageAmount.multiply(BigDecimal("0.005"))
                InsuranceType.RENTERS -> BigDecimal("25")
                InsuranceType.TRAVEL -> BigDecimal("50")
                InsuranceType.DISABILITY -> BigDecimal("200")
                InsuranceType.UMBRELLA -> BigDecimal("400")
                InsuranceType.BUSINESS -> BigDecimal("500")
                InsuranceType.PET -> BigDecimal("40")
            }
            
            // Apply age factor if available
            val age = userDetails["age"]?.toString()?.toIntOrNull() ?: 30
            val ageFactor = when {
                age < 25 -> BigDecimal("1.2")
                age > 65 -> BigDecimal("1.5")
                else -> BigDecimal("1.0")
            }
            
            val finalPremium = basePremium.multiply(ageFactor)
            Result.success(finalPremium)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProviders(insuranceType: InsuranceType): Result<List<String>> {
        return try {
            val providers = when (insuranceType) {
                InsuranceType.LIFE -> listOf("Life Insurance Co.", "Secure Life", "Family Protection")
                InsuranceType.HEALTH -> listOf("Health Plus", "Medical Care Inc.", "Wellness Insurance")
                InsuranceType.AUTO -> listOf("Auto Shield", "Drive Safe Insurance", "Car Protection Co.")
                InsuranceType.HOME -> listOf("Home Guard", "Property Shield", "Secure Homes Inc.")
                InsuranceType.RENTERS -> listOf("Renter's Choice", "Tenant Protection", "Lease Guard")
                InsuranceType.TRAVEL -> listOf("Travel Safe", "Journey Insurance", "Trip Protection")
                InsuranceType.DISABILITY -> listOf("Disability Shield", "Income Protection", "Work Guard")
                InsuranceType.UMBRELLA -> listOf("Umbrella Coverage", "Extra Protection", "Liability Plus")
                InsuranceType.BUSINESS -> listOf("Business Shield", "Commercial Guard", "Enterprise Protection")
                InsuranceType.PET -> listOf("Pet Care Insurance", "Animal Health Plus", "Furry Friends Coverage")
            }
            Result.success(providers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun renewPolicy(policyId: String): Result<Unit> {
        return try {
            val index = policies.indexOfFirst { it.id == policyId }
            if (index != -1) {
                val policy = policies[index]
                val renewedPolicy = policy.copy(
                    startDate = policy.endDate,
                    endDate = policy.endDate.plusYears(1),
                    status = PolicyStatus.ACTIVE,
                    updatedAt = LocalDateTime.now()
                )
                policies[index] = renewedPolicy
                Result.success(Unit)
            } else {
                Result.failure(Exception("Policy not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}