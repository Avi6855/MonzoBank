package com.avinashpatil.app.monzobank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.avinashpatil.app.monzobank.domain.model.*
import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import com.avinashpatil.app.monzobank.domain.repository.TransactionRepository
import com.avinashpatil.app.monzobank.domain.repository.UserRepository
import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.CardStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

// @HiltViewModel - Temporarily disabled for build
class CardViewModel : ViewModel() {
    
    // Mock implementations for now - will be restored when dependencies are available
    // private val cardRepository: CardRepository? = null
    // private val transactionRepository: TransactionRepository? = null
    // private val userRepository: UserRepository? = null
    
    private val _uiState = MutableStateFlow(CardUiState())
    val uiState: StateFlow<CardUiState> = _uiState.asStateFlow()
    
    private val _currentUserId = MutableStateFlow<String?>(null)
    
    init {
        // Mock initialization - will be restored when dependencies are available
        // viewModelScope.launch {
        //     userRepository?.getCurrentUser()?.collect { user ->
        //         _currentUserId.value = user?.id
        //         user?.id?.let { userId ->
        //             loadCards(userId)
        //             loadRecentTransactions(userId)
        //         }
        //     }
        // }
    }
    
    fun loadCards(userId: String? = null) {
        val targetUserId = userId ?: _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Mock implementation - will be restored when dependencies are available
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cards = emptyList(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load cards: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun createCard(cardType: CardType) {
        val userId = _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isCreatingCard = false,
                    successMessage = when (cardType) {
                        CardType.VIRTUAL -> "Virtual card created successfully (mock)"
                        CardType.DEBIT -> "Debit card ordered successfully (mock). It will arrive in 2-3 business days."
                        CardType.CREDIT -> "Credit card ordered successfully (mock). It will arrive in 2-3 business days."
                        CardType.PREPAID -> "Prepaid card created successfully (mock)"
                        CardType.BUSINESS_DEBIT -> "Business debit card ordered successfully (mock). It will arrive in 2-3 business days."
                        CardType.BUSINESS_CREDIT -> "Business credit card ordered successfully (mock). It will arrive in 2-3 business days."
                        CardType.PREMIUM -> "Premium card ordered successfully (mock). It will arrive in 2-3 business days."
                    }
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun freezeCard(cardId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Card frozen successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun unfreezeCard(cardId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Card unfrozen successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun cancelCard(cardId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Card cancelled successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateSpendingLimit(cardId: String, newLimit: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Spending limit updated (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun toggleContactless(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = if (enabled) "Contactless enabled (mock)" else "Contactless disabled (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun toggleInternationalUsage(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = if (enabled) "International usage enabled (mock)" else "International usage disabled (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun toggleOnlinePayments(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = if (enabled) "Online payments enabled (mock)" else "Online payments disabled (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun toggleATMWithdrawals(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = if (enabled) "ATM withdrawals enabled (mock)" else "ATM withdrawals disabled (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun reportLostOrStolen(cardId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Card reported and frozen (mock). A replacement will be sent."
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun requestReplacement(cardId: String, reason: String, deliveryAddress: Any? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Replacement card ordered (mock). It will arrive in 2-3 business days."
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun activateCard(cardId: String, activationCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Card activated successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun changePIN(cardId: String, currentPIN: String, newPIN: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "PIN changed successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun loadCardUsage(cardId: String) {
        viewModelScope.launch {
            // Mock implementation - will be restored when dependencies are available
            // Don't show error for usage failure, it's not critical
            println("Mock: Loading card usage for $cardId")
        }
    }
    
    fun loadRecentTransactions(userId: String? = null) {
        val targetUserId = userId ?: _currentUserId.value ?: return
        
        viewModelScope.launch {
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(recentTransactions = emptyList())
            }
            println("Mock: Loading recent transactions for user $targetUserId")
        }
    }
    
    fun searchCards(query: String) {
        val userId = _currentUserId.value ?: return
        
        if (query.isBlank()) {
            loadCards(userId)
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isLoading = false,
                    cards = emptyList()
                )
            }
            println("Mock: Searching cards for user $userId with query: $query")
        }
    }
    
    fun filterCardsByType(cardType: CardType?) {
        val userId = _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            if (cardType == null) {
                loadCards(userId)
            } else {
                // Mock implementation - will be restored when dependencies are available
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cards = emptyList()
                    )
                }
                println("Mock: Filtering cards by type $cardType for user $userId")
            }
        }
    }
    
    fun refreshData() {
        val userId = _currentUserId.value ?: return
        loadCards(userId)
        loadRecentTransactions(userId)
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearSuccessMessage() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000) // Show success message for 3 seconds
            _uiState.update { it.copy(successMessage = null) }
        }
    }
    
    fun getCardById(cardId: String): Card? {
        return _uiState.value.cards.find { it.id == cardId }
    }
    
    fun getActiveCards(): List<Card> {
        return _uiState.value.cards.filter { it.status == CardStatus.ACTIVE }
    }
    
    fun getCardsByType(type: CardType): List<Card> {
        return _uiState.value.cards.filter { it.cardType == type }
    }
    
    fun getCardUsage(cardId: String): CardUsage? {
        return _uiState.value.cardUsage[cardId]
    }
    
    fun updateContactlessEnabled(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Contactless ${if (enabled) "enabled" else "disabled"} (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateInternationalEnabled(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "International payments ${if (enabled) "enabled" else "disabled"} (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateOnlineEnabled(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Online payments ${if (enabled) "enabled" else "disabled"} (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateAtmEnabled(cardId: String, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "ATM withdrawals ${if (enabled) "enabled" else "disabled"} (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateDailyLimit(cardId: String, newLimit: BigDecimal) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Daily limit updated to £${newLimit} (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateMonthlyLimit(cardId: String, newLimit: BigDecimal) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCard = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingCard = false,
                    successMessage = "Monthly limit updated to £${newLimit} (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
}

data class CardUiState(
    val isLoading: Boolean = false,
    val isCreatingCard: Boolean = false,
    val isUpdatingCard: Boolean = false,
    val cards: List<Card> = emptyList(),
    val cardUsage: Map<String, CardUsage> = emptyMap(),
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
) {
    val hasCards: Boolean
        get() = cards.isNotEmpty()
    
    val activeCards: List<Card>
        get() = cards.filter { it.status == CardStatus.ACTIVE }
    
    val frozenCards: List<Card>
        get() = cards.filter { it.status == CardStatus.FROZEN }
    
    val physicalCards: List<Card>
        get() = cards.filter { it.cardType == CardType.DEBIT || it.cardType == CardType.CREDIT }
    
    val virtualCards: List<Card>
        get() = cards.filter { it.cardType == CardType.VIRTUAL }
    
    val inactiveCards: List<Card>
        get() = cards.filter { it.status == CardStatus.INACTIVE }
    
    val expiredCards: List<Card>
        get() = cards.filter { it.isExpired }
    
    val totalSpendingLimit: Double
        get() = activeCards.sumOf { it.dailyLimit.toDouble() }
    
    val totalDailySpent: Double
        get() = cardUsage.values.sumOf { it.dailySpent.toDouble() }
    
    val totalMonthlySpent: Double
        get() = cardUsage.values.sumOf { it.monthlySpent.toDouble() }
}