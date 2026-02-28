package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SanctionsRepositoryImpl @Inject constructor() : SanctionsRepository {
    
    private val sanctionsEntries = mutableListOf<SanctionsEntry>()
    private val matches = mutableListOf<SanctionsMatch>()
    private val screeningResults = mutableListOf<SanctionsScreeningResult>()
    
    init {
        // Initialize with some mock sanctions entries
        sanctionsEntries.addAll(listOf(
            SanctionsEntry(
                id = "1",
                name = "John Doe Sanctioned",
                aliases = listOf("J. Doe", "Johnny Doe"),
                sanctionType = SanctionType.INDIVIDUAL,
                country = "Country X",
                dateOfBirth = LocalDateTime.of(1980, 1, 1, 0, 0),
                addedDate = LocalDateTime.now().minusYears(2)
            ),
            SanctionsEntry(
                id = "2",
                name = "Evil Corp",
                aliases = listOf("E-Corp", "Evil Corporation"),
                sanctionType = SanctionType.ENTITY,
                country = "Country Y",
                dateOfBirth = null,
                addedDate = LocalDateTime.now().minusYears(1)
            )
        ))
    }
    
    override suspend fun screenUser(userId: String, userName: String, dateOfBirth: LocalDateTime?): Result<SanctionsScreeningResult> {
        return try {
            val screeningId = UUID.randomUUID().toString()
            val foundMatches = mutableListOf<SanctionsMatch>()
            
            // Perform name matching
            sanctionsEntries.forEach { entry ->
                val nameScore = calculateNameMatchScore(userName, entry.name)
                val aliasScore = entry.aliases.maxOfOrNull { alias ->
                    calculateNameMatchScore(userName, alias)
                } ?: 0.0
                
                val maxScore = maxOf(nameScore, aliasScore)
                
                if (maxScore > 0.7) { // Threshold for potential match
                    val matchType = if (nameScore > aliasScore) MatchType.EXACT_NAME else MatchType.ALIAS
                    val match = SanctionsMatch(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        sanctionsEntryId = entry.id,
                        matchScore = maxScore,
                        matchType = matchType,
                        status = MatchStatus.PENDING_REVIEW,
                        reviewedBy = null,
                        reviewedAt = null,
                        createdAt = LocalDateTime.now()
                    )
                    foundMatches.add(match)
                    matches.add(match)
                }
            }
            
            val overallRiskScore = if (foundMatches.isNotEmpty()) {
                foundMatches.maxOf { it.matchScore }
            } else {
                0.0
            }
            
            val result = SanctionsScreeningResult(
                userId = userId,
                screeningId = screeningId,
                matches = foundMatches,
                overallRiskScore = overallRiskScore,
                screenedAt = LocalDateTime.now(),
                isCleared = foundMatches.isEmpty()
            )
            
            screeningResults.add(result)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMatches(userId: String): Result<List<SanctionsMatch>> {
        return try {
            val userMatches = matches.filter { it.userId == userId }
            Result.success(userMatches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMatchStatus(matchId: String, status: MatchStatus, reviewerId: String): Result<Unit> {
        return try {
            val index = matches.indexOfFirst { it.id == matchId }
            if (index != -1) {
                val updated = matches[index].copy(
                    status = status,
                    reviewedBy = reviewerId,
                    reviewedAt = LocalDateTime.now()
                )
                matches[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Match not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPendingReviews(): Result<List<SanctionsMatch>> {
        return try {
            val pending = matches.filter { it.status == MatchStatus.PENDING_REVIEW }
            Result.success(pending)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addSanctionsEntry(entry: SanctionsEntry): Result<String> {
        return try {
            sanctionsEntries.add(entry)
            Result.success(entry.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSanctionsEntry(entryId: String, entry: SanctionsEntry): Result<Unit> {
        return try {
            val index = sanctionsEntries.indexOfFirst { it.id == entryId }
            if (index != -1) {
                sanctionsEntries[index] = entry
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sanctions entry not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchSanctionsList(query: String): Result<List<SanctionsEntry>> {
        return try {
            val results = sanctionsEntries.filter { entry ->
                entry.name.contains(query, ignoreCase = true) ||
                entry.aliases.any { it.contains(query, ignoreCase = true) } ||
                entry.country?.contains(query, ignoreCase = true) == true
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSanctionsEntry(entryId: String): Result<SanctionsEntry?> {
        return try {
            val entry = sanctionsEntries.find { it.id == entryId }
            Result.success(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun performBulkScreening(userIds: List<String>): Result<List<SanctionsScreeningResult>> {
        return try {
            val results = mutableListOf<SanctionsScreeningResult>()
            userIds.forEach { userId ->
                // Mock bulk screening - in real implementation, would get user details
                val result = screenUser(userId, "User $userId", null).getOrNull()
                result?.let { results.add(it) }
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getScreeningHistory(userId: String): Result<List<SanctionsScreeningResult>> {
        return try {
            val history = screeningResults.filter { it.userId == userId }
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSanctionsList(): Result<Int> {
        return try {
            // Mock update - in real implementation, would fetch from external source
            val newEntriesCount = 5
            Result.success(newEntriesCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getHighRiskMatches(): Result<List<SanctionsMatch>> {
        return try {
            val highRisk = matches.filter { it.matchScore > 0.9 }
            Result.success(highRisk)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateNameMatchScore(name1: String, name2: String): Double {
        // Simple similarity calculation - in real implementation, would use more sophisticated algorithm
        val cleanName1 = name1.lowercase().replace("[^a-z]".toRegex(), "")
        val cleanName2 = name2.lowercase().replace("[^a-z]".toRegex(), "")
        
        if (cleanName1 == cleanName2) return 1.0
        if (cleanName1.contains(cleanName2) || cleanName2.contains(cleanName1)) return 0.8
        
        // Calculate Levenshtein distance ratio
        val maxLength = maxOf(cleanName1.length, cleanName2.length)
        if (maxLength == 0) return 1.0
        
        val distance = levenshteinDistance(cleanName1, cleanName2)
        return 1.0 - (distance.toDouble() / maxLength)
    }
    
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        
        return dp[s1.length][s2.length]
    }
}