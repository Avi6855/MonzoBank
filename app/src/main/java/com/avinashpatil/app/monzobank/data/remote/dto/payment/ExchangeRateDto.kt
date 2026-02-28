package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for exchange rate information
 */
data class ExchangeRateDto(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val timestamp: String,
    val provider: String? = null,
    val validUntil: String? = null
)