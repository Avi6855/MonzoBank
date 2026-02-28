package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor() : OnboardingRepository {
    
    private val flows = listOf(
        OnboardingFlow(
            id = "flow1",
            name = "New User Onboarding",
            description = "Welcome new users to Monzo",
            steps = listOf(
                OnboardingStep(
                    id = "step1",
                    title = "Welcome to Monzo",
                    description = "Your new banking experience starts here",
                    imageUrl = null,
                    videoUrl = null,
                    actionText = "Get Started",
                    actionUrl = null,
                    order = 1,
                    isRequired = true
                ),
                OnboardingStep(
                    id = "step2",
                    title = "Verify Your Identity",
                    description = "We need to verify your identity for security",
                    imageUrl = null,
                    videoUrl = null,
                    actionText = "Verify Now",
                    actionUrl = "/verify",
                    order = 2,
                    isRequired = true
                )
            ),
            targetAudience = "new_users",
            isActive = true
        )
    )
    
    private val progressList = mutableListOf<OnboardingProgress>()
    
    override suspend fun getOnboardingFlows(): Result<List<OnboardingFlow>> {
        return try {
            val activeFlows = flows.filter { it.isActive }
            Result.success(activeFlows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOnboardingFlow(flowId: String): Result<OnboardingFlow?> {
        return try {
            val flow = flows.find { it.id == flowId && it.isActive }
            Result.success(flow)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun startOnboarding(userId: String, flowId: String): Result<String> {
        return try {
            val existingProgress = progressList.find { 
                it.userId == userId && it.flowId == flowId 
            }
            
            if (existingProgress == null) {
                val progress = OnboardingProgress(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    flowId = flowId,
                    currentStep = 1,
                    isCompleted = false,
                    completedSteps = emptyList(),
                    startedAt = LocalDateTime.now(),
                    completedAt = null,
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
    
    override suspend fun updateProgress(userId: String, flowId: String, stepId: String): Result<Unit> {
        return try {
            val index = progressList.indexOfFirst { 
                it.userId == userId && it.flowId == flowId 
            }
            
            if (index != -1) {
                val progress = progressList[index]
                val updatedCompletedSteps = progress.completedSteps.toMutableList()
                if (!updatedCompletedSteps.contains(stepId)) {
                    updatedCompletedSteps.add(stepId)
                }
                
                val flow = flows.find { it.id == flowId }
                val stepOrder = flow?.steps?.find { it.id == stepId }?.order ?: 0
                
                val updated = progress.copy(
                    currentStep = stepOrder + 1,
                    completedSteps = updatedCompletedSteps,
                    lastAccessedAt = LocalDateTime.now()
                )
                progressList[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Onboarding progress not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completeOnboarding(userId: String, flowId: String): Result<Unit> {
        return try {
            val index = progressList.indexOfFirst { 
                it.userId == userId && it.flowId == flowId 
            }
            
            if (index != -1) {
                val progress = progressList[index]
                val flow = flows.find { it.id == flowId }
                val allStepIds = flow?.steps?.map { it.id } ?: emptyList()
                
                val updated = progress.copy(
                    isCompleted = true,
                    completedSteps = allStepIds,
                    completedAt = LocalDateTime.now(),
                    lastAccessedAt = LocalDateTime.now()
                )
                progressList[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Onboarding progress not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserProgress(userId: String): Result<List<OnboardingProgress>> {
        return try {
            val userProgress = progressList.filter { it.userId == userId }
                .sortedByDescending { it.lastAccessedAt }
            Result.success(userProgress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOnboardingProgress(userId: String, flowId: String): Result<OnboardingProgress?> {
        return try {
            val progress = progressList.find { 
                it.userId == userId && it.flowId == flowId 
            }
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun skipStep(userId: String, flowId: String, stepId: String): Result<Unit> {
        return try {
            val index = progressList.indexOfFirst { 
                it.userId == userId && it.flowId == flowId 
            }
            
            if (index != -1) {
                val progress = progressList[index]
                val flow = flows.find { it.id == flowId }
                val step = flow?.steps?.find { it.id == stepId }
                
                if (step != null && !step.isRequired) {
                    val updatedCompletedSteps = progress.completedSteps.toMutableList()
                    if (!updatedCompletedSteps.contains(stepId)) {
                        updatedCompletedSteps.add(stepId)
                    }
                    
                    val updated = progress.copy(
                        currentStep = step.order + 1,
                        completedSteps = updatedCompletedSteps,
                        lastAccessedAt = LocalDateTime.now()
                    )
                    progressList[index] = updated
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Step cannot be skipped"))
                }
            } else {
                Result.failure(Exception("Onboarding progress not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}