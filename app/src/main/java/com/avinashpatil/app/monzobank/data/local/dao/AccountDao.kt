package com.avinashpatil.app.monzobank.data.local.dao

import androidx.room.*
import com.avinashpatil.app.monzobank.data.local.entity.AccountEntity
import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

@Dao
interface AccountDao {
    
    @Query("SELECT * FROM accounts WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getAccountsByUserId(userId: String): List<AccountEntity>
    
    @Query("SELECT * FROM accounts WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun observeAccountsByUserId(userId: String): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun observeAccountById(accountId: String): Flow<AccountEntity?>
    
    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: String): AccountEntity?
    
    @Query("SELECT * FROM accounts WHERE accountNumber = :accountNumber")
    suspend fun getAccountByNumber(accountNumber: String): AccountEntity?
    
    @Query("SELECT * FROM accounts WHERE userId = :userId AND accountType = :accountType AND isActive = 1")
    suspend fun getAccountsByType(userId: String, accountType: AccountType): List<AccountEntity>
    
    @Query("SELECT SUM(balance) FROM accounts WHERE userId = :userId AND isActive = 1 AND currency = :currency")
    suspend fun getTotalBalanceByCurrency(userId: String, currency: String): BigDecimal?
    
    @Query("SELECT * FROM accounts WHERE userId = :userId AND isActive = 1 AND balance > 0 ORDER BY balance DESC")
    suspend fun getAccountsWithBalance(userId: String): List<AccountEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)
    
    @Update
    suspend fun updateAccount(account: AccountEntity)
    
    @Query("UPDATE accounts SET balance = :newBalance, updatedAt = :updatedAt WHERE id = :accountId")
    suspend fun updateBalance(accountId: String, newBalance: BigDecimal, updatedAt: Date = Date())
    
    @Query("UPDATE accounts SET isActive = 0 WHERE id = :accountId")
    suspend fun deactivateAccount(accountId: String)
    
    @Delete
    suspend fun deleteAccount(account: AccountEntity)
    
    @Query("DELETE FROM accounts WHERE userId = :userId")
    suspend fun deleteAccountsByUserId(userId: String)
    
    @Query("SELECT COUNT(*) FROM accounts WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveAccountCount(userId: String): Int
    
    @Query("SELECT * FROM accounts WHERE userId = :userId AND accountType = 'CURRENT' AND isActive = 1 LIMIT 1")
    suspend fun getPrimaryAccount(userId: String): AccountEntity?
    
    @Query("""SELECT a.* FROM accounts a 
             WHERE a.userId = :userId 
             AND a.isActive = 1 
             AND a.currency = :currency 
             ORDER BY 
                CASE WHEN a.accountType = 'CURRENT' THEN 1 
                     WHEN a.accountType = 'SAVINGS' THEN 2 
                     WHEN a.accountType = 'BUSINESS' THEN 3 
                     ELSE 4 END, 
                a.balance DESC""")
    suspend fun getAccountsByCurrencyPriority(userId: String, currency: String): List<AccountEntity>
    
    @Query("SELECT * FROM accounts WHERE userId = :userId AND status = :status")
    suspend fun getAccountsByStatus(userId: String, status: AccountStatus): List<AccountEntity>
    
    @Query("UPDATE accounts SET status = :status WHERE id = :accountId")
    suspend fun updateAccountStatus(accountId: String, status: AccountStatus)
}