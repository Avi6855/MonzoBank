package com.avinashpatil.app.monzobank.domain.usecase.card

import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import java.math.BigDecimal
import javax.inject.Inject

class UpdateCardControlsUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    suspend fun freezeCard(cardId: String): Result<Unit> {
        return try {
            cardRepository.updateCardFreezeStatus(cardId, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unfreezeCard(cardId: String): Result<Unit> {
        return try {
            cardRepository.updateCardFreezeStatus(cardId, false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateDailyLimit(cardId: String, limit: BigDecimal): Result<Unit> {
        return try {
            if (limit < BigDecimal.ZERO) {
                return Result.failure(Exception("Daily limit cannot be negative"))
            }
            if (limit > BigDecimal("5000.00")) {
                return Result.failure(Exception("Daily limit cannot exceed £5,000"))
            }
            
            cardRepository.updateCardDailyLimit(cardId, limit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMonthlyLimit(cardId: String, limit: BigDecimal): Result<Unit> {
        return try {
            if (limit < BigDecimal.ZERO) {
                return Result.failure(Exception("Monthly limit cannot be negative"))
            }
            if (limit > BigDecimal("50000.00")) {
                return Result.failure(Exception("Monthly limit cannot exceed £50,000"))
            }
            
            cardRepository.updateCardMonthlyLimit(cardId, limit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateContactlessEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardRepository.updateCardContactlessEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateInternationalEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardRepository.updateCardInternationalEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOnlineEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardRepository.updateCardOnlineEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAtmEnabled(cardId: String, enabled: Boolean): Result<Unit> {
        return try {
            cardRepository.updateCardAtmEnabled(cardId, enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}