package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class FraudAlert(
    val id: String,
    val userId: String,
    val transactionId: String,
    val riskLevel: FraudRiskLevel,
    val message: String,
    val timestamp: LocalDateTime,
    val isResolved: Boolean = false,
    val actionTaken: String? = null,
    val resolvedBy: String? = null,
    val resolvedAt: LocalDateTime? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    val isActive: Boolean
        get() = !isResolved
    
    val urgencyLevel: String
        get() = when (riskLevel) {
            FraudRiskLevel.HIGH -> "URGENT"
            FraudRiskLevel.MEDIUM -> "MODERATE"
            FraudRiskLevel.LOW -> "LOW"
        }
}

enum class FraudRiskLevel {
    LOW,
    MEDIUM,
    HIGH;
    
    val displayName: String
        get() = when (this) {
            LOW -> "Low Risk"
            MEDIUM -> "Medium Risk"
            HIGH -> "High Risk"
        }
    
    val color: String
        get() = when (this) {
            LOW -> "#4CAF50" // Green
            MEDIUM -> "#FF9800" // Orange
            HIGH -> "#F44336" // Red
        }
}

data class FraudRule(
    val id: String,
    val name: String,
    val description: String,
    val condition: String,
    val riskScore: Double,
    val isActive: Boolean = true,
    val priority: Int = 1,
    val category: FraudRuleCategory = FraudRuleCategory.TRANSACTION,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class FraudRuleCategory {
    TRANSACTION,
    VELOCITY,
    LOCATION,
    DEVICE,
    BEHAVIORAL,
    MERCHANT
}

// SecurityEvent, SecurityEventType, and SecuritySeverity are defined in SecurityEvent.kt
// Import them from: com.avinashpatil.app.monzobank.domain.model.SecurityEvent
// Import them from: com.avinashpatil.app.monzobank.domain.model.SecurityEventType
// Import them from: com.avinashpatil.app.monzobank.domain.model.SecuritySeverity

data class BiometricAuthResult(
    val isSuccessful: Boolean,
    val biometricType: BiometricType,
    val errorMessage: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class BiometricType {
    FINGERPRINT,
    FACE,
    FACE_ID,
    VOICE,
    IRIS;
    
    val displayName: String
        get() = when (this) {
            FINGERPRINT -> "Fingerprint"
            FACE -> "Face Recognition"
            FACE_ID -> "Face ID"
            VOICE -> "Voice Recognition"
            IRIS -> "Iris Scan"
        }
}

data class DeviceFingerprint(
    val id: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val deviceType: String, // mobile, tablet, desktop
    val operatingSystem: String,
    val browserInfo: String? = null,
    val screenResolution: String? = null,
    val timezone: String,
    val language: String,
    val ipAddress: String,
    val isTrusted: Boolean = false,
    val firstSeen: LocalDateTime,
    val lastSeen: LocalDateTime,
    val riskScore: Double = 0.0
) {
    val isNewDevice: Boolean
        get() = firstSeen.isAfter(LocalDateTime.now().minusDays(7))
    
    val daysSinceFirstSeen: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(firstSeen, LocalDateTime.now())
}

data class SessionInfo(
    val sessionId: String,
    val userId: String,
    val deviceFingerprint: DeviceFingerprint,
    val startTime: LocalDateTime,
    val lastActivity: LocalDateTime,
    val ipAddress: String,
    val location: String? = null,
    val isActive: Boolean = true,
    val expiresAt: LocalDateTime,
    val authenticationLevel: AuthenticationLevel = AuthenticationLevel.BASIC
) {
    val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(expiresAt)
    
    val minutesRemaining: Long
        get() = if (isExpired) 0 else java.time.temporal.ChronoUnit.MINUTES.between(LocalDateTime.now(), expiresAt)
}

enum class AuthenticationLevel {
    BASIC,
    TWO_FACTOR,
    BIOMETRIC,
    MULTI_FACTOR;
    
    val displayName: String
        get() = when (this) {
            BASIC -> "Basic Authentication"
            TWO_FACTOR -> "Two-Factor Authentication"
            BIOMETRIC -> "Biometric Authentication"
            MULTI_FACTOR -> "Multi-Factor Authentication"
        }
    
    val securityScore: Int
        get() = when (this) {
            BASIC -> 1
            TWO_FACTOR -> 2
            BIOMETRIC -> 3
            MULTI_FACTOR -> 4
        }
}

data class EncryptionInfo(
    val algorithm: String,
    val keySize: Int,
    val mode: String,
    val padding: String,
    val isQuantumResistant: Boolean = false
) {
    val strength: EncryptionStrength
        get() = when {
            keySize >= 256 && isQuantumResistant -> EncryptionStrength.QUANTUM_RESISTANT
            keySize >= 256 -> EncryptionStrength.STRONG
            keySize >= 128 -> EncryptionStrength.MEDIUM
            else -> EncryptionStrength.WEAK
        }
}

enum class EncryptionStrength {
    WEAK,
    MEDIUM,
    STRONG,
    QUANTUM_RESISTANT;
    
    val displayName: String
        get() = when (this) {
            WEAK -> "Weak Encryption"
            MEDIUM -> "Medium Encryption"
            STRONG -> "Strong Encryption"
            QUANTUM_RESISTANT -> "Quantum-Resistant Encryption"
        }
}

data class AuditLog(
    val id: String,
    val userId: String,
    val action: String,
    val resource: String,
    val timestamp: LocalDateTime,
    val ipAddress: String,
    val userAgent: String? = null,
    val result: AuditResult,
    val details: Map<String, Any> = emptyMap(),
    val riskLevel: FraudRiskLevel = FraudRiskLevel.LOW
)

enum class AuditResult {
    SUCCESS,
    FAILURE,
    BLOCKED,
    SUSPICIOUS;
    
    val displayName: String
        get() = when (this) {
            SUCCESS -> "Success"
            FAILURE -> "Failure"
            BLOCKED -> "Blocked"
            SUSPICIOUS -> "Suspicious"
        }
}

data class ComplianceReport(
    val id: String,
    val reportType: ComplianceType,
    val generatedAt: LocalDateTime,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val findings: List<ComplianceFinding>,
    val overallScore: Double,
    val status: ComplianceStatus
)

enum class ComplianceType {
    PCI_DSS,
    GDPR,
    SOX,
    ISO_27001,
    NIST;
    
    val displayName: String
        get() = when (this) {
            PCI_DSS -> "PCI DSS"
            GDPR -> "GDPR"
            SOX -> "Sarbanes-Oxley"
            ISO_27001 -> "ISO 27001"
            NIST -> "NIST Framework"
        }
}

data class ComplianceFinding(
    val id: String,
    val category: String,
    val severity: SecuritySeverity,
    val description: String,
    val recommendation: String,
    val isResolved: Boolean = false
)

enum class ComplianceStatus {
    COMPLIANT,
    NON_COMPLIANT,
    PARTIALLY_COMPLIANT,
    UNDER_REVIEW;
    
    val displayName: String
        get() = when (this) {
            COMPLIANT -> "Compliant"
            NON_COMPLIANT -> "Non-Compliant"
            PARTIALLY_COMPLIANT -> "Partially Compliant"
            UNDER_REVIEW -> "Under Review"
        }
}

data class ThreatIntelligence(
    val id: String,
    val threatType: ThreatType,
    val severity: SecuritySeverity,
    val description: String,
    val indicators: List<String>,
    val mitigationSteps: List<String>,
    val discoveredAt: LocalDateTime,
    val source: String,
    val isActive: Boolean = true
)

enum class ThreatType {
    MALWARE,
    PHISHING,
    SOCIAL_ENGINEERING,
    DATA_BREACH,
    INSIDER_THREAT,
    DDOS,
    ACCOUNT_TAKEOVER,
    CARD_FRAUD;
    
    val displayName: String
        get() = when (this) {
            MALWARE -> "Malware"
            PHISHING -> "Phishing"
            SOCIAL_ENGINEERING -> "Social Engineering"
            DATA_BREACH -> "Data Breach"
            INSIDER_THREAT -> "Insider Threat"
            DDOS -> "DDoS Attack"
            ACCOUNT_TAKEOVER -> "Account Takeover"
            CARD_FRAUD -> "Card Fraud"
        }
}

data class SecurityMetrics(
    val totalSecurityEvents: Int,
    val criticalEvents: Int,
    val resolvedEvents: Int,
    val averageResolutionTime: Double, // in minutes
    val fraudDetectionRate: Double, // percentage
    val falsePositiveRate: Double, // percentage
    val complianceScore: Double, // percentage
    val lastUpdated: LocalDateTime
) {
    val resolutionRate: Double
        get() = if (totalSecurityEvents > 0) (resolvedEvents.toDouble() / totalSecurityEvents) * 100 else 0.0
    
    val criticalEventRate: Double
        get() = if (totalSecurityEvents > 0) (criticalEvents.toDouble() / totalSecurityEvents) * 100 else 0.0
}

// Utility classes for security operations
object SecurityUtils {
    
    fun generateSecureId(): String {
        return java.util.UUID.randomUUID().toString()
    }
    
    fun hashSensitiveData(data: String): String {
        // Mock implementation - use proper hashing in production
        return "HASH_${data.hashCode().toString(16)}"
    }
    
    fun encryptSensitiveData(data: String): String {
        // Mock implementation - use proper encryption in production
        return "ENC_${java.util.Base64.getEncoder().encodeToString(data.toByteArray())}"
    }
    
    fun validateIPAddress(ipAddress: String): Boolean {
        val ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        return ipAddress.matches(ipv4Pattern.toRegex())
    }
    
    fun calculateRiskScore(factors: Map<String, Double>): Double {
        return factors.values.sum().coerceIn(0.0, 1.0)
    }
    
    fun isHighRiskTransaction(
        amount: java.math.BigDecimal,
        location: String?,
        timeOfDay: Int
    ): Boolean {
        return amount > java.math.BigDecimal(1000) ||
                location?.contains("foreign", ignoreCase = true) == true ||
                timeOfDay < 6 || timeOfDay > 22
    }
}