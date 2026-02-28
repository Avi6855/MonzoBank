package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class Tutorial(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val steps: List<TutorialStep>,
    val duration: Int, // in minutes
    val difficulty: TutorialDifficulty,
    val isPublished: Boolean,
    val completionCount: Int,
    val rating: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class TutorialStep(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val videoUrl: String?,
    val order: Int
)

data class TutorialProgress(
    val id: String,
    val userId: String,
    val tutorialId: String,
    val currentStep: Int,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    val startedAt: LocalDateTime,
    val lastAccessedAt: LocalDateTime
)

enum class TutorialDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

interface TutorialRepository {
    suspend fun getTutorials(): Result<List<Tutorial>>
    suspend fun getTutorial(tutorialId: String): Result<Tutorial?>
    suspend fun getTutorialsByCategory(category: String): Result<List<Tutorial>>
    suspend fun startTutorial(userId: String, tutorialId: String): Result<String>
    suspend fun updateProgress(userId: String, tutorialId: String, currentStep: Int): Result<Unit>
    suspend fun completeTutorial(userId: String, tutorialId: String): Result<Unit>
    suspend fun getUserProgress(userId: String): Result<List<TutorialProgress>>
    suspend fun getTutorialProgress(userId: String, tutorialId: String): Result<TutorialProgress?>
    suspend fun getRecommendedTutorials(userId: String): Result<List<Tutorial>>
}