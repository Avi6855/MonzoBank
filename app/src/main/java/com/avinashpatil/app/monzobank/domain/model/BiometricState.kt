package com.avinashpatil.app.monzobank.domain.model

data class BiometricState(
    val isAvailable: Boolean = false,
    val isEnabled: Boolean = false,
    val isRequired: Boolean = false,
    val authenticationStatus: AuthenticationStatus = AuthenticationStatus.NONE,
    val supportedTypes: List<BiometricType> = emptyList(),
    val lastAuthenticationTime: Long = 0L,
    val failedAttempts: Int = 0,
    val maxFailedAttempts: Int = 3,
    val errorMessage: String? = null,
    val isLocked: Boolean = false,
    val lockoutEndTime: Long = 0L
) {
    enum class AuthenticationStatus {
        NONE,
        PENDING,
        SUCCESS,
        FAILED,
        ERROR,
        FALLBACK_TO_PIN,
        LOCKED_OUT
    }
    
    enum class BiometricType {
        FINGERPRINT,
        FACE,
        IRIS,
        VOICE
    }
    
    val isLockedOut: Boolean
        get() = isLocked && System.currentTimeMillis() < lockoutEndTime
    
    val canAuthenticate: Boolean
        get() = isAvailable && isEnabled && !isLockedOut
}