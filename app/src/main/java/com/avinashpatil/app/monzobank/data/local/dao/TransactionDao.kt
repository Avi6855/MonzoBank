package com.avinashpatil.app.monzobank.data.local.dao

import androidx.room.*
import com.avinashpatil.app.monzobank.data.local.entity.TransactionEntity
import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

@Dao
interface TransactionDao {
    
    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY transactionDate DESC")
    fun getTransactionsByAccountId(accountId: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             AND transactionDate BETWEEN :startDate AND :endDate 
             ORDER BY transactionDate DESC""")
    suspend fun getTransactionsByDateRange(
        accountId: String, 
        startDate: Date, 
        endDate: Date
    ): List<TransactionEntity>
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             AND category = :category 
             ORDER BY transactionDate DESC""")
    suspend fun getTransactionsByCategory(accountId: String, category: String): List<TransactionEntity>
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             AND status = :status 
             ORDER BY transactionDate DESC""")
    suspend fun getTransactionsByStatus(accountId: String, status: TransactionStatus): List<TransactionEntity>
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             AND (description LIKE '%' || :searchQuery || '%' 
                  OR merchantName LIKE '%' || :searchQuery || '%') 
             ORDER BY transactionDate DESC""")
    suspend fun searchTransactions(accountId: String, searchQuery: String): List<TransactionEntity>
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             AND transactionDate >= :date 
             ORDER BY transactionDate DESC 
             LIMIT :limit""")
    suspend fun getRecentTransactions(accountId: String, date: Date, limit: Int = 10): List<TransactionEntity>
    
    @Query("""SELECT SUM(amount) FROM transactions 
             WHERE accountId = :accountId 
             AND transactionType = :type 
             AND status = 'COMPLETED' 
             AND transactionDate BETWEEN :startDate AND :endDate""")
    suspend fun getTotalAmountByTypeAndDateRange(
        accountId: String,
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): BigDecimal?
    
    @Query("""SELECT category, SUM(amount) as total FROM transactions 
             WHERE accountId = :accountId 
             AND transactionType = 'DEBIT' 
             AND status = 'COMPLETED' 
             AND transactionDate BETWEEN :startDate AND :endDate 
             AND category IS NOT NULL 
             GROUP BY category 
             ORDER BY total DESC""")
    suspend fun getSpendingByCategory(
        accountId: String,
        startDate: Date,
        endDate: Date
    ): List<CategorySpending>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
    
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    @Query("UPDATE transactions SET status = :status WHERE id = :transactionId")
    suspend fun updateTransactionStatus(transactionId: String, status: TransactionStatus)
    
    @Query("UPDATE transactions SET category = :category WHERE id = :transactionId")
    suspend fun updateTransactionCategory(transactionId: String, category: String)
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteTransactionsByAccountId(accountId: String)
    
    @Query("SELECT COUNT(*) FROM transactions WHERE accountId = :accountId AND status = 'PENDING'")
    suspend fun getPendingTransactionCount(accountId: String): Int
    
    @Query("""SELECT * FROM transactions 
             WHERE (fromAccountId = :accountId OR toAccountId = :accountId) 
             AND transactionType IN ('TRANSFER_IN', 'TRANSFER_OUT') 
             ORDER BY transactionDate DESC""")
    suspend fun getTransferTransactions(accountId: String): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun observeTransaction(transactionId: String): Flow<TransactionEntity?>
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             AND status = 'PENDING' 
             ORDER BY transactionDate DESC""")
    fun getPendingTransactions(accountId: String): Flow<List<TransactionEntity>>
    
    @Query("""SELECT * FROM transactions 
             WHERE accountId = :accountId 
             ORDER BY transactionDate DESC 
             LIMIT :limit""")
    fun getRecentTransactions(accountId: String, limit: Int): Flow<List<TransactionEntity>>
}

data class CategorySpending(
    val category: String,
    val total: BigDecimal
)