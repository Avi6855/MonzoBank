package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class Walkthrough(
    val id: String,
    val title: String,
    val description: String,
    val feature: String,
    val steps: List<WalkthroughStep>,
    val isActive: Boolean,
    val targetVersion: String?,
    val createdAt: LocalDateTime
)

data class WalkthroughStep(
    val id: String,
    val title: String,
    val description: String,
    val targetElement: String?,
    val position: WalkthroughPosition,
    val order: Int
)

data class WalkthroughProgress(
    val id: String,
    val userId: String,
    val walkthroughId: String,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    val skippedAt: LocalDateTime?
)

enum class WalkthroughPosition {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    CENTER
}

interface WalkthroughRepository {
    suspend fun getWalkthroughs(): Result<List<Walkthrough>>
    suspend fun getWalkthrough(walkthroughId: String): Result<Walkthrough?>
    suspend fun getWalkthroughsForFeature(feature: String): Result<List<Walkthrough>>
    suspend fun markWalkthroughCompleted(userId: String, walkthroughId: String): Result<Unit>
    suspend fun skipWalkthrough(userId: String, walkthroughId: String): Result<Unit>
    suspend fun getUserProgress(userId: String): Result<List<WalkthroughProgress>>
    suspend fun shouldShowWalkthrough(userId: String, walkthroughId: String): Result<Boolean>
}