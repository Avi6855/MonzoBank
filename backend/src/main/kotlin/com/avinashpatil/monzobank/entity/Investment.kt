package com.avinashpatil.monzobank.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "investments")
data class Investment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @Column(length = 10, nullable = false)
    val symbol: String,
    
    @Column(name = "asset_name", nullable = false)
    val assetName: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    val assetType: AssetType,
    
    @Column(precision = 20, scale = 8, nullable = false)
    val quantity: BigDecimal,
    
    @Column(name = "purchase_price", precision = 15, scale = 2, nullable = false)
    val purchasePrice: BigDecimal,
    
    @Column(name = "current_price", precision = 15, scale = 2)
    val currentPrice: BigDecimal? = null,
    
    @Column(name = "total_invested", precision = 15, scale = 2, nullable = false)
    val totalInvested: BigDecimal,
    
    @Column(name = "current_value", precision = 15, scale = 2)
    val currentValue: BigDecimal? = null,
    
    @Column(name = "profit_loss", precision = 15, scale = 2)
    val profitLoss: BigDecimal? = null,
    
    @Column(name = "profit_loss_percentage", precision = 5, scale = 2)
    val profitLossPercentage: BigDecimal? = null,
    
    @Column(name = "purchase_date", nullable = false)
    val purchaseDate: LocalDateTime,
    
    @Enumerated(EnumType.STRING)
    val status: InvestmentStatus = InvestmentStatus.ACTIVE,
    
    @Column(name = "dividend_yield", precision = 5, scale = 4)
    val dividendYield: BigDecimal? = null,
    
    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime? = null,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class AssetType {
    STOCK, ETF, CRYPTO, BOND, COMMODITY, FOREX
}

enum class InvestmentStatus {
    ACTIVE, SOLD, PENDING, SUSPENDED
}