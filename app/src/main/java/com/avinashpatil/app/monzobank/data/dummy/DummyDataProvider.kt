package com.avinashpatil.app.monzobank.data.dummy

import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.domain.model.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * Dummy Data Provider for Development and Testing
 * 
 * WARNING: This class contains mock data for development purposes only.
 * Remove all references to this class before production deployment.
 * 
 * Usage:
 * - Use DummyDataProvider.getAccounts() to get sample accounts
 * - Use DummyDataProvider.getTransactions() to get sample transactions
 * - Use DummyDataProvider.getCards() to get sample cards
 * - Use DummyDataProvider.getUser() to get sample user data
 */
object DummyDataProvider {
    
    // Test User Credentials
    const val TEST_EMAIL = "john.doe@example.com"
    const val TEST_PASSWORD = "password123"
    const val TEST_PHONE = "+44 7700 900123"
    
    // Alternative Test Accounts
    const val TEST_EMAIL_2 = "jane.smith@example.com"
    const val TEST_PASSWORD_2 = "testpass456"
    const val TEST_PHONE_2 = "+44 7700 900456"
    
    private val currentDate = Date()
    private val calendar = Calendar.getInstance()
    
    /**
     * Sample User Data
     */
    fun getUser(): User {
        return User(
            id = "user_123456",
            email = TEST_EMAIL,
            phoneNumber = TEST_PHONE,
            firstName = "John",
            lastName = "Doe",
            dateOfBirth = "1990-05-15",
            profileImageUrl = null,
            address = Address(
                street = "123 High Street",
                city = "London",
                state = "England",
                postalCode = "SW1A 1AA",
                country = "United Kingdom"
            ),
            kycStatus = KYCStatus.APPROVED,
            accountType = AccountType.CURRENT,
            createdAt = LocalDateTime.now().minusMonths(6),
            updatedAt = LocalDateTime.now(),
            lastLoginAt = LocalDateTime.now().minusHours(2),
            isEmailVerified = true,
            isPhoneVerified = true
        )
    }
    
    /**
     * Sample Accounts Data
     */
    fun getAccounts(): List<Account> {
        return listOf(
            // Main Current Account
            Account(
                id = "acc_current_001",
                userId = "user_123456",
                name = "Current Account",
                type = AccountType.CURRENT,
                balance = BigDecimal("2847.63"),
                availableBalance = BigDecimal("2847.63"),
                currency = "GBP",
                accountNumber = "12345678",
                sortCode = "040004",
                iban = "GB29 NWBK 6016 1331 9268 19",
                status = AccountStatus.ACTIVE,
                overdraftLimit = BigDecimal("500.00"),
                interestRate = BigDecimal("0.01"),
                createdAt = getDateDaysAgo(180),
                updatedAt = currentDate,
                isDefault = true
            ),
            
            // Savings Account
            Account(
                id = "acc_savings_001",
                userId = "user_123456",
                name = "Instant Access Savings",
                type = AccountType.SAVINGS,
                balance = BigDecimal("15420.89"),
                availableBalance = BigDecimal("15420.89"),
                currency = "GBP",
                accountNumber = "87654321",
                sortCode = "040004",
                iban = "GB29 NWBK 6016 1331 9268 20",
                status = AccountStatus.ACTIVE,
                interestRate = BigDecimal("2.5"),
                createdAt = getDateDaysAgo(120),
                updatedAt = currentDate
            ),
            
            // Joint Account
            Account(
                id = "acc_joint_001",
                userId = "user_123456",
                name = "Joint Account",
                type = AccountType.JOINT,
                balance = BigDecimal("1256.42"),
                availableBalance = BigDecimal("1256.42"),
                currency = "GBP",
                accountNumber = "11223344",
                sortCode = "040004",
                iban = "GB29 NWBK 6016 1331 9268 21",
                status = AccountStatus.ACTIVE,
                createdAt = getDateDaysAgo(90),
                updatedAt = currentDate
            )
        )
    }
    
