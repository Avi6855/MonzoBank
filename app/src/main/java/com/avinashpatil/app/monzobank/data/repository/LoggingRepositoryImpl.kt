package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.LogEntry
import com.avinashpatil.app.monzobank.domain.repository.LogLevel
import com.avinashpatil.app.monzobank.domain.repository.LoggingRepository
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingRepositoryImpl @Inject constructor() : LoggingRepository {
    
    private val logs = ConcurrentLinkedQueue<LogEntry>()
    private val maxLogEntries = 10000
    
    override suspend fun log(
        level: LogLevel,
        message: String,
        logger: String,
        exception: Throwable?,
        context: Map<String, String>
    ): Result<Unit> {
        return try {
            val logEntry = LogEntry(
                id = UUID.randomUUID().toString(),
                level = level,
                message = message,
                timestamp = LocalDateTime.now(),
                logger = logger,
                thread = Thread.currentThread().name,
                exception = exception?.stackTraceToString(),
                context = context
            )
            
            logs.offer(logEntry)
            
            // Keep only the most recent entries
            while (logs.size > maxLogEntries) {
                logs.poll()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun trace(message: String, logger: String, context: Map<String, String>): Result<Unit> {
        return log(LogLevel.TRACE, message, logger, null, context)
    }
    
    override suspend fun debug(message: String, logger: String, context: Map<String, String>): Result<Unit> {
        return log(LogLevel.DEBUG, message, logger, null, context)
    }
    
    override suspend fun info(message: String, logger: String, context: Map<String, String>): Result<Unit> {
        return log(LogLevel.INFO, message, logger, null, context)
    }
    
    override suspend fun warn(message: String, logger: String, context: Map<String, String>): Result<Unit> {
        return log(LogLevel.WARN, message, logger, null, context)
    }
    
    override suspend fun error(
        message: String,
        logger: String,
        exception: Throwable?,
        context: Map<String, String>
    ): Result<Unit> {
        return log(LogLevel.ERROR, message, logger, exception, context)
    }
    
    override suspend fun fatal(
        message: String,
        logger: String,
        exception: Throwable?,
        context: Map<String, String>
    ): Result<Unit> {
        return log(LogLevel.FATAL, message, logger, exception, context)
    }
    
    override suspend fun getLogs(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        level: LogLevel?
    ): Result<List<LogEntry>> {
        return try {
            val filteredLogs = logs.filter { logEntry ->
                logEntry.timestamp.isAfter(startTime) &&
                logEntry.timestamp.isBefore(endTime) &&
                (level == null || logEntry.level == level)
            }.sortedByDescending { it.timestamp }
            
            Result.success(filteredLogs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLogsByLogger(logger: String, limit: Int): Result<List<LogEntry>> {
        return try {
            val filteredLogs = logs.filter { it.logger == logger }
                .sortedByDescending { it.timestamp }
                .take(limit)
            
            Result.success(filteredLogs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchLogs(query: String, limit: Int): Result<List<LogEntry>> {
        return try {
            val filteredLogs = logs.filter { logEntry ->
                logEntry.message.contains(query, ignoreCase = true) ||
                logEntry.logger.contains(query, ignoreCase = true) ||
                logEntry.exception?.contains(query, ignoreCase = true) == true
            }.sortedByDescending { it.timestamp }
                .take(limit)
            
            Result.success(filteredLogs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearLogs(olderThan: LocalDateTime): Result<Int> {
        return try {
            val initialSize = logs.size
            logs.removeIf { it.timestamp.isBefore(olderThan) }
            val removedCount = initialSize - logs.size
            
            Result.success(removedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportLogs(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        format: String
    ): Result<String> {
        return try {
            val logsResult = getLogs(startTime, endTime).getOrThrow()
            
            when (format.lowercase()) {
                "json" -> {
                    val json = logsResult.joinToString(
                        prefix = "[\n",
                        postfix = "\n]",
                        separator = ",\n"
                    ) { logEntry ->
                        """
                        {
                            "id": "${logEntry.id}",
                            "level": "${logEntry.level}",
                            "message": "${logEntry.message.replace("\"", "\\\"")}",
                            "timestamp": "${logEntry.timestamp}",
                            "logger": "${logEntry.logger}",
                            "thread": "${logEntry.thread}",
                            "exception": ${if (logEntry.exception != null) "\"${logEntry.exception.replace("\"", "\\\"")}\"" else "null"},
                            "context": ${logEntry.context}
                        }
                        """.trimIndent()
                    }
                    Result.success(json)
                }
                "csv" -> {
                    val csv = buildString {
                        appendLine("id,level,message,timestamp,logger,thread,exception,context")
                        logsResult.forEach { logEntry ->
                            appendLine("${logEntry.id},${logEntry.level},\"${logEntry.message.replace("\"", "\\\"")}\",${logEntry.timestamp},${logEntry.logger},${logEntry.thread},\"${logEntry.exception?.replace("\"", "\\\"") ?: ""}\",\"${logEntry.context}\"")
                        }
                    }
                    Result.success(csv)
                }
                "txt" -> {
                    val txt = logsResult.joinToString("\n") { logEntry ->
                        "[${logEntry.timestamp}] ${logEntry.level} ${logEntry.logger} - ${logEntry.message}${if (logEntry.exception != null) "\n${logEntry.exception}" else ""}"
                    }
                    Result.success(txt)
                }
                else -> Result.failure(Exception("Unsupported format: $format"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}