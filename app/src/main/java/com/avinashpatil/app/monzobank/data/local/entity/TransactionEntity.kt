package com.avinashpatil.app.monzobank.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["fromAccountId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["transactionDate"]),
        Index(value = ["status"]),
        Index(value = ["category"])
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val accountId: String,
    val fromAccountId: String? = null,
    val toAccountId: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val description: String,
    val category: String? = null,
    val merchantName: String? = null,
    val merchantId: String? = null,
    val location: String? = null, // JSON string for location data
    val status: TransactionStatus = TransactionStatus.PENDING,
    val transactionDate: Date,
    val createdAt: Date = Date(),
    val reference: String? = null,
    val receiptUrl: String? = null,
    val transactionType: TransactionType = TransactionType.DEBIT,
    val balanceAfter: BigDecimal? = null,
    val isOnlineTransaction: Boolean = false,
    val isAtmTransaction: Boolean = false,
    val isContactlessTransaction: Boolean = false,
    val isInternationalTransaction: Boolean = false
)