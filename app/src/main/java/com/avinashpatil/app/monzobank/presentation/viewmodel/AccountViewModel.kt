package com.avinashpatil.app.monzobank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.model.AccountInsight
import com.avinashpatil.app.monzobank.domain.model.AccountSummary
import com.avinashpatil.app.monzobank.domain.usecase.account.CreateAccountUseCase
import com.avinashpatil.app.monzobank.domain.usecase.account.GetAccountsUseCase
import com.avinashpatil.app.monzobank.domain.repository.AccountRepository
import com.avinashpatil.app.monzobank.domain.repository.UserRepository
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import java.math.BigDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Temporarily disabled for build
class AccountViewModel : ViewModel() {
    
    // Mock implementations for now - will be restored when dependencies are available
    // private val getAccountsUseCase: GetAccountsUseCase? = null
    // private val createAccountUseCase: CreateAccountUseCase? = null
    // private val accountRepository: AccountRepository? = null
    // private val userRepository: UserRepository? = null
    
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()
    
    private val _currentUserId = MutableStateFlow<String?>(null)
    
    init {
        // Mock initialization - will be restored when dependencies are available
        // viewModelScope.launch {
        //     userRepository?.getCurrentUser()?.collect { user ->
        //         _currentUserId.value = user?.id
        //         user?.id?.let { userId ->
        //             loadAccounts(userId)
        //             loadAccountSummary(userId)
        //             loadAccountInsights(userId)
        //         }
        //     }
        // }
    }
    
    fun loadAccounts(userId: String? = null) {
        val targetUserId = userId ?: _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Mock implementation - will be restored when dependencies are available
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        accounts = emptyList(),
                        totalBalance = 0.0,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load accounts: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun createAccount(
        accountType: AccountType,
        accountName: String,
        initialDeposit: Double = 0.0
    ) {
        val userId = _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingAccount = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isCreatingAccount = false,
                    successMessage = "Account created successfully (mock)"
                )
            }
            
            // Clear success message after delay
            clearSuccessMessage()
        }
    }
    
    fun freezeAccount(accountId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAccount = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingAccount = false,
                    successMessage = "Account frozen successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun unfreezeAccount(accountId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAccount = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingAccount = false,
                    successMessage = "Account unfrozen successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun closeAccount(accountId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAccount = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingAccount = false,
                    successMessage = "Account closed successfully (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun setDefaultAccount(accountId: String) {
        val userId = _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAccount = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingAccount = false,
                    successMessage = "Default account updated (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun updateOverdraftLimit(accountId: String, newLimit: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAccount = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isUpdatingAccount = false,
                    successMessage = "Overdraft limit updated (mock)"
                )
            }
            clearSuccessMessage()
        }
    }
    
    fun searchAccounts(query: String) {
        val userId = _currentUserId.value ?: return
        
        if (query.isBlank()) {
            loadAccounts(userId)
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Mock implementation - will be restored when dependencies are available
            _uiState.update {
                it.copy(
                    isLoading = false,
                    accounts = emptyList(),
                    error = null
                )
            }
        }
    }
    
    fun filterAccountsByType(accountType: AccountType?) {
        val userId = _currentUserId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            if (accountType == null) {
                loadAccounts(userId)
            } else {
                // Mock implementation - will be restored when dependencies are available
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        accounts = emptyList(),
                        error = null
                    )
                }
            }
        }
    }
    
    private fun loadAccountSummary(userId: String) {
        viewModelScope.launch {
            // Mock implementation - will be restored when dependencies are available
            // accountRepository?.getAccountSummary(userId)?.fold(
            //     onSuccess = { summary ->
            //         _uiState.update {
            //             it.copy(accountSummary = summary)
            //         }
            //     },
            //     onFailure = { exception ->
            //         println("Failed to load account summary: ${exception.message}")
            //     }
            // )
        }
    }
    
    private fun loadAccountInsights(userId: String) {
        viewModelScope.launch {
            // Mock implementation - will be restored when dependencies are available
            // accountRepository?.getAccountInsights(userId)?.fold(
            //     onSuccess = { insights ->
            //         _uiState.update {
            //             it.copy(accountInsights = insights)
            //         }
            //     },
            //     onFailure = { exception ->
            //         println("Failed to load account insights: ${exception.message}")
            //     }
            // )
        }
    }
    
    fun refreshData() {
        val userId = _currentUserId.value ?: return
        loadAccounts(userId)
        loadAccountSummary(userId)
        loadAccountInsights(userId)
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
    
    fun getAccountById(accountId: String): Account? {
        return _uiState.value.accounts.find { it.id == accountId }
    }
    
    fun getDefaultAccount(): Account? {
        return _uiState.value.accounts.find { it.isDefault }
    }
    
    fun getAccountsByType(type: AccountType): List<Account> {
        return _uiState.value.accounts.filter { it.type == type }
    }
    
    fun getTotalAvailableBalance(): Double {
        return 0.0 // Mock calculation - will be restored when Account model is fixed
    }
    
    fun hasOverdraftAccounts(): Boolean {
        return false // Mock value - will be restored when Account model is fixed
    }
    
    fun getOverdraftUsage(): Double {
        return 0.0 // Mock calculation - will be restored when Account model is fixed
    }
}

data class AccountUiState(
    val isLoading: Boolean = false,
    val isCreatingAccount: Boolean = false,
    val isUpdatingAccount: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val accountSummary: AccountSummary? = null,
    val accountInsights: List<AccountInsight> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
) {
    val hasAccounts: Boolean
        get() = accounts.isNotEmpty()
    
    val activeAccounts: List<Account>
        get() = accounts.filter { true } // Mock filter - will be restored when Account model is fixed
    
    val frozenAccounts: List<Account>
        get() = accounts.filter { false } // Mock filter - will be restored when Account model is fixed
    
    val accountsByType: Map<AccountType, List<Account>>
        get() = accounts.groupBy { it.type }
    
    val totalAvailableBalance: Double
        get() = accounts.sumOf { 0.0 } // Mock calculation - will be restored when Account model is fixed
    
    val hasOverdrawnAccounts: Boolean
        get() = false // Mock value - will be restored when Account model is fixed
    
    val overdrawnAccounts: List<Account>
        get() = emptyList() // Mock list - will be restored when Account model is fixed
}