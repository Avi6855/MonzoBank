package com.avinashpatil.app.monzobank.data.service

import com.avinashpatil.app.monzobank.domain.model.Currency
import com.avinashpatil.app.monzobank.domain.model.ExchangeRate
import com.avinashpatil.app.monzobank.domain.model.CurrencyConversion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

interface CurrencyService {
    suspend fun getSupportedCurrencies(): Result<List<Currency>>
    suspend fun getExchangeRates(baseCurrency: String): Result<List<ExchangeRate>>
    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Result<ExchangeRate>
    suspend fun convertCurrency(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String
    ): Result<CurrencyConversion>
    suspend fun getHistoricalRates(
        fromCurrency: String,
        toCurrency: String,
        days: Int = 30
    ): Result<List<ExchangeRate>>
    fun getRealTimeRates(baseCurrency: String): Flow<List<ExchangeRate>>
    suspend fun createRateAlert(
        fromCurrency: String,
        toCurrency: String,
        targetRate: BigDecimal,
        userId: String
    ): Result<String>
    suspend fun getRateAlerts(userId: String): Result<List<RateAlert>>
    suspend fun deleteRateAlert(alertId: String): Result<Unit>
}

class CurrencyServiceImpl : CurrencyService {
    
    companion object {
        private const val BASE_CURRENCY = "GBP"
        private val SUPPORTED_CURRENCIES = listOf(
            Currency("GBP", "British Pound", "£", "🇬🇧"),
            Currency("USD", "US Dollar", "$", "🇺🇸"),
            Currency("EUR", "Euro", "€", "🇪🇺"),
            Currency("JPY", "Japanese Yen", "¥", "🇯🇵"),
            Currency("CHF", "Swiss Franc", "CHF", "🇨🇭"),
            Currency("CAD", "Canadian Dollar", "C$", "🇨🇦"),
            Currency("AUD", "Australian Dollar", "A$", "🇦🇺"),
            Currency("CNY", "Chinese Yuan", "¥", "🇨🇳"),
            Currency("INR", "Indian Rupee", "₹", "🇮🇳"),
            Currency("SGD", "Singapore Dollar", "S$", "🇸🇬"),
            Currency("HKD", "Hong Kong Dollar", "HK$", "🇭🇰"),
            Currency("NZD", "New Zealand Dollar", "NZ$", "🇳🇿"),
            Currency("SEK", "Swedish Krona", "kr", "🇸🇪"),
            Currency("NOK", "Norwegian Krone", "kr", "🇳🇴"),
            Currency("DKK", "Danish Krone", "kr", "🇩🇰")
        )
    }
    
    // Mock exchange rates - in production, fetch from real API
    private val mockExchangeRates = mapOf(
        "USD" to BigDecimal("1.27"),
        "EUR" to BigDecimal("1.17"),
        "JPY" to BigDecimal("188.50"),
        "CHF" to BigDecimal("1.12"),
        "CAD" to BigDecimal("1.71"),
        "AUD" to BigDecimal("1.95"),
        "CNY" to BigDecimal("9.15"),
        "INR" to BigDecimal("105.75"),
        "SGD" to BigDecimal("1.70"),
        "HKD" to BigDecimal("9.92"),
        "NZD" to BigDecimal("2.08"),
        "SEK" to BigDecimal("13.85"),
        "NOK" to BigDecimal("13.92"),
        "DKK" to BigDecimal("8.72")
    )
    
    private val rateAlerts = mutableListOf<RateAlert>()
    
