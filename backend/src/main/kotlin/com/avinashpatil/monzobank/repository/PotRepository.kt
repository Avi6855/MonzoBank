package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.DepositFrequency
import com.avinashpatil.monzobank.entity.Pot
import com.avinashpatil.monzobank.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface PotRepository : JpaRepository<Pot, UUID> {
    
    // Find pots by user
    fun findByUser(user: User): List<Pot>
    fun findByUserId(userId: UUID): List<Pot>
    
    // Find active pots
    fun findByIsActiveTrue(): List<Pot>
    fun findByUserAndIsActiveTrue(user: User): List<Pot>
    
    // Find pots by name
    fun findByUserAndName(user: User, name: String): Optional<Pot>
    fun findByUserIdAndName(userId: UUID, name: String): Optional<Pot>
    
    // Find pots by target amount
    fun findByTargetAmountGreaterThan(targetAmount: BigDecimal): List<Pot>
    fun findByTargetAmountLessThan(targetAmount: BigDecimal): List<Pot>
    fun findByTargetAmountBetween(minAmount: BigDecimal, maxAmount: BigDecimal): List<Pot>
    
    // Find pots by current balance
    fun findByCurrentBalanceGreaterThan(currentBalance: BigDecimal): List<Pot>
    fun findByCurrentBalanceLessThan(currentBalance: BigDecimal): List<Pot>
    fun findByCurrentBalanceBetween(minBalance: BigDecimal, maxBalance: BigDecimal): List<Pot>
    
    // Find pots by deposit frequency
    fun findByDepositFrequency(depositFrequency: DepositFrequency): List<Pot>
    fun findByUserAndDepositFrequency(user: User, depositFrequency: DepositFrequency): List<Pot>
    
    // Find pots by auto deposit status
    fun findByAutoDepositEnabled(autoDepositEnabled: Boolean): List<Pot>
    fun findByUserAndAutoDepositEnabled(user: User, autoDepositEnabled: Boolean): List<Pot>
    
    // Find pots by target date
    fun findByTargetDateBefore(targetDate: LocalDateTime): List<Pot>
    fun findByTargetDateAfter(targetDate: LocalDateTime): List<Pot>
    fun findByTargetDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Pot>
    
    // Custom queries
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId AND p.isActive = true ORDER BY p.createdAt DESC")
    fun findActiveUserPotsOrderByCreatedAtDesc(@Param("userId") userId: UUID): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.currentBalance >= p.targetAmount AND p.isActive = true")
    fun findCompletedPots(): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId AND p.currentBalance >= p.targetAmount")
    fun findCompletedPotsByUserId(@Param("userId") userId: UUID): List<Pot>
    
    @Query("SELECT SUM(p.currentBalance) FROM Pot p WHERE p.user.id = :userId AND p.isActive = true")
    fun getTotalSavingsByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT SUM(p.targetAmount) FROM Pot p WHERE p.user.id = :userId AND p.isActive = true")
    fun getTotalTargetAmountByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT COUNT(p) FROM Pot p WHERE p.user.id = :userId AND p.isActive = true")
    fun countActivePotsByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT p FROM Pot p WHERE p.targetDate <= :date AND p.currentBalance < p.targetAmount AND p.isActive = true")
    fun findOverduePots(@Param("date") date: LocalDateTime): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.autoDepositEnabled = true AND p.nextDepositDate <= :date AND p.isActive = true")
    fun findPotsForAutoDeposit(@Param("date") date: LocalDateTime): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId AND p.currentBalance > :minBalance AND p.isActive = true")
    fun findPotsByUserWithMinimumBalance(@Param("userId") userId: UUID, @Param("minBalance") minBalance: BigDecimal): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.targetDate BETWEEN :startDate AND :endDate AND p.isActive = true")
    fun findPotsByTargetDateRange(@Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId AND p.name LIKE %:searchTerm% AND p.isActive = true")
    fun searchPotsByName(@Param("userId") userId: UUID, @Param("searchTerm") searchTerm: String): List<Pot>
    
    @Query("SELECT AVG(p.currentBalance) FROM Pot p WHERE p.user.id = :userId AND p.isActive = true")
    fun getAverageBalanceByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId ORDER BY p.currentBalance DESC")
    fun findPotsByUserOrderByBalanceDesc(@Param("userId") userId: UUID): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId ORDER BY p.targetAmount DESC")
    fun findPotsByUserOrderByTargetAmountDesc(@Param("userId") userId: UUID): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.user.id = :userId AND (p.currentBalance / p.targetAmount) >= :percentage")
    fun findPotsByCompletionPercentage(@Param("userId") userId: UUID, @Param("percentage") percentage: BigDecimal): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.createdAt >= :date AND p.isActive = true")
    fun findRecentlyCreatedPots(@Param("date") date: LocalDateTime): List<Pot>
    
    @Query("SELECT p FROM Pot p WHERE p.updatedAt >= :date AND p.isActive = true")
    fun findRecentlyUpdatedPots(@Param("date") date: LocalDateTime): List<Pot>
    
    @Query("SELECT COUNT(p) FROM Pot p WHERE p.isActive = true")
    fun countActivePots(): Long
    
    @Query("SELECT COUNT(p) FROM Pot p WHERE p.autoDepositEnabled = true AND p.isActive = true")
    fun countAutoDepositEnabledPots(): Long
    
    @Query("SELECT COUNT(p) FROM Pot p WHERE p.currentBalance >= p.targetAmount")
    fun countCompletedPots(): Long
}