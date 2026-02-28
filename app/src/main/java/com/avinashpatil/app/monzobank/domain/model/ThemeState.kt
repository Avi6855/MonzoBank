package com.avinashpatil.app.monzobank.domain.model

data class ThemeState(
    val isDarkMode: Boolean = false,
    val useDynamicColors: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.MONZO_CORAL,
    val fontSize: FontSize = FontSize.MEDIUM,
    val isHighContrast: Boolean = false
) {
    enum class ThemeMode {
        LIGHT,
        DARK,
        SYSTEM
    }
    
    enum class AccentColor {
        MONZO_CORAL,
        BLUE,
        GREEN,
        PURPLE,
        ORANGE
    }
    
    enum class FontSize {
        SMALL,
        MEDIUM,
        LARGE,
        EXTRA_LARGE
    }
}