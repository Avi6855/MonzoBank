package com.avinashpatil.app.monzobank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.monzobank.domain.model.SessionState
// import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
// import javax.inject.Inject

// @HiltViewModel
class SessionViewModel(
    // TODO: Inject session repository when available
) : ViewModel() {
    
    private val _sessionState = MutableStateFlow(
        SessionState(
            isActive = false,
            sessionId = null,
            userId = null,
            startTime = 0L,
            lastActivityTime = 0L,
            expirationTime = Long.MAX_VALUE, // For offline mode, never expire
            sessionType = SessionState.SessionType.GUEST,
            deviceId = null,
            ipAddress = null,
            location = null
        )
    )
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()
    
    companion object {
        private const val SESSION_TIMEOUT_MINUTES = 30
        private const val INACTIVITY_WARNING_MINUTES = 25
    }
    
    fun startSession(userId: String, sessionType: SessionState.SessionType = SessionState.SessionType.AUTHENTICATED) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val sessionId = generateSessionId()
                // For offline mode, never expire sessions
                val expirationTime = Long.MAX_VALUE
                
                _sessionState.value = SessionState(
                    isActive = true,
                    sessionId = sessionId,
                    userId = userId,
                    startTime = currentTime,
                    lastActivityTime = currentTime,
                    expirationTime = expirationTime,
                    sessionType = sessionType,
                    deviceId = getDeviceId(),
                    ipAddress = "127.0.0.1", // Mock IP for offline mode
                    location = "Unknown" // Mock location for offline mode
                )
                
                Timber.d("Session started for user: $userId (offline mode - no expiration)")
            } catch (e: Exception) {
                Timber.e(e, "Error starting session")
            }
        }
    }
    
    fun updateActivity() {
        viewModelScope.launch {
            val currentState = _sessionState.value
            if (currentState.isActive) {
                val currentTime = System.currentTimeMillis()
                val newExpirationTime = currentTime + (SESSION_TIMEOUT_MINUTES * 60 * 1000)
                
                _sessionState.value = currentState.copy(
                    lastActivityTime = currentTime,
                    expirationTime = newExpirationTime
                )
                
                Timber.d("Session activity updated")
            }
        }
    }
    
    fun extendSession(additionalMinutes: Int = SESSION_TIMEOUT_MINUTES) {
        viewModelScope.launch {
            val currentState = _sessionState.value
            if (currentState.isActive) {
                val newExpirationTime = currentState.expirationTime + (additionalMinutes * 60 * 1000)
                
                _sessionState.value = currentState.copy(
                    expirationTime = newExpirationTime,
                    lastActivityTime = System.currentTimeMillis()
                )
                
                Timber.d("Session extended by $additionalMinutes minutes")
            }
        }
    }
    
    fun expireSession() {
        viewModelScope.launch {
            val currentState = _sessionState.value
            _sessionState.value = currentState.copy(
                isActive = false,
                expirationTime = System.currentTimeMillis()
            )
            
            Timber.d("Session expired for user: ${currentState.userId}")
        }
    }
    
    fun endSession() {
        viewModelScope.launch {
            val currentState = _sessionState.value
            _sessionState.value = SessionState(
                isActive = false,
                sessionId = null,
                userId = null,
                startTime = 0L,
                lastActivityTime = 0L,
                expirationTime = Long.MAX_VALUE, // For offline mode, never expire
                sessionType = SessionState.SessionType.GUEST,
                deviceId = null,
                ipAddress = null,
                location = null
            )
            
            Timber.d("Session ended for user: ${currentState.userId}")
        }
    }
    
    fun clearExpiredSession() {
        viewModelScope.launch {
            _sessionState.value = SessionState(
                isActive = false,
                sessionId = null,
                userId = null,
                startTime = 0L,
                lastActivityTime = 0L,
                expirationTime = Long.MAX_VALUE, // For offline mode, never expire
                sessionType = SessionState.SessionType.GUEST,
                deviceId = null,
                ipAddress = null,
                location = null
            )
            
            Timber.d("Expired session cleared")
        }
    }
    
    fun isSessionExpired(): Boolean {
        val currentState = _sessionState.value
        return !currentState.isActive || System.currentTimeMillis() > currentState.expirationTime
    }
    
    fun getTimeUntilExpiration(): Long {
        val currentState = _sessionState.value
        return if (currentState.isActive) {
            maxOf(0, currentState.expirationTime - System.currentTimeMillis())
        } else {
            0
        }
    }
    
    fun shouldShowInactivityWarning(): Boolean {
        val currentState = _sessionState.value
        if (!currentState.isActive) return false
        
        val timeUntilExpiration = getTimeUntilExpiration()
        val warningThreshold = (SESSION_TIMEOUT_MINUTES - INACTIVITY_WARNING_MINUTES) * 60 * 1000
        
        return timeUntilExpiration <= warningThreshold
    }
    
    fun renewSession() {
        viewModelScope.launch {
            try {
                val currentState = _sessionState.value
                if (currentState.isActive && currentState.userId != null) {
                    // Renew the current session
                    val currentTime = System.currentTimeMillis()
                    val newExpirationTime = currentTime + (SESSION_TIMEOUT_MINUTES * 60 * 1000)
                    
                    _sessionState.value = currentState.copy(
                        lastActivityTime = currentTime,
                        expirationTime = newExpirationTime
                    )
                    
                    Timber.d("Session renewed for user: ${currentState.userId}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error renewing session")
            }
        }
    }
    
    fun refreshAuthentication() {
        viewModelScope.launch {
            try {
                val currentState = _sessionState.value
                if (currentState.userId != null) {
                    // Refresh authentication - restart session
                    startSession(currentState.userId, currentState.sessionType)
                    Timber.d("Authentication refreshed for user: ${currentState.userId}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing authentication")
            }
        }
    }
    
    fun showExpirationWarning() {
        viewModelScope.launch {
            try {
                val currentState = _sessionState.value
                if (shouldShowInactivityWarning()) {
                    Timber.w("Session expiration warning shown for user: ${currentState.userId}")
                    // TODO: Show actual warning dialog or notification
                }
            } catch (e: Exception) {
                Timber.e(e, "Error showing expiration warning")
            }
        }
    }
    
    fun cleanup() {
        endSession()
        Timber.d("SessionViewModel cleaned up")
    }
    
    // For testing purposes - simulate session expiry
    fun simulateSessionExpiry() {
        viewModelScope.launch {
            val currentState = _sessionState.value
            _sessionState.value = currentState.copy(
                expirationTime = System.currentTimeMillis() - 1000 // Set expiry to 1 second ago
            )
            Timber.d("Session expiry simulated for testing")
        }
    }
    
    private fun generateSessionId(): String {
        return "session_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    private fun getDeviceId(): String {
        // TODO: Get actual device ID
        return "device_${System.currentTimeMillis()}"
    }
    
    private fun getIpAddress(): String? {
        // TODO: Get actual IP address
        return null
    }
    
    private fun getCurrentLocation(): String? {
        // TODO: Get current location if permission granted
        return null
    }
}