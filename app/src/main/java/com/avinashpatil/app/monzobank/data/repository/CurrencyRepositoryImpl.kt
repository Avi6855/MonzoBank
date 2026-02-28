package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.Currency
import com.avinashpatil.app.monzobank.domain.repository.CurrencyRate
import com.avinashpatil.app.monzobank.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {
    
    private val supportedCurrencies = listOf(
        Currency("USD", "US Dollar", "$"),
        Currency("EUR", "Euro", "€"),
        Currency("GBP", "British Pound", "£"),
        Currency("JPY", "Japanese Yen", "¥"),
        Currency("CAD", "Canadian Dollar", "C$")
    )
    
    private val favoriteCurrencies = mutableSetOf<String>()
    
    override suspend fun getSupportedCurrencies(): Result<List<Currency>> {
        return try {
            Result.success(supportedCurrencies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrencyByCode(code: String): Result<Currency?> {
        return try {
            val currency = supportedCurrencies.find { it.code == code }
            Result.success(currency)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Result<CurrencyRate> {
        return try {
            // Mock exchange rate
            val rate = when ("$fromCurrency-$toCurrency") {
                "USD-EUR" -> 0.85
                "EUR-USD" -> 1.18
                "GBP-USD" -> 1.25
                "USD-GBP" -> 0.80
                else -> 1.0
            }
            val currencyRate = CurrencyRate(fromCurrency, toCurrency, rate, System.currentTimeMillis())
            Result.success(currencyRate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getHistoricalRates(currency: String, days: Int): Result<List<CurrencyRate>> {
        return try {
            // Mock historical data
            val rates = (1..days).map { day ->
                CurrencyRate(
                    fromCurrency = "USD",
                    toCurrency = currency,
                    rate = 1.0 + (day * 0.01),
                    timestamp = System.currentTimeMillis() - (day * 24 * 60 * 60 * 1000)
                )
            }
            Result.success(rates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeCurrencyRates(): Flow<List<CurrencyRate>> {
        return flowOf(emptyList())
    }
    
    override suspend fun refreshRates(): Result<Unit> {
        return try {
            // Mock refresh
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Result<Double> {
        return try {
            val rateResult = getExchangeRate(fromCurrency, toCurrency)
            if (rateResult.isSuccess) {
                val rate = rateResult.getOrNull()?.rate ?: 1.0
                Result.success(amount * rate)
            } else {
                Result.failure(rateResult.exceptionOrNull() ?: Exception("Failed to get exchange rate"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPopularCurrencies(): Result<List<Currency>> {
        return try {
            val popular = supportedCurrencies.take(3)
            Result.success(popular)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addFavoriteCurrency(currencyCode: String): Result<Unit> {
        return try {
            favoriteCurrencies.add(currencyCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeFavoriteCurrency(currencyCode: String): Result<Unit> {
        return try {
            favoriteCurrencies.remove(currencyCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFavoriteCurrencies(): Result<List<Currency>> {
        return try {
            val favorites = supportedCurrencies.filter { it.code in favoriteCurrencies }
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}