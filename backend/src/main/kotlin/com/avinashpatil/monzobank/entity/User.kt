package com.avinashpatil.monzobank.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(unique = true, nullable = false)
    val phone: String,
    
    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,
    
    @Column(name = "first_name", nullable = false)
    val firstName: String,
    
    @Column(name = "last_name", nullable = false)
    val lastName: String,
    
    @Column(name = "date_of_birth", nullable = false)
    val dateOfBirth: LocalDate,
    
    @Column(columnDefinition = "JSON")
    val address: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    val kycStatus: KycStatus = KycStatus.PENDING,
    
    @Column(name = "biometric_enabled")
    val biometricEnabled: Boolean = false,
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    @Column(name = "email_verified")
    val emailVerified: Boolean = false,
    
    @Column(name = "phone_verified")
    val phoneVerified: Boolean = false,
    
    @Column(name = "profile_image_url")
    val profileImageUrl: String? = null,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val accounts: List<Account> = emptyList(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val cards: List<Card> = emptyList(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val pots: List<Pot> = emptyList(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val investments: List<Investment> = emptyList(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val loans: List<Loan> = emptyList()
)

enum class KycStatus {
    PENDING, VERIFIED, REJECTED, UNDER_REVIEW
}