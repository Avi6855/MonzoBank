package com.avinashpatil.monzobank.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    val account: Account,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    val fromAccount: Account? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    val toAccount: Account? = null,
    
    @Column(precision = 15, scale = 2, nullable = false)
    val amount: BigDecimal,
    
    @Column(length = 3, nullable = false)
    val currency: String,
    
    @Column(columnDefinition = "TEXT", nullable = false)
    val description: String,
    
    @Column(length = 50)
    val category: String? = null,
    
    @Column(name = "merchant_name")
    val merchantName: String? = null,
    
    @Column(name = "merchant_id")
    val merchantId: UUID? = null,
    
    @Column(columnDefinition = "JSON")
    val location: String? = null,
    
    @Enumerated(EnumType.STRING)
    val status: TransactionStatus = TransactionStatus.PENDING,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    val transactionType: TransactionType,
    
    @Column(name = "reference_number")
    val referenceNumber: String? = null,
    
    @Column(name = "external_transaction_id")
    val externalTransactionId: String? = null,
    
    @Column(name = "fee_amount", precision = 10, scale = 2)
    val feeAmount: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    val exchangeRate: BigDecimal? = null,
    
    @Column(name = "original_amount", precision = 15, scale = 2)
    val originalAmount: BigDecimal? = null,
    
    @Column(name = "original_currency", length = 3)
    val originalCurrency: String? = null,
    
    @Column(name = "transaction_date", nullable = false)
    val transactionDate: LocalDateTime,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "processed_at")
    val processedAt: LocalDateTime? = null
)

enum class TransactionStatus {
    PENDING, COMPLETED, FAILED, CANCELLED, PROCESSING, DECLINED
}

enum class TransactionType {
    DEBIT, CREDIT, TRANSFER, PAYMENT, DEPOSIT, WITHDRAWAL, 
    FEE, INTEREST, REFUND, CHARGEBACK, DIRECT_DEBIT, STANDING_ORDER
}