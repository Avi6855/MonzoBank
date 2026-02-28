package com.avinashpatil.app.monzobank.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Sealed class representing different types of application errors
 */
sealed class AppError {
    abstract val message: String
    abstract val userMessage: String
    abstract val icon: ImageVector
    
    data class NetworkError(
        override val message: String = "Network connection failed",
        override val userMessage: String = "Please check your internet connection and try again",
        override val icon: ImageVector = Icons.Default.Warning
    ) : AppError()
    
    data class ValidationError(
        override val message: String,
        override val userMessage: String = message,
        override val icon: ImageVector = Icons.Default.Warning
    ) : AppError()
    
    data class AuthenticationError(
        override val message: String = "Authentication failed",
        override val userMessage: String = "Please log in again to continue",
        override val icon: ImageVector = Icons.Default.Lock
    ) : AppError()
    
    data class ServerError(
        override val message: String = "Server error occurred",
        override val userMessage: String = "Something went wrong on our end. Please try again later",
        override val icon: ImageVector = Icons.Default.Warning
    ) : AppError()
    
    data class SecurityError(
        override val message: String = "Security check failed",
        override val userMessage: String = "This transaction has been flagged for security review",
        override val icon: ImageVector = Icons.Default.Lock
    ) : AppError()
    
    data class InsufficientFundsError(
        override val message: String = "Insufficient funds",
        override val userMessage: String = "You don't have enough funds for this transaction",
        override val icon: ImageVector = Icons.Default.Warning
    ) : AppError()
    
    data class RateLimitError(
        override val message: String = "Rate limit exceeded",
        override val userMessage: String = "Too many requests. Please wait a moment and try again",
        override val icon: ImageVector = Icons.Default.Warning
    ) : AppError()
    
    data class UnknownError(
        override val message: String = "Unknown error occurred",
        override val userMessage: String = "An unexpected error occurred. Please try again",
        override val icon: ImageVector = Icons.Default.Info
    ) : AppError()
}

/**
 * Result wrapper for handling success and error states
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Extension function to convert exceptions to AppError
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is ConnectException, is UnknownHostException -> AppError.NetworkError()
        is SocketTimeoutException -> AppError.NetworkError(
            message = "Connection timeout",
            userMessage = "The request timed out. Please check your connection and try again"
        )
        is SecurityException -> AppError.SecurityError(message = this.message ?: "Security error")
        is IllegalArgumentException -> AppError.ValidationError(message = this.message ?: "Invalid input")
        is IllegalStateException -> AppError.ValidationError(message = this.message ?: "Invalid state")
        else -> AppError.UnknownError(message = this.message ?: "Unknown error")
    }
}

/**
 * Error handling utilities
 */
object ErrorHandler {
    
    /**
     * Handle API response and convert to Result
     */
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Result<T> {
        return try {
            Result.Success(apiCall())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
    
    /**
     * Handle database operations
     */
    suspend fun <T> safeDatabaseCall(
        databaseCall: suspend () -> T
    ): Result<T> {
        return try {
            Result.Success(databaseCall())
        } catch (e: Exception) {
            Result.Error(
                AppError.UnknownError(
                    message = "Database error: ${e.message}",
                    userMessage = "Failed to save data. Please try again"
                )
            )
        }
    }
    
    /**
     * Validate transaction amount
     */
    fun validateTransactionAmount(
        amount: String,
        accountBalance: java.math.BigDecimal,
        overdraftLimit: java.math.BigDecimal? = null
    ): AppError? {
        return try {
            val amountDecimal = java.math.BigDecimal(amount)
            val availableBalance = accountBalance + (overdraftLimit ?: java.math.BigDecimal.ZERO)
            
            when {
                amountDecimal <= java.math.BigDecimal.ZERO -> 
                    AppError.ValidationError("Amount must be greater than zero")
                amountDecimal > availableBalance -> 
                    AppError.InsufficientFundsError()
                amountDecimal > java.math.BigDecimal("10000") -> 
                    AppError.ValidationError("Amount cannot exceed £10,000")
                else -> null
            }
        } catch (e: NumberFormatException) {
            AppError.ValidationError("Please enter a valid amount")
        }
    }
    
    /**
     * Rate limiting check
     */
    private val requestCounts = mutableMapOf<String, Pair<Long, Int>>()
    
    fun checkRateLimit(userId: String, maxRequests: Int = 10, windowMs: Long = 60000): AppError? {
        val now = System.currentTimeMillis()
        val (lastReset, count) = requestCounts[userId] ?: (now to 0)
        
        return if (now - lastReset > windowMs) {
            requestCounts[userId] = now to 1
            null
        } else if (count >= maxRequests) {
            AppError.RateLimitError()
        } else {
            requestCounts[userId] = lastReset to count + 1
            null
        }
    }
}

/**
 * Composable for displaying error messages
 */
@Composable
fun ErrorMessage(
    error: AppError,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = error.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Text(
                        text = error.userMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                
                if (onDismiss != null) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again")
                }
            }
        }
    }
}

/**
 * Composable for displaying loading state
 */
@Composable
fun LoadingIndicator(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable for handling Result states
 */
@Composable
fun <T> ResultHandler(
    result: Result<T>,
    onLoading: @Composable () -> Unit = { LoadingIndicator() },
    onError: @Composable (AppError) -> Unit = { error ->
        ErrorMessage(error = error)
    },
    onSuccess: @Composable (T) -> Unit
) {
    when (result) {
        is Result.Loading -> onLoading()
        is Result.Error -> onError(result.error)
        is Result.Success -> onSuccess(result.data)
    }
}

/**
 * Extension function for safe string to BigDecimal conversion
 */
fun String.toBigDecimalOrNull(): java.math.BigDecimal? {
    return try {
        java.math.BigDecimal(this)
    } catch (e: NumberFormatException) {
        null
    }
}

/**
 * Extension function for safe string to Int conversion
 */
fun String.toIntOrNull(): Int? {
    return try {
        this.toInt()
    } catch (e: NumberFormatException) {
        null
    }
}

/**
 * Logging utility for errors
 */
object ErrorLogger {
    fun logError(error: AppError, context: String = "") {
        // In a real app, this would log to a service like Firebase Crashlytics
        println("ERROR [$context]: ${error.message}")
    }
    
    fun logException(exception: Throwable, context: String = "") {
        // In a real app, this would log to a service like Firebase Crashlytics
        println("EXCEPTION [$context]: ${exception.message}")
        exception.printStackTrace()
    }
}

/**
 * Network connectivity checker
 */
object NetworkUtils {
    fun isNetworkAvailable(): Boolean {
        // In a real app, this would check actual network connectivity
        // For now, we'll assume network is always available
        return true
    }
    
    fun getNetworkError(): AppError? {
        return if (!isNetworkAvailable()) {
            AppError.NetworkError()
        } else {
            null
        }
    }
}