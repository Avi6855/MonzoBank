package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepositoryImpl @Inject constructor() : ExchangeRateRepository {
    
    private val alerts = mutableListOf<ExchangeRateAlert>()
    
    override suspend fun getCurrentRate(baseCurrency: String, targetCurrency: String): Result<ExchangeRate> {
        return try {
            val rate = when ("$baseCurrency-$targetCurrency") {
                "USD-EUR" -> 0.85
                "EUR-USD" -> 1.18
                "GBP-USD" -> 1.25
                "USD-GBP" -> 0.80
                "USD-JPY" -> 110.0
                "JPY-USD" -> 0.009
                else -> 1.0
            }
            
            val exchangeRate = ExchangeRate(
                id = UUID.randomUUID().toString(),
                baseCurrency = baseCurrency,
                targetCurrency = targetCurrency,
                rate = rate,
                bid = rate * 0.999,
                ask = rate * 1.001,
                timestamp = LocalDateTime.now(),
                source = "Mock Provider",
                isLive = true
            )
            Result.success(exchangeRate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRateHistory(baseCurrency: String, targetCurrency: String, period: TimePeriod): Result<RateHistory> {
        return try {
            val baseRate = getCurrentRate(baseCurrency, targetCurrency).getOrNull()?.rate ?: 1.0
            val days = when (period) {
                TimePeriod.HOUR -> 1
                TimePeriod.DAY -> 7
                TimePeriod.WEEK -> 30
                TimePeriod.MONTH -> 90
                TimePeriod.YEAR -> 365
            }
            
            val rates = (1..days).map { day ->
                ExchangeRate(
                    id = UUID.randomUUID().toString(),
                    baseCurrency = baseCurrency,
                    targetCurrency = targetCurrency,
                    rate = baseRate + (Math.random() - 0.5) * 0.1,
                    bid = baseRate * 0.999,
                    ask = baseRate * 1.001,
                    timestamp = LocalDateTime.now().minusDays(day.toLong()),
                    source = "Mock Provider",
                    isLive = false
                )
            }
            
            val history = RateHistory("$baseCurrency-$targetCurrency", rates, period)
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllRates(baseCurrency: String): Result<List<ExchangeRate>> {
        return try {
            val currencies = listOf("USD", "EUR", "GBP", "JPY", "CAD")
            val rates = currencies.filter { it != baseCurrency }.map { targetCurrency ->
                getCurrentRate(baseCurrency, targetCurrency).getOrNull()
            }.filterNotNull()
            Result.success(rates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeRates(baseCurrency: String, targetCurrency: String): Flow<ExchangeRate> {
        return flowOf()
    }
    
    override suspend fun refreshRates(): Result<Unit> {
        return try {
            // Mock refresh
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createRateAlert(alert: ExchangeRateAlert): Result<String> {
        return try {
            alerts.add(alert)
            Result.success(alert.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteRateAlert(alertId: String): Result<Unit> {
        return try {
            alerts.removeIf { it.id == alertId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserAlerts(userId: String): Result<List<ExchangeRateAlert>> {
        return try {
            val userAlerts = alerts.filter { it.userId == userId }
            Result.success(userAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkAlerts(): Result<List<ExchangeRateAlert>> {
        return try {
            val triggeredAlerts = mutableListOf<ExchangeRateAlert>()
            alerts.filter { it.isActive }.forEach { alert ->
                val currentRate = getCurrentRate(alert.baseCurrency, alert.targetCurrency).getOrNull()?.rate
                if (currentRate != null) {
                    val triggered = when (alert.condition) {
                        AlertCondition.ABOVE -> currentRate > alert.targetRate
                        AlertCondition.BELOW -> currentRate < alert.targetRate
                        AlertCondition.EQUALS -> Math.abs(currentRate - alert.targetRate) < 0.001
                    }
                    if (triggered) {
                        triggeredAlerts.add(alert)
                    }
                }
            }
            Result.success(triggeredAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMarketSummary(): Result<List<ExchangeRate>> {
        return try {
            val summary = listOf(
                getCurrentRate("USD", "EUR").getOrNull(),
                getCurrentRate("USD", "GBP").getOrNull(),
                getCurrentRate("USD", "JPY").getOrNull(),
                getCurrentRate("EUR", "GBP").getOrNull()
            ).filterNotNull()
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRateSpread(baseCurrency: String, targetCurrency: String): Result<Double> {
        return try {
            val rate = getCurrentRate(baseCurrency, targetCurrency).getOrNull()
            if (rate != null) {
                val spread = rate.ask - rate.bid
                Result.success(spread)
            } else {
                Result.failure(Exception("Rate not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getVolatility(baseCurrency: String, targetCurrency: String, days: Int): Result<Double> {
        return try {
            val history = getRateHistory(baseCurrency, targetCurrency, TimePeriod.DAY).getOrNull()
            if (history != null && history.rates.isNotEmpty()) {
                val rates = history.rates.map { it.rate }
                val mean = rates.average()
                val variance = rates.map { (it - mean) * (it - mean) }.average()
                val volatility = Math.sqrt(variance)
                Result.success(volatility)
            } else {
                Result.success(0.0)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}