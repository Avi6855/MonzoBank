package com.avinashpatil.monzobank.repository

import com.avinashpatil.monzobank.entity.AssetType
import com.avinashpatil.monzobank.entity.Investment
import com.avinashpatil.monzobank.entity.InvestmentStatus
import com.avinashpatil.monzobank.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface InvestmentRepository : JpaRepository<Investment, UUID> {
    
    // Find investments by user
    fun findByUser(user: User): List<Investment>
    fun findByUserId(userId: UUID): List<Investment>
    
    // Find investments by asset type
    fun findByAssetType(assetType: AssetType): List<Investment>
    fun findByUserAndAssetType(user: User, assetType: AssetType): List<Investment>
    
    // Find investments by status
    fun findByStatus(status: InvestmentStatus): List<Investment>
    fun findByUserAndStatus(user: User, status: InvestmentStatus): List<Investment>
    
    // Find investments by symbol
    fun findBySymbol(symbol: String): List<Investment>
    fun findByUserAndSymbol(user: User, symbol: String): Optional<Investment>
    
    // Find investments by quantity
    fun findByQuantityGreaterThan(quantity: BigDecimal): List<Investment>
    fun findByQuantityLessThan(quantity: BigDecimal): List<Investment>
    fun findByQuantityBetween(minQuantity: BigDecimal, maxQuantity: BigDecimal): List<Investment>
    
    // Find investments by purchase price
    fun findByPurchasePriceGreaterThan(purchasePrice: BigDecimal): List<Investment>
    fun findByPurchasePriceLessThan(purchasePrice: BigDecimal): List<Investment>
    fun findByPurchasePriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Investment>
    
    // Find investments by current price
    fun findByCurrentPriceGreaterThan(currentPrice: BigDecimal): List<Investment>
    fun findByCurrentPriceLessThan(currentPrice: BigDecimal): List<Investment>
    fun findByCurrentPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Investment>
    
    // Find investments by purchase date
    fun findByPurchaseDateBefore(purchaseDate: LocalDateTime): List<Investment>
    fun findByPurchaseDateAfter(purchaseDate: LocalDateTime): List<Investment>
    fun findByPurchaseDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Investment>
    
    // Custom queries
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE' ORDER BY i.purchaseDate DESC")
    fun findActiveInvestmentsByUserIdOrderByPurchaseDateDesc(@Param("userId") userId: UUID): List<Investment>
    
    @Query("SELECT SUM(i.quantity * i.purchasePrice) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun getTotalInvestmentValueByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT SUM(i.quantity * i.currentPrice) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun getCurrentPortfolioValueByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT COUNT(i) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun countActiveInvestmentsByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND (i.currentPrice - i.purchasePrice) > 0 AND i.status = 'ACTIVE'")
    fun findProfitableInvestmentsByUserId(@Param("userId") userId: UUID): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND (i.currentPrice - i.purchasePrice) < 0 AND i.status = 'ACTIVE'")
    fun findLosingInvestmentsByUserId(@Param("userId") userId: UUID): List<Investment>
    
    @Query("SELECT SUM((i.currentPrice - i.purchasePrice) * i.quantity) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun getTotalGainLossByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND i.assetType = :assetType AND i.status = 'ACTIVE' ORDER BY i.quantity * i.currentPrice DESC")
    fun findInvestmentsByUserAndAssetTypeOrderByValueDesc(@Param("userId") userId: UUID, @Param("assetType") assetType: AssetType): List<Investment>
    
    @Query("SELECT i.assetType, SUM(i.quantity * i.currentPrice) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE' GROUP BY i.assetType")
    fun getPortfolioAllocationByUserId(@Param("userId") userId: UUID): List<Array<Any>>
    
    @Query("SELECT i FROM Investment i WHERE i.symbol LIKE %:searchTerm% OR i.name LIKE %:searchTerm% AND i.status = 'ACTIVE'")
    fun searchInvestmentsBySymbolOrName(@Param("searchTerm") searchTerm: String): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND (i.symbol LIKE %:searchTerm% OR i.name LIKE %:searchTerm%) AND i.status = 'ACTIVE'")
    fun searchUserInvestmentsBySymbolOrName(@Param("userId") userId: UUID, @Param("searchTerm") searchTerm: String): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND i.quantity * i.currentPrice >= :minValue AND i.status = 'ACTIVE'")
    fun findInvestmentsByUserWithMinimumValue(@Param("userId") userId: UUID, @Param("minValue") minValue: BigDecimal): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId ORDER BY (i.currentPrice - i.purchasePrice) / i.purchasePrice DESC")
    fun findInvestmentsByUserOrderByPercentageGainDesc(@Param("userId") userId: UUID): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId ORDER BY (i.currentPrice - i.purchasePrice) / i.purchasePrice ASC")
    fun findInvestmentsByUserOrderByPercentageGainAsc(@Param("userId") userId: UUID): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId ORDER BY i.quantity * i.currentPrice DESC")
    fun findInvestmentsByUserOrderByCurrentValueDesc(@Param("userId") userId: UUID): List<Investment>
    
    @Query("SELECT AVG(i.quantity * i.currentPrice) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun getAverageInvestmentValueByUserId(@Param("userId") userId: UUID): BigDecimal?
    
    @Query("SELECT i FROM Investment i WHERE i.purchaseDate >= :date AND i.status = 'ACTIVE'")
    fun findRecentInvestments(@Param("date") date: LocalDateTime): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND i.purchaseDate >= :date AND i.status = 'ACTIVE'")
    fun findRecentInvestmentsByUserId(@Param("userId") userId: UUID, @Param("date") date: LocalDateTime): List<Investment>
    
    @Query("SELECT i FROM Investment i WHERE i.updatedAt >= :date AND i.status = 'ACTIVE'")
    fun findRecentlyUpdatedInvestments(@Param("date") date: LocalDateTime): List<Investment>
    
    @Query("SELECT COUNT(DISTINCT i.symbol) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun countUniqueSymbolsByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT COUNT(DISTINCT i.assetType) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    fun countUniqueAssetTypesByUserId(@Param("userId") userId: UUID): Long
    
    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND ABS((i.currentPrice - i.purchasePrice) / i.purchasePrice) >= :threshold AND i.status = 'ACTIVE'")
    fun findInvestmentsWithSignificantPriceChange(@Param("userId") userId: UUID, @Param("threshold") threshold: BigDecimal): List<Investment>
    
    @Query("SELECT COUNT(i) FROM Investment i WHERE i.status = 'ACTIVE'")
    fun countActiveInvestments(): Long
    
    @Query("SELECT COUNT(i) FROM Investment i WHERE i.status = 'SOLD'")
    fun countSoldInvestments(): Long
    
    @Query("SELECT COUNT(DISTINCT i.user.id) FROM Investment i WHERE i.status = 'ACTIVE'")
    fun countActiveInvestors(): Long
}