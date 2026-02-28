package com.avinashpatil.app.monzobank.domain.usecase.account

import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.repository.AccountRepository
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        userId: String,
        accountType: AccountType,
        accountName: String? = null,
        currency: String = "GBP",
        initialBalance: BigDecimal = BigDecimal.ZERO
    ): Result<Account> {
        return try {
            val accountNumber = generateAccountNumber()
            val sortCode = generateSortCode()
            
            val account = Account(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = accountName ?: "${accountType.name.lowercase().replaceFirstChar { it.uppercase() }} Account",
                type = accountType,
                balance = initialBalance,
                availableBalance = initialBalance,
                currency = currency,
                accountNumber = accountNumber,
                sortCode = sortCode,
                iban = "GB29 NWBK 6016 1331 9268 19", // Generate proper IBAN later
                status = AccountStatus.ACTIVE,
                overdraftLimit = getDefaultOverdraftLimit(accountType) ?: BigDecimal.ZERO,
                interestRate = BigDecimal.valueOf(getDefaultInterestRate(accountType) ?: 0.0),
                createdAt = Date(),
                updatedAt = Date(),
                isDefault = false,
                metadata = emptyMap()
            )
            
            accountRepository.createAccount(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateAccountNumber(): String {
        // Generate 8-digit account number
        return (10000000..99999999).random().toString()
    }
    
    private fun generateSortCode(): String {
        // Monzo sort code format: 04-00-04
        return "04-00-04"
    }
    
    private fun getDefaultInterestRate(accountType: AccountType): Double? {
        return when (accountType) {
            AccountType.SAVINGS -> 2.5
            AccountType.BUSINESS -> 1.0
            AccountType.ISA -> 3.0
            AccountType.PREMIUM -> 1.5
            else -> null
        }
    }
    
    private fun getDefaultOverdraftLimit(accountType: AccountType): BigDecimal? {
        return when (accountType) {
            AccountType.CURRENT -> BigDecimal("500.00")
            AccountType.BUSINESS -> BigDecimal("2000.00")
            AccountType.PREMIUM -> BigDecimal("1000.00")
            AccountType.ISA -> null
            AccountType.SAVINGS -> null
            AccountType.JOINT -> BigDecimal("750.00")
        }
    }
    
    private fun getDefaultMinimumBalance(accountType: AccountType): BigDecimal? {
        return when (accountType) {
            AccountType.SAVINGS -> BigDecimal("1.00")
            AccountType.BUSINESS -> BigDecimal("100.00")
            AccountType.ISA -> BigDecimal("1.00")
            AccountType.PREMIUM -> BigDecimal("500.00")
            AccountType.CURRENT -> BigDecimal.ZERO
            AccountType.JOINT -> BigDecimal.ZERO
        }
    }
}