package com.avinashpatil.app.monzobank.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["accountId"]),
        Index(value = ["cardNumber"], unique = true)
    ]
)
data class CardEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val accountId: String,
    val cardNumber: String,
    val cardType: CardType,
    val expiryDate: Date,
    val cvvHash: String,
    val isActive: Boolean = true,
    val isFrozen: Boolean = false,
    val contactlessEnabled: Boolean = true,
    val dailyLimit: BigDecimal = BigDecimal("500.00"),
    val monthlyLimit: BigDecimal = BigDecimal("2000.00"),
    val internationalEnabled: Boolean = false,
    val onlineEnabled: Boolean = true,
    val atmEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val cardHolderName: String,
    val cardName: String? = null,
    val pinHash: String? = null,
    val lastUsedAt: Date? = null,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.PENDING,
    val cardDesign: String? = null,
    val trackingNumber: String? = null,
    val estimatedDelivery: Date? = null,
    val deliveryAddress: String? = null,
    val isLocked: Boolean = false,
    val lockReason: String? = null
)