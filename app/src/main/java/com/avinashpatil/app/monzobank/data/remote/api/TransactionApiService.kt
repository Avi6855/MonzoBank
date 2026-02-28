package com.avinashpatil.app.monzobank.data.remote.api

import com.avinashpatil.app.monzobank.data.remote.dto.TransactionDto
import com.avinashpatil.app.monzobank.data.remote.dto.common.BaseResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.TransactionListDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.CreateTransactionRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.UpdateTransactionRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.TransactionCategoryDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.SpendingInsightsDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.ExportResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.RecurringTransactionDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.CreateRecurringTransactionDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.UpdateRecurringTransactionDto
import com.avinashpatil.app.monzobank.data.remote.dto.transaction.TransactionReceiptDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Transaction management API service interface
 * Based on the technical architecture API definitions
 */
interface TransactionApiService {
    
    /**
     * Get transactions for account
     * GET /api/transactions
     */
    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("category") category: String? = null,
        @Query("type") type: String? = null
    ): Response<TransactionListDto>
    
    /**
     * Get transaction by ID
     * GET /api/transactions/{id}
     */
    @GET("transactions/{id}")
    suspend fun getTransactionById(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String
    ): Response<TransactionDto>
    
    /**
     * Create new transaction
     * POST /api/transactions
     */
    @POST("transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body request: CreateTransactionRequestDto
    ): Response<TransactionDto>
    
    /**
     * Update transaction details
     * PUT /api/transactions/{id}
     */
    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String,
        @Body request: UpdateTransactionRequestDto
    ): Response<TransactionDto>
    
    /**
     * Get transaction categories
     * GET /api/transactions/categories
     */
    @GET("transactions/categories")
    suspend fun getTransactionCategories(
        @Header("Authorization") token: String
    ): Response<List<TransactionCategoryDto>>
    
    /**
     * Get spending insights
     * GET /api/transactions/insights
     */
    @GET("transactions/insights")
    suspend fun getSpendingInsights(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("period") period: String = "month"
    ): Response<SpendingInsightsDto>
    
    /**
     * Search transactions
     * GET /api/transactions/search
     */
    @GET("transactions/search")
    suspend fun searchTransactions(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("accountId") accountId: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<TransactionListDto>
    
    /**
     * Export transactions
     * GET /api/transactions/export
     */
    @GET("transactions/export")
    suspend fun exportTransactions(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("format") format: String = "csv"
    ): Response<ExportResponseDto>
    
    /**
     * Get recurring transactions
     * GET /api/transactions/recurring
     */
    @GET("transactions/recurring")
    suspend fun getRecurringTransactions(
        @Header("Authorization") token: String,
        @Query("accountId") accountId: String
    ): Response<List<RecurringTransactionDto>>
    
    /**
     * Create recurring transaction
     * POST /api/transactions/recurring
     */
    @POST("transactions/recurring")
    suspend fun createRecurringTransaction(
        @Header("Authorization") token: String,
        @Body request: CreateRecurringTransactionDto
    ): Response<RecurringTransactionDto>
    
    /**
     * Update recurring transaction
     * PUT /api/transactions/recurring/{id}
     */
    @PUT("transactions/recurring/{id}")
    suspend fun updateRecurringTransaction(
        @Header("Authorization") token: String,
        @Path("id") recurringId: String,
        @Body request: UpdateRecurringTransactionDto
    ): Response<RecurringTransactionDto>
    
    /**
     * Cancel recurring transaction
     * DELETE /api/transactions/recurring/{id}
     */
    @DELETE("transactions/recurring/{id}")
    suspend fun cancelRecurringTransaction(
        @Header("Authorization") token: String,
        @Path("id") recurringId: String
    ): Response<BaseResponseDto>
    
    /**
     * Get transaction receipt
     * GET /api/transactions/{id}/receipt
     */
    @GET("transactions/{id}/receipt")
    suspend fun getTransactionReceipt(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String
    ): Response<TransactionReceiptDto>
    
    /**
     * Upload transaction receipt
     * POST /api/transactions/{id}/receipt
     */
    @Multipart
    @POST("transactions/{id}/receipt")
    suspend fun uploadTransactionReceipt(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String,
        @Part receipt: okhttp3.MultipartBody.Part
    ): Response<BaseResponseDto>
}