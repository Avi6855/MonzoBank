package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyBankAccountRequestDto(
    @Json(name = "account_number")
    val accountNumber: String,
    
    @Json(name = "routing_number")
    val routingNumber: String,
    
    @Json(name = "account_type")
    val accountType: String, // "checking", "savings"
    
    @Json(name = "account_holder_name")
    val accountHolderName: String,
    
    @Json(name = "bank_name")
    val bankName: String?
)

@JsonClass(generateAdapter = true)
data class BankAccountVerificationDto(
    @Json(name = "verification_id")
    val verificationId: String,
    
    @Json(name = "status")
    val status: String, // "verified", "pending", "failed", "requires_manual_review"
    
    @Json(name = "account_number_masked")
    val accountNumberMasked: String,
    
    @Json(name = "routing_number")
    val routingNumber: String,
    
    @Json(name = "bank_name")
    val bankName: String,
    
    @Json(name = "account_type")
    val accountType: String,
    
    @Json(name = "account_holder_name_match")
    val accountHolderNameMatch: Boolean,
    
    @Json(name = "verification_method")
    val verificationMethod: String, // "instant", "micro_deposits", "manual"
    
    @Json(name = "verification_details")
    val verificationDetails: VerificationDetailsDto?,
    
    @Json(name = "risk_score")
    val riskScore: Int?, // 0-100
    
    @Json(name = "risk_factors")
    val riskFactors: List<String>?,
    
    @Json(name = "verified_at")
    val verifiedAt: String?,
    
    @Json(name = "expires_at")
    val expiresAt: String?,
    
    @Json(name = "error_code")
    val errorCode: String?,
    
    @Json(name = "error_message")
    val errorMessage: String?
)

@JsonClass(generateAdapter = true)
data class VerificationDetailsDto(
    @Json(name = "micro_deposit_amounts")
    val microDepositAmounts: List<String>?, // For micro deposit verification
    
    @Json(name = "expected_deposit_date")
    val expectedDepositDate: String?,
    
    @Json(name = "attempts_remaining")
    val attemptsRemaining: Int?,
    
    @Json(name = "next_attempt_allowed_at")
    val nextAttemptAllowedAt: String?
)