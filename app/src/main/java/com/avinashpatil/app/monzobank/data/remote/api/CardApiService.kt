package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.*
import com.avinashpatil.app.monzobank.data.remote.dto.card.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Card management API service interface
 * Based on the technical architecture API definitions
 */
interface CardApiService {
    
    /**
     * Get all cards for user
     * GET /api/cards
     */
    @GET("cards")
    suspend fun getCards(
        @Header("Authorization") token: String
    ): Response<List<CardDto>>
    
    /**
     * Get card by ID
     * GET /api/cards/{id}
     */
    @GET("cards/{id}")
    suspend fun getCardById(
        @Header("Authorization") token: String,
        @Path("id") cardId: String
    ): Response<CardDto>
    
    /**
     * Create new card
     * POST /api/cards
     */
    @POST("cards")
    suspend fun createCard(
        @Header("Authorization") token: String,
        @Body request: CreateCardRequestDto
    ): Response<CardDto>
    
    /**
     * Update card details
     * PUT /api/cards/{id}
     */
    @PUT("cards/{id}")
    suspend fun updateCard(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: UpdateCardRequestDto
    ): Response<CardDto>
    
    /**
     * Freeze/Unfreeze card
     * PUT /api/cards/{id}/freeze
     */
    @PUT("cards/{id}/freeze")
    suspend fun toggleCardFreeze(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: ToggleCardFreezeDto
    ): Response<CardDto>
    
    /**
     * Update card limits
     * PUT /api/cards/{id}/limits
     */
    @PUT("cards/{id}/limits")
    suspend fun updateCardLimits(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: UpdateCardLimitsDto
    ): Response<CardDto>
    
    /**
     * Update card controls
     * PUT /api/cards/{id}/controls
     */
    @PUT("cards/{id}/controls")
    suspend fun updateCardControls(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: UpdateCardControlsDto
    ): Response<CardDto>
    
    /**
     * Change card PIN
     * PUT /api/cards/{id}/pin
     */
    @PUT("cards/{id}/pin")
    suspend fun changeCardPin(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: ChangeCardPinDto
    ): Response<BaseResponseDto>
    
    /**
     * Get card transactions
     * GET /api/cards/{id}/transactions
     */
    @GET("cards/{id}/transactions")
    suspend fun getCardTransactions(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<TransactionListDto>
    
    /**
     * Block/Unblock card
     * PUT /api/cards/{id}/block
     */
    @PUT("cards/{id}/block")
    suspend fun toggleCardBlock(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: ToggleCardBlockDto
    ): Response<CardDto>
    
    /**
     * Report card lost/stolen
     * POST /api/cards/{id}/report
     */
    @POST("cards/{id}/report")
    suspend fun reportCard(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: ReportCardDto
    ): Response<BaseResponseDto>
    
    /**
     * Replace card
     * POST /api/cards/{id}/replace
     */
    @POST("cards/{id}/replace")
    suspend fun replaceCard(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: ReplaceCardDto
    ): Response<CardDto>
    
    /**
     * Cancel card
     * DELETE /api/cards/{id}
     */
    @DELETE("cards/{id}")
    suspend fun cancelCard(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: CancelCardDto
    ): Response<BaseResponseDto>
    
    /**
     * Get card delivery status
     * GET /api/cards/{id}/delivery
     */
    @GET("cards/{id}/delivery")
    suspend fun getCardDeliveryStatus(
        @Header("Authorization") token: String,
        @Path("id") cardId: String
    ): Response<CardDeliveryStatusDto>
    
    /**
     * Get card spending insights
     * GET /api/cards/{id}/insights
     */
    @GET("cards/{id}/insights")
    suspend fun getCardSpendingInsights(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Query("period") period: String = "month"
    ): Response<CardSpendingInsightsDto>
    
    /**
     * Activate card
     * POST /api/cards/{id}/activate
     */
    @POST("cards/{id}/activate")
    suspend fun activateCard(
        @Header("Authorization") token: String,
        @Path("id") cardId: String,
        @Body request: ActivateCardDto
    ): Response<CardDto>
    
    /**
     * Get available card designs
     * GET /api/cards/designs
     */
    @GET("cards/designs")
    suspend fun getCardDesigns(
        @Header("Authorization") token: String
    ): Response<List<CardDesignDto>>
}