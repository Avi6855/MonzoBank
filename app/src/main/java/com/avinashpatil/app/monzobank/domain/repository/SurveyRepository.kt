package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Survey(
    val id: String,
    val title: String,
    val description: String,
    val type: SurveyType,
    val status: SurveyStatus,
    val targetAudience: List<String>,
    val questions: List<SurveyQuestion>,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isAnonymous: Boolean,
    val maxResponses: Int?,
    val responseCount: Int,
    val estimatedDuration: Int, // in minutes
    val incentive: SurveyIncentive?,
    val createdBy: String,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime
)

data class SurveyQuestion(
    val id: String,
    val surveyId: String,
    val questionText: String,
    val questionType: QuestionType,
    val isRequired: Boolean,
    val order: Int,
    val options: List<QuestionOption> = emptyList(),
    val validation: QuestionValidation?
)

data class QuestionOption(
    val id: String,
    val text: String,
    val value: String,
    val order: Int
)

data class QuestionValidation(
    val minLength: Int?,
    val maxLength: Int?,
    val minValue: BigDecimal?,
    val maxValue: BigDecimal?,
    val pattern: String?
)

data class SurveyResponse(
    val id: String,
    val surveyId: String,
    val userId: String?,
    val answers: List<SurveyAnswer>,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
    val isCompleted: Boolean,
    val ipAddress: String?,
    val userAgent: String?,
    val deviceInfo: String?
)

data class SurveyAnswer(
    val id: String,
    val responseId: String,
    val questionId: String,
    val answerText: String?,
    val selectedOptions: List<String> = emptyList(),
    val numericValue: BigDecimal?,
    val answeredAt: LocalDateTime
)

data class SurveyIncentive(
    val type: IncentiveType,
    val value: BigDecimal,
    val description: String,
    val eligibilityCriteria: List<String>
)

data class SurveyAnalytics(
    val surveyId: String,
    val totalResponses: Int,
    val completedResponses: Int,
    val partialResponses: Int,
    val completionRate: Double,
    val averageDuration: Double, // in minutes
    val responsesByDate: Map<String, Int>,
    val questionAnalytics: List<QuestionAnalytics>
)

data class QuestionAnalytics(
    val questionId: String,
    val questionText: String,
    val responseCount: Int,
    val skipCount: Int,
    val averageRating: Double?,
    val optionCounts: Map<String, Int>,
    val textResponses: List<String>
)

enum class SurveyType {
    CUSTOMER_SATISFACTION,
    PRODUCT_FEEDBACK,
    USER_EXPERIENCE,
    MARKET_RESEARCH,
    EMPLOYEE_FEEDBACK,
    NET_PROMOTER_SCORE,
    FEATURE_REQUEST,
    USABILITY_TESTING,
    BRAND_PERCEPTION,
    DEMOGRAPHIC
}

enum class SurveyStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED,
    ARCHIVED
}

enum class QuestionType {
    SINGLE_CHOICE,
    MULTIPLE_CHOICE,
    TEXT_SHORT,
    TEXT_LONG,
    RATING_SCALE,
    LIKERT_SCALE,
    NUMERIC,
    DATE,
    EMAIL,
    PHONE,
    URL,
    MATRIX,
    RANKING
}

enum class IncentiveType {
    CASH_REWARD,
    POINTS,
    DISCOUNT_COUPON,
    GIFT_CARD,
    CHARITY_DONATION,
    ENTRY_TO_DRAW,
    PREMIUM_FEATURE_ACCESS
}

interface SurveyRepository {
    suspend fun getSurveys(): Result<List<Survey>>
    suspend fun getSurvey(surveyId: String): Result<Survey?>
    suspend fun createSurvey(survey: Survey): Result<String>
    suspend fun updateSurvey(survey: Survey): Result<Unit>
    suspend fun deleteSurvey(surveyId: String): Result<Unit>
    suspend fun getActiveSurveys(): Result<List<Survey>>
    suspend fun getSurveysByType(type: SurveyType): Result<List<Survey>>
    suspend fun getSurveysByStatus(status: SurveyStatus): Result<List<Survey>>
    suspend fun getSurveysForUser(userId: String): Result<List<Survey>>
    suspend fun getSurveyQuestions(surveyId: String): Result<List<SurveyQuestion>>
    suspend fun addQuestionToSurvey(question: SurveyQuestion): Result<String>
    suspend fun updateSurveyQuestion(question: SurveyQuestion): Result<Unit>
    suspend fun deleteQuestionFromSurvey(questionId: String): Result<Unit>
    suspend fun reorderSurveyQuestions(surveyId: String, questionIds: List<String>): Result<Unit>
    suspend fun getSurveyResponses(surveyId: String): Result<List<SurveyResponse>>
    suspend fun getSurveyResponse(responseId: String): Result<SurveyResponse?>
    suspend fun getUserSurveyResponses(userId: String): Result<List<SurveyResponse>>
    suspend fun startSurveyResponse(surveyId: String, userId: String?): Result<String>
    suspend fun saveSurveyAnswer(answer: SurveyAnswer): Result<Unit>
    suspend fun completeSurveyResponse(responseId: String): Result<Unit>
    suspend fun getSurveyAnalytics(surveyId: String): Result<SurveyAnalytics>
    suspend fun exportSurveyResponses(surveyId: String, format: String): Result<String>
    suspend fun duplicateSurvey(surveyId: String, newTitle: String): Result<String>
    suspend fun publishSurvey(surveyId: String): Result<Unit>
    suspend fun pauseSurvey(surveyId: String): Result<Unit>
    suspend fun resumeSurvey(surveyId: String): Result<Unit>
    suspend fun archiveSurvey(surveyId: String): Result<Unit>
    suspend fun getQuestionAnalytics(questionId: String): Result<QuestionAnalytics>
    suspend fun getSurveyParticipants(surveyId: String): Result<List<String>>
    suspend fun sendSurveyInvitation(surveyId: String, userIds: List<String>): Result<Unit>
}