package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.data.local.dao.AccountDao
import com.avinashpatil.app.monzobank.data.local.entity.AccountEntity
import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.model.AccountInsight
import com.avinashpatil.app.monzobank.domain.model.AccountSummary
import com.avinashpatil.app.monzobank.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override suspend fun getAccountsByUserId(userId: String): Result<List<Account>> {
        return try {
            val accounts = accountDao.getAccountsByUserId(userId)
            Result.success(accounts.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountById(accountId: String): Result<Account?> {
        return try {
            val account = accountDao.getAccountById(accountId)
            Result.success(account?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountByNumber(accountNumber: String): Result<Account?> {
        return try {
            val account = accountDao.getAccountByNumber(accountNumber)
            Result.success(account?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountsByType(userId: String, type: AccountType): Result<List<Account>> {
        return try {
            val accounts = accountDao.getAccountsByType(userId, type)
            Result.success(accounts.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalBalanceByCurrency(userId: String, currency: String): Result<Double> {
        return try {
            val balance = accountDao.getTotalBalanceByCurrency(userId, currency)?.toDouble() ?: 0.0
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountsWithBalance(userId: String): Result<List<Account>> {
        return try {
            val accounts = accountDao.getAccountsWithBalance(userId)
            Result.success(accounts.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPrimaryAccount(userId: String): Result<Account?> {
        return try {
            val account = accountDao.getPrimaryAccount(userId)
            Result.success(account?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAccount(account: Account): Result<Account> {
        return try {
            val accountEntity = account.toEntityModel()
            accountDao.insertAccount(accountEntity)
            val createdAccount = accountDao.getAccountById(account.id)
            Result.success(createdAccount!!.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAccount(account: Account): Result<Account> {
        return try {
            accountDao.updateAccount(account.toEntityModel())
            val updatedAccount = accountDao.getAccountById(account.id)
            Result.success(updatedAccount!!.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(accountId: String): Result<Unit> {
        return try {
            val account = accountDao.getAccountById(accountId)
            if (account != null) {
                accountDao.deleteAccount(account)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountsByStatus(userId: String, status: AccountStatus): Result<List<Account>> {
        return try {
            val accounts = accountDao.getAccountsByStatus(userId, status)
            Result.success(accounts.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAccountStatus(accountId: String, status: AccountStatus): Result<Unit> {
        return try {
            accountDao.updateAccountStatus(accountId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Missing interface methods - simplified implementations
    override suspend fun createAccount(
        userId: String,
        name: String,
        type: AccountType,
        initialDeposit: Double
    ): Result<Account> {
        return try {
            val account = Account(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                type = type,
                balance = BigDecimal.valueOf(initialDeposit),
                availableBalance = BigDecimal.valueOf(initialDeposit),
                currency = "GBP",
                accountNumber = generateAccountNumber(),
                sortCode = "12-34-56",
                iban = "GB29 NWBK 6016 1331 9268 19",
                status = AccountStatus.ACTIVE,
                overdraftLimit = BigDecimal.ZERO,
                interestRate = BigDecimal.ZERO,
                createdAt = Date(),
                updatedAt = Date(),
                isDefault = false,
                metadata = emptyMap()
            )
            accountDao.insertAccount(account.toEntityModel())
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccount(accountId: String): Result<Account?> {
        return getAccountById(accountId)
    }

    override suspend fun freezeAccount(accountId: String): Result<Unit> {
        return updateAccountStatus(accountId, AccountStatus.SUSPENDED)
    }

    override suspend fun unfreezeAccount(accountId: String): Result<Unit> {
        return updateAccountStatus(accountId, AccountStatus.ACTIVE)
    }

    override suspend fun closeAccount(accountId: String): Result<Unit> {
        return updateAccountStatus(accountId, AccountStatus.CLOSED)
    }

    override suspend fun updateBalance(
        accountId: String,
        newBalance: Double,
        transactionId: String
    ): Result<Unit> {
        return try {
            accountDao.updateBalance(accountId, BigDecimal.valueOf(newBalance), Date())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountBalance(accountId: String): Result<Double> {
        return try {
            val account = accountDao.getAccountById(accountId)
            val balance = account?.balance?.toDouble() ?: 0.0
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableBalance(accountId: String): Result<Double> {
        return try {
            val account = accountDao.getAccountById(accountId)
            val availableBalance = account?.let {
                (it.balance + it.overdraftLimit).toDouble()
            } ?: 0.0
            Result.success(availableBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalBalance(userId: String): Result<Double> {
        return getTotalBalanceByCurrency(userId, "GBP")
    }

    override suspend fun updateOverdraftLimit(accountId: String, newLimit: Double): Result<Unit> {
        return try {
            // TODO: Implement overdraft limit update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateInterestRate(accountId: String, newRate: Double): Result<Unit> {
        return try {
            // TODO: Implement interest rate update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setDefaultAccount(userId: String, accountId: String): Result<Unit> {
        return try {
            // TODO: Implement default account setting
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDefaultAccount(userId: String): Result<Account?> {
        return getPrimaryAccount(userId)
    }

    // Real-time Updates
    override fun observeAccount(accountId: String): Flow<Account?> {
        return accountDao.observeAccountById(accountId).map { it?.toDomainModel() }
    }

    override fun observeAccountsByUserId(userId: String): Flow<List<Account>> {
        return accountDao.observeAccountsByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeAccountBalance(accountId: String): Flow<Double> {
        return accountDao.observeAccountById(accountId).map { it?.balance?.toDouble() ?: 0.0 }
    }

    override fun observeTotalBalance(userId: String): Flow<Double> {
        return accountDao.observeAccountsByUserId(userId).map { accounts ->
            accounts.sumOf { it.balance.toDouble() }
        }
    }

    // Account Analytics - Stub implementations
    override suspend fun getAccountSummary(userId: String): Result<AccountSummary> {
        return try {
            // TODO: Implement proper account summary
            Result.success(AccountSummary(
                totalBalance = BigDecimal.ZERO,
                totalAvailableBalance = BigDecimal.ZERO,
                accountCount = 0,
                activeAccountCount = 0,
                totalOverdraftUsed = BigDecimal.ZERO,
                totalOverdraftLimit = BigDecimal.ZERO,
                monthlyIncome = BigDecimal.ZERO,
                monthlyExpenses = BigDecimal.ZERO,
                savingsRate = BigDecimal.ZERO
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccountInsights(userId: String, accountId: String?): Result<List<AccountInsight>> {
        return try {
            // TODO: Implement account insights
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSpendingAnalytics(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<Map<String, Double>> {
        return try {
            // TODO: Implement spending analytics
            Result.success(emptyMap())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIncomeAnalytics(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<Map<String, Double>> {
        return try {
            // TODO: Implement income analytics
            Result.success(emptyMap())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Account Statements - Stub implementations
    override suspend fun generateStatement(
        accountId: String,
        startDate: Date,
        endDate: Date,
        format: StatementFormat
    ): Result<String> {
        return try {
            val fileName = "statement_${accountId}_${System.currentTimeMillis()}.${format.name.lowercase()}"
            Result.success(fileName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStatementHistory(accountId: String): Result<List<AccountStatement>> {
        return try {
            // TODO: Implement statement history
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Account Validation
    override suspend fun validateAccountNumber(accountNumber: String): Result<Boolean> {
        return try {
            val isValid = accountNumber.length == 8 && accountNumber.all { it.isDigit() }
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateSortCode(sortCode: String): Result<Boolean> {
        return try {
            val isValid = sortCode.matches(Regex("\\d{2}-\\d{2}-\\d{2}"))
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateIBAN(iban: String): Result<Boolean> {
        return try {
            val isValid = iban.length >= 15 && iban.length <= 34
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Account Search and Filtering
    override suspend fun searchAccounts(
        userId: String,
        query: String,
        filters: AccountFilters?
    ): Result<List<Account>> {
        return try {
            // TODO: Implement account search
            val accounts = accountDao.getAccountsByUserId(userId)
            Result.success(accounts.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Stub implementations for remaining methods
    override suspend fun linkExternalAccount(
        userId: String,
        externalAccountInfo: ExternalAccountInfo
    ): Result<Account> {
        return try {
            // TODO: Implement external account linking
            Result.success(Account(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "External Account",
                type = AccountType.CURRENT,
                balance = BigDecimal.ZERO,
                availableBalance = BigDecimal.ZERO,
                currency = "GBP",
                accountNumber = "00000000",
                sortCode = "00-00-00",
                iban = "GB29 NWBK 6016 1331 9268 19",
                status = AccountStatus.ACTIVE,
                overdraftLimit = BigDecimal.ZERO,
                interestRate = BigDecimal.ZERO,
                createdAt = Date(),
                updatedAt = Date(),
                isDefault = false,
                metadata = emptyMap()
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlinkExternalAccount(accountId: String): Result<Unit> {
        return try {
            // TODO: Implement external account unlinking
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncExternalAccount(accountId: String): Result<Account> {
        return try {
            // TODO: Implement external account sync
            val account = accountDao.getAccountById(accountId)?.toDomainModel()
            Result.success(account!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Joint Account Operations - Stub implementations
    override suspend fun createJointAccount(
        primaryUserId: String,
        secondaryUserId: String,
        name: String,
        permissions: JointAccountPermissions
    ): Result<Account> {
        return try {
            // TODO: Implement joint account creation
            Result.success(Account(
                id = UUID.randomUUID().toString(),
                userId = primaryUserId,
                name = name,
                type = AccountType.JOINT,
                balance = BigDecimal.ZERO,
                availableBalance = BigDecimal.ZERO,
                currency = "GBP",
                accountNumber = generateAccountNumber(),
                sortCode = "12-34-56",
                iban = "GB29 NWBK 6016 1331 9268 19",
                status = AccountStatus.ACTIVE,
                overdraftLimit = BigDecimal.ZERO,
                interestRate = BigDecimal.ZERO,
                createdAt = Date(),
                updatedAt = Date(),
                isDefault = false,
                metadata = emptyMap()
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addJointAccountHolder(
        accountId: String,
        userId: String,
        permissions: JointAccountPermissions
    ): Result<Unit> {
        return try {
            // TODO: Implement joint account holder addition
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeJointAccountHolder(
        accountId: String,
        userId: String
    ): Result<Unit> {
        return try {
            // TODO: Implement joint account holder removal
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateJointAccountPermissions(
        accountId: String,
        userId: String,
        permissions: JointAccountPermissions
    ): Result<Unit> {
        return try {
            // TODO: Implement joint account permissions update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Account Notifications - Stub implementations
    override suspend fun enableBalanceAlerts(accountId: String, threshold: Double): Result<Unit> {
        return try {
            // TODO: Implement balance alerts
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disableBalanceAlerts(accountId: String): Result<Unit> {
        return try {
            // TODO: Implement balance alerts disabling
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enableOverdraftAlerts(accountId: String): Result<Unit> {
        return try {
            // TODO: Implement overdraft alerts
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disableOverdraftAlerts(accountId: String): Result<Unit> {
        return try {
            // TODO: Implement overdraft alerts disabling
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Account Backup and Recovery - Stub implementations
    override suspend fun backupAccountData(accountId: String): Result<String> {
        return try {
            // TODO: Implement account data backup
            Result.success("backup_${accountId}_${System.currentTimeMillis()}.json")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreAccountData(
        accountId: String,
        backupData: String
    ): Result<Unit> {
        return try {
            // TODO: Implement account data restoration
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper methods
    private fun generateAccountNumber(): String {
        return (10000000..99999999).random().toString()
    }

    // Extension functions for conversion
    private fun AccountEntity.toDomainModel(): Account {
        return Account(
            id = this.id,
            userId = this.userId,
            name = this.accountName ?: "Account",
            type = this.accountType,
            balance = this.balance,
            availableBalance = this.balance,
            currency = this.currency,
            accountNumber = this.accountNumber,
            sortCode = this.sortCode,
            iban = "GB29 NWBK 6016 1331 9268 19",
            status = this.status,
            overdraftLimit = this.overdraftLimit,
            minimumBalance = if (this.minimumBalance == BigDecimal.ZERO) null else this.minimumBalance,
            interestRate = this.interestRate,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            isDefault = false,
            metadata = emptyMap()
        )
    }

    private fun Account.toEntityModel(): AccountEntity {
        return AccountEntity(
            id = this.id.ifEmpty { UUID.randomUUID().toString() },
            userId = this.userId,
            accountType = this.type,
            accountNumber = this.accountNumber,
            sortCode = this.sortCode,
            balance = this.balance,
            currency = this.currency,
            status = this.status,
            isActive = this.status == AccountStatus.ACTIVE,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            accountName = this.name,
            interestRate = this.interestRate,
            overdraftLimit = this.overdraftLimit,
            minimumBalance = this.minimumBalance ?: BigDecimal.ZERO
        )
    }
    
}