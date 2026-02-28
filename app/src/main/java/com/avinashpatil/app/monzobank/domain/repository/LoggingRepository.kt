package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class LogEntry(
    val id: String,
    val level: LogLevel,
    val message: String,
    val timestamp: LocalDateTime,
    val logger: String,
    val thread: String,
    val exception: String? = null,
    val context: Map<String, String> = emptyMap()
)

enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
}

interface LoggingRepository {
    suspend fun log(level: LogLevel, message: String, logger: String, exception: Throwable? = null, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun trace(message: String, logger: String, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun debug(message: String, logger: String, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun info(message: String, logger: String, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun warn(message: String, logger: String, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun error(message: String, logger: String, exception: Throwable? = null, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun fatal(message: String, logger: String, exception: Throwable? = null, context: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun getLogs(startTime: LocalDateTime, endTime: LocalDateTime, level: LogLevel? = null): Result<List<LogEntry>>
    suspend fun getLogsByLogger(logger: String, limit: Int = 100): Result<List<LogEntry>>
    suspend fun searchLogs(query: String, limit: Int = 100): Result<List<LogEntry>>
    suspend fun clearLogs(olderThan: LocalDateTime): Result<Int>
    suspend fun exportLogs(startTime: LocalDateTime, endTime: LocalDateTime, format: String): Result<String>
}