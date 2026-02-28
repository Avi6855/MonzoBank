package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Bulk Payment domain model
 * Represents a batch of multiple payments processed together
 */
data class BulkPayment(
    val id: String,
    val userId: String = "",
    val accountId: String,
    val batchName: String? = null,
    val payments: List<BulkPaymentItem>,
    val status: BulkPaymentStatus,
    val totalAmount: BigDecimal,
    val currency: String = "GBP",
    val successCount: Int = 0,
    val failureCount: Int = 0,
    val pendingCount: Int = 0,
    val scheduledDate: LocalDateTime? = null,
    val processedDate: LocalDateTime? = null,
    val completedDate: LocalDateTime? = null,
    val approvedBy: String? = null,
    val approvedAt: LocalDateTime? = null,
    val description: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)