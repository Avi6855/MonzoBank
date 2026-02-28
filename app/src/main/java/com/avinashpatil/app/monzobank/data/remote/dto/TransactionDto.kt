package com.avinashpatil.app.monzobank.data.remote.dto

import java.math.BigDecimal

/**
 * Data transfer object for transaction information
 */
data class TransactionDto(
    val id: String,
    val accountId: String,
    val amount: BigDecimal,
    val currency: String,
    val description: String,
    val category: String? = null,
    val merchantName: String? = null,
    val merchantId: String? = null,
    val status: String,
    val transactionDate: String,
    val createdAt: String,
    val reference: String? = null,
    val transactionType: String,
    val balanceAfter: BigDecimal? = null,
    val location: String? = null
)