package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.Account
import com.avinashpatil.monzobank.entity.AccountType
import com.avinashpatil.monzobank.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
interface AccountRepository : JpaRepository<Account, UUID> {
    
    fun findByUser(user: User): List<Account>
    
    fun findByUserAndIsActiveTrue(user: User): List<Account>
    
    fun findByAccountNumber(accountNumber: String): Account?
    
    fun findByAccountNumberAndSortCode(accountNumber: String, sortCode: String): Account?
    
    fun findByUserAndAccountType(user: User, accountType: AccountType): List<Account>
    
    fun findByAccountType(accountType: AccountType): List<Account>
    
    fun existsByAccountNumber(accountNumber: String): Boolean
    
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    fun findActiveAccountsByUserId(@Param("userId") userId: UUID): List<Account>
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    fun getTotalBalanceByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance AND a.isActive = true")
    fun findAccountsWithMinBalance(@Param("minBalance") minBalance: BigDecimal): List<Account>
    
    @Query("SELECT a FROM Account a WHERE a.balance < :maxBalance AND a.isActive = true")
    fun findAccountsWithMaxBalance(@Param("maxBalance") maxBalance: BigDecimal): List<Account>
    
    @Query("SELECT a FROM Account a WHERE a.currency = :currency")
    fun findAccountsByCurrency(@Param("currency") currency: String): List<Account>
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    fun countAccountsByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT a FROM Account a WHERE a.overdraftLimit > 0 AND a.balance < 0")
    fun findOverdrawnAccounts(): List<Account>
    
    @Query("SELECT AVG(a.balance) FROM Account a WHERE a.accountType = :accountType AND a.isActive = true")
    fun getAverageBalanceByAccountType(@Param("accountType") accountType: AccountType): BigDecimal?
}