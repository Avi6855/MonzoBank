package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class ExchangeRatesDto(
    @Json(name = "base_currency")
    val baseCurrency: String,
    
    @Json(name = "rates")
    val rates: Map<String, BigDecimal>,
    
    @Json(name = "timestamp")
    val timestamp: Long,
    
    @Json(name = "date")
    val date: String,
    
    @Json(name = "source")
    val source: String, // "ECB", "Fed", "BOE", etc.
    
    @Json(name = "last_updated")
    val lastUpdated: String
)