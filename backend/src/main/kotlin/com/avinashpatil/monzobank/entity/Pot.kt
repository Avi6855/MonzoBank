package com.avinashpatil.monzobank.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "pots")
data class Pot(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @Column(nullable = false, length = 100)
    val name: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    
    @Column(name = "target_amount", precision = 15, scale = 2)
    val targetAmount: BigDecimal? = null,
    
    @Column(name = "current_amount", precision = 15, scale = 2)
    val currentAmount: BigDecimal = BigDecimal.ZERO,
    
    @Column(length = 7)
    val color: String = "#FF5733",
    
    @Column(length = 10)
    val emoji: String = "🎯",
    
    @Column(name = "round_up_enabled")
    val roundUpEnabled: Boolean = false,
    
    @Column(name = "target_date")
    val targetDate: LocalDate? = null,
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    @Column(name = "auto_deposit_enabled")
    val autoDepositEnabled: Boolean = false,
    
    @Column(name = "auto_deposit_amount", precision = 10, scale = 2)
    val autoDepositAmount: BigDecimal? = null,
    
    @Column(name = "auto_deposit_frequency")
    @Enumerated(EnumType.STRING)
    val autoDepositFrequency: DepositFrequency? = null,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class DepositFrequency {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
}