    override suspend fun getSupportedCurrencies(): Result<List<Currency>> {
        return try {
            Result.success(SUPPORTED_CURRENCIES)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExchangeRates(baseCurrency: String): Result<List<ExchangeRate>> {
        return try {
            val rates = if (baseCurrency == BASE_CURRENCY) {
                // Direct rates from GBP
                mockExchangeRates.map { (currency, rate) ->
                    ExchangeRate(
                        fromCurrency = baseCurrency,
                        toCurrency = currency,
                        rate = rate,
                        timestamp = LocalDateTime.now(),
                        source = "Mock Exchange API"
                    )
                }
            } else {
                // Calculate cross rates
                val baseToGbp = BigDecimal.ONE.divide(
                    mockExchangeRates[baseCurrency] ?: BigDecimal.ONE,
                    6,
                    RoundingMode.HALF_UP
                )
                
                mockExchangeRates.map { (currency, gbpRate) ->
                    val crossRate = if (currency == baseCurrency) {
                        BigDecimal.ONE
                    } else {
                        gbpRate.multiply(baseToGbp)
                    }
                    
                    ExchangeRate(
                        fromCurrency = baseCurrency,
                        toCurrency = currency,
                        rate = crossRate,
                        timestamp = LocalDateTime.now(),
                        source = "Mock Exchange API"
                    )
                }
            }
            
            Result.success(rates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExchangeRate(
        fromCurrency: String,
        toCurrency: String
    ): Result<ExchangeRate> {
        return try {
            val rate = calculateExchangeRate(fromCurrency, toCurrency)
            val exchangeRate = ExchangeRate(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                rate = rate,
                timestamp = LocalDateTime.now(),
                source = "Mock Exchange API",
                bid = rate.multiply(BigDecimal("0.999")),
                ask = rate.multiply(BigDecimal("1.001")),
                spread = rate.multiply(BigDecimal("0.002"))
            )
            
            Result.success(exchangeRate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun convertCurrency(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String
    ): Result<CurrencyConversion> {
        return try {
            val exchangeRate = getExchangeRate(fromCurrency, toCurrency).getOrThrow()
            val convertedAmount = amount.multiply(exchangeRate.rate)
                .setScale(2, RoundingMode.HALF_UP)
            
            // Calculate fees (mock implementation)
            val feePercentage = BigDecimal("0.005") // 0.5%
            val fee = convertedAmount.multiply(feePercentage)
                .setScale(2, RoundingMode.HALF_UP)
            
            val finalAmount = convertedAmount.subtract(fee)
            
            val conversion = CurrencyConversion(
                id = "conv_${System.currentTimeMillis()}",
                fromAmount = amount,
                fromCurrency = fromCurrency,
                toAmount = finalAmount,
                toCurrency = toCurrency,
                exchangeRate = exchangeRate.rate,
                fee = fee,
                feePercentage = feePercentage,
                timestamp = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plusMinutes(15)
            )
            
            Result.success(conversion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getHistoricalRates(
        fromCurrency: String,
        toCurrency: String,
        days: Int
    ): Result<List<ExchangeRate>> {
        return try {
            val baseRate = calculateExchangeRate(fromCurrency, toCurrency)
            val historicalRates = mutableListOf<ExchangeRate>()
            
            // Generate mock historical data
            for (i in days downTo 0) {
                val date = LocalDateTime.now().minusDays(i.toLong())
                // Add some random variation to the base rate
                val variation = (Math.random() - 0.5) * 0.1 // ±5% variation
                val historicalRate = baseRate.multiply(
                    BigDecimal.ONE.add(BigDecimal.valueOf(variation))
                ).setScale(6, RoundingMode.HALF_UP)
                
                historicalRates.add(
                    ExchangeRate(
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        rate = historicalRate,
                        timestamp = date,
                        source = "Mock Historical API"
                    )
                )
            }
            
            Result.success(historicalRates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getRealTimeRates(baseCurrency: String): Flow<List<ExchangeRate>> {
        return flow {
            while (true) {
                val rates = getExchangeRates(baseCurrency).getOrNull() ?: emptyList()
                emit(rates)
                kotlinx.coroutines.delay(30000) // Update every 30 seconds
            }
        }
    }
    
    override suspend fun createRateAlert(
        fromCurrency: String,
        toCurrency: String,
        targetRate: BigDecimal,
        userId: String
    ): Result<String> {
        return try {
            val alertId = "alert_${System.currentTimeMillis()}"
            val alert = RateAlert(
                id = alertId,
                userId = userId,
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                targetRate = targetRate,
                currentRate = calculateExchangeRate(fromCurrency, toCurrency),
                isActive = true,
                createdAt = LocalDateTime.now()
            )
            
            rateAlerts.add(alert)
            Result.success(alertId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRateAlerts(userId: String): Result<List<RateAlert>> {
        return try {
            val userAlerts = rateAlerts.filter { it.userId == userId && it.isActive }
            Result.success(userAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteRateAlert(alertId: String): Result<Unit> {
        return try {
            rateAlerts.removeAll { it.id == alertId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal {
        if (fromCurrency == toCurrency) return BigDecimal.ONE
        
        return when {
            fromCurrency == BASE_CURRENCY -> {
                mockExchangeRates[toCurrency] ?: BigDecimal.ONE
            }
            toCurrency == BASE_CURRENCY -> {
                BigDecimal.ONE.divide(
                    mockExchangeRates[fromCurrency] ?: BigDecimal.ONE,
                    6,
                    RoundingMode.HALF_UP
                )
            }
            else -> {
                // Cross rate calculation
                val fromToGbp = BigDecimal.ONE.divide(
                    mockExchangeRates[fromCurrency] ?: BigDecimal.ONE,
                    6,
                    RoundingMode.HALF_UP
                )
                val gbpToTo = mockExchangeRates[toCurrency] ?: BigDecimal.ONE
                fromToGbp.multiply(gbpToTo)
            }
        }
    }
}

data class RateAlert(
    val id: String,
    val userId: String,
    val fromCurrency: String,
    val toCurrency: String,
    val targetRate: BigDecimal,
    val currentRate: BigDecimal,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val triggeredAt: LocalDateTime? = null
) {
    val currencyPair: String
        get() = "$fromCurrency/$toCurrency"
    
    val isTargetReached: Boolean
        get() = currentRate >= targetRate
    
    val percentageToTarget: Double
        get() = ((currentRate.toDouble() / targetRate.toDouble()) - 1) * 100
}

// Utility functions for currency operations
object CurrencyUtils {
    
    fun formatAmount(amount: BigDecimal, currency: String): String {
        val currencyInfo = getCurrencyInfo(currency)
        return "${currencyInfo.symbol}${amount.setScale(2, RoundingMode.HALF_UP)}"
    }
    
    fun formatExchangeRate(rate: BigDecimal, fromCurrency: String, toCurrency: String): String {
        return "1 $fromCurrency = ${rate.setScale(4, RoundingMode.HALF_UP)} $toCurrency"
    }
    
    fun getCurrencyInfo(currencyCode: String): Currency {
        return when (currencyCode) {
            "GBP" -> Currency("GBP", "British Pound", "£", "🇬🇧")
            "USD" -> Currency("USD", "US Dollar", "$", "🇺🇸")
            "EUR" -> Currency("EUR", "Euro", "€", "🇪🇺")
            "JPY" -> Currency("JPY", "Japanese Yen", "¥", "🇯🇵")
            "CHF" -> Currency("CHF", "Swiss Franc", "CHF", "🇨🇭")
            "CAD" -> Currency("CAD", "Canadian Dollar", "C$", "🇨🇦")
            "AUD" -> Currency("AUD", "Australian Dollar", "A$", "🇦🇺")
            "CNY" -> Currency("CNY", "Chinese Yuan", "¥", "🇨🇳")
            "INR" -> Currency("INR", "Indian Rupee", "₹", "🇮🇳")
            "SGD" -> Currency("SGD", "Singapore Dollar", "S$", "🇸🇬")
            "HKD" -> Currency("HKD", "Hong Kong Dollar", "HK$", "🇭🇰")
            "NZD" -> Currency("NZD", "New Zealand Dollar", "NZ$", "🇳🇿")
            "SEK" -> Currency("SEK", "Swedish Krona", "kr", "🇸🇪")
            "NOK" -> Currency("NOK", "Norwegian Krone", "kr", "🇳🇴")
            "DKK" -> Currency("DKK", "Danish Krone", "kr", "🇩🇰")
            else -> Currency(currencyCode, currencyCode, currencyCode, "🌍")
        }
    }
    
    fun isValidCurrencyCode(code: String): Boolean {
        return code.length == 3 && code.all { it.isLetter() }
    }
    
    fun calculatePercentageChange(oldRate: BigDecimal, newRate: BigDecimal): Double {
        if (oldRate == BigDecimal.ZERO) return 0.0
        return ((newRate.subtract(oldRate)).divide(oldRate, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))).toDouble()
    }
    
    fun getMajorCurrencies(): List<String> {
        return listOf("USD", "EUR", "JPY", "GBP", "CHF", "CAD", "AUD")
    }
    
    fun getPopularCurrencyPairs(): List<Pair<String, String>> {
        return listOf(
            "GBP" to "USD",
            "GBP" to "EUR",
            "USD" to "EUR",
            "GBP" to "JPY",
            "USD" to "JPY",
            "EUR" to "JPY",
            "GBP" to "CHF",
            "USD" to "CHF"
        )
    }
}