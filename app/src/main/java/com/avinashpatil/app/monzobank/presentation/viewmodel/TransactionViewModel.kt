package com.avinashpatil.app.monzobank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.model.TransactionCategory
import com.avinashpatil.app.monzobank.domain.usecase.transaction.CreateTransactionUseCase
import com.avinashpatil.app.monzobank.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

// @HiltViewModel - Temporarily disabled for build
class TransactionViewModel : ViewModel() {
    
    // Mock implementations for now - will be restored when dependencies are available
    // private val getTransactionsUseCase: GetTransactionsUseCase? = null
    // private val createTransactionUseCase: CreateTransactionUseCase? = null
    
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()
    
    fun loadTransactions(
        accountId: String,
        startDate: Date? = null,
        endDate: Date? = null,
        category: TransactionCategory? = null,
        searchQuery: String? = null
    ) {
        // Mock implementation - in real app this would use getTransactionsUseCase
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            transactions = emptyList(),
            error = null
        )
    }
    
    fun loadRecentTransactions(accountId: String, limit: Int = 10) {
        // Mock implementation - in real app this would use getTransactionsUseCase
        _uiState.value = _uiState.value.copy(
            isLoadingRecent = false,
            recentTransactions = emptyList(),
            error = null
        )
    }
    
    fun createTransaction(
        accountId: String,
        amount: BigDecimal,
        transactionType: TransactionType,
        description: String,
        merchantName: String? = null,
        category: TransactionCategory? = null,
        location: Any? = null, // Transaction.TransactionLocation? = null,
        reference: String? = null
    ) {
        // Mock implementation - in real app this would use createTransactionUseCase
        _uiState.value = _uiState.value.copy(
            isCreatingTransaction = false,
            error = null
        )
    }
    
    fun selectTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
    }
    
    fun getTransactionsByCategory(): Map<String, List<Transaction>> {
        return _uiState.value.transactions.groupBy { it.category.name }
    }
    
    fun getSpendingByCategory(): Map<String, BigDecimal> {
        return _uiState.value.transactions
            .filter { it.type == TransactionType.DEBIT || it.type == TransactionType.WITHDRAWAL }
            .groupBy { it.category.name }
            .mapValues { (_, transactions) ->
                transactions.map { it.amount }.fold(BigDecimal.ZERO) { acc, amount -> acc + amount }
            }
    }
    
    fun getTotalSpending(): BigDecimal {
        return _uiState.value.transactions
            .filter { it.type == TransactionType.DEBIT || it.type == TransactionType.WITHDRAWAL }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { acc, amount -> acc + amount }
    }
    
    fun getTotalIncome(): BigDecimal {
        return _uiState.value.transactions
            .filter { it.type == TransactionType.CREDIT || it.type == TransactionType.DEPOSIT }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { acc, amount -> acc + amount }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSelectedTransaction() {
        _selectedTransaction.value = null
    }
}

data class TransactionUiState(
    val isLoading: Boolean = false,
    val isLoadingRecent: Boolean = false,
    val isCreatingTransaction: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null
)