package com.avinashpatil.app.monzobank.presentation.navigation

/**
 * Sealed class representing all screens in the Monzo Bank app
 * Following the technical architecture document's route definitions
 */
sealed class Screen(val route: String) {
    
    // Splash & Onboarding
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    
    // Authentication
    object Login : Screen("auth/login")
    object Register : Screen("auth/register")
    object ForgotPassword : Screen("auth/forgot-password")
    object EmailVerification : Screen("auth/email-verification")
    object PhoneVerification : Screen("auth/phone-verification")
    object BiometricSetup : Screen("auth/biometric-setup")
    
    // Main Dashboard
    object Dashboard : Screen("dashboard")
    
    // Account Management
    object Accounts : Screen("accounts")
    object AccountDetails : Screen("accounts/{id}") {
        fun createRoute(accountId: String) = "accounts/$accountId"
    }
    
    // Transactions
    object Transactions : Screen("transactions")
    object TransactionDetails : Screen("transactions/{id}") {
        fun createRoute(transactionId: String) = "transactions/$transactionId"
    }
    
    // Cards
    object Cards : Screen("cards")
    object CardDetails : Screen("cards/{id}") {
        fun createRoute(cardId: String) = "cards/$cardId"
    }
    
    // Pots & Savings
    object Pots : Screen("pots")
    object PotDetails : Screen("pots/{id}") {
        fun createRoute(potId: String) = "pots/$potId"
    }
    
    // Payments
    object Payments : Screen("payments")
    object Transfer : Screen("payments/transfer")
    object Bills : Screen("payments/bills")
    object International : Screen("payments/international")
    
    // Budget & Analytics
    object Budget : Screen("budget")
    object Analytics : Screen("analytics")
    
    // Investments
    object Investments : Screen("investments")
    object Stocks : Screen("investments/stocks")
    object Crypto : Screen("investments/crypto")
    
    // Loans & Credit
    object Loans : Screen("loans")
    
    // Insurance
    object Insurance : Screen("insurance")
    
    // Business Banking
    object Business : Screen("business")
    object Invoices : Screen("business/invoices")
    object Payroll : Screen("business/payroll")
    
    // Family & Social
    object Family : Screen("family")
    
    // Security
    object Security : Screen("security")
    
    // Support
    object Support : Screen("support")
    
    // Settings
    object Settings : Screen("settings")
    
    // Additional screens for comprehensive banking
    object QRPayment : Screen("payments/qr")
    object SplitBill : Screen("payments/split")
    object CreatePot : Screen("pots/create")
    object LoanApplication : Screen("loans/apply")
    object CreditScore : Screen("loans/credit-score")
    object InsuranceHub : Screen("insurance/hub")
    object BusinessDashboard : Screen("business/dashboard")
    object InvoiceManagement : Screen("business/invoices/manage")
    object PayrollSystem : Screen("business/payroll/system")
    object FamilyAccounts : Screen("family/accounts")
    object TeenAccount : Screen("family/teen")
    object SecurityCenter : Screen("security/center")
    object FraudDetection : Screen("security/fraud")
    object CustomerSupport : Screen("support/chat")
    object VideoSupport : Screen("support/video")
    object ExportData : Screen("settings/export")
    object SubscriptionManager : Screen("subscriptions")
    object RewardsProgram : Screen("rewards")
}