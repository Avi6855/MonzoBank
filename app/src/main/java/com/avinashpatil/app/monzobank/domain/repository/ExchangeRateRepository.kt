package com.avinashpatil.app.monzobank.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

data class ExchangeRate(
    val id: String,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val bid: Double,
    val ask: Double,
    val timestamp: LocalDateTime,
    val source: String,
    val isLive: Boolean = true
)

data class ExchangeRateAlert(
    val id: String,
    val userId: String,
    val baseCurrency: String,
    val targetCurrency: String,
    val targetRate: Double,
    val condition: AlertCondition,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime
)

enum class AlertCondition {
    ABOVE, BELOW, EQUALS
}

data class RateHistory(
    val currency: String,
    val rates: List<ExchangeRate>,
    val period: TimePeriod
)

enum class TimePeriod {
    HOUR, DAY, WEEK, MONTH, YEAR
}

interface ExchangeRateRepository {
    suspend fun getCurrentRate(baseCurrency: String, targetCurrency: String): Result<ExchangeRate>
    suspend fun getRateHistory(baseCurrency: String, targetCurrency: String, period: TimePeriod): Result<RateHistory>
    suspend fun getAllRates(baseCurrency: String): Result<List<ExchangeRate>>
    fun observeRates(baseCurrency: String, targetCurrency: String): Flow<ExchangeRate>
    suspend fun refreshRates(): Result<Unit>
    suspend fun createRateAlert(alert: ExchangeRateAlert): Result<String>
    suspend fun deleteRateAlert(alertId: String): Result<Unit>
    suspend fun getUserAlerts(userId: String): Result<List<ExchangeRateAlert>>
    suspend fun checkAlerts(): Result<List<ExchangeRateAlert>>
    suspend fun getMarketSummary(): Result<List<ExchangeRate>>
    suspend fun getRateSpread(baseCurrency: String, targetCurrency: String): Result<Double>
    suspend fun getVolatility(baseCurrency: String, targetCurrency: String, days: Int): Result<Double>
}