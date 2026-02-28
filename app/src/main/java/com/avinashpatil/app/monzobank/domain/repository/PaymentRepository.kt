package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

/**
 * Payment repository interface
 * Defines the contract for payment operations
 */
interface PaymentRepository {
    
    // P2P Transfers
    suspend fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal,
        currency: String = "GBP",
        reference: String,
        description: String? = null
    ): Result<Payment>
    
    suspend fun getTransferById(transferId: String): Result<Payment>
    
    suspend fun getTransferHistory(
        accountId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<Payment>>
    
    // Bill Payments
    suspend fun createBillPayment(
        accountId: String,
        payeeId: String,
        amount: BigDecimal,
        currency: String = "GBP",
        reference: String,
        scheduledDate: Date? = null
    ): Result<Payment>
    
    suspend fun getBillPayments(
        accountId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<Payment>>
    
    // Standing Orders
    suspend fun createStandingOrder(
        accountId: String,
        payeeId: String,
        amount: BigDecimal,
        currency: String = "GBP",
        reference: String,
        frequency: PaymentFrequency,
        startDate: Date,
        endDate: Date? = null
    ): Result<StandingOrder>
    
    suspend fun getStandingOrders(accountId: String): Result<List<StandingOrder>>
    
    suspend fun updateStandingOrder(
        standingOrderId: String,
        amount: BigDecimal? = null,
        frequency: PaymentFrequency? = null,
        endDate: Date? = null
    ): Result<StandingOrder>
    
    suspend fun cancelStandingOrder(standingOrderId: String): Result<Unit>
    
    // Direct Debits
    suspend fun getDirectDebits(accountId: String): Result<List<DirectDebit>>
    
    suspend fun cancelDirectDebit(directDebitId: String): Result<Unit>
    
    // Payment Requests
    suspend fun createPaymentRequest(
        fromUserId: String,
        toUserId: String,
        amount: BigDecimal,
        currency: String = "GBP",
        description: String,
        dueDate: Date? = null
    ): Result<PaymentRequest>
    
    suspend fun getPaymentRequests(
        userId: String,
        type: PaymentRequestType? = null
    ): Result<List<PaymentRequest>>
    
    suspend fun respondToPaymentRequest(
        requestId: String,
        action: PaymentRequestAction
    ): Result<Unit>
    
    // Payment Validation
    suspend fun validatePayment(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal
    ): Result<PaymentValidation>
    
    suspend fun validatePayee(payeeDetails: PayeeDetails): Result<PayeeValidation>
    
    // Payment Limits
    suspend fun getPaymentLimits(accountId: String): Result<PaymentLimits>
    
    suspend fun updatePaymentLimits(
        accountId: String,
        limits: PaymentLimits
    ): Result<Unit>
    
    // Bulk Payments
    suspend fun createBulkPayment(
        accountId: String,
        payments: List<BulkPaymentItem>
    ): Result<BulkPayment>
    
    suspend fun getBulkPaymentStatus(bulkPaymentId: String): Result<BulkPayment>
    
    // Payment Templates
    suspend fun savePaymentTemplate(
        userId: String,
        template: PaymentTemplate
    ): Result<PaymentTemplate>
    
    suspend fun getPaymentTemplates(userId: String): Result<List<PaymentTemplate>>
    
    suspend fun deletePaymentTemplate(templateId: String): Result<Unit>
    
    // Real-time updates
    fun observePaymentStatus(paymentId: String): Flow<PaymentStatus>
    
    fun observeAccountPayments(accountId: String): Flow<List<Payment>>
}