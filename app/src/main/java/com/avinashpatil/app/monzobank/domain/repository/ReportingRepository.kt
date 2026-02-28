package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

/**
 * Repository interface for reporting operations
 */
interface ReportingRepository {
    
    /**
     * Generate financial report
     */
    suspend fun generateFinancialReport(
        userId: String,
        reportType: ReportType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Result<FinancialReport>
    
    /**
     * Get available reports
     */
    suspend fun getAvailableReports(userId: String): Result<List<FinancialReport>>
    
    /**
     * Export report
     */
    suspend fun exportReport(reportId: String, format: ExportFormat): Result<String>
    
    /**
     * Schedule report generation
     */
    suspend fun scheduleReport(
        userId: String,
        reportType: ReportType,
        frequency: ReportFrequency
    ): Result<Unit>
}

enum class ReportType {
    MONTHLY_STATEMENT,
    ANNUAL_SUMMARY,
    TAX_REPORT,
    SPENDING_ANALYSIS,
    INCOME_REPORT
}

enum class ExportFormat {
    PDF,
    CSV,
    EXCEL,
    JSON
}

enum class ReportFrequency {
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    ANNUALLY
}

data class FinancialReport(
    val id: String,
    val userId: String,
    val type: ReportType,
    val generatedAt: LocalDateTime,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val data: Map<String, Any>,
    val status: ReportStatus
)

enum class ReportStatus {
    GENERATING,
    COMPLETED,
    FAILED,
    CANCELLED
}