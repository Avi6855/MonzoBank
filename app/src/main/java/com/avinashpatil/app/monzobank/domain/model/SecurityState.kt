package com.avinashpatil.app.monzobank.domain.model

data class SecurityState(
    val isDeviceCompromised: Boolean = false,
    val isRootDetected: Boolean = false,
    val isDebuggerAttached: Boolean = false,
    val isEmulatorDetected: Boolean = false,
    val isTamperingDetected: Boolean = false,
    val securityLevel: SecurityLevel = SecurityLevel.HIGH,
    val lastSecurityCheck: Long = 0L,
    val securityAlerts: List<SecurityAlert> = emptyList(),
    val isSecurityLockActive: Boolean = false,
    val failedSecurityAttempts: Int = 0,
    val isLocked: Boolean = false,
    val lockReason: String? = null,
    val isFraudDetected: Boolean = false,
    val isUnauthorizedAccess: Boolean = false,
    val isSuspiciousActivity: Boolean = false
) {
    enum class SecurityLevel {
        CRITICAL,
        LOW,
        MEDIUM,
        HIGH
    }
    
    data class SecurityAlert(
        val id: String,
        val type: SecurityAlertType,
        val message: String,
        val timestamp: Long,
        val severity: SecurityLevel
    )
    
    enum class SecurityAlertType {
        ROOT_DETECTED,
        DEBUGGER_ATTACHED,
        EMULATOR_DETECTED,
        TAMPERING_DETECTED,
        SUSPICIOUS_ACTIVITY,
        UNAUTHORIZED_ACCESS
    }
}