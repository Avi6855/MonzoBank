package com.avinashpatil.app.monzobank.domain.usecase.card

import com.avinashpatil.app.monzobank.domain.repository.CardRepository
import com.avinashpatil.app.monzobank.utils.SecurityUtils
import javax.inject.Inject

class PinManagementUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val securityUtils: SecurityUtils
) {
    suspend fun changePin(
        cardId: String,
        currentPin: String,
        newPin: String
    ): Result<Unit> {
        return try {
            // Validate PIN format
            if (!isValidPin(newPin)) {
                return Result.failure(Exception("PIN must be 4 digits"))
            }
            
            // Get current card
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            // Verify current PIN
            val currentPinHash = hashPin(currentPin)
            if (card.pinHash != currentPinHash) {
                return Result.failure(Exception("Current PIN is incorrect"))
            }
            
            // Hash new PIN
            val newPinHash = hashPin(newPin)
            
            // Update PIN in repository
            cardRepository.updateCardPin(cardId, newPinHash)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPin(
        cardId: String,
        verificationCode: String,
        newPin: String
    ): Result<Unit> {
        return try {
            // Validate PIN format
            if (!isValidPin(newPin)) {
                return Result.failure(Exception("PIN must be 4 digits"))
            }
            
            // Verify reset code (in real app, this would be sent via SMS/email)
            if (!isValidVerificationCode(verificationCode)) {
                return Result.failure(Exception("Invalid verification code"))
            }
            
            // Hash new PIN
            val newPinHash = hashPin(newPin)
            
            // Update PIN in repository
            cardRepository.updateCardPin(cardId, newPinHash)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyPin(cardId: String, pin: String): Result<Boolean> {
        return try {
            val card = cardRepository.getCardById(cardId)
                ?: return Result.failure(Exception("Card not found"))
            
            val pinHash = hashPin(pin)
            val isValid = card.pinHash == pinHash
            
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun lockCard(cardId: String, reason: String): Result<Unit> {
        return try {
            cardRepository.updateCardLockStatus(cardId, true, reason)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unlockCard(cardId: String): Result<Unit> {
        return try {
            cardRepository.updateCardLockStatus(cardId, false, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun isValidPin(pin: String): Boolean {
        return pin.length == 4 && pin.all { it.isDigit() }
    }
    
    private fun isValidVerificationCode(code: String): Boolean {
        // In real app, this would verify against a stored code
        return code.length == 6 && code.all { it.isDigit() }
    }
    
    private fun hashPin(pin: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        return digest.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}