package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class RegulatoryCompliance(
    val id: String,
    val regulation: String,
    val status: ComplianceStatus,
    val lastChecked: LocalDateTime,
    val nextReview: LocalDateTime,
    val description: String
)

enum class ComplianceStatus {
    COMPLIANT, NON_COMPLIANT, PENDING_REVIEW, UNDER_INVESTIGATION
}

data class RegulatoryReport(
    val id: String,
    val reportType: RegulatoryReportType,
    val period: String,
    val status: RegulatoryReportStatus,
    val submittedAt: LocalDateTime?,
    val dueDate: LocalDateTime,
    val filePath: String?
)

enum class RegulatoryReportType {
    SAR, CTR, FBAR, FORM_8300, BSA_REPORT
}

enum class RegulatoryReportStatus {
    DRAFT, SUBMITTED, APPROVED, REJECTED, OVERDUE
}

data class RegulatoryAlert(
    val id: String,
    val alertType: RegulatoryAlertType,
    val severity: AlertSeverity,
    val message: String,
    val createdAt: LocalDateTime,
    val isResolved: Boolean = false
)

enum class RegulatoryAlertType {
    COMPLIANCE_VIOLATION, REPORTING_DEADLINE, REGULATORY_CHANGE, AUDIT_REQUIRED
}

enum class AlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

interface RegulatoryRepository {
    suspend fun getComplianceStatus(): Result<List<RegulatoryCompliance>>
    suspend fun updateComplianceStatus(complianceId: String, status: ComplianceStatus): Result<Unit>
    suspend fun generateReport(reportType: RegulatoryReportType, period: String): Result<String>
    suspend fun submitReport(reportId: String): Result<Unit>
    suspend fun getReports(status: RegulatoryReportStatus?): Result<List<RegulatoryReport>>
    suspend fun getOverdueReports(): Result<List<RegulatoryReport>>
    suspend fun createAlert(alert: RegulatoryAlert): Result<String>
    suspend fun getAlerts(isResolved: Boolean?): Result<List<RegulatoryAlert>>
    suspend fun resolveAlert(alertId: String): Result<Unit>
    suspend fun scheduleComplianceReview(regulation: String, reviewDate: LocalDateTime): Result<Unit>
    suspend fun getUpcomingDeadlines(): Result<List<RegulatoryReport>>
    suspend fun performComplianceCheck(): Result<List<RegulatoryCompliance>>
}