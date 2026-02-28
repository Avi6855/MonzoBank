package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.User
import com.avinashpatil.monzobank.entity.KycStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    
    fun findByEmail(email: String): User?
    
    fun findByPhone(phone: String): User?
    
    fun existsByEmail(email: String): Boolean
    
    fun existsByPhone(phone: String): Boolean
    
    fun findByEmailOrPhone(email: String, phone: String): User?
    
    fun findByKycStatus(kycStatus: KycStatus): List<User>
    
    fun findByIsActiveTrue(): List<User>
    
    fun findByEmailVerifiedFalse(): List<User>
    
    fun findByPhoneVerifiedFalse(): List<User>
    
    @Query("SELECT u FROM User u WHERE u.biometricEnabled = true AND u.isActive = true")
    fun findActiveBiometricUsers(): List<User>
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    fun countNewUsersFromDate(@Param("startDate") startDate: java.time.LocalDateTime): Long
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    fun findByNameContaining(@Param("name") name: String): List<User>
    
    @Query("SELECT u FROM User u JOIN u.accounts a WHERE a.balance > :minBalance")
    fun findUsersWithMinBalance(@Param("minBalance") minBalance: java.math.BigDecimal): List<User>
}