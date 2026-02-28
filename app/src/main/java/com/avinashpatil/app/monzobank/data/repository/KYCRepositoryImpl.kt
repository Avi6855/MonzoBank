package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KYCRepositoryImpl @Inject constructor() : KYCRepository {
    
    private val documents = mutableListOf<KYCDocument>()
    private val profiles = mutableMapOf<String, KYCProfile>()
    
    override suspend fun uploadDocument(document: KYCDocument): Result<String> {
        return try {
            documents.add(document)
            updateProfileAfterDocumentUpload(document.userId)
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyDocument(documentId: String, isValid: Boolean, notes: String?): Result<Unit> {
        return try {
            val index = documents.indexOfFirst { it.id == documentId }
            if (index != -1) {
                val status = if (isValid) DocumentStatus.VERIFIED else DocumentStatus.REJECTED
                val updated = documents[index].copy(
                    status = status,
                    verifiedAt = if (isValid) LocalDateTime.now() else null
                )
                documents[index] = updated
                updateProfileAfterDocumentUpload(updated.userId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getKYCProfile(userId: String): Result<KYCProfile> {
        return try {
            val profile = profiles[userId] ?: createDefaultProfile(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateRiskLevel(userId: String, riskLevel: KYCRiskLevel): Result<Unit> {
        return try {
            val currentProfile = profiles[userId] ?: createDefaultProfile(userId)
            val updated = currentProfile.copy(
                riskLevel = riskLevel,
                lastUpdated = LocalDateTime.now()
            )
            profiles[userId] = updated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDocuments(userId: String): Result<List<KYCDocument>> {
        return try {
            val userDocuments = documents.filter { it.userId == userId }
            Result.success(userDocuments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteDocument(documentId: String): Result<Unit> {
        return try {
            val document = documents.find { it.id == documentId }
            documents.removeIf { it.id == documentId }
            document?.let { updateProfileAfterDocumentUpload(it.userId) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun performKYCCheck(userId: String): Result<KYCProfile> {
        return try {
            val userDocuments = documents.filter { it.userId == userId }
            val verifiedDocs = userDocuments.filter { it.status == DocumentStatus.VERIFIED }
            
            val completionStatus = when {
                verifiedDocs.isEmpty() -> KYCStatus.NOT_STARTED
                verifiedDocs.size < 2 -> KYCStatus.IN_PROGRESS
                else -> KYCStatus.COMPLETED
            }
            
            val riskLevel = calculateRiskLevel(userDocuments)
            val verificationScore = calculateVerificationScore(verifiedDocs)
            
            val profile = KYCProfile(
                userId = userId,
                riskLevel = riskLevel,
                completionStatus = completionStatus,
                lastUpdated = LocalDateTime.now(),
                documents = userDocuments,
                verificationScore = verificationScore,
                notes = null
            )
            
            profiles[userId] = profile
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExpiredDocuments(): Result<List<KYCDocument>> {
        return try {
            val now = LocalDateTime.now()
            val expired = documents.filter { doc ->
                doc.expiryDate?.isBefore(now) == true
            }
            Result.success(expired)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPendingVerifications(): Result<List<KYCDocument>> {
        return try {
            val pending = documents.filter { it.status == DocumentStatus.PENDING }
            Result.success(pending)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateKYCReport(userId: String): Result<String> {
        return try {
            val reportId = UUID.randomUUID().toString()
            // Mock report generation
            Result.success(reportId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateKYCStatus(userId: String, status: KYCStatus): Result<Unit> {
        return try {
            val currentProfile = profiles[userId] ?: createDefaultProfile(userId)
            val updated = currentProfile.copy(
                completionStatus = status,
                lastUpdated = LocalDateTime.now()
            )
            profiles[userId] = updated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun scheduleReview(userId: String, reviewDate: LocalDateTime): Result<Unit> {
        return try {
            // Mock scheduling
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createDefaultProfile(userId: String): KYCProfile {
        val userDocuments = documents.filter { it.userId == userId }
        return KYCProfile(
            userId = userId,
            riskLevel = KYCRiskLevel.MEDIUM,
            completionStatus = KYCStatus.NOT_STARTED,
            lastUpdated = LocalDateTime.now(),
            documents = userDocuments,
            verificationScore = 0,
            notes = null
        )
    }
    
    private fun updateProfileAfterDocumentUpload(userId: String) {
        val userDocuments = documents.filter { it.userId == userId }
        val verifiedDocs = userDocuments.filter { it.status == DocumentStatus.VERIFIED }
        
        val completionStatus = when {
            verifiedDocs.isEmpty() -> KYCStatus.NOT_STARTED
            verifiedDocs.size < 2 -> KYCStatus.IN_PROGRESS
            else -> KYCStatus.COMPLETED
        }
        
        val currentProfile = profiles[userId] ?: createDefaultProfile(userId)
        val updated = currentProfile.copy(
            completionStatus = completionStatus,
            lastUpdated = LocalDateTime.now(),
            documents = userDocuments,
            verificationScore = calculateVerificationScore(verifiedDocs)
        )
        profiles[userId] = updated
    }
    
    private fun calculateRiskLevel(documents: List<KYCDocument>): KYCRiskLevel {
        val verifiedCount = documents.count { it.status == DocumentStatus.VERIFIED }
        return when {
            verifiedCount >= 3 -> KYCRiskLevel.LOW
            verifiedCount >= 2 -> KYCRiskLevel.MEDIUM
            verifiedCount >= 1 -> KYCRiskLevel.HIGH
            else -> KYCRiskLevel.VERY_HIGH
        }
    }
    
    private fun calculateVerificationScore(verifiedDocs: List<KYCDocument>): Int {
        return verifiedDocs.size * 25 // Simple scoring: 25 points per verified document
    }
}