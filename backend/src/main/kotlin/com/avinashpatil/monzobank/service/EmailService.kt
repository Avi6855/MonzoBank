package com.avinashpatil.monzobank.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    
    fun sendVerificationEmail(email: String, verificationToken: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(email)
                subject = "Verify Your Monzo Bank Account"
                text = """
                    Welcome to Monzo Bank!
                    
                    Please verify your email address by clicking the link below:
                    
                    http://localhost:8080/api/auth/verify-email?token=$verificationToken
                    
                    This link will expire in 24 hours.
                    
                    If you didn't create an account with Monzo Bank, please ignore this email.
                    
                    Best regards,
                    Monzo Bank Team
                """.trimIndent()
            }
            
            mailSender.send(message)
            logger.info("Verification email sent to: $email")
        } catch (e: Exception) {
            logger.error("Failed to send verification email to: $email", e)
            throw RuntimeException("Failed to send verification email", e)
        }
    }
    
    fun sendPasswordResetEmail(email: String, resetToken: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(email)
                subject = "Reset Your Monzo Bank Password"
                text = """
                    Hello,
                    
                    You requested to reset your password for your Monzo Bank account.
                    
                    Please click the link below to reset your password:
                    
                    http://localhost:8080/api/auth/reset-password?token=$resetToken
                    
                    This link will expire in 1 hour.
                    
                    If you didn't request a password reset, please ignore this email and your password will remain unchanged.
                    
                    Best regards,
                    Monzo Bank Team
                """.trimIndent()
            }
            
            mailSender.send(message)
            logger.info("Password reset email sent to: $email")
        } catch (e: Exception) {
            logger.error("Failed to send password reset email to: $email", e)
            throw RuntimeException("Failed to send password reset email", e)
        }
    }
    
    fun sendWelcomeEmail(email: String, firstName: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(email)
                subject = "Welcome to Monzo Bank!"
                text = """
                    Hello $firstName,
                    
                    Welcome to Monzo Bank! We're excited to have you as a customer.
                    
                    Your account has been successfully created. You can now:
                    
                    • View your account balance and transactions
                    • Make payments and transfers
                    • Set up savings pots
                    • Manage your cards
                    • Track your spending with budgets
                    
                    Download our mobile app to get started:
                    • iOS: https://apps.apple.com/monzo-bank
                    • Android: https://play.google.com/store/apps/monzo-bank
                    
                    If you have any questions, our support team is here to help 24/7.
                    
                    Best regards,
                    Monzo Bank Team
                """.trimIndent()
            }
            
            mailSender.send(message)
            logger.info("Welcome email sent to: $email")
        } catch (e: Exception) {
            logger.error("Failed to send welcome email to: $email", e)
            // Don't throw exception for welcome email as it's not critical
        }
    }
    
    fun sendTransactionNotification(email: String, transactionDetails: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(email)
                subject = "Transaction Notification - Monzo Bank"
                text = """
                    Hello,
                    
                    A transaction has been made on your Monzo Bank account:
                    
                    $transactionDetails
                    
                    If you didn't make this transaction, please contact us immediately.
                    
                    Best regards,
                    Monzo Bank Team
                """.trimIndent()
            }
            
            mailSender.send(message)
            logger.info("Transaction notification email sent to: $email")
        } catch (e: Exception) {
            logger.error("Failed to send transaction notification email to: $email", e)
            // Don't throw exception for notification email
        }
    }
    
    fun sendSecurityAlert(email: String, alertMessage: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(email)
                subject = "Security Alert - Monzo Bank"
                text = """
                    SECURITY ALERT
                    
                    $alertMessage
                    
                    If this was you, you can ignore this email. If not, please contact us immediately.
                    
                    For your security:
                    • Never share your login details
                    • Always log out when using shared devices
                    • Contact us if you notice any suspicious activity
                    
                    Best regards,
                    Monzo Bank Security Team
                """.trimIndent()
            }
            
            mailSender.send(message)
            logger.info("Security alert email sent to: $email")
        } catch (e: Exception) {
            logger.error("Failed to send security alert email to: $email", e)
            // Don't throw exception for security alert email
        }
    }
}