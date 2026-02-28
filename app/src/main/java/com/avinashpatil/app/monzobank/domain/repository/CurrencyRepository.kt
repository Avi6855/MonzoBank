package com.avinashpatil.app.monzobank.domain.repository

import kotlinx.coroutines.flow.Flow

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val isActive: Boolean = true
)

data class CurrencyRate(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val timestamp: Long
)

interface CurrencyRepository {
    suspend fun getSupportedCurrencies(): Result<List<Currency>>
    suspend fun getCurrencyByCode(code: String): Result<Currency?>
    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Result<CurrencyRate>
    suspend fun getHistoricalRates(currency: String, days: Int): Result<List<CurrencyRate>>
    fun observeCurrencyRates(): Flow<List<CurrencyRate>>
    suspend fun refreshRates(): Result<Unit>
    suspend fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Result<Double>
    suspend fun getPopularCurrencies(): Result<List<Currency>>
    suspend fun addFavoriteCurrency(currencyCode: String): Result<Unit>
    suspend fun removeFavoriteCurrency(currencyCode: String): Result<Unit>
    suspend fun getFavoriteCurrencies(): Result<List<Currency>>
}