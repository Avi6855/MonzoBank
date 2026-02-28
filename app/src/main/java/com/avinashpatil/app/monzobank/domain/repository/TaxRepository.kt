package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class TaxDocument(
    val id: String,
    val userId: String,
    val taxYear: Int,
    val documentType: TaxDocumentType,
    val filePath: String,
    val status: TaxDocumentStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class TaxDocumentType {
    W2, FORM_1099, TAX_RETURN, RECEIPT, INVOICE
}

enum class TaxDocumentStatus {
    PENDING, PROCESSED, APPROVED, REJECTED
}

data class TaxSummary(
    val userId: String,
    val taxYear: Int,
    val totalIncome: BigDecimal,
    val totalDeductions: BigDecimal,
    val taxableIncome: BigDecimal,
    val estimatedTax: BigDecimal,
    val paidTax: BigDecimal,
    val refundDue: BigDecimal
)

data class TaxCategory(
    val id: String,
    val name: String,
    val description: String,
    val isDeductible: Boolean
)

interface TaxRepository {
    suspend fun uploadTaxDocument(document: TaxDocument): Result<String>
    suspend fun getTaxDocuments(userId: String, taxYear: Int): Result<List<TaxDocument>>
    suspend fun deleteTaxDocument(documentId: String): Result<Unit>
    suspend fun getTaxSummary(userId: String, taxYear: Int): Result<TaxSummary>
    suspend fun calculateEstimatedTax(userId: String, income: BigDecimal): Result<BigDecimal>
    suspend fun getTaxCategories(): Result<List<TaxCategory>>
    suspend fun categorizeTransaction(transactionId: String, categoryId: String): Result<Unit>
    suspend fun generateTaxReport(userId: String, taxYear: Int): Result<String>
    suspend fun getDeductibleTransactions(userId: String, taxYear: Int): Result<List<String>>
    suspend fun updateTaxSettings(userId: String, settings: Map<String, Any>): Unit
}