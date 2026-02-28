package com.avinashpatil.app.monzobank.domain.model

data class SessionState(
    val isActive: Boolean = false,
    val sessionId: String? = null,
    val userId: String? = null,
    val startTime: Long = 0L,
    val lastActivityTime: Long = 0L,
    val expirationTime: Long = 0L,
    val sessionType: SessionType = SessionType.GUEST,
    val deviceId: String? = null,
    val ipAddress: String? = null,
    val location: String? = null,
    val isSecure: Boolean = true,
    val authenticationLevel: AuthenticationLevel = AuthenticationLevel.NONE
) {
    enum class SessionType {
        GUEST,
        AUTHENTICATED,
        BIOMETRIC_AUTHENTICATED,
        TWO_FACTOR_AUTHENTICATED
    }
    
    enum class AuthenticationLevel {
        NONE,
        BASIC,
        ENHANCED,
        MAXIMUM
    }
    
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expirationTime
    
    val isExpiring: Boolean
        get() = remainingTime <= 300000 // 5 minutes warning
    
    val remainingTime: Long
        get() = maxOf(0, expirationTime - System.currentTimeMillis())
    
    val sessionDuration: Long
        get() = if (startTime > 0) System.currentTimeMillis() - startTime else 0
    
    val inactivityDuration: Long
        get() = if (lastActivityTime > 0) System.currentTimeMillis() - lastActivityTime else 0
}