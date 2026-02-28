package com.avinashpatil.monzobank.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "cards")
data class Card(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    val account: Account,
    
    @JsonIgnore
    @Column(name = "card_number", unique = true, nullable = false)
    val cardNumber: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    val cardType: CardType,
    
    @Column(name = "expiry_date", nullable = false)
    val expiryDate: LocalDate,
    
    @JsonIgnore
    @Column(name = "cvv_hash", nullable = false)
    val cvvHash: String,
    
    @Column(name = "cardholder_name", nullable = false)
    val cardholderName: String,
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    @Column(name = "is_frozen")
    val isFrozen: Boolean = false,
    
    @Column(name = "contactless_enabled")
    val contactlessEnabled: Boolean = true,
    
    @Column(name = "online_payments_enabled")
    val onlinePaymentsEnabled: Boolean = true,
    
    @Column(name = "international_enabled")
    val internationalEnabled: Boolean = false,
    
    @Column(name = "daily_limit", precision = 10, scale = 2)
    val dailyLimit: BigDecimal = BigDecimal("500.00"),
    
    @Column(name = "monthly_limit", precision = 10, scale = 2)
    val monthlyLimit: BigDecimal = BigDecimal("2000.00"),
    
    @Column(name = "atm_limit", precision = 10, scale = 2)
    val atmLimit: BigDecimal = BigDecimal("300.00"),
    
    @Column(name = "contactless_limit", precision = 10, scale = 2)
    val contactlessLimit: BigDecimal = BigDecimal("100.00"),
    
    @Column(name = "pin_attempts")
    val pinAttempts: Int = 0,
    
    @Column(name = "is_pin_locked")
    val isPinLocked: Boolean = false,
    
    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    val deliveryStatus: DeliveryStatus = DeliveryStatus.ORDERED,
    
    @Column(name = "delivery_address", columnDefinition = "JSON")
    val deliveryAddress: String? = null,
    
    @Column(name = "activation_date")
    val activationDate: LocalDateTime? = null,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class CardType {
    DEBIT, CREDIT, VIRTUAL, PREPAID
}

enum class DeliveryStatus {
    ORDERED, DISPATCHED, DELIVERED, ACTIVATED, CANCELLED
}