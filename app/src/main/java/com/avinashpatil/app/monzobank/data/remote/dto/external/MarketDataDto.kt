package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class MarketDataDto(
    @Json(name = "indices")
    val indices: List<MarketIndexDto>,
    
    @Json(name = "currencies")
    val currencies: List<CurrencyPairDto>,
    
    @Json(name = "commodities")
    val commodities: List<CommodityDto>,
    
    @Json(name = "interest_rates")
    val interestRates: List<InterestRateDto>,
    
    @Json(name = "market_summary")
    val marketSummary: MarketSummaryDto,
    
    @Json(name = "last_updated")
    val lastUpdated: String,
    
    @Json(name = "data_source")
    val dataSource: String
)

@JsonClass(generateAdapter = true)
data class MarketIndexDto(
    @Json(name = "symbol")
    val symbol: String, // "SPX", "FTSE", "NIKKEI", etc.
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "current_value")
    val currentValue: BigDecimal,
    
    @Json(name = "change")
    val change: BigDecimal,
    
    @Json(name = "change_percent")
    val changePercent: BigDecimal,
    
    @Json(name = "previous_close")
    val previousClose: BigDecimal,
    
    @Json(name = "day_high")
    val dayHigh: BigDecimal,
    
    @Json(name = "day_low")
    val dayLow: BigDecimal,
    
    @Json(name = "year_high")
    val yearHigh: BigDecimal,
    
    @Json(name = "year_low")
    val yearLow: BigDecimal,
    
    @Json(name = "market_status")
    val marketStatus: String, // "open", "closed", "pre_market", "after_hours"
    
    @Json(name = "last_trade_time")
    val lastTradeTime: String
)

@JsonClass(generateAdapter = true)
data class CurrencyPairDto(
    @Json(name = "pair")
    val pair: String, // "USD/EUR", "GBP/USD", etc.
    
    @Json(name = "base_currency")
    val baseCurrency: String,
    
    @Json(name = "quote_currency")
    val quoteCurrency: String,
    
    @Json(name = "rate")
    val rate: BigDecimal,
    
    @Json(name = "change")
    val change: BigDecimal,
    
    @Json(name = "change_percent")
    val changePercent: BigDecimal,
    
    @Json(name = "bid")
    val bid: BigDecimal,
    
    @Json(name = "ask")
    val ask: BigDecimal,
    
    @Json(name = "day_high")
    val dayHigh: BigDecimal,
    
    @Json(name = "day_low")
    val dayLow: BigDecimal,
    
    @Json(name = "last_updated")
    val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class CommodityDto(
    @Json(name = "symbol")
    val symbol: String, // "GOLD", "OIL", "SILVER", etc.
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "price")
    val price: BigDecimal,
    
    @Json(name = "currency")
    val currency: String,
    
    @Json(name = "unit")
    val unit: String, // "oz", "barrel", "ton", etc.
    
    @Json(name = "change")
    val change: BigDecimal,
    
    @Json(name = "change_percent")
    val changePercent: BigDecimal,
    
    @Json(name = "day_high")
    val dayHigh: BigDecimal,
    
    @Json(name = "day_low")
    val dayLow: BigDecimal,
    
    @Json(name = "last_updated")
    val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class InterestRateDto(
    @Json(name = "country")
    val country: String,
    
    @Json(name = "rate_type")
    val rateType: String, // "central_bank", "10_year_bond", "mortgage", etc.
    
    @Json(name = "rate")
    val rate: BigDecimal, // Percentage
    
    @Json(name = "change")
    val change: BigDecimal,
    
    @Json(name = "last_change_date")
    val lastChangeDate: String,
    
    @Json(name = "next_meeting_date")
    val nextMeetingDate: String?
)

@JsonClass(generateAdapter = true)
data class MarketSummaryDto(
    @Json(name = "market_sentiment")
    val marketSentiment: String, // "bullish", "bearish", "neutral"
    
    @Json(name = "volatility_index")
    val volatilityIndex: BigDecimal,
    
    @Json(name = "global_market_status")
    val globalMarketStatus: String,
    
    @Json(name = "major_movers")
    val majorMovers: MajorMoversDto,
    
    @Json(name = "economic_events_today")
    val economicEventsToday: List<EconomicEventDto>
)

@JsonClass(generateAdapter = true)
data class MajorMoversDto(
    @Json(name = "gainers")
    val gainers: List<MarketMoverDto>,
    
    @Json(name = "losers")
    val losers: List<MarketMoverDto>
)

@JsonClass(generateAdapter = true)
data class MarketMoverDto(
    @Json(name = "symbol")
    val symbol: String,
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "change_percent")
    val changePercent: BigDecimal
)

@JsonClass(generateAdapter = true)
data class EconomicEventDto(
    @Json(name = "time")
    val time: String,
    
    @Json(name = "country")
    val country: String,
    
    @Json(name = "event")
    val event: String,
    
    @Json(name = "importance")
    val importance: String, // "high", "medium", "low"
    
    @Json(name = "forecast")
    val forecast: String?,
    
    @Json(name = "previous")
    val previous: String?
)