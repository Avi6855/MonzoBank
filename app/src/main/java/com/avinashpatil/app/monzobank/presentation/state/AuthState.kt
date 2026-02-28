package com.avinashpatil.app.monzobank.presentation.state

import com.avinashpatil.app.monzobank.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val authToken: String? = null,
    val refreshToken: String? = null,
    val authMethod: AuthMethod = AuthMethod.NONE,
    val requiresBiometric: Boolean = false,
    val requiresPin: Boolean = false,
    val requires2FA: Boolean = false,
    val errorMessage: String? = null,
    val isFirstTimeUser: Boolean = false,
    val hasCompletedKYC: Boolean = false,
    val accountStatus: AccountStatus = AccountStatus.PENDING,
    val sessionExpiry: Long? = null,
    val lastLoginTime: Long? = null,
    val loginAttempts: Int = 0,
    val maxLoginAttempts: Int = 5,
    val isAccountLocked: Boolean = false,
    val lockoutEndTime: Long? = null,
    val isOnboardingComplete: Boolean = false
) {
    val isSessionValid: Boolean
        get() = sessionExpiry?.let { it > System.currentTimeMillis() } ?: false
    
    val isAccountActive: Boolean
        get() = accountStatus == AccountStatus.ACTIVE && !isAccountLocked
    
    val canAttemptLogin: Boolean
        get() = !isAccountLocked && loginAttempts < maxLoginAttempts
    
    val remainingLoginAttempts: Int
        get() = maxLoginAttempts - loginAttempts
    
    val isLockoutActive: Boolean
        get() = lockoutEndTime?.let { it > System.currentTimeMillis() } ?: false
}

enum class AuthMethod {
    NONE,
    PIN,
    BIOMETRIC,
    PASSWORD,
    TWO_FACTOR,
    SOCIAL_LOGIN
}

enum class AccountStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    VERIFICATION_REQUIRED,
    KYC_PENDING,
    DOCUMENTS_REQUIRED
}