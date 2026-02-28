package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.*
import com.avinashpatil.app.monzobank.data.remote.dto.payment.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Payment processing API service interface
 * Based on the technical architecture API definitions
 */
interface PaymentApiService {
    
    /**
     * Create P2P transfer
     * POST /api/payments/transfer
     */
    @POST("payments/transfer")
    suspend fun createTransfer(
        @Header("Authorization") token: String,
        @Body request: CreateTransferRequestDto
    ): Response<PaymentResponseDto>
    
    /**
     * Create bill payment
     * POST /api/payments/bill
     */
    @POST("payments/bill")
    suspend fun createBillPayment(
        @Header("Authorization") token: String,
        @Body request: CreateBillPaymentRequestDto
    ): Response<PaymentResponseDto>
    
    /**
     * Create international transfer
     * POST /api/payments/international
     */
    @POST("payments/international")
    suspend fun createInternationalTransfer(
        @Header("Authorization") token: String,
        @Body request: CreateInternationalTransferDto
    ): Response<PaymentResponseDto>
    
    /**
     * Get payment status
     * GET /api/payments/{id}/status
     */
    @GET("payments/{id}/status")
    suspend fun getPaymentStatus(
        @Header("Authorization") token: String,
        @Path("id") paymentId: String
    ): Response<PaymentStatusDto>
    
    /**
     * Cancel payment
     * DELETE /api/payments/{id}
     */
    @DELETE("payments/{id}")
    suspend fun cancelPayment(
        @Header("Authorization") token: String,
        @Path("id") paymentId: String
    ): Response<BaseResponseDto>
    
    /**
     * Get payment history
     * GET /api/payments/history
     */
    @GET("payments/history")
    suspend fun getPaymentHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("type") type: String? = null
    ): Response<PaymentHistoryDto>
    
    /**
     * Get saved payees
     * GET /api/payments/payees
     */
    @GET("payments/payees")
    suspend fun getSavedPayees(
        @Header("Authorization") token: String
    ): Response<List<PayeeDto>>
    
    /**
     * Add new payee
     * POST /api/payments/payees
     */
    @POST("payments/payees")
    suspend fun addPayee(
        @Header("Authorization") token: String,
        @Body request: AddPayeeRequestDto
    ): Response<PayeeDto>
    
    /**
     * Update payee
     * PUT /api/payments/payees/{id}
     */
    @PUT("payments/payees/{id}")
    suspend fun updatePayee(
        @Header("Authorization") token: String,
        @Path("id") payeeId: String,
        @Body request: UpdatePayeeRequestDto
    ): Response<PayeeDto>
    
    /**
     * Delete payee
     * DELETE /api/payments/payees/{id}
     */
    @DELETE("payments/payees/{id}")
    suspend fun deletePayee(
        @Header("Authorization") token: String,
        @Path("id") payeeId: String
    ): Response<BaseResponseDto>
    
    /**
     * Get exchange rates
     * GET /api/payments/exchange-rates
     */
    @GET("payments/exchange-rates")
    suspend fun getExchangeRates(
        @Header("Authorization") token: String,
        @Query("from") fromCurrency: String,
        @Query("to") toCurrency: String
    ): Response<ExchangeRateDto>
    
    /**
     * Create scheduled payment
     * POST /api/payments/scheduled
     */
    @POST("payments/scheduled")
    suspend fun createScheduledPayment(
        @Header("Authorization") token: String,
        @Body request: CreateScheduledPaymentDto
    ): Response<ScheduledPaymentDto>
    
    /**
     * Get scheduled payments
     * GET /api/payments/scheduled
     */
    @GET("payments/scheduled")
    suspend fun getScheduledPayments(
        @Header("Authorization") token: String
    ): Response<List<ScheduledPaymentDto>>
    
    /**
     * Update scheduled payment
     * PUT /api/payments/scheduled/{id}
     */
    @PUT("payments/scheduled/{id}")
    suspend fun updateScheduledPayment(
        @Header("Authorization") token: String,
        @Path("id") scheduledPaymentId: String,
        @Body request: UpdateScheduledPaymentDto
    ): Response<ScheduledPaymentDto>
    
    /**
     * Cancel scheduled payment
     * DELETE /api/payments/scheduled/{id}
     */
    @DELETE("payments/scheduled/{id}")
    suspend fun cancelScheduledPayment(
        @Header("Authorization") token: String,
        @Path("id") scheduledPaymentId: String
    ): Response<BaseResponseDto>
    
    /**
     * Create split bill
     * POST /api/payments/split-bill
     */
    @POST("payments/split-bill")
    suspend fun createSplitBill(
        @Header("Authorization") token: String,
        @Body request: CreateSplitBillDto
    ): Response<SplitBillDto>
    
    /**
     * Get split bills
     * GET /api/payments/split-bills
     */
    @GET("payments/split-bills")
    suspend fun getSplitBills(
        @Header("Authorization") token: String
    ): Response<List<SplitBillDto>>
    
    /**
     * Respond to split bill
     * PUT /api/payments/split-bills/{id}/respond
     */
    @PUT("payments/split-bills/{id}/respond")
    suspend fun respondToSplitBill(
        @Header("Authorization") token: String,
        @Path("id") splitBillId: String,
        @Body request: SplitBillResponseDto
    ): Response<BaseResponseDto>
    
    /**
     * Create payment request
     * POST /api/payments/request
     */
    @POST("payments/request")
    suspend fun createPaymentRequest(
        @Header("Authorization") token: String,
        @Body request: CreatePaymentRequestDto
    ): Response<PaymentRequestDto>
    
    /**
     * Get payment requests
     * GET /api/payments/requests
     */
    @GET("payments/requests")
    suspend fun getPaymentRequests(
        @Header("Authorization") token: String,
        @Query("type") type: String = "all" // sent, received, all
    ): Response<List<PaymentRequestDto>>
    
    /**
     * Respond to payment request
     * PUT /api/payments/requests/{id}/respond
     */
    @PUT("payments/requests/{id}/respond")
    suspend fun respondToPaymentRequest(
        @Header("Authorization") token: String,
        @Path("id") requestId: String,
        @Body request: PaymentRequestResponseDto
    ): Response<BaseResponseDto>
}