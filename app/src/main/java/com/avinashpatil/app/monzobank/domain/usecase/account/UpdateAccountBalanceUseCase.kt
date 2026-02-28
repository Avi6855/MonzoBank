package com.avinashpatil.app.monzobank.domain.usecase.account

import com.avinashpatil.app.monzobank.domain.repository.AccountRepository
import java.math.BigDecimal
import javax.inject.Inject

class UpdateAccountBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        accountId: String,
        amount: BigDecimal,
        isDebit: Boolean = false
    ): Result<Unit> {
        return try {
            val accountResult = accountRepository.getAccountById(accountId)
            val account = accountResult.getOrNull()
                ?: return Result.failure(Exception("Account not found"))
            
            val newBalance = if (isDebit) {
                account.balance - amount
            } else {
                account.balance + amount
            }
            
            // Check for overdraft limits
            if (isDebit && account.overdraftLimit != null) {
                val availableBalance = account.balance + account.overdraftLimit!!
                if (amount > availableBalance) {
                    return Result.failure(Exception("Insufficient funds. Transaction exceeds overdraft limit."))
                }
            } else if (isDebit && newBalance < BigDecimal.ZERO) {
                return Result.failure(Exception("Insufficient funds. No overdraft facility available."))
            }
            
            // Check minimum balance requirements
            if (account.minimumBalance != null && newBalance < account.minimumBalance!!) {
                return Result.failure(Exception("Transaction would breach minimum balance requirement."))
            }
            
            accountRepository.updateBalance(accountId, newBalance.toDouble(), "balance_update")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}