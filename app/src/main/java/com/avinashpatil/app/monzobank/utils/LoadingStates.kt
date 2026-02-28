package com.avinashpatil.app.monzobank.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Loading state management
 */
data class LoadingState(
    val isLoading: Boolean = false,
    val message: String = "Loading...",
    val progress: Float? = null,
    val canCancel: Boolean = false
)

/**
 * Different types of loading operations
 */
enum class LoadingType {
    AUTHENTICATION,
    ACCOUNT_CREATION,
    TRANSACTION_PROCESSING,
    DATA_SYNC,
    CARD_OPERATIONS,
    PAYMENT_PROCESSING,
    SECURITY_CHECK,
    GENERAL
}

/**
 * Loading messages for different operations
 */
object LoadingMessages {
    fun getMessage(type: LoadingType): String {
        return when (type) {
            LoadingType.AUTHENTICATION -> "Signing you in..."
            LoadingType.ACCOUNT_CREATION -> "Creating your account..."
            LoadingType.TRANSACTION_PROCESSING -> "Processing transaction..."
            LoadingType.DATA_SYNC -> "Syncing your data..."
            LoadingType.CARD_OPERATIONS -> "Updating card settings..."
            LoadingType.PAYMENT_PROCESSING -> "Processing payment..."
            LoadingType.SECURITY_CHECK -> "Performing security checks..."
            LoadingType.GENERAL -> "Loading..."
        }
    }
    
    fun getIcon(type: LoadingType): ImageVector {
        return when (type) {
            LoadingType.AUTHENTICATION -> Icons.Default.Person
            LoadingType.ACCOUNT_CREATION -> Icons.Default.AccountBox
            LoadingType.TRANSACTION_PROCESSING -> Icons.Default.Send
            LoadingType.DATA_SYNC -> Icons.Default.Refresh
            LoadingType.CARD_OPERATIONS -> Icons.Default.Star
            LoadingType.PAYMENT_PROCESSING -> Icons.Default.Send
            LoadingType.SECURITY_CHECK -> Icons.Default.Lock
            LoadingType.GENERAL -> Icons.Default.Info
        }
    }
}

/**
 * Composable for displaying loading overlay
 */
@Composable
fun LoadingOverlay(
    isVisible: Boolean,
    loadingType: LoadingType = LoadingType.GENERAL,
    message: String? = null,
    progress: Float? = null,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .widthIn(min = 280.dp, max = 400.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Loading animation
                    if (progress != null) {
                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Loading message
                    Text(
                        text = message ?: LoadingMessages.getMessage(loadingType),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Progress percentage
                    if (progress != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Cancel button
                    if (onCancel != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline loading indicator for buttons
 */
@Composable
fun ButtonLoadingIndicator(
    isLoading: Boolean,
    loadingText: String = "Loading...",
    normalContent: @Composable () -> Unit
) {
    if (isLoading) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = loadingText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        normalContent()
    }
}

/**
 * Skeleton loading for list items
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                        )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subtitle placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                        )
                )
            }
            
            // Amount placeholder
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
            )
        }
    }
}

/**
 * Skeleton loading for account cards
 */
@Composable
fun SkeletonAccountCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Account name placeholder
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                        )
                )
                
                // Icon placeholder
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                        )
                )
            }
            
            // Account number placeholder
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
            )
            
            // Balance placeholder
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
            )
        }
    }
}

/**
 * Pull-to-refresh loading indicator
 */
@Composable
fun PullToRefreshIndicator(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    if (isRefreshing) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

/**
 * Loading state manager
 */
class LoadingStateManager {
    private val _loadingStates = mutableMapOf<String, MutableState<LoadingState>>()
    
    fun getLoadingState(key: String): State<LoadingState> {
        return _loadingStates.getOrPut(key) {
            mutableStateOf(LoadingState())
        }
    }
    
    fun setLoading(
        key: String,
        isLoading: Boolean,
        message: String? = null,
        progress: Float? = null
    ) {
        val currentState = _loadingStates.getOrPut(key) {
            mutableStateOf(LoadingState())
        }
        
        currentState.value = LoadingState(
            isLoading = isLoading,
            message = message ?: currentState.value.message,
            progress = progress,
            canCancel = currentState.value.canCancel
        )
    }
    
    fun updateProgress(key: String, progress: Float) {
        _loadingStates[key]?.let { state ->
            state.value = state.value.copy(progress = progress)
        }
    }
    
    fun clearLoading(key: String) {
        _loadingStates[key]?.value = LoadingState()
    }
    
    fun clearAllLoading() {
        _loadingStates.clear()
    }
}

/**
 * Composable function to simulate loading with progress
 */
@Composable
fun SimulateProgressLoading(
    isLoading: Boolean,
    durationMs: Long = 3000,
    onComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(isLoading) {
        if (isLoading) {
            val steps = 100
            val stepDuration = durationMs / steps
            
            for (i in 0..steps) {
                progress = i / steps.toFloat()
                delay(stepDuration)
            }
            
            onComplete()
        } else {
            progress = 0f
        }
    }
    
    if (isLoading) {
        LoadingOverlay(
            isVisible = true,
            progress = progress,
            message = "Processing..."
        )
    }
}