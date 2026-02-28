package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyRepositoryImpl @Inject constructor() : SurveyRepository {
    
    private val surveys = mutableListOf<Survey>()
    private val surveyQuestions = mutableListOf<SurveyQuestion>()
    private val surveyResponses = mutableListOf<SurveyResponse>()
    private val surveyAnswers = mutableListOf<SurveyAnswer>()
    
    init {
        initializeMockData()
    }
    
    override suspend fun getSurveys(): Result<List<Survey>> {
        return try {
            val sortedSurveys = surveys.sortedByDescending { it.createdDate }
            Result.success(sortedSurveys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurvey(surveyId: String): Result<Survey?> {
        return try {
            val survey = surveys.find { it.id == surveyId }
            Result.success(survey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createSurvey(survey: Survey): Result<String> {
        return try {
            surveys.add(survey)
            Result.success(survey.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSurvey(survey: Survey): Result<Unit> {
        return try {
            val index = surveys.indexOfFirst { it.id == survey.id }
            if (index != -1) {
                surveys[index] = survey.copy(updatedDate = LocalDateTime.now())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Survey not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteSurvey(surveyId: String): Result<Unit> {
        return try {
            val removed = surveys.removeIf { it.id == surveyId }
            if (removed) {
                // Clean up related data
                surveyQuestions.removeIf { it.surveyId == surveyId }
                val responseIds = surveyResponses.filter { it.surveyId == surveyId }.map { it.id }
                surveyResponses.removeIf { it.surveyId == surveyId }
                surveyAnswers.removeIf { responseIds.contains(it.responseId) }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Survey not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveSurveys(): Result<List<Survey>> {
        return try {
            val now = LocalDateTime.now()
            val activeSurveys = surveys.filter { 
                it.status == SurveyStatus.ACTIVE &&
                it.startDate.isBefore(now) &&
                it.endDate.isAfter(now) &&
                (it.maxResponses == null || it.responseCount < it.maxResponses)
            }.sortedBy { it.endDate }
            
            Result.success(activeSurveys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveysByType(type: SurveyType): Result<List<Survey>> {
        return try {
            val typeSurveys = surveys.filter { it.type == type }
                .sortedByDescending { it.createdDate }
            Result.success(typeSurveys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveysByStatus(status: SurveyStatus): Result<List<Survey>> {
        return try {
            val statusSurveys = surveys.filter { it.status == status }
                .sortedByDescending { it.createdDate }
            Result.success(statusSurveys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveysForUser(userId: String): Result<List<Survey>> {
        return try {
            val now = LocalDateTime.now()
            val userSurveys = surveys.filter { survey ->
                survey.status == SurveyStatus.ACTIVE &&
                survey.startDate.isBefore(now) &&
                survey.endDate.isAfter(now) &&
                (survey.maxResponses == null || survey.responseCount < survey.maxResponses) &&
                (survey.targetAudience.isEmpty() || survey.targetAudience.contains("all_users")) &&
                !surveyResponses.any { it.surveyId == survey.id && it.userId == userId && it.isCompleted }
            }.sortedBy { it.endDate }
            
            Result.success(userSurveys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveyQuestions(surveyId: String): Result<List<SurveyQuestion>> {
        return try {
            val questions = surveyQuestions.filter { it.surveyId == surveyId }
                .sortedBy { it.order }
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addQuestionToSurvey(question: SurveyQuestion): Result<String> {
        return try {
            surveyQuestions.add(question)
            Result.success(question.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSurveyQuestion(question: SurveyQuestion): Result<Unit> {
        return try {
            val index = surveyQuestions.indexOfFirst { it.id == question.id }
            if (index != -1) {
                surveyQuestions[index] = question
                Result.success(Unit)
            } else {
                Result.failure(Exception("Question not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteQuestionFromSurvey(questionId: String): Result<Unit> {
        return try {
            val removed = surveyQuestions.removeIf { it.id == questionId }
            if (removed) {
                // Remove related answers
                surveyAnswers.removeIf { it.questionId == questionId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Question not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reorderSurveyQuestions(surveyId: String, questionIds: List<String>): Result<Unit> {
        return try {
            questionIds.forEachIndexed { index, questionId ->
                val questionIndex = surveyQuestions.indexOfFirst { it.id == questionId && it.surveyId == surveyId }
                if (questionIndex != -1) {
                    val question = surveyQuestions[questionIndex]
                    surveyQuestions[questionIndex] = question.copy(order = index + 1)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveyResponses(surveyId: String): Result<List<SurveyResponse>> {
        return try {
            val responses = surveyResponses.filter { it.surveyId == surveyId }
                .sortedByDescending { it.startedAt }
            Result.success(responses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveyResponse(responseId: String): Result<SurveyResponse?> {
        return try {
            val response = surveyResponses.find { it.id == responseId }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserSurveyResponses(userId: String): Result<List<SurveyResponse>> {
        return try {
            val userResponses = surveyResponses.filter { it.userId == userId }
                .sortedByDescending { it.startedAt }
            Result.success(userResponses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun startSurveyResponse(surveyId: String, userId: String?): Result<String> {
        return try {
            val survey = surveys.find { it.id == surveyId }
                ?: return Result.failure(Exception("Survey not found"))
            
            // Check if user already has a response for this survey
            if (userId != null) {
                val existingResponse = surveyResponses.find { 
                    it.surveyId == surveyId && it.userId == userId 
                }
                if (existingResponse != null) {
                    return Result.success(existingResponse.id)
                }
            }
            
            val response = SurveyResponse(
                id = UUID.randomUUID().toString(),
                surveyId = surveyId,
                userId = userId,
                answers = emptyList(),
                startedAt = LocalDateTime.now(),
                completedAt = null,
                isCompleted = false,
                ipAddress = "192.168.1.1", // Mock IP
                userAgent = "MockUserAgent",
                deviceInfo = "MockDevice"
            )
            
            surveyResponses.add(response)
            Result.success(response.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun saveSurveyAnswer(answer: SurveyAnswer): Result<Unit> {
        return try {
            // Remove existing answer for this question in this response
            surveyAnswers.removeIf { 
                it.responseId == answer.responseId && it.questionId == answer.questionId 
            }
            
            surveyAnswers.add(answer)
            
            // Update response with current answers
            val responseIndex = surveyResponses.indexOfFirst { it.id == answer.responseId }
            if (responseIndex != -1) {
                val response = surveyResponses[responseIndex]
                val updatedAnswers = surveyAnswers.filter { it.responseId == answer.responseId }
                surveyResponses[responseIndex] = response.copy(answers = updatedAnswers)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completeSurveyResponse(responseId: String): Result<Unit> {
        return try {
            val index = surveyResponses.indexOfFirst { it.id == responseId }
            if (index != -1) {
                val response = surveyResponses[index]
                surveyResponses[index] = response.copy(
                    isCompleted = true,
                    completedAt = LocalDateTime.now()
                )
                
                // Update survey response count
                val surveyIndex = surveys.indexOfFirst { it.id == response.surveyId }
                if (surveyIndex != -1) {
                    val survey = surveys[surveyIndex]
                    surveys[surveyIndex] = survey.copy(
                        responseCount = survey.responseCount + 1,
                        updatedDate = LocalDateTime.now()
                    )
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Response not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveyAnalytics(surveyId: String): Result<SurveyAnalytics> {
        return try {
            val survey = surveys.find { it.id == surveyId }
                ?: return Result.failure(Exception("Survey not found"))
            
            val responses = surveyResponses.filter { it.surveyId == surveyId }
            val completedResponses = responses.filter { it.isCompleted }
            val partialResponses = responses.filter { !it.isCompleted }
            
            val completionRate = if (responses.isNotEmpty()) {
                (completedResponses.size.toDouble() / responses.size) * 100
            } else 0.0
            
            val averageDuration = completedResponses.mapNotNull { response ->
                response.completedAt?.let { completed ->
                    java.time.temporal.ChronoUnit.MINUTES.between(response.startedAt, completed).toDouble()
                }
            }.average().takeIf { !it.isNaN() } ?: 0.0
            
            // Mock response by date data
            val responsesByDate = mapOf(
                "2024-01-01" to 5,
                "2024-01-02" to 8,
                "2024-01-03" to 12,
                "2024-01-04" to 7,
                "2024-01-05" to 15
            )
            
            val questions = surveyQuestions.filter { it.surveyId == surveyId }
            val questionAnalytics = questions.map { question ->
                val questionAnswers = surveyAnswers.filter { it.questionId == question.id }
                
                QuestionAnalytics(
                    questionId = question.id,
                    questionText = question.questionText,
                    responseCount = questionAnswers.size,
                    skipCount = completedResponses.size - questionAnswers.size,
                    averageRating = questionAnswers.mapNotNull { it.numericValue?.toDouble() }.average().takeIf { !it.isNaN() },
                    optionCounts = questionAnswers.flatMap { it.selectedOptions }.groupingBy { it }.eachCount(),
                    textResponses = questionAnswers.mapNotNull { it.answerText }.take(10) // Limit to 10 for performance
                )
            }
            
            val analytics = SurveyAnalytics(
                surveyId = surveyId,
                totalResponses = responses.size,
                completedResponses = completedResponses.size,
                partialResponses = partialResponses.size,
                completionRate = completionRate,
                averageDuration = averageDuration,
                responsesByDate = responsesByDate,
                questionAnalytics = questionAnalytics
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportSurveyResponses(surveyId: String, format: String): Result<String> {
        return try {
            // Mock export functionality
            val responses = surveyResponses.filter { it.surveyId == surveyId }
            val exportId = UUID.randomUUID().toString()
            
            // In real implementation, this would generate actual file
            val mockExportUrl = "https://exports.monzobank.com/surveys/$surveyId/export_$exportId.$format"
            
            Result.success(mockExportUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun duplicateSurvey(surveyId: String, newTitle: String): Result<String> {
        return try {
            val originalSurvey = surveys.find { it.id == surveyId }
                ?: return Result.failure(Exception("Survey not found"))
            
            val newSurveyId = UUID.randomUUID().toString()
            val duplicatedSurvey = originalSurvey.copy(
                id = newSurveyId,
                title = newTitle,
                status = SurveyStatus.DRAFT,
                responseCount = 0,
                createdDate = LocalDateTime.now(),
                updatedDate = LocalDateTime.now()
            )
            
            surveys.add(duplicatedSurvey)
            
            // Duplicate questions
            val originalQuestions = surveyQuestions.filter { it.surveyId == surveyId }
            originalQuestions.forEach { question ->
                val newQuestion = question.copy(
                    id = UUID.randomUUID().toString(),
                    surveyId = newSurveyId
                )
                surveyQuestions.add(newQuestion)
            }
            
            Result.success(newSurveyId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun publishSurvey(surveyId: String): Result<Unit> {
        return try {
            val index = surveys.indexOfFirst { it.id == surveyId }
            if (index != -1) {
                val survey = surveys[index]
                if (survey.status == SurveyStatus.DRAFT) {
                    surveys[index] = survey.copy(
                        status = SurveyStatus.ACTIVE,
                        updatedDate = LocalDateTime.now()
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Only draft surveys can be published"))
                }
            } else {
                Result.failure(Exception("Survey not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pauseSurvey(surveyId: String): Result<Unit> {
        return try {
            val index = surveys.indexOfFirst { it.id == surveyId }
            if (index != -1) {
                val survey = surveys[index]
                if (survey.status == SurveyStatus.ACTIVE) {
                    surveys[index] = survey.copy(
                        status = SurveyStatus.PAUSED,
                        updatedDate = LocalDateTime.now()
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Only active surveys can be paused"))
                }
            } else {
                Result.failure(Exception("Survey not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resumeSurvey(surveyId: String): Result<Unit> {
        return try {
            val index = surveys.indexOfFirst { it.id == surveyId }
            if (index != -1) {
                val survey = surveys[index]
                if (survey.status == SurveyStatus.PAUSED) {
                    surveys[index] = survey.copy(
                        status = SurveyStatus.ACTIVE,
                        updatedDate = LocalDateTime.now()
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Only paused surveys can be resumed"))
                }
            } else {
                Result.failure(Exception("Survey not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun archiveSurvey(surveyId: String): Result<Unit> {
        return try {
            val index = surveys.indexOfFirst { it.id == surveyId }
            if (index != -1) {
                val survey = surveys[index]
                surveys[index] = survey.copy(
                    status = SurveyStatus.ARCHIVED,
                    updatedDate = LocalDateTime.now()
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Survey not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getQuestionAnalytics(questionId: String): Result<QuestionAnalytics> {
        return try {
            val question = surveyQuestions.find { it.id == questionId }
                ?: return Result.failure(Exception("Question not found"))
            
            val questionAnswers = surveyAnswers.filter { it.questionId == questionId }
            val completedResponses = surveyResponses.filter { 
                it.surveyId == question.surveyId && it.isCompleted 
            }
            
            val analytics = QuestionAnalytics(
                questionId = questionId,
                questionText = question.questionText,
                responseCount = questionAnswers.size,
                skipCount = completedResponses.size - questionAnswers.size,
                averageRating = questionAnswers.mapNotNull { it.numericValue?.toDouble() }.average().takeIf { !it.isNaN() },
                optionCounts = questionAnswers.flatMap { it.selectedOptions }.groupingBy { it }.eachCount(),
                textResponses = questionAnswers.mapNotNull { it.answerText }
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSurveyParticipants(surveyId: String): Result<List<String>> {
        return try {
            val participants = surveyResponses.filter { it.surveyId == surveyId }
                .mapNotNull { it.userId }
                .distinct()
            Result.success(participants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sendSurveyInvitation(surveyId: String, userIds: List<String>): Result<Unit> {
        return try {
            // Mock invitation sending
            // In real implementation, this would send actual invitations via email/push notifications
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun initializeMockData() {
        val mockSurveys = listOf(
            Survey(
                id = UUID.randomUUID().toString(),
                title = "Customer Satisfaction Survey",
                description = "Help us improve our banking services by sharing your feedback",
                type = SurveyType.CUSTOMER_SATISFACTION,
                status = SurveyStatus.ACTIVE,
                targetAudience = listOf("all_users"),
                questions = emptyList(), // Will be populated separately
                startDate = LocalDateTime.now().minusDays(7),
                endDate = LocalDateTime.now().plusDays(23),
                isAnonymous = false,
                maxResponses = 1000,
                responseCount = 156,
                estimatedDuration = 5,
                incentive = SurveyIncentive(
                    type = IncentiveType.POINTS,
                    value = BigDecimal("100"),
                    description = "Earn 100 reward points for completing this survey",
                    eligibilityCriteria = listOf("Complete all questions")
                ),
                createdBy = "research_team",
                createdDate = LocalDateTime.now().minusDays(10),
                updatedDate = LocalDateTime.now().minusDays(7)
            ),
            Survey(
                id = UUID.randomUUID().toString(),
                title = "Mobile App Usability Study",
                description = "Share your experience using our mobile banking app",
                type = SurveyType.USER_EXPERIENCE,
                status = SurveyStatus.DRAFT,
                targetAudience = listOf("mobile_users"),
                questions = emptyList(),
                startDate = LocalDateTime.now().plusDays(3),
                endDate = LocalDateTime.now().plusDays(33),
                isAnonymous = true,
                maxResponses = 500,
                responseCount = 0,
                estimatedDuration = 8,
                incentive = SurveyIncentive(
                    type = IncentiveType.GIFT_CARD,
                    value = BigDecimal("10.00"),
                    description = "£10 Amazon gift card for completed responses",
                    eligibilityCriteria = listOf("Complete survey", "Provide valid email")
                ),
                createdBy = "ux_team",
                createdDate = LocalDateTime.now().minusDays(5),
                updatedDate = LocalDateTime.now().minusDays(2)
            )
        )
        
        surveys.addAll(mockSurveys)
        
        // Mock questions for the first survey
        val mockQuestions = listOf(
            SurveyQuestion(
                id = UUID.randomUUID().toString(),
                surveyId = surveys[0].id,
                questionText = "How satisfied are you with our banking services overall?",
                questionType = QuestionType.RATING_SCALE,
                isRequired = true,
                order = 1,
                options = (1..5).map { 
                    QuestionOption(
                        id = UUID.randomUUID().toString(),
                        text = "$it",
                        value = "$it",
                        order = it
                    )
                },
                validation = QuestionValidation(
                    minValue = BigDecimal("1"),
                    maxValue = BigDecimal("5"),
                    minLength = null,
                    maxLength = null,
                    pattern = null
                )
            ),
            SurveyQuestion(
                id = UUID.randomUUID().toString(),
                surveyId = surveys[0].id,
                questionText = "What features would you like to see improved?",
                questionType = QuestionType.MULTIPLE_CHOICE,
                isRequired = false,
                order = 2,
                options = listOf(
                    QuestionOption(UUID.randomUUID().toString(), "Mobile app interface", "mobile_app", 1),
                    QuestionOption(UUID.randomUUID().toString(), "Customer support", "support", 2),
                    QuestionOption(UUID.randomUUID().toString(), "Transaction speed", "speed", 3),
                    QuestionOption(UUID.randomUUID().toString(), "Security features", "security", 4),
                    QuestionOption(UUID.randomUUID().toString(), "Other", "other", 5)
                ),
                validation = null
            ),
            SurveyQuestion(
                id = UUID.randomUUID().toString(),
                surveyId = surveys[0].id,
                questionText = "Please share any additional comments or suggestions:",
                questionType = QuestionType.TEXT_LONG,
                isRequired = false,
                order = 3,
                options = emptyList(),
                validation = QuestionValidation(
                    minLength = null,
                    maxLength = 500,
                    minValue = null,
                    maxValue = null,
                    pattern = null
                )
            )
        )
        
        surveyQuestions.addAll(mockQuestions)
        
        // Update surveys with question references
        surveys.forEachIndexed { index, survey ->
            val questions = surveyQuestions.filter { it.surveyId == survey.id }
            surveys[index] = survey.copy(questions = questions)
        }
    }
}