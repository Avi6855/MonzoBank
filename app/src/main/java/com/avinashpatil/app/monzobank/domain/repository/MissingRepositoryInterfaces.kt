package com.avinashpatil.app.monzobank.domain.repository

interface MediaRepository {
    suspend fun uploadMedia(mediaData: ByteArray, mediaType: String): Result<String>
    suspend fun downloadMedia(mediaUrl: String): Result<ByteArray>
    suspend fun deleteMedia(mediaUrl: String): Result<Unit>
    suspend fun getMediaMetadata(mediaUrl: String): Result<Map<String, Any>>
}

interface SearchRepository {
    suspend fun search(query: String): Result<List<String>>
    suspend fun searchTransactions(query: String, userId: String): Result<List<String>>
    suspend fun searchUsers(query: String): Result<List<String>>
    suspend fun getSearchHistory(userId: String): Result<List<String>>
    suspend fun clearSearchHistory(userId: String): Result<Unit>
}

interface RecommendationRepository {
    suspend fun getRecommendations(userId: String): Result<List<String>>
    suspend fun getProductRecommendations(userId: String): Result<List<String>>
    suspend fun getServiceRecommendations(userId: String): Result<List<String>>
    suspend fun updateRecommendationPreferences(userId: String, preferences: Map<String, Any>): Result<Unit>
}

interface PersonalizationRepository {
    suspend fun getPersonalizedContent(userId: String): Result<Map<String, Any>>
    suspend fun updatePersonalizationSettings(userId: String, settings: Map<String, Any>): Result<Unit>
    suspend fun getPersonalizedOffers(userId: String): Result<List<String>>
    suspend fun trackUserBehavior(userId: String, action: String, data: Map<String, Any>): Result<Unit>
}

interface MachineLearningRepository {
    suspend fun trainModel(modelId: String, data: List<Map<String, Any>>): Result<Unit>
    suspend fun predictRisk(userId: String, transactionData: Map<String, Any>): Result<Double>
    suspend fun detectFraud(transactionData: Map<String, Any>): Result<Boolean>
    suspend fun getModelMetrics(modelId: String): Result<Map<String, Double>>
}

interface AIRepository {
    suspend fun processNaturalLanguage(text: String): Result<Map<String, Any>>
    suspend fun generateInsights(userId: String, data: Map<String, Any>): Result<List<String>>
    suspend fun analyzeSpendingPatterns(userId: String): Result<Map<String, Any>>
    suspend fun predictUserBehavior(userId: String): Result<Map<String, Any>>
}

interface ChatbotRepository {
    suspend fun processMessage(userId: String, message: String): Result<String>
    suspend fun getChatHistory(userId: String): Result<List<Map<String, String>>>
    suspend fun clearChatHistory(userId: String): Result<Unit>
    suspend fun updateChatbotSettings(settings: Map<String, Any>): Result<Unit>
}

interface VoiceRepository {
    suspend fun processVoiceCommand(userId: String, audioData: ByteArray): Result<String>
    suspend fun textToSpeech(text: String): Result<ByteArray>
    suspend fun getVoiceSettings(userId: String): Result<Map<String, Any>>
    suspend fun updateVoiceSettings(userId: String, settings: Map<String, Any>): Result<Unit>
}

interface AccessibilityRepository {
    suspend fun getAccessibilitySettings(userId: String): Result<Map<String, Any>>
    suspend fun updateAccessibilitySettings(userId: String, settings: Map<String, Any>): Result<Unit>
    suspend fun getScreenReaderContent(contentId: String): Result<String>
    suspend fun getHighContrastTheme(): Result<Map<String, String>>
}

interface InternationalizationRepository {
    suspend fun getSupportedLanguages(): Result<List<String>>
    suspend fun getTranslations(language: String): Result<Map<String, String>>
    suspend fun updateTranslations(language: String, translations: Map<String, String>): Result<Unit>
    suspend fun detectLanguage(text: String): Result<String>
}

interface LocalizationRepository {
    suspend fun getLocalizedContent(userId: String, contentKey: String): Result<String>
    suspend fun updateLocalizationSettings(userId: String, settings: Map<String, Any>): Result<Unit>
    suspend fun getRegionalSettings(region: String): Result<Map<String, Any>>
    suspend fun getCurrencyFormat(currencyCode: String): Result<Map<String, String>>
}