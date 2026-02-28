package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.AuditLog
import java.time.LocalDateTime

/**
 * Repository interface for audit operations
 */
interface AuditRepository {
    
    /**
     * Log audit event
     */
    suspend fun logAuditEvent(
        userId: String,
        action: String,
        resource: String,
        details: Map<String, Any> = emptyMap()
    ): Result<Unit>
    
    /**
     * Get audit logs
     */
    suspend fun getAuditLogs(
        userId: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        limit: Int = 100
    ): Result<List<AuditLog>>
    
    /**
     * Search audit logs
     */
    suspend fun searchAuditLogs(query: String): Result<List<AuditLog>>
    
    /**
     * Export audit logs
     */
    suspend fun exportAuditLogs(
        format: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Result<String>
}