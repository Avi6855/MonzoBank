package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.*
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.PotRepository
import com.avinashpatil.monzobank.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class PotService(
    private val potRepository: PotRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val smsService: SmsService
) {
    
    private val logger = LoggerFactory.getLogger(PotService::class.java)
    
    fun createPot(userId: UUID, request: CreatePotRequest): PotResponse {
        logger.info("Creating pot for user: $userId")
        
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        // Check if user already has a pot with the same name
        val existingPot = potRepository.findByUserIdAndName(userId, request.name)
        if (existingPot.isPresent) {
            throw BusinessRuleException("A pot with name '${request.name}' already exists")
        }
        
        // Validate target amount
        if (request.targetAmount <= BigDecimal.ZERO) {
            throw ValidationException("Target amount must be positive")
        }
        
        val pot = Pot(
            user = user,
            name = request.name,
            description = request.description,
            targetAmount = request.targetAmount,
            currentAmount = BigDecimal.ZERO,
            targetDate = request.targetDate?.toLocalDate(),
            autoDepositEnabled = request.autoDepositEnabled ?: false,
            autoDepositAmount = request.autoDepositAmount,
            autoDepositFrequency = request.depositFrequency,
            isActive = true,
            createdAt = LocalDateTime.now()
        )
        
        val savedPot = potRepository.save(pot)
        
        // Send pot creation notification
        sendPotCreationNotification(user, savedPot)
        
        logger.info("Pot created successfully: ${savedPot.id}")
        return mapToPotResponse(savedPot)
    }
    
    @Transactional(readOnly = true)
    fun getPotById(potId: UUID): PotResponse {
        val pot = potRepository.findById(potId)
            .orElseThrow { PotNotFoundException("Pot not found with ID: $potId") }
        
        return mapToPotResponse(pot)
    }
    
    @Transactional(readOnly = true)
    fun getPotsByUserId(userId: UUID): List<PotResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return potRepository.findActiveUserPotsOrderByCreatedAtDesc(userId)
            .map { mapToPotResponse(it) }
    }
    
    fun updatePot(potId: UUID, request: UpdatePotRequest): PotResponse {
        logger.info("Updating pot: $potId")
        
        val pot = potRepository.findById(potId)
            .orElseThrow { PotNotFoundException("Pot not found with ID: $potId") }
        
        if (!pot.isActive) {
            throw BusinessRuleException("Cannot update inactive pot")
        }
        
        // Update fields if provided
        request.name?.let { 
            // Check for duplicate name
            val existingPot = potRepository.findByUserIdAndName(pot.user.id, it)
            if (existingPot.isPresent && existingPot.get().id != potId) {
                throw BusinessRuleException("A pot with name '$it' already exists")
            }
        }
        
        request.targetAmount?.let {
            if (it <= BigDecimal.ZERO) {
                throw ValidationException("Target amount must be positive")
            }
        }
        
        val updatedPot = pot.copy(
            name = request.name ?: pot.name,
            description = request.description ?: pot.description,
            targetAmount = request.targetAmount ?: pot.targetAmount,
            targetDate = request.targetDate?.toLocalDate() ?: pot.targetDate,
            autoDepositEnabled = request.autoDepositEnabled ?: pot.autoDepositEnabled,
            autoDepositAmount = request.autoDepositAmount ?: pot.autoDepositAmount,
            autoDepositFrequency = request.depositFrequency ?: pot.autoDepositFrequency
        )
        
        val savedPot = potRepository.save(updatedPot)
        
        logger.info("Pot updated successfully: $potId")
        return mapToPotResponse(savedPot)
    }
    
    fun addMoney(potId: UUID, request: AddMoneyToPotRequest): PotResponse {
        logger.info("Adding money to pot: $potId, amount: ${request.amount}")
        
        val pot = potRepository.findById(potId)
            .orElseThrow { PotNotFoundException("Pot not found with ID: $potId") }
        
        if (!pot.isActive) {
            throw BusinessRuleException("Cannot add money to inactive pot")
        }
        
        if (request.amount <= BigDecimal.ZERO) {
            throw ValidationException("Amount must be positive")
        }
        
        // Add money to pot
        val updatedPot = pot.copy(currentAmount = pot.currentAmount.add(request.amount))
        val savedPot = potRepository.save(updatedPot)
        
        // Check if target reached
        if (savedPot.currentAmount >= (savedPot.targetAmount ?: BigDecimal.ZERO)) {
            sendTargetReachedNotification(pot.user, savedPot)
        }
        
        // Send deposit notification
        sendDepositNotification(pot.user, savedPot, request.amount)
        
        logger.info("Money added successfully to pot: $potId")
        return mapToPotResponse(savedPot)
    }
    
    fun withdrawMoney(potId: UUID, request: WithdrawMoneyFromPotRequest): PotResponse {
        logger.info("Withdrawing money from pot: $potId, amount: ${request.amount}")
        
        val pot = potRepository.findById(potId)
            .orElseThrow { PotNotFoundException("Pot not found with ID: $potId") }
        
        if (!pot.isActive) {
            throw BusinessRuleException("Cannot withdraw money from inactive pot")
        }
        
        if (request.amount <= BigDecimal.ZERO) {
            throw ValidationException("Amount must be positive")
        }
        
        if (request.amount > pot.currentAmount) {
            throw InsufficientFundsException("Insufficient funds in pot")
        }
        
        // Withdraw money from pot
        val updatedPot = pot.copy(currentAmount = pot.currentAmount.subtract(request.amount))
        val savedPot = potRepository.save(updatedPot)
        
        // Send withdrawal notification
        sendWithdrawalNotification(pot.user, savedPot, request.amount)
        
        logger.info("Money withdrawn successfully from pot: $potId")
        return mapToPotResponse(savedPot)
    }
    
    fun closePot(potId: UUID): PotResponse {
        logger.info("Closing pot: $potId")
        
        val pot = potRepository.findById(potId)
            .orElseThrow { PotNotFoundException("Pot not found with ID: $potId") }
        
        if (!pot.isActive) {
            throw BusinessRuleException("Pot is already closed")
        }
        
        if (pot.currentAmount > BigDecimal.ZERO) {
            throw BusinessRuleException("Cannot close pot with remaining balance. Please withdraw all funds first.")
        }
        
        val updatedPot = pot.copy(isActive = false)
        val savedPot = potRepository.save(updatedPot)
        
        // Send closure notification
        sendPotClosureNotification(pot.user, savedPot)
        
        logger.info("Pot closed successfully: $potId")
        return mapToPotResponse(savedPot)
    }
    
    @Transactional(readOnly = true)
    fun getPotSummary(userId: UUID): PotSummaryResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        val activePots = potRepository.findActiveUserPotsOrderByCreatedAtDesc(userId)
        val totalSavings = potRepository.getTotalSavingsByUserId(userId) ?: BigDecimal.ZERO
        val totalTarget = potRepository.getTotalTargetAmountByUserId(userId) ?: BigDecimal.ZERO
        val completedPots = potRepository.findCompletedPotsByUserId(userId)
        
        return PotSummaryResponse(
            totalPots = activePots.size.toLong(),
            totalSavings = totalSavings,
            totalTargetAmount = totalTarget,
            completedPots = completedPots.size.toLong(),
            averageSavings = if (activePots.isNotEmpty()) totalSavings.divide(BigDecimal(activePots.size)) else BigDecimal.ZERO,
            progressPercentage = if (totalTarget > BigDecimal.ZERO) 
                totalSavings.divide(totalTarget, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal(100)) 
                else BigDecimal.ZERO
        )
    }
    
    @Transactional(readOnly = true)
    fun getCompletedPots(userId: UUID): List<PotResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with ID: $userId") }
        
        return potRepository.findCompletedPotsByUserId(userId)
            .map { mapToPotResponse(it) }
    }
    
    fun processAutoDeposits() {
        logger.info("Processing auto deposits")
        
        val potsForAutoDeposit = potRepository.findPotsForAutoDeposit(LocalDateTime.now())
        
        potsForAutoDeposit.forEach { pot ->
            try {
                if (pot.autoDepositAmount != null && pot.autoDepositAmount > BigDecimal.ZERO) {
                    val updatedPot = pot.copy(
                        currentAmount = pot.currentAmount.add(pot.autoDepositAmount)
                    )
                    
                    potRepository.save(updatedPot)
                    
                    // Send auto deposit notification
                    sendAutoDepositNotification(pot.user, updatedPot, pot.autoDepositAmount)
                    
                    // Check if target reached
                    if (updatedPot.currentAmount >= (pot.targetAmount ?: BigDecimal.ZERO)) {
                        sendTargetReachedNotification(pot.user, updatedPot)
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to process auto deposit for pot: ${pot.id}", e)
            }
        }
        
        logger.info("Auto deposits processed for ${potsForAutoDeposit.size} pots")
    }
    
    private fun calculateNextDepositDate(frequency: DepositFrequency?): LocalDateTime? {
        if (frequency == null) return null
        
        val now = LocalDateTime.now()
        return when (frequency) {
            DepositFrequency.DAILY -> now.plusDays(1)
            DepositFrequency.WEEKLY -> now.plusWeeks(1)
            DepositFrequency.MONTHLY -> now.plusMonths(1)
            DepositFrequency.QUARTERLY -> now.plusMonths(3)
            DepositFrequency.YEARLY -> now.plusYears(1)
        }
    }
    
    private fun sendPotCreationNotification(user: User, pot: Pot) {
        try {
            val details = "Your savings pot '${pot.name}' has been created with a target of £${pot.targetAmount}."
            emailService.sendTransactionNotification(user.email, details)
        } catch (e: Exception) {
            logger.error("Failed to send pot creation notification", e)
        }
    }
    
    private fun sendDepositNotification(user: User, pot: Pot, amount: BigDecimal) {
        try {
            val details = "£$amount has been added to your pot '${pot.name}'. Current balance: £${pot.currentAmount}."
            emailService.sendTransactionNotification(user.email, details)
        } catch (e: Exception) {
            logger.error("Failed to send deposit notification", e)
        }
    }
    
    private fun sendWithdrawalNotification(user: User, pot: Pot, amount: BigDecimal) {
        try {
            val details = "£$amount has been withdrawn from your pot '${pot.name}'. Remaining balance: £${pot.currentAmount}."
            emailService.sendTransactionNotification(user.email, details)
        } catch (e: Exception) {
            logger.error("Failed to send withdrawal notification", e)
        }
    }
    
    private fun sendTargetReachedNotification(user: User, pot: Pot) {
        try {
            val details = "Congratulations! You've reached your savings target of £${pot.targetAmount} for pot '${pot.name}'."
            emailService.sendTransactionNotification(user.email, details)
            
            smsService.sendTransactionAlert(
                user.phone,
                "£${pot.targetAmount ?: BigDecimal.ZERO}",
                "Savings Target Reached"
            )
        } catch (e: Exception) {
            logger.error("Failed to send target reached notification", e)
        }
    }
    
    private fun sendAutoDepositNotification(user: User, pot: Pot, amount: BigDecimal) {
        try {
            val details = "£$amount has been automatically deposited to your pot '${pot.name}'. Current balance: £${pot.currentAmount}."
            emailService.sendTransactionNotification(user.email, details)
        } catch (e: Exception) {
            logger.error("Failed to send auto deposit notification", e)
        }
    }
    
    private fun sendPotClosureNotification(user: User, pot: Pot) {
        try {
            val details = "Your savings pot '${pot.name}' has been closed."
            emailService.sendTransactionNotification(user.email, details)
        } catch (e: Exception) {
            logger.error("Failed to send pot closure notification", e)
        }
    }
    
    private fun mapToPotResponse(pot: Pot): PotResponse {
        val targetAmount = pot.targetAmount ?: BigDecimal.ZERO
        val progressPercentage = if (targetAmount > BigDecimal.ZERO) {
            pot.currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal(100))
        } else {
            BigDecimal.ZERO
        }
        
        return PotResponse(
            id = pot.id,
            userId = pot.user.id,
            name = pot.name,
            description = pot.description,
            targetAmount = targetAmount,
            currentBalance = pot.currentAmount,
            progressPercentage = progressPercentage,
            targetDate = pot.targetDate?.atStartOfDay(), // Convert LocalDate to LocalDateTime
            autoDepositEnabled = pot.autoDepositEnabled,
            autoDepositAmount = pot.autoDepositAmount,
            depositFrequency = pot.autoDepositFrequency,
            nextDepositDate = null, // Not available in entity
            isActive = pot.isActive,
            isCompleted = pot.currentAmount >= targetAmount,
            createdAt = pot.createdAt,
            updatedAt = pot.createdAt // Use createdAt since updatedAt doesn't exist
        )
    }
}