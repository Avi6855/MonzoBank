package com.avinashpatil.app.monzobank.domain.repository

interface NetworkRepository {
    suspend fun isNetworkAvailable(): Result<Boolean>
    suspend fun getNetworkType(): Result<String>
    suspend fun checkConnectivity(): Result<Boolean>
}

interface EncryptionRepository {
    suspend fun encrypt(data: String): Result<String>
    suspend fun decrypt(encryptedData: String): Result<String>
    suspend fun generateKey(): Result<String>
}

interface KeyManagementRepository {
    suspend fun storeKey(keyId: String, key: String): Result<Unit>
    suspend fun retrieveKey(keyId: String): Result<String?>
    suspend fun deleteKey(keyId: String): Result<Unit>
}

interface SessionRepository {
    suspend fun createSession(userId: String): Result<String>
    suspend fun validateSession(sessionId: String): Result<Boolean>
    suspend fun invalidateSession(sessionId: String): Result<Unit>
}

interface TokenRepository {
    suspend fun storeToken(token: String): Result<Unit>
    suspend fun getToken(): Result<String?>
    suspend fun refreshToken(): Result<String>
    suspend fun clearToken(): Result<Unit>
}

interface RateLimitRepository {
    suspend fun checkRateLimit(key: String): Result<Boolean>
    suspend fun incrementCounter(key: String): Result<Unit>
    suspend fun resetCounter(key: String): Result<Unit>
}

interface ThrottleRepository {
    suspend fun shouldThrottle(key: String): Result<Boolean>
    suspend fun recordRequest(key: String): Result<Unit>
}

interface QueueRepository {
    suspend fun enqueue(item: String): Result<Unit>
    suspend fun dequeue(): Result<String?>
    suspend fun getQueueSize(): Result<Int>
}

interface SchedulerRepository {
    suspend fun scheduleTask(taskId: String, delay: Long): Result<Unit>
    suspend fun cancelTask(taskId: String): Result<Unit>
    suspend fun getScheduledTasks(): Result<List<String>>
}

interface WorkManagerRepository {
    suspend fun enqueueWork(workId: String): Result<Unit>
    suspend fun cancelWork(workId: String): Result<Unit>
    suspend fun getWorkStatus(workId: String): Result<String>
}

interface ImageRepository {
    suspend fun uploadImage(imageData: ByteArray): Result<String>
    suspend fun downloadImage(imageUrl: String): Result<ByteArray>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}

interface DocumentRepository {
    suspend fun uploadDocument(documentData: ByteArray, fileName: String): Result<String>
    suspend fun downloadDocument(documentUrl: String): Result<ByteArray>
    suspend fun deleteDocument(documentUrl: String): Result<Unit>
}