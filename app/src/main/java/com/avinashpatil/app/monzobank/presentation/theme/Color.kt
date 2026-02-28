package com.avinashpatil.app.monzobank.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Monzo Brand Colors
val MonzoPrimary = Color(0xFF14233C)
val MonzoSecondary = Color(0xFF00D4FF)
val MonzoAccent = Color(0xFFFF6B6B)

// Additional Monzo Colors referenced in UI files
val MonzoCoralPrimary = Color(0xFF14233C)
val MonzoCoralSecondary = Color(0xFFFF6B6B)
val MonzoCoralTertiary = MonzoSecondary
val MonzoBackground = Color(0xFFF8F9FA)
val MonzoSurface = Color(0xFFFFFFFF)
val MonzoError = Color(0xFFE53E3E)
val MonzoSuccess = Color(0xFF38A169)
val MonzoWarning = Color(0xFFD69E2E)

// Light Theme Colors
val LightColorScheme = lightColorScheme(
    primary = MonzoPrimary,
    onPrimary = Color.White,
    primaryContainer = MonzoPrimary.copy(alpha = 0.1f),
    onPrimaryContainer = MonzoPrimary,
    
    secondary = MonzoSecondary,
    onSecondary = Color.White,
    secondaryContainer = MonzoSecondary.copy(alpha = 0.1f),
    onSecondaryContainer = MonzoSecondary,
    
    tertiary = MonzoAccent,
    onTertiary = Color.White,
    tertiaryContainer = MonzoAccent.copy(alpha = 0.1f),
    onTertiaryContainer = MonzoAccent,
    
    error = MonzoError,
    onError = Color.White,
    errorContainer = MonzoError.copy(alpha = 0.1f),
    onErrorContainer = MonzoError,
    
    background = MonzoBackground,
    onBackground = Color(0xFF1C1B1F),
    
    surface = MonzoSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    
    scrim = Color(0xFF000000),
    
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFD0BCFF)
)

// Dark Theme Colors
val DarkColorScheme = darkColorScheme(
    primary = MonzoSecondary,
    onPrimary = Color.Black,
    primaryContainer = MonzoSecondary.copy(alpha = 0.2f),
    onPrimaryContainer = MonzoSecondary,
    
    secondary = MonzoPrimary.copy(alpha = 0.8f),
    onSecondary = Color.White,
    secondaryContainer = MonzoPrimary.copy(alpha = 0.3f),
    onSecondaryContainer = Color.White,
    
    tertiary = MonzoAccent,
    onTertiary = Color.White,
    tertiaryContainer = MonzoAccent.copy(alpha = 0.2f),
    onTertiaryContainer = MonzoAccent,
    
    error = MonzoError.copy(alpha = 0.9f),
    onError = Color.White,
    errorContainer = MonzoError.copy(alpha = 0.2f),
    onErrorContainer = MonzoError,
    
    background = Color(0xFF10131A),
    onBackground = Color(0xFFE6E1E5),
    
    surface = Color(0xFF1A1C23),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    
    scrim = Color(0xFF000000),
    
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = MonzoPrimary
)

// Additional semantic colors
val SuccessColor = MonzoSuccess
val WarningColor = MonzoWarning
val InfoColor = MonzoSecondary

// Transaction colors
val IncomeColor = MonzoSuccess
val ExpenseColor = MonzoError
val TransferColor = MonzoSecondary

// Card colors
val CardPrimary = Color(0xFF2D3748)
val CardSecondary = Color(0xFF4A5568)
val CardAccent = MonzoAccent

// Status colors
val ActiveColor = MonzoSuccess
val InactiveColor = Color(0xFF9CA3AF)
val PendingColor = MonzoWarning
val BlockedColor = MonzoError