package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.ComplianceReport
import com.avinashpatil.app.monzobank.domain.model.ComplianceType
import java.time.LocalDateTime

/**
 * Repository interface for compliance operations
 */
interface ComplianceRepository {
    
    /**
     * Generate compliance report
     */
    suspend fun generateComplianceReport(
        type: ComplianceType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Result<ComplianceReport>
    
    /**
     * Get compliance reports
     */
    suspend fun getComplianceReports(): Result<List<ComplianceReport>>
    
    /**
     * Check compliance status
     */
    suspend fun checkComplianceStatus(type: ComplianceType): Result<Boolean>
    
    /**
     * Update compliance settings
     */
    suspend fun updateComplianceSettings(settings: Map<String, Any>): Result<Unit>
}