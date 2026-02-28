package com.avinashpatil.app.monzobank.domain.repository

/**
 * Repository interface for external service integrations
 */
interface ExternalServiceRepository {
    
    /**
     * Get exchange rates
     */
    suspend fun getExchangeRates(baseCurrency: String): Result<Map<String, Double>>
    
    /**
     * Get merchant information
     */
    suspend fun getMerchantInfo(merchantId: String): Result<MerchantInfo>
    
    /**
     * Validate bank account
     */
    suspend fun validateBankAccount(accountNumber: String, sortCode: String): Result<Boolean>
    
    /**
     * Get location services
     */
    suspend fun getNearbyATMs(latitude: Double, longitude: Double): Result<List<ATMLocation>>
}

data class MerchantInfo(
    val id: String,
    val name: String,
    val category: String,
    val logo: String? = null
)

data class ATMLocation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String
)