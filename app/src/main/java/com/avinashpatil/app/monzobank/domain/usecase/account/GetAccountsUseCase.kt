package com.avinashpatil.app.monzobank.domain.usecase.account

import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Account>> {
        return try {
            accountRepository.getAccountsByUserId(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observeAccounts(userId: String): Flow<List<Account>> {
        return accountRepository.observeAccountsByUserId(userId)
            .map { accounts ->
                // Sort accounts by default status, then by balance
                accounts.sortedWith(
                    compareByDescending<Account> { it.isDefault }
                        .thenByDescending { it.balance }
                )
            }
    }
    
    suspend fun getActiveAccounts(userId: String): Result<List<Account>> {
        return try {
            val result = accountRepository.getAccountsByUserId(userId)
            result.map { accounts ->
                accounts.filter { it.status == com.avinashpatil.app.monzobank.data.local.entity.AccountStatus.ACTIVE }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAccountsByType(
        userId: String,
        type: com.avinashpatil.app.monzobank.data.local.entity.AccountType
    ): Result<List<Account>> {
        return try {
            accountRepository.getAccountsByType(userId, type)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}