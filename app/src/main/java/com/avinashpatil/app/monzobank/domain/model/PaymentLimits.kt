package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Payment Limits domain model
 * Represents the payment limits for an account
 */
data class PaymentLimits(
    val dailyLimit: BigDecimal,
    val monthlyLimit: BigDecimal,
    val singleTransactionLimit: BigDecimal,
    val remainingDailyLimit: BigDecimal,
    val remainingMonthlyLimit: BigDecimal,
    val weeklyLimit: BigDecimal? = null,
    val remainingWeeklyLimit: BigDecimal? = null,
    val yearlyLimit: BigDecimal? = null,
    val remainingYearlyLimit: BigDecimal? = null,
    val currency: String = "GBP",
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)