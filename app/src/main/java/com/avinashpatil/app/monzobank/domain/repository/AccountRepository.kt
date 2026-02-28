package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.model.AccountInsight
import com.avinashpatil.app.monzobank.domain.model.AccountSummary
import kotlinx.coroutines.flow.Flow
import java.util.*

interface AccountRepository {
    
    // Account CRUD Operations
    suspend fun createAccount(
        userId: String,
        name: String,
        type: AccountType,
        initialDeposit: Double = 0.0
    ): Result<Account>
    
    suspend fun createAccount(account: Account): Result<Account>
    
    suspend fun getAccount(accountId: String): Result<Account?>
    
    suspend fun getAccountById(accountId: String): Result<Account?>
    
    suspend fun getAccountByNumber(accountNumber: String): Result<Account?>
    
    suspend fun getAccountsByUserId(userId: String): Result<List<Account>>
    
    suspend fun updateAccount(account: Account): Result<Account>
    
    suspend fun deleteAccount(accountId: String): Result<Unit>
    
    suspend fun freezeAccount(accountId: String): Result<Unit>
    
    suspend fun unfreezeAccount(accountId: String): Result<Unit>
    
    suspend fun closeAccount(accountId: String): Result<Unit>
    
    // Account Balance Operations
    suspend fun updateBalance(
        accountId: String,
        newBalance: Double,
        transactionId: String
    ): Result<Unit>
    
    suspend fun getAccountBalance(accountId: String): Result<Double>
    
    suspend fun getAvailableBalance(accountId: String): Result<Double>
    
    suspend fun getTotalBalance(userId: String): Result<Double>
    
    suspend fun getTotalBalanceByCurrency(userId: String, currency: String): Result<Double>
    
    suspend fun getAccountsWithBalance(userId: String): Result<List<Account>>
    
    suspend fun getPrimaryAccount(userId: String): Result<Account?>
    
    // Account Settings
    suspend fun updateOverdraftLimit(
        accountId: String,
        newLimit: Double
    ): Result<Unit>
    
    suspend fun updateInterestRate(
        accountId: String,
        newRate: Double
    ): Result<Unit>
    
    suspend fun setDefaultAccount(
        userId: String,
        accountId: String
    ): Result<Unit>
    
    suspend fun getDefaultAccount(userId: String): Result<Account?>
    
    // Account Analytics
    suspend fun getAccountSummary(userId: String): Result<AccountSummary>
    
    suspend fun getAccountInsights(
        userId: String,
        accountId: String? = null
    ): Result<List<AccountInsight>>
    
    suspend fun getSpendingAnalytics(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<Map<String, Double>>
    
    suspend fun getIncomeAnalytics(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): Result<Map<String, Double>>
    
    // Account Statements
    suspend fun generateStatement(
        accountId: String,
        startDate: Date,
        endDate: Date,
        format: StatementFormat = StatementFormat.PDF
    ): Result<String> // Returns file path or URL
    
    suspend fun getStatementHistory(
        accountId: String
    ): Result<List<AccountStatement>>
    
    // Account Validation
    suspend fun validateAccountNumber(accountNumber: String): Result<Boolean>
    
    suspend fun validateSortCode(sortCode: String): Result<Boolean>
    
    suspend fun validateIBAN(iban: String): Result<Boolean>
    
    // Real-time Updates
    fun observeAccount(accountId: String): Flow<Account?>
    
    fun observeAccountsByUserId(userId: String): Flow<List<Account>>
    
    fun observeAccountBalance(accountId: String): Flow<Double>
    
    fun observeTotalBalance(userId: String): Flow<Double>
    
    // Account Search and Filtering
    suspend fun searchAccounts(
        userId: String,
        query: String,
        filters: AccountFilters? = null
    ): Result<List<Account>>
    
    suspend fun getAccountsByType(
        userId: String,
        type: AccountType
    ): Result<List<Account>>
    
    suspend fun getAccountsByStatus(
        userId: String,
        status: AccountStatus
    ): Result<List<Account>>
    
    suspend fun updateAccountStatus(accountId: String, status: AccountStatus): Result<Unit>
    
    // Account Linking
    suspend fun linkExternalAccount(
        userId: String,
        externalAccountInfo: ExternalAccountInfo
    ): Result<Account>
    
    suspend fun unlinkExternalAccount(accountId: String): Result<Unit>
    
    suspend fun syncExternalAccount(accountId: String): Result<Account>
    
    // Joint Account Operations
    suspend fun createJointAccount(
        primaryUserId: String,
        secondaryUserId: String,
        name: String,
        permissions: JointAccountPermissions
    ): Result<Account>
    
    suspend fun addJointAccountHolder(
        accountId: String,
        userId: String,
        permissions: JointAccountPermissions
    ): Result<Unit>
    
    suspend fun removeJointAccountHolder(
        accountId: String,
        userId: String
    ): Result<Unit>
    
    suspend fun updateJointAccountPermissions(
        accountId: String,
        userId: String,
        permissions: JointAccountPermissions
    ): Result<Unit>
    
    // Account Notifications
    suspend fun enableBalanceAlerts(
        accountId: String,
        threshold: Double
    ): Result<Unit>
    
    suspend fun disableBalanceAlerts(accountId: String): Result<Unit>
    
    suspend fun enableOverdraftAlerts(accountId: String): Result<Unit>
    
    suspend fun disableOverdraftAlerts(accountId: String): Result<Unit>
    
    // Account Backup and Recovery
    suspend fun backupAccountData(accountId: String): Result<String>
    
    suspend fun restoreAccountData(
        accountId: String,
        backupData: String
    ): Result<Unit>
}

data class AccountFilters(
    val types: List<AccountType>? = null,
    val statuses: List<AccountStatus>? = null,
    val minBalance: Double? = null,
    val maxBalance: Double? = null,
    val createdAfter: Date? = null,
    val createdBefore: Date? = null,
    val hasOverdraft: Boolean? = null,
    val isDefault: Boolean? = null
)

data class ExternalAccountInfo(
    val bankName: String,
    val accountNumber: String,
    val sortCode: String,
    val accountHolderName: String,
    val accountType: AccountType,
    val credentials: Map<String, String> = emptyMap()
)

data class JointAccountPermissions(
    val canView: Boolean = true,
    val canTransfer: Boolean = false,
    val canPayBills: Boolean = false,
    val canManageCards: Boolean = false,
    val canCloseAccount: Boolean = false,
    val dailyTransferLimit: Double = 0.0,
    val monthlyTransferLimit: Double = 0.0,
    val requiresApproval: Boolean = true,
    val approvalThreshold: Double = 1000.0
)

data class AccountStatement(
    val id: String,
    val accountId: String,
    val startDate: Date,
    val endDate: Date,
    val format: StatementFormat,
    val fileUrl: String,
    val fileSize: Long,
    val generatedAt: Date,
    val downloadCount: Int = 0,
    val lastDownloadedAt: Date? = null
)

enum class StatementFormat {
    PDF,
    CSV,
    EXCEL,
    JSON
}