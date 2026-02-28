package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class SanctionsEntry(
    val id: String,
    val name: String,
    val aliases: List<String>,
    val sanctionType: SanctionType,
    val country: String?,
    val dateOfBirth: LocalDateTime?,
    val addedDate: LocalDateTime,
    val isActive: Boolean = true
)

enum class SanctionType {
    INDIVIDUAL, ENTITY, COUNTRY, VESSEL, AIRCRAFT
}

data class SanctionsMatch(
    val id: String,
    val userId: String,
    val sanctionsEntryId: String,
    val matchScore: Double,
    val matchType: MatchType,
    val status: MatchStatus,
    val reviewedBy: String?,
    val reviewedAt: LocalDateTime?,
    val createdAt: LocalDateTime
)

enum class MatchType {
    EXACT_NAME, PARTIAL_NAME, ALIAS, DATE_OF_BIRTH, ADDRESS
}

enum class MatchStatus {
    PENDING_REVIEW, CONFIRMED_MATCH, FALSE_POSITIVE, ESCALATED
}

data class SanctionsScreeningResult(
    val userId: String,
    val screeningId: String,
    val matches: List<SanctionsMatch>,
    val overallRiskScore: Double,
    val screenedAt: LocalDateTime,
    val isCleared: Boolean
)

interface SanctionsRepository {
    suspend fun screenUser(userId: String, userName: String, dateOfBirth: LocalDateTime?): Result<SanctionsScreeningResult>
    suspend fun getMatches(userId: String): Result<List<SanctionsMatch>>
    suspend fun updateMatchStatus(matchId: String, status: MatchStatus, reviewerId: String): Result<Unit>
    suspend fun getPendingReviews(): Result<List<SanctionsMatch>>
    suspend fun addSanctionsEntry(entry: SanctionsEntry): Result<String>
    suspend fun updateSanctionsEntry(entryId: String, entry: SanctionsEntry): Result<Unit>
    suspend fun searchSanctionsList(query: String): Result<List<SanctionsEntry>>
    suspend fun getSanctionsEntry(entryId: String): Result<SanctionsEntry?>
    suspend fun performBulkScreening(userIds: List<String>): Result<List<SanctionsScreeningResult>>
    suspend fun getScreeningHistory(userId: String): Result<List<SanctionsScreeningResult>>
    suspend fun updateSanctionsList(): Result<Int>
    suspend fun getHighRiskMatches(): Result<List<SanctionsMatch>>
}