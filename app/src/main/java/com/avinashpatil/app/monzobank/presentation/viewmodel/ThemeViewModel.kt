package com.avinashpatil.app.monzobank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.monzobank.domain.model.ThemeState
// import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
// import javax.inject.Inject

// @HiltViewModel
class ThemeViewModel(
    // TODO: Inject preferences repository when available
) : ViewModel() {
    
    private val _themeState = MutableStateFlow(
        ThemeState(
            isDarkMode = false,
            useDynamicColors = true,
            themeMode = ThemeState.ThemeMode.SYSTEM,
            accentColor = ThemeState.AccentColor.MONZO_CORAL
        )
    )
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()
    
    init {
        loadThemePreferences()
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            val currentState = _themeState.value
            val newState = currentState.copy(
                isDarkMode = !currentState.isDarkMode,
                themeMode = if (!currentState.isDarkMode) ThemeState.ThemeMode.DARK else ThemeState.ThemeMode.LIGHT
            )
            _themeState.value = newState
            saveThemePreferences(newState)
            Timber.d("Theme toggled - Dark mode: ${newState.isDarkMode}")
        }
    }
    
    fun setThemeMode(themeMode: ThemeState.ThemeMode) {
        viewModelScope.launch {
            val currentState = _themeState.value
            val isDarkMode = when (themeMode) {
                ThemeState.ThemeMode.LIGHT -> false
                ThemeState.ThemeMode.DARK -> true
                ThemeState.ThemeMode.SYSTEM -> currentState.isDarkMode // Keep current for now
            }
            
            val newState = currentState.copy(
                themeMode = themeMode,
                isDarkMode = isDarkMode
            )
            _themeState.value = newState
            saveThemePreferences(newState)
            Timber.d("Theme mode set to: $themeMode")
        }
    }
    
    fun toggleDynamicColors() {
        viewModelScope.launch {
            val currentState = _themeState.value
            val newState = currentState.copy(
                useDynamicColors = !currentState.useDynamicColors
            )
            _themeState.value = newState
            saveThemePreferences(newState)
            Timber.d("Dynamic colors toggled: ${newState.useDynamicColors}")
        }
    }
    
    fun setAccentColor(accentColor: ThemeState.AccentColor) {
        viewModelScope.launch {
            val currentState = _themeState.value
            val newState = currentState.copy(
                accentColor = accentColor
            )
            _themeState.value = newState
            saveThemePreferences(newState)
            Timber.d("Accent color set to: $accentColor")
        }
    }
    
    private fun loadThemePreferences() {
        viewModelScope.launch {
            try {
                // TODO: Load from preferences repository
                Timber.d("Theme preferences loaded")
            } catch (e: Exception) {
                Timber.e(e, "Error loading theme preferences")
            }
        }
    }
    
    private fun saveThemePreferences(themeState: ThemeState) {
        viewModelScope.launch {
            try {
                // TODO: Save to preferences repository
                Timber.d("Theme preferences saved")
            } catch (e: Exception) {
                Timber.e(e, "Error saving theme preferences")
            }
        }
    }
}