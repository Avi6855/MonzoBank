package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class OnboardingStep(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val videoUrl: String?,
    val actionText: String?,
    val actionUrl: String?,
    val order: Int,
    val isRequired: Boolean
)

data class OnboardingFlow(
    val id: String,
    val name: String,
    val description: String,
    val steps: List<OnboardingStep>,
    val targetAudience: String,
    val isActive: Boolean
)

data class OnboardingProgress(
    val id: String,
    val userId: String,
    val flowId: String,
    val currentStep: Int,
    val isCompleted: Boolean,
    val completedSteps: List<String>,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
    val lastAccessedAt: LocalDateTime
)

interface OnboardingRepository {
    suspend fun getOnboardingFlows(): Result<List<OnboardingFlow>>
    suspend fun getOnboardingFlow(flowId: String): Result<OnboardingFlow?>
    suspend fun startOnboarding(userId: String, flowId: String): Result<String>
    suspend fun updateProgress(userId: String, flowId: String, stepId: String): Result<Unit>
    suspend fun completeOnboarding(userId: String, flowId: String): Result<Unit>
    suspend fun getUserProgress(userId: String): Result<List<OnboardingProgress>>
    suspend fun getOnboardingProgress(userId: String, flowId: String): Result<OnboardingProgress?>
    suspend fun skipStep(userId: String, flowId: String, stepId: String): Result<Unit>
}