package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class KYCDocument(
    val id: String,
    val userId: String,
    val documentType: DocumentType,
    val documentNumber: String,
    val issuedDate: LocalDateTime,
    val expiryDate: LocalDateTime?,
    val issuingAuthority: String,
    val status: DocumentStatus,
    val filePath: String,
    val verifiedAt: LocalDateTime?
)

enum class DocumentType {
    PASSPORT, DRIVERS_LICENSE, NATIONAL_ID, UTILITY_BILL, BANK_STATEMENT, TAX_RETURN
}

enum class DocumentStatus {
    PENDING, VERIFIED, REJECTED, EXPIRED, UNDER_REVIEW
}

data class KYCProfile(
    val userId: String,
    val riskLevel: KYCRiskLevel,
    val completionStatus: KYCStatus,
    val lastUpdated: LocalDateTime,
    val documents: List<KYCDocument>,
    val verificationScore: Int,
    val notes: String?
)

enum class KYCRiskLevel {
    LOW, MEDIUM, HIGH, VERY_HIGH
}

enum class KYCStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED, REJECTED, EXPIRED
}

interface KYCRepository {
    suspend fun uploadDocument(document: KYCDocument): Result<String>
    suspend fun verifyDocument(documentId: String, isValid: Boolean, notes: String?): Result<Unit>
    suspend fun getKYCProfile(userId: String): Result<KYCProfile>
    suspend fun updateRiskLevel(userId: String, riskLevel: KYCRiskLevel): Result<Unit>
    suspend fun getDocuments(userId: String): Result<List<KYCDocument>>
    suspend fun deleteDocument(documentId: String): Result<Unit>
    suspend fun performKYCCheck(userId: String): Result<KYCProfile>
    suspend fun getExpiredDocuments(): Result<List<KYCDocument>>
    suspend fun getPendingVerifications(): Result<List<KYCDocument>>
    suspend fun generateKYCReport(userId: String): Result<String>
    suspend fun updateKYCStatus(userId: String, status: KYCStatus): Result<Unit>
    suspend fun scheduleReview(userId: String, reviewDate: LocalDateTime): Result<Unit>
}