package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.Account
import com.avinashpatil.monzobank.entity.AccountType
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.AccountRepository
import com.avinashpatil.monzobank.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

@Service
@Transactional
class AccountService(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository
) {
    
    fun createAccount(userId: UUID, request: CreateAccountRequest): AccountResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        // Generate unique account number and sort code
        val accountNumber = generateAccountNumber()
        val sortCode = generateSortCode()
        
        // Validate account type limits
        validateAccountCreation(user.id, request.accountType)
        
        val account = Account(
            user = user,
            accountType = request.accountType,
            accountNumber = accountNumber,
            sortCode = sortCode,
            balance = request.initialDeposit ?: BigDecimal.ZERO,
            currency = request.currency ?: "GBP",
            accountName = request.accountName,
            overdraftLimit = request.overdraftLimit ?: BigDecimal.ZERO,
            interestRate = getInterestRateForAccountType(request.accountType)
        )
        
        val savedAccount = accountRepository.save(account)
        return mapToAccountResponse(savedAccount)
    }
    
    @Transactional(readOnly = true)
    fun getAccountById(accountId: UUID): AccountResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with id: $accountId") }
        return mapToAccountResponse(account)
    }
    
    @Transactional(readOnly = true)
    fun getAccountsByUserId(userId: UUID): List<AccountResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        return accountRepository.findByUserAndIsActiveTrue(user)
            .map { mapToAccountResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun getAccountByAccountNumber(accountNumber: String): AccountResponse {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw AccountNotFoundException("Account not found with account number: $accountNumber")
        return mapToAccountResponse(account)
    }
    
    @Transactional(readOnly = true)
    fun getTotalBalance(userId: UUID): BigDecimal {
        return accountRepository.getTotalBalanceByUserId(userId) ?: BigDecimal.ZERO
    }
    
    fun updateAccountBalance(accountId: UUID, amount: BigDecimal, operation: BalanceOperation): AccountResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with id: $accountId") }
        
        val newBalance = when (operation) {
            BalanceOperation.ADD -> account.balance.add(amount)
            BalanceOperation.SUBTRACT -> {
                val newBalance = account.balance.subtract(amount)
                if (newBalance < BigDecimal.ZERO && newBalance.abs() > account.overdraftLimit) {
                    throw InsufficientFundsException("Insufficient funds. Available balance: ${account.balance}, Overdraft limit: ${account.overdraftLimit}")
                }
                newBalance
            }
        }
        
        val updatedAccount = account.copy(balance = newBalance)
        val savedAccount = accountRepository.save(updatedAccount)
        return mapToAccountResponse(savedAccount)
    }
    
    fun updateAccount(accountId: UUID, request: UpdateAccountRequest): AccountResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with id: $accountId") }
        
        val updatedAccount = account.copy(
            accountName = request.accountName ?: account.accountName,
            overdraftLimit = request.overdraftLimit ?: account.overdraftLimit,
            isActive = request.isActive ?: account.isActive
        )
        
        val savedAccount = accountRepository.save(updatedAccount)
        return mapToAccountResponse(savedAccount)
    }
    
    fun closeAccount(accountId: UUID): AccountResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { AccountNotFoundException("Account not found with id: $accountId") }
        
        // Check if account has balance
        if (account.balance != BigDecimal.ZERO) {
            throw BusinessRuleException("Cannot close account with non-zero balance. Current balance: ${account.balance}")
        }
        
        val closedAccount = account.copy(isActive = false)
        val savedAccount = accountRepository.save(closedAccount)
        return mapToAccountResponse(savedAccount)
    }
    
    @Transactional(readOnly = true)
    fun getAccountsByType(userId: UUID, accountType: AccountType): List<AccountResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        return accountRepository.findByUserAndAccountType(user, accountType)
            .map { mapToAccountResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun getAccountSummary(userId: UUID): AccountSummaryResponse {
        val accounts = getAccountsByUserId(userId)
        val totalBalance = getTotalBalance(userId)
        
        val accountsByType = accounts.groupBy { it.accountType }
        
        return AccountSummaryResponse(
            totalBalance = totalBalance,
            totalAccounts = accounts.size,
            accountsByType = accountsByType.mapValues { it.value.size },
            accounts = accounts
        )
    }
    
    private fun generateAccountNumber(): String {
        var accountNumber: String
        do {
            accountNumber = (10000000..99999999).random().toString()
        } while (accountRepository.existsByAccountNumber(accountNumber))
        return accountNumber
    }
    
    private fun generateSortCode(): String {
        // Generate a sort code in format XX-XX-XX
        val part1 = (10..99).random()
        val part2 = (10..99).random()
        val part3 = (10..99).random()
        return "$part1-$part2-$part3"
    }
    
    private fun validateAccountCreation(userId: UUID, accountType: AccountType) {
        val existingAccounts = accountRepository.countAccountsByUserId(userId)
        
        // Business rules for account creation
        when (accountType) {
            AccountType.CURRENT -> {
                if (existingAccounts >= 3) {
                    throw BusinessRuleException("Maximum of 3 current accounts allowed per user")
                }
            }
            AccountType.SAVINGS -> {
                if (existingAccounts >= 5) {
                    throw BusinessRuleException("Maximum of 5 savings accounts allowed per user")
                }
            }
            AccountType.BUSINESS -> {
                val businessAccounts = accountRepository.findByUserIdAndAccountType(userId, AccountType.BUSINESS)
                if (businessAccounts.isNotEmpty()) {
                    throw BusinessRuleException("Only one business account allowed per user")
                }
            }
            else -> {
                // No specific limits for other account types
            }
        }
    }
    
    private fun getInterestRateForAccountType(accountType: AccountType): BigDecimal {
        return when (accountType) {
            AccountType.SAVINGS -> BigDecimal("0.0250") // 2.5%
            AccountType.CURRENT -> BigDecimal("0.0100") // 1.0%
            AccountType.BUSINESS -> BigDecimal("0.0150") // 1.5%
            AccountType.INVESTMENT -> BigDecimal("0.0300") // 3.0%
            else -> BigDecimal.ZERO
        }
    }
    
    private fun mapToAccountResponse(account: Account): AccountResponse {
        return AccountResponse(
            id = account.id,
            userId = account.user.id,
            accountType = account.accountType,
            accountNumber = account.accountNumber,
            sortCode = account.sortCode,
            balance = account.balance,
            currency = account.currency,
            isActive = account.isActive,
            accountName = account.accountName,
            overdraftLimit = account.overdraftLimit,
            interestRate = account.interestRate,
            createdAt = account.createdAt
        )
    }
}

enum class BalanceOperation {
    ADD, SUBTRACT
}