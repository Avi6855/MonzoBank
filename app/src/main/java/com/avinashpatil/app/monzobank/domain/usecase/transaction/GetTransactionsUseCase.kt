package com.avinashpatil.app.monzobank.domain.usecase.transaction

import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.model.TransactionCategory
import com.avinashpatil.app.monzobank.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(
        accountId: String,
        startDate: Date? = null,
        endDate: Date? = null,
        category: TransactionCategory? = null,
        searchQuery: String? = null
    ): Flow<List<Transaction>> = flow {
        val result = when {
            startDate != null && endDate != null -> {
                transactionRepository.getTransactionsByDateRange(accountId, startDate, endDate)
            }
            category != null -> {
                transactionRepository.getTransactionsByCategory(accountId, category)
            }
            searchQuery != null -> {
                transactionRepository.searchTransactions("user123", searchQuery) // TODO: Get actual userId
            }
            else -> {
                transactionRepository.getTransactionsByAccountId(accountId)
            }
        }
        
        result.fold(
            onSuccess = { transactions -> emit(transactions) },
            onFailure = { emit(emptyList()) }
        )
    }
    
    fun getRecentTransactions(accountId: String, limit: Int = 10): Flow<List<Transaction>> = flow {
        val result = transactionRepository.getTransactionsByAccountId(accountId, limit)
        result.fold(
            onSuccess = { transactions -> emit(transactions) },
            onFailure = { emit(emptyList()) }
        )
    }
}