package com.avinashpatil.app.monzobank.presentation.ui.accounts

import com.avinashpatil.app.monzobank.domain.model.Account

/**
 * UI state for the Accounts screen
 */
data class AccountUiState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatingAccount: Boolean = false,
    val error: String? = null,
    val selectedAccount: Account? = null,
    val totalBalance: Double = 0.0,
    val isRefreshing: Boolean = false
) {
    val hasAccounts: Boolean
        get() = accounts.isNotEmpty()
    
    val accountCount: Int
        get() = accounts.size
    
    val formattedTotalBalance: String
        get() = "£${String.format("%.2f", totalBalance)}"
}