    /**
     * Sample Cards Data
     */
    fun getCards(): List<Card> {
        return listOf(
            // Main Debit Card
            Card(
                id = "card_debit_001",
                userId = "user_123456",
                accountId = "acc_current_001",
                cardNumber = "4532123456789012",
                cardType = CardType.DEBIT,
                expiryDate = getDateMonthsFromNow(24),
                cvv = "123",
                isActive = true,
                isFrozen = false,
                contactlessEnabled = true,
                dailyLimit = BigDecimal("500.00"),
                monthlyLimit = BigDecimal("2000.00"),
                internationalEnabled = true,
                onlineEnabled = true,
                atmEnabled = true,
                createdAt = getDateDaysAgo(180),
                updatedAt = currentDate,
                cardHolderName = "JOHN DOE",
                cardName = "Monzo Debit Card",
                lastUsedAt = getDateHoursAgo(6),
                deliveryStatus = DeliveryStatus.DELIVERED,
                status = CardStatus.ACTIVE
            ),
            
            // Virtual Card
            Card(
                id = "card_virtual_001",
                userId = "user_123456",
                accountId = "acc_current_001",
                cardNumber = "4532987654321098",
                cardType = CardType.VIRTUAL,
                expiryDate = getDateMonthsFromNow(12),
                cvv = "456",
                isActive = true,
                isFrozen = false,
                contactlessEnabled = false,
                dailyLimit = BigDecimal("200.00"),
                monthlyLimit = BigDecimal("800.00"),
                internationalEnabled = false,
                onlineEnabled = true,
                atmEnabled = false,
                createdAt = getDateDaysAgo(30),
                updatedAt = currentDate,
                cardHolderName = "JOHN DOE",
                cardName = "Virtual Shopping Card",
                lastUsedAt = getDateDaysAgo(2),
                deliveryStatus = DeliveryStatus.DELIVERED,
                status = CardStatus.ACTIVE
            )
        )
    }
    
    /**
     * Sample Transactions Data
     */
    fun getTransactions(): List<Transaction> {
        return listOf(
            // Recent transactions
            Transaction(
                id = "txn_001",
                accountId = "acc_current_001",
                cardId = "card_debit_001",
                userId = "user_123456",
                type = TransactionType.DEBIT,
                status = TransactionStatus.COMPLETED,
                amount = BigDecimal("-45.67"),
                currency = "GBP",
                description = "Tesco Express",
                category = TransactionCategory.GROCERIES,
                merchantName = "Tesco Express",
                paymentMethod = PaymentMethodType.CONTACTLESS,
                balanceAfter = BigDecimal("2847.63"),
                runningBalance = BigDecimal("2847.63"),
                createdAt = getDateHoursAgo(2),
                processedAt = getDateHoursAgo(2),
                updatedAt = getDateHoursAgo(2),
                isContactlessTransaction = true
            ),
            
            Transaction(
                id = "txn_002",
                accountId = "acc_current_001",
                userId = "user_123456",
                type = TransactionType.CREDIT,
                status = TransactionStatus.COMPLETED,
                amount = BigDecimal("2500.00"),
                currency = "GBP",
                description = "Salary Payment",
                category = TransactionCategory.SALARY,
                paymentMethod = PaymentMethodType.BANK_TRANSFER,
                balanceAfter = BigDecimal("2893.30"),
                runningBalance = BigDecimal("2893.30"),
                createdAt = getDateDaysAgo(1),
                processedAt = getDateDaysAgo(1),
                updatedAt = getDateDaysAgo(1)
            ),
            
            Transaction(
                id = "txn_003",
                accountId = "acc_current_001",
                cardId = "card_debit_001",
                userId = "user_123456",
                type = TransactionType.DEBIT,
                status = TransactionStatus.COMPLETED,
                amount = BigDecimal("-12.50"),
                currency = "GBP",
                description = "Costa Coffee",
                category = TransactionCategory.RESTAURANTS,
                merchantName = "Costa Coffee",
                paymentMethod = PaymentMethodType.CONTACTLESS,
                balanceAfter = BigDecimal("393.30"),
                runningBalance = BigDecimal("393.30"),
                createdAt = getDateDaysAgo(2),
                processedAt = getDateDaysAgo(2),
                updatedAt = getDateDaysAgo(2),
                isContactlessTransaction = true
            ),
            
            Transaction(
                id = "txn_004",
                accountId = "acc_current_001",
                cardId = "card_debit_001",
                userId = "user_123456",
                type = TransactionType.DEBIT,
                status = TransactionStatus.COMPLETED,
                amount = BigDecimal("-89.99"),
                currency = "GBP",
                description = "Amazon.co.uk",
                category = TransactionCategory.SHOPPING,
                merchantName = "Amazon",
                paymentMethod = PaymentMethodType.ONLINE_PAYMENT,
                balanceAfter = BigDecimal("405.80"),
                runningBalance = BigDecimal("405.80"),
                createdAt = getDateDaysAgo(3),
                processedAt = getDateDaysAgo(3),
                updatedAt = getDateDaysAgo(3),
                isOnlineTransaction = true
            ),
            
            Transaction(
                id = "txn_005",
                accountId = "acc_current_001",
                userId = "user_123456",
                type = TransactionType.DEBIT,
                status = TransactionStatus.COMPLETED,
                amount = BigDecimal("-125.00"),
                currency = "GBP",
                description = "British Gas",
                category = TransactionCategory.BILLS,
                paymentMethod = PaymentMethodType.DIRECT_DEBIT,
                balanceAfter = BigDecimal("495.79"),
                runningBalance = BigDecimal("495.79"),
                createdAt = getDateDaysAgo(5),
                processedAt = getDateDaysAgo(5),
                updatedAt = getDateDaysAgo(5)
            )
        )
    }
    
