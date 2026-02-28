package com.avinashpatil.monzobank.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "accounts")
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    val accountType: AccountType,
    
    @Column(name = "account_number", unique = true, nullable = false)
    val accountNumber: String,
    
    @Column(name = "sort_code", nullable = false)
    val sortCode: String,
    
    @Column(precision = 15, scale = 2)
    val balance: BigDecimal = BigDecimal.ZERO,
    
    @Column(length = 3)
    val currency: String = "GBP",
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    @Column(name = "account_name")
    val accountName: String? = null,
    
    @Column(name = "overdraft_limit", precision = 10, scale = 2)
    val overdraftLimit: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "interest_rate", precision = 5, scale = 4)
    val interestRate: BigDecimal = BigDecimal.ZERO,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactions: List<Transaction> = emptyList(),
    
    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val cards: List<Card> = emptyList()
)

enum class AccountType {
    CURRENT, SAVINGS, BUSINESS, JOINT, INVESTMENT,