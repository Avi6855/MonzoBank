package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.Account
import com.avinashpatil.monzobank.entity.Transaction
import com.avinashpatil.monzobank.entity.TransactionStatus
import com.avinashpatil.monzobank.entity.TransactionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface TransactionRepository : JpaRepository<Transaction, UUID> {
    
    fun findByAccount(account: Account, pageable: Pageable): Page<Transaction>
    
    fun findByAccountOrderByTransactionDateDesc(account: Account): List<Transaction>
    
    fun findByAccountAndStatus(account: Account, status: TransactionStatus): List<Transaction>
    
    fun findByAccountAndTransactionType(account: Account, transactionType: TransactionType): List<Transaction>
    
    fun findByStatus(status: TransactionStatus): List<Transaction>
    
    fun findByReferenceNumber(referenceNumber: String): Transaction?
    
    fun findByExternalTransactionId(externalTransactionId: String): Transaction?
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.transactionDate DESC")
    fun findByUserIdOrderByDateDesc(@Param("userId") userId: UUID, pageable: Pageable): Page<Transaction>
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    fun findByAccountAndDateRange(
        @Param("accountId") accountId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Transaction>
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.category = :category ORDER BY t.transactionDate DESC")
    fun findByAccountAndCategory(
        @Param("accountId") accountId: UUID,
        @Param("category") category: String
    ): List<Transaction>
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.amount >= :minAmount AND t.amount <= :maxAmount")
    fun findByAccountAndAmountRange(
        @Param("accountId") accountId: UUID,
        @Param("minAmount") minAmount: BigDecimal,
        @Param("maxAmount") maxAmount: BigDecimal
    ): List<Transaction>
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.id = :accountId AND t.transactionType = :transactionType AND t.status = 'COMPLETED'")
    fun getTotalAmountByAccountAndType(
        @Param("accountId") accountId: UUID,
        @Param("transactionType") transactionType: TransactionType
    ): BigDecimal?
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.user.id = :userId AND t.transactionDate >= :startDate")
    fun countTransactionsByUserFromDate(
        @Param("userId") userId: UUID,
        @Param("startDate") startDate: LocalDateTime
    ): Long
    
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.transactionType = 'DEBIT' GROUP BY t.category")
    fun getSpendingByCategory(
        @Param("userId") userId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Array<Any>>
    
    @Query("SELECT t FROM Transaction t WHERE t.merchantName LIKE %:merchantName% ORDER BY t.transactionDate DESC")
    fun findByMerchantNameContaining(@Param("merchantName") merchantName: String): List<Transaction>
    
    @Query("SELECT t FROM Transaction t WHERE t.amount > :threshold AND t.status = 'COMPLETED' AND t.transactionDate >= :recentDate")
    fun findLargeRecentTransactions(
        @Param("threshold") threshold: BigDecimal,
        @Param("recentDate") recentDate: LocalDateTime
    ): List<Transaction>
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) AND t.transactionType = 'TRANSFER'