    /**
     * Sample Payments Data
     */
    fun getPayments(): List<Payment> {
        return listOf(
            Payment(
                id = "pay_001",
                userId = "user_123456",
                fromAccountId = "acc_current_001",
                toAccountId = "acc_external_001",
                amount = BigDecimal("250.00"),
                currency = "GBP",
                description = "Rent payment",
                reference = "RENT-JAN-2024",
                status = PaymentStatus.COMPLETED,
                type = PaymentType.BANK_TRANSFER,
                recipientName = "Property Management Ltd",
                scheduledDate = java.time.LocalDateTime.now().minusDays(7),
                processedDate = java.time.LocalDateTime.now().minusDays(7),
                createdAt = java.time.LocalDateTime.now().minusDays(10),
                updatedAt = java.time.LocalDateTime.now().minusDays(7)
            ),
            
            Payment(
                id = "pay_002",
                userId = "user_123456",
                fromAccountId = "acc_current_001",
                amount = BigDecimal("50.00"),
                currency = "GBP",
                description = "Dinner split",
                reference = "DINNER-SPLIT",
                status = PaymentStatus.PENDING,
                type = PaymentType.INSTANT_TRANSFER,
                recipientName = "Sarah Johnson",
                recipientPhone = "+44 7700 900789",
                scheduledDate = java.time.LocalDateTime.now(),
                createdAt = java.time.LocalDateTime.now().minusHours(1),
                updatedAt = java.time.LocalDateTime.now().minusHours(1)
            )
        )
    }
    
    // Helper functions for date generation
    private fun getDateDaysAgo(days: Int): Date {
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -days)
        return calendar.time
    }
    
    private fun getDateHoursAgo(hours: Int): Date {
        calendar.time = currentDate
        calendar.add(Calendar.HOUR_OF_DAY, -hours)
        return calendar.time
    }
    
    private fun getDateMonthsFromNow(months: Int): Date {
        calendar.time = currentDate
        calendar.add(Calendar.MONTH, months)
        return calendar.time
    }
    
    /**
     * Account Summary with dummy data
     */
    fun getAccountSummary(): AccountSummary {
        val accounts = getAccounts()
        return AccountSummary(
            totalBalance = accounts.sumOf { it.balance },
            totalAvailableBalance = accounts.sumOf { it.availableBalance },
            accountCount = accounts.size,
            activeAccountCount = accounts.count { it.status == AccountStatus.ACTIVE },
            totalOverdraftUsed = BigDecimal.ZERO,
            totalOverdraftLimit = accounts.sumOf { it.overdraftLimit },
            monthlyIncome = BigDecimal("2500.00"),
            monthlyExpenses = BigDecimal("1850.00"),
            savingsRate = BigDecimal("26.0")
        )
    }
    
    /**
     * Recent activity for dashboard
     */
    fun getRecentActivity(): List<Transaction> {
        return getTransactions().take(5)
    }
    
    /**
     * Quick actions for dashboard
     */
    fun getQuickActions(): List<QuickAction> {
        return listOf(
            QuickAction("Send Money", "💸", "payments/transfer"),
            QuickAction("Pay Bills", "📄", "payments/bills"),
            QuickAction("Investments", "📈", "investments"),
            QuickAction("Loans", "🏦", "loans"),
            QuickAction("Insurance", "🛡️", "insurance"),
            QuickAction("Business", "💼", "business"),
            QuickAction("Family", "👨‍👩‍👧‍👦", "family"),
            QuickAction("Security", "🔒", "security"),
            QuickAction("Support", "💬", "support"),
            QuickAction("Settings", "⚙️", "settings"),
            QuickAction("Rewards", "🎁", "rewards"),
            QuickAction("Subscriptions", "📱", "subscriptions")
        )
    }
}

/**
 * Quick Action data class for dashboard
 */
data class QuickAction(
    val title: String,
    val icon: String,
    val route: String
)

/**
 * Development Notes:
 * 
 * 1. All dummy data uses realistic UK banking formats
 * 2. Account numbers and sort codes follow UK banking standards
 * 3. Transaction amounts and categories reflect typical spending patterns
 * 4. Dates are generated relative to current time for realistic timeline
 * 5. Card numbers use valid Luhn algorithm format (test numbers)
 * 
 * IMPORTANT: Remove this entire file before production deployment!
 */