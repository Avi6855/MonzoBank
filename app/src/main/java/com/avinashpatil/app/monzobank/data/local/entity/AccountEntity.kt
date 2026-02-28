package com.avinashpatil.app.monzobank.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["accountNumber"], unique = true),
        Index(value = ["accountType"]),
        Index(value = ["status"])
    ]
)
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val accountType: AccountType,
    val accountNumber: String,
    val sortCode: String,
    val balance: BigDecimal,
    val currency: String = "GBP",
    val status: AccountStatus = AccountStatus.ACTIVE,
    val isActive: Boolean = true,
    val createdAt: Date,
    val updatedAt: Date = Date(),
    val accountName: String? = null,
    val interestRate: BigDecimal = BigDecimal.ZERO,
    val overdraftLimit: BigDecimal = BigDecimal.ZERO,
    val minimumBalance: BigDecimal = BigDecimal.ZERO
)