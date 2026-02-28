package com.avinashpatil.monzobank.service

import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class SmsService {
    
    private val logger = LoggerFactory.getLogger(SmsService::class.java)
    
    // In a real implementation, you would integrate with SMS providers like Twilio, AWS SNS, etc.
    
    fun sendVerificationSms(phoneNumber: String, verificationCode: String) {
        try {
            val message = "Your Monzo Bank verification code is: $verificationCode. This code will expire in 10 minutes."
            
            // Simulate SMS sending
            logger.info("Sending SMS to $phoneNumber: $message")
            
            // In a real implementation:
            // twilioClient.messages.create(
            //     Message.creator(
            //         PhoneNumber(phoneNumber),
            //         PhoneNumber(twilioPhoneNumber),
            //         message
            //     )
            // )
            
            logger.info("Verification SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send verification SMS to: $phoneNumber", e)
            throw RuntimeException("Failed to send verification SMS", e)
        }
    }
    
    fun sendTransactionAlert(phoneNumber: String, transactionAmount: String, merchantName: String) {
        try {
            val message = "Monzo Bank Alert: Transaction of $transactionAmount at $merchantName. If this wasn't you, contact us immediately."
            
            logger.info("Sending transaction alert SMS to $phoneNumber: $message")
            
            // Simulate SMS sending
            // In real implementation, integrate with SMS provider
            
            logger.info("Transaction alert SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send transaction alert SMS to: $phoneNumber", e)
            // Don't throw exception for alert SMS as it's not critical
        }
    }
    
    fun sendSecurityAlert(phoneNumber: String, alertMessage: String) {
        try {
            val message = "Monzo Bank Security Alert: $alertMessage. Contact us if this wasn't you."
            
            logger.info("Sending security alert SMS to $phoneNumber: $message")
            
            // Simulate SMS sending
            // In real implementation, integrate with SMS provider
            
            logger.info("Security alert SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send security alert SMS to: $phoneNumber", e)
            // Don't throw exception for security alert SMS
        }
    }
    
    fun sendPasswordResetSms(phoneNumber: String, resetCode: String) {
        try {
            val message = "Your Monzo Bank password reset code is: $resetCode. This code will expire in 10 minutes."
            
            logger.info("Sending password reset SMS to $phoneNumber: $message")
            
            // Simulate SMS sending
            // In real implementation, integrate with SMS provider
            
            logger.info("Password reset SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send password reset SMS to: $phoneNumber", e)
            throw RuntimeException("Failed to send password reset SMS", e)
        }
    }
    
    fun sendLowBalanceAlert(phoneNumber: String, currentBalance: String) {
        try {
            val message = "Monzo Bank: Your account balance is low. Current balance: $currentBalance."
            
            logger.info("Sending low balance alert SMS to $phoneNumber: $message")
            
            // Simulate SMS sending
            // In real implementation, integrate with SMS provider
            
            logger.info("Low balance alert SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send low balance alert SMS to: $phoneNumber", e)
            // Don't throw exception for alert SMS
        }
    }
    
    fun sendCardBlockedAlert(phoneNumber: String, cardLastFourDigits: String) {
        try {
            val message = "Monzo Bank: Your card ending in $cardLastFourDigits has been blocked for security reasons. Contact us for assistance."
            
            logger.info("Sending card blocked alert SMS to $phoneNumber: $message")
            
            // Simulate SMS sending
            // In real implementation, integrate with SMS provider
            
            logger.info("Card blocked alert SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send card blocked alert SMS to: $phoneNumber", e)
            // Don't throw exception for alert SMS
        }
    }
    
    fun sendPaymentConfirmation(phoneNumber: String, recipientName: String, amount: String) {
        try {
            val message = "Monzo Bank: Payment of $amount to $recipientName has been completed successfully."
            
            logger.info("Sending payment confirmation SMS to $phoneNumber: $message")
            
            // Simulate SMS sending
            // In real implementation, integrate with SMS provider
            
            logger.info("Payment confirmation SMS sent successfully to: $phoneNumber")
        } catch (e: Exception) {
            logger.error("Failed to send payment confirmation SMS to: $phoneNumber", e)
            // Don't throw exception for confirmation SMS
        }
    }
}