package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.common.BaseResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.AddressValidationRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.AddressValidationResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.BankAccountVerificationDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.CreditScoreDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.EmailNotificationRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.ExchangeRatesDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.FinancialNewsDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.LocationServicesDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.MarketDataDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.MerchantInfoDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.OpenBankingSyncRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.OpenBankingSyncResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.SmsNotificationRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.external.VerifyBankAccountRequestDto
import retrofit2.Response
import retrofit2.http.*

/**
 * External services integration API service interface
 * Based on the technical architecture API definitions
 */
interface ExternalApiService {
    
    /**
     * Get exchange rates
     * GET /api/external/exchange-rates
     */
    @GET("external/exchange-rates")
    suspend fun getExchangeRates(
        @Header("Authorization") token: String,
        @Query("baseCurrency") baseCurrency: String = "GBP",
        @Query("targetCurrencies") targetCurrencies: String? = null
    ): Response<ExchangeRatesDto>
    
    /**
     * Get merchant information
     * GET /api/external/merchant-info
     */
    @GET("external/merchant-info")
    suspend fun getMerchantInfo(
        @Header("Authorization") token: String,
        @Query("merchantId") merchantId: String
    ): Response<MerchantInfoDto>
    
    /**
     * Verify bank account
     * POST /api/external/verify-bank-account
     */
    @POST("external/verify-bank-account")
    suspend fun verifyBankAccount(
        @Header("Authorization") token: String,
        @Body request: VerifyBankAccountRequestDto
    ): Response<BankAccountVerificationDto>
    
    /**
     * Get credit score
     * GET /api/external/credit-score
     */
    @GET("external/credit-score")
    suspend fun getCreditScore(
        @Header("Authorization") token: String
    ): Response<CreditScoreDto>
    
    /**
     * Sync with open banking
     * POST /api/external/open-banking/sync
     */
    @POST("external/open-banking/sync")
    suspend fun syncOpenBanking(
        @Header("Authorization") token: String,
        @Body request: OpenBankingSyncRequestDto
    ): Response<OpenBankingSyncResponseDto>
    
    /**
     * Get financial news
     * GET /api/external/financial-news
     */
    @GET("external/financial-news")
    suspend fun getFinancialNews(
        @Header("Authorization") token: String,
        @Query("category") category: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<FinancialNewsDto>
    
    /**
     * Get market data
     * GET /api/external/market-data
     */
    @GET("external/market-data")
    suspend fun getMarketData(
        @Header("Authorization") token: String,
        @Query("symbols") symbols: String
    ): Response<MarketDataDto>
    
    /**
     * Validate address
     * POST /api/external/validate-address
     */
    @POST("external/validate-address")
    suspend fun validateAddress(
        @Header("Authorization") token: String,
        @Body request: AddressValidationRequestDto
    ): Response<AddressValidationResponseDto>
    
    /**
     * Get location-based services
     * GET /api/external/location-services
     */
    @GET("external/location-services")
    suspend fun getLocationServices(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int = 1000
    ): Response<LocationServicesDto>
    
    /**
     * Send SMS notification
     * POST /api/external/sms
     */
    @POST("external/sms")
    suspend fun sendSmsNotification(
        @Header("Authorization") token: String,
        @Body request: SmsNotificationRequestDto
    ): Response<BaseResponseDto>
    
    /**
     * Send email notification
     * POST /api/external/email
     */
    @POST("external/email")
    suspend fun sendEmailNotification(
        @Header("Authorization") token: String,
        @Body request: EmailNotificationRequestDto
    ): Response<BaseResponseDto>
}