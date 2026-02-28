package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TutorialRepositoryImpl @Inject constructor() : TutorialRepository {
    
    private val tutorials = listOf(
        Tutorial(
            id = "tut1",
            title = "Getting Started with Monzo",
            description = "Learn the basics of using your Monzo account",
            category = "Basics",
            steps = listOf(
                TutorialStep(
                    id = "step1",
                    title = "Welcome to Monzo",
                    content = "Welcome to your new Monzo account! Let's get you started.",
                    imageUrl = null,
                    videoUrl = null,
                    order = 1
                ),
                TutorialStep(
                    id = "step2",
                    title = "Your Dashboard",
                    content = "This is your main dashboard where you can see your balance and recent transactions.",
                    imageUrl = null,
                    videoUrl = null,
                    order = 2
                )
            ),
            duration = 5,
            difficulty = TutorialDifficulty.BEGINNER,
            isPublished = true,
            completionCount = 1250,
            rating = 4.8,
            createdAt = LocalDateTime.now().minusDays(30),
            updatedAt = LocalDateTime.now().minusDays(5)
        )
    )
    
    private val progressList = mutableListOf<TutorialProgress>()
    
    override suspend fun getTutorials(): Result<List<Tutorial>> {
        return try {
            val publishedTutorials = tutorials.filter { it.isPublished }
                .sortedBy { it.difficulty }
            Result.success(publishedTutorials)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTutorial(tutorialId: String): Result<Tutorial?> {
        return try {
            val tutorial = tutorials.find { it.id == tutorialId && it.isPublished }
            Result.success(tutorial)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTutorialsByCategory(category: String): Result<List<Tutorial>> {
        return try {
            val categoryTutorials = tutorials.filter { 
                it.category.equals(category, ignoreCase = true) && it.isPublished 
            }.sortedBy { it.difficulty }
            Result.success(categoryTutorials)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun startTutorial(userId: String, tutorialId: String): Result<String> {
        return try {
            val existingProgress = progressList.find { 
                it.userId == userId && it.tutorialId == tutorialId 
            }
            
            if (existingProgress == null) {
                val progress = TutorialProgress(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    tutorialId = tutorialId,
                    currentStep = 1,
                    isCompleted = false,
                    completedAt = null,
                    startedAt = LocalDateTime.now(),
                    lastAccessedAt = LocalDateTime.now()
                )
                progressList.add(progress)
                Result.success(progress.id)
            } else {
                // Update last accessed time
                val index = progressList.indexOfFirst { it.id == existingProgress.id }
                progressList[index] = existingProgress.copy(lastAccessedAt = LocalDateTime.now())
                Result.success(existingProgress.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProgress(userId: String, tutorialId: String, currentStep: Int): Result<Unit> {
        return try {
            val index = progressList.indexOfFirst { 
                it.userId == userId && it.tutorialId == tutorialId 
            }
            
            if (index != -1) {
                val progress = progressList[index]
                val updated = progress.copy(
                    currentStep = currentStep,
                    lastAccessedAt = LocalDateTime.now()
                )
                progressList[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tutorial progress not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completeTutorial(userId: String, tutorialId: String): Result<Unit> {
        return try {
            val index = progressList.indexOfFirst { 
                it.userId == userId && it.tutorialId == tutorialId 
            }
            
            if (index != -1) {
                val progress = progressList[index]
                val tutorial = tutorials.find { it.id == tutorialId }
                val updated = progress.copy(
                    currentStep = tutorial?.steps?.size ?: progress.currentStep,
                    isCompleted = true,
                    completedAt = LocalDateTime.now(),
                    lastAccessedAt = LocalDateTime.now()
                )
                progressList[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tutorial progress not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserProgress(userId: String): Result<List<TutorialProgress>> {
        return try {
            val userProgress = progressList.filter { it.userId == userId }
                .sortedByDescending { it.lastAccessedAt }
            Result.success(userProgress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTutorialProgress(userId: String, tutorialId: String): Result<TutorialProgress?> {
        return try {
            val progress = progressList.find { 
                it.userId == userId && it.tutorialId == tutorialId 
            }
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecommendedTutorials(userId: String): Result<List<Tutorial>> {
        return try {
            val userProgress = progressList.filter { it.userId == userId }
            val completedTutorialIds = userProgress.filter { it.isCompleted }.map { it.tutorialId }
            
            val recommendedTutorials = tutorials.filter { tutorial ->
                tutorial.isPublished && !completedTutorialIds.contains(tutorial.id)
            }.sortedBy { it.difficulty }
            
            Result.success(recommendedTutorials)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}