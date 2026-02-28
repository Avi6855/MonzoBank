package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinancialNewsDto(
    @Json(name = "articles")
    val articles: List<NewsArticleDto>,
    
    @Json(name = "total_results")
    val totalResults: Int,
    
    @Json(name = "page")
    val page: Int,
    
    @Json(name = "page_size")
    val pageSize: Int,
    
    @Json(name = "categories")
    val categories: List<String>,
    
    @Json(name = "last_updated")
    val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class NewsArticleDto(
    @Json(name = "id")
    val id: String,
    
    @Json(name = "title")
    val title: String,
    
    @Json(name = "summary")
    val summary: String,
    
    @Json(name = "content")
    val content: String?,
    
    @Json(name = "url")
    val url: String,
    
    @Json(name = "image_url")
    val imageUrl: String?,
    
    @Json(name = "source")
    val source: NewsSourceDto,
    
    @Json(name = "author")
    val author: String?,
    
    @Json(name = "published_at")
    val publishedAt: String,
    
    @Json(name = "category")
    val category: String, // "markets", "banking", "crypto", "economy", "personal_finance"
    
    @Json(name = "tags")
    val tags: List<String>,
    
    @Json(name = "sentiment")
    val sentiment: String?, // "positive", "negative", "neutral"
    
    @Json(name = "relevance_score")
    val relevanceScore: Double?, // 0.0 to 1.0
    
    @Json(name = "reading_time_minutes")
    val readingTimeMinutes: Int?,
    
    @Json(name = "is_breaking")
    val isBreaking: Boolean,
    
    @Json(name = "is_trending")
    val isTrending: Boolean
)

@JsonClass(generateAdapter = true)
data class NewsSourceDto(
    @Json(name = "name")
    val name: String,
    
    @Json(name = "domain")
    val domain: String,
    
    @Json(name = "logo_url")
    val logoUrl: String?,
    
    @Json(name = "credibility_score")
    val credibilityScore: Double?, // 0.0 to 1.0
    
    @Json(name = "bias_rating")
    val biasRating: String? // "left", "center", "right", "mixed"
)