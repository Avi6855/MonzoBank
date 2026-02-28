package com.avinashpatil.app.monzobank.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.avinashpatil.app.monzobank.presentation.screens.main.MainScreen
import com.avinashpatil.app.monzobank.presentation.screens.main.AccountsScreen
import com.avinashpatil.app.monzobank.presentation.screens.main.CardsScreen
import com.avinashpatil.app.monzobank.presentation.screens.main.PaymentsScreen
import com.avinashpatil.app.monzobank.presentation.state.AuthState
import com.avinashpatil.app.monzobank.presentation.viewmodel.AuthViewModel
import com.avinashpatil.app.monzobank.presentation.ui.auth.*
import com.avinashpatil.app.monzobank.presentation.ui.accounts.*
import com.avinashpatil.app.monzobank.presentation.ui.transactions.*
import com.avinashpatil.app.monzobank.presentation.ui.cards.CardDetailsScreen
import com.avinashpatil.app.monzobank.presentation.ui.cards.CreateCardDialog
import com.avinashpatil.app.monzobank.presentation.ui.pots.*
import com.avinashpatil.app.monzobank.presentation.ui.budget.*
import com.avinashpatil.app.monzobank.presentation.ui.payments.TransferScreen
import com.avinashpatil.app.monzobank.presentation.ui.payments.BillPaymentScreen
import com.avinashpatil.app.monzobank.presentation.ui.payments.InternationalTransferScreen
import com.avinashpatil.app.monzobank.presentation.ui.payments.QRPaymentScreen
import com.avinashpatil.app.monzobank.presentation.ui.payments.SplitBillScreen
import com.avinashpatil.app.monzobank.presentation.ui.investments.*
import com.avinashpatil.app.monzobank.presentation.ui.loans.*
import com.avinashpatil.app.monzobank.presentation.ui.insurance.*
import com.avinashpatil.app.monzobank.presentation.ui.business.*
import com.avinashpatil.app.monzobank.presentation.ui.family.*
import com.avinashpatil.app.monzobank.presentation.ui.security.*
import com.avinashpatil.app.monzobank.presentation.ui.support.*
import com.avinashpatil.app.monzobank.presentation.ui.settings.*
import com.avinashpatil.app.monzobank.presentation.ui.analytics.AnalyticsScreen
import com.avinashpatil.app.monzobank.presentation.ui.cards.CardControlsScreen
import com.avinashpatil.app.monzobank.presentation.ui.cards.PinManagementScreen
import com.avinashpatil.app.monzobank.presentation.ui.dashboard.DashboardOverviewScreen
import com.avinashpatil.app.monzobank.presentation.ui.payments.P2PTransferScreen
import com.avinashpatil.app.monzobank.presentation.ui.payments.PaymentRequestScreen
import com.avinashpatil.app.monzobank.presentation.ui.bulk.BulkPaymentScreen
import com.avinashpatil.app.monzobank.presentation.ui.business.InvoiceManagementScreen
import com.avinashpatil.app.monzobank.presentation.ui.business.PayrollSystemScreen
import com.avinashpatil.app.monzobank.presentation.screens.splash.SplashScreen
import com.avinashpatil.app.monzobank.presentation.ui.onboarding.OnboardingScreen
// import com.avinashpatil.app.monzobank.presentation.ui.subscriptions.*
// import com.avinashpatil.app.monzobank.presentation.ui.rewards.*

@Composable
fun MonzoBankNavigation(
    navController: NavHostController,
    authState: AuthState,
    startDestination: String,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash screen
        composable("splash") {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding flow
        composable("onboarding") {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        // Authentication flow
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                onRegistrationSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        // Authentication screens
        composable("auth/forgot-password") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetEmailSent = { navController.popBackStack() }
            )
        }
        
        composable("auth/email-verification") {
            EmailVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onVerificationComplete = {
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable("auth/phone-verification") {
            PhoneVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onVerificationComplete = {
                    navController.navigate("auth/biometric-setup")
                }
            )
        }
        
        composable("auth/biometric-setup") {
            BiometricSetupScreen(
                onBackClick = { navController.popBackStack() },
                onSetupComplete = {
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Account screens
        composable("accounts/{id}") { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("id") ?: ""
            AccountDetailsScreen(
                accountId = accountId,
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                },
                onTransferClick = {
                    navController.navigate("payments/transfer")
                }
            )
        }
        
        composable("accounts/create") {
            CreateAccountScreen(
                onBackClick = { navController.popBackStack() },
                onAccountCreated = { accountId ->
                    navController.navigate("accounts/$accountId") {
                        popUpTo("accounts/create") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard/overview") {
            DashboardOverviewScreen(
                onBackClick = { navController.popBackStack() },
                onAccountClick = { accountId ->
                    navController.navigate("accounts/$accountId")
                },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                }
            )
        }
        
        // Transaction screens
        composable("transactions/{id}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("id") ?: ""
            TransactionDetailsScreen(
                transactionId = transactionId,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("transactions/history") {
            TransactionHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                },
                onExportClick = {
                    navController.navigate("settings/export")
                }
            )
        }
        
        // Card screens
        composable("cards/{id}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("id") ?: ""
            CardDetailsScreen(
                cardId = cardId,
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                }
            )
        }
        
        composable("cards/controls/{id}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("id") ?: ""
            CardControlsScreen(
                cardId = cardId,
                onBackClick = { navController.popBackStack() },
                onPinManagementClick = {
                    navController.navigate("cards/pin/$cardId")
                }
            )
        }
        
        composable("cards/pin/{id}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("id") ?: ""
            PinManagementScreen(
                cardId = cardId,
                onBackClick = { navController.popBackStack() },
                onPinChanged = { 
                    /* Handle PIN changed */
                    navController.popBackStack()
                }
            )
        }
        
        // Pots & Savings screens
        composable("pots") {
            PotsScreen(
                onBackClick = { navController.popBackStack() },
                onPotClick = { pot ->
                    navController.navigate("pots/${pot.id}")
                },
                onCreatePotClick = {
                    navController.navigate("pots/create")
                }
            )
        }
        
        composable("pots/{id}") { backStackEntry ->
            val potId = backStackEntry.arguments?.getString("id") ?: ""
            // Create dummy pot for details
            val pot = com.avinashpatil.app.monzobank.presentation.ui.pots.Pot(
                id = potId,
                name = "Holiday Fund",
                targetAmount = 2000.0,
                currentAmount = 850.0,
                color = androidx.compose.ui.graphics.Color.Blue,
                emoji = "🏖️"
            )
            PotDetailsScreen(
                pot = pot,
                onBackClick = { navController.popBackStack() },
                onAddMoney = { /* Handle add money */ },
                onWithdraw = { /* Handle withdraw */ }
            )
        }
        
        composable("pots/create") {
            CreatePotScreen(
                onBackClick = { navController.popBackStack() },
                onPotCreated = {
                    navController.popBackStack()
                }
            )
        }
        
        // Budget & Analytics screens
        composable("budget") {
            BudgetScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category ->
                    // Navigate to category details
                },
                onCreateBudgetClick = {
                    navController.navigate("budget/create")
                }
            )
        }
        
        composable("budget/create") {
            CreateBudgetScreen(
                onBackClick = { navController.popBackStack() },
                onBudgetCreated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("analytics") {
            AnalyticsScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category -> /* Handle category click */ },
                onExportClick = { navController.navigate("settings/export") }
            )
        }
        
        // Payment screens
        composable("payments/transfer") {
            TransferScreen(
                onBackClick = { navController.popBackStack() },
                onTransferComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/bills") {
            BillPaymentScreen(
                onBackClick = { navController.popBackStack() },
                onPaymentComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/international") {
            InternationalTransferScreen(
                onBackClick = { navController.popBackStack() },
                onTransferComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/qr") {
            QRPaymentScreen(
                onBackClick = { navController.popBackStack() },
                onPaymentComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/split") {
            SplitBillScreen(
                onBackClick = { navController.popBackStack() },
                onSplitComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/p2p") {
            P2PTransferScreen(
                onBackClick = { navController.popBackStack() },
                onContactSelect = { contact -> /* Handle contact selection */ },
                onTransferComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/request") {
            PaymentRequestScreen(
                onBackClick = { navController.popBackStack() },
                onContactSelect = { contact -> /* Handle contact selection */ },
                onRequestSent = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("payments/bulk") {
            BulkPaymentScreen(
                accounts = emptyList(), // TODO: Pass actual accounts
                payees = emptyList(), // TODO: Pass actual payees
                onNavigateBack = { navController.popBackStack() },
                onBulkPaymentSubmitted = { bulkPaymentData -> 
                    /* Handle bulk payment submission */
                    navController.popBackStack()
                }
            )
        }
        
        // Main screens
        composable("transactions") {
            TransactionsScreen(
                accountId = "main",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTransactionDetail = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                }
            )
        }
        
        composable("accounts") {
            AccountsScreen(navController = navController)
        }
        
        composable("cards") {
            CardsScreen(navController = navController)
        }
        
        composable("payments") {
            PaymentsScreen(navController = navController)
        }

        // Main app flow
        composable("dashboard") {
            MainScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        // Investment screens
        composable("investments") {
            InvestmentsScreen(
                onBackClick = { navController.popBackStack() },
                onInvestmentClick = { investment -> /* Navigate to investment details */ },
                onStocksClick = { navController.navigate("investments/stocks") },
                onCryptoClick = { navController.navigate("investments/crypto") }
            )
        }
        
        composable("investments/stocks") {
            StocksScreen(
                onBackClick = { navController.popBackStack() },
                onStockClick = { stock -> /* Navigate to stock details */ },
                onBuyStock = { stock -> /* Handle buy stock */ }
            )
        }
        
        composable("investments/crypto") {
            CryptoScreen(
                onBackClick = { navController.popBackStack() },
                onCryptoClick = { crypto -> /* Navigate to crypto details */ },
                onBuyCrypto = { crypto -> /* Handle buy crypto */ }
            )
        }
        
        // Loan screens
        composable("loans") {
            LoansScreen(
                onBackClick = { navController.popBackStack() },
                onLoanClick = { loan -> /* Navigate to loan details */ },
                onApplyLoanClick = { navController.navigate("loans/application") },
                onCreditScoreClick = { navController.navigate("loans/credit-score") }
            )
        }
        
        composable("loans/apply") {
            LoanApplicationScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitApplication = { application -> 
                    /* Handle application submission */
                    navController.popBackStack()
                }
            )
        }
        
        composable("loans/credit-score") {
            CreditScoreScreen(
                onBackClick = { navController.popBackStack() },
                onImproveTipsClick = { /* Navigate to improve tips */ }
            )
        }
        
        // Insurance screens
        composable("insurance") {
            InsuranceScreen(
                onBackClick = { navController.popBackStack() },
                onPolicyClick = { policyId -> /* Navigate to policy details */ },
                onBuyInsuranceClick = { navController.navigate("insurance/hub") },
                onClaimClick = { claimId -> /* Navigate to claim details */ }
            )
        }
        
        composable("insurance/hub") {
            InsuranceHubScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { categoryId -> /* Navigate to category details */ },
                onArticleClick = { articleId -> /* Navigate to article details */ },
                onCalculatorClick = { /* Navigate to insurance calculator */ },
                onCompareClick = { /* Navigate to insurance comparison */ }
            )
        }
        
        // Business screens
        composable("business") {
            BusinessScreen(
                onBackClick = { navController.popBackStack() },
                onAccountClick = { accountId -> 
                    navController.navigate("accounts/$accountId")
                },
                onServiceClick = { serviceId -> 
                    when (serviceId) {
                        "dashboard" -> navController.navigate("business/dashboard")
                        "invoices" -> navController.navigate("business/invoices")
                        "payroll" -> navController.navigate("business/payroll")
                        else -> { /* Handle other services */ }
                    }
                },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                },
                onViewAllTransactionsClick = {
                    navController.navigate("transactions")
                }
            )
        }
        
        composable("business/dashboard") {
            BusinessDashboardScreen(
                onBackClick = { navController.popBackStack() },
                onMetricClick = { metricId -> /* Navigate to metric details */ },
                onActivityClick = { activityId -> /* Navigate to activity details */ },
                onGoalClick = { goalId -> /* Navigate to goal details */ },
                onAlertClick = { alertId -> /* Navigate to alert details */ }
            )
        }
        
        composable("business/invoices") {
            InvoicesScreen(
                onBackClick = { navController.popBackStack() },
                onInvoiceClick = { invoiceId -> /* Navigate to invoice details */ },
                onCreateInvoiceClick = { /* Navigate to create invoice */ },
                onTemplateClick = { templateId -> /* Navigate to template details */ }
            )
        }
        
        composable("business/invoices/manage") {
            InvoiceManagementScreen(
                onBackClick = { navController.popBackStack() },
                onInvoiceClick = { invoiceId -> /* Navigate to invoice details */ },
                onEditInvoiceClick = { invoiceId -> /* Handle edit invoice */ },
                onDeleteInvoiceClick = { invoiceId -> /* Handle delete invoice */ },
                onSendInvoiceClick = { invoiceId -> /* Handle send invoice */ },
                onDuplicateInvoiceClick = { invoiceId -> /* Handle duplicate invoice */ }
            )
        }
        
        composable("business/payroll") {
            PayrollScreen(
                onBackClick = { navController.popBackStack() },
                onEmployeeClick = { employeeId -> /* Navigate to employee details */ },
                onPayrollRunClick = { payrollRunId -> /* Navigate to payroll run details */ },
                onAddEmployeeClick = { /* Navigate to add employee */ },
                onRunPayrollClick = { /* Handle run payroll */ }
            )
        }
        
        composable("business/payroll/system") {
            PayrollSystemScreen(
                onBackClick = { navController.popBackStack() },
                onEmployeeClick = { employeeId -> /* Navigate to employee details */ },
                onPayrollRunClick = { payrollRunId -> /* Navigate to payroll run details */ },
                onReportClick = { reportType -> /* Handle report generation */ },
                onSettingsClick = { /* Navigate to payroll settings */ },
                onTaxCalculationClick = { /* Handle tax calculation */ }
            )
        }
        
        // Family screens
        composable("family") {
            FamilyScreen(
                onBackClick = { navController.popBackStack() },
                onMemberClick = { memberId -> /* Navigate to member details */ },
                onGoalClick = { goalId -> /* Navigate to goal details */ },
                onActivityClick = { activityId -> /* Navigate to activity details */ },
                onAddMemberClick = { /* Navigate to add member */ },
                onCreateGoalClick = { /* Navigate to create goal */ }
            )
        }
        
        composable("family/accounts") {
            FamilyAccountsScreen(
                onBackClick = { navController.popBackStack() },
                onAccountClick = { accountId ->
                    navController.navigate("accounts/$accountId")
                },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                },
                onCreateAccountClick = {
                    navController.navigate("accounts/create")
                },
                onManagePermissionsClick = { accountId -> /* Handle permissions */ }
            )
        }
        
        composable("family/teen") {
            TeenAccountScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("transactions/$transactionId")
                },
                onGoalClick = { goalId -> /* Handle goal click */ },
                onLessonClick = { lessonId -> /* Handle lesson click */ },
                onRequestMoneyClick = { /* Handle request money */ },
                onParentalControlsClick = { /* Handle parental controls */ }
            )
        }
        
        // Security screens
        composable("security") {
            SecurityCenterScreen(
                onBackClick = { navController.popBackStack() },
                onFeatureClick = { featureId -> /* Handle feature click */ },
                onAlertClick = { alertId -> /* Handle alert click */ },
                onDeviceClick = { deviceId -> /* Handle device click */ },
                onFraudDetectionClick = { navController.navigate("security/fraud") }
            )
        }
        
        composable("security/center") {
            SecurityCenterScreen(
                onBackClick = { navController.popBackStack() },
                onFeatureClick = { featureId -> /* Handle feature click */ },
                onAlertClick = { alertId -> /* Handle alert click */ },
                onDeviceClick = { deviceId -> /* Handle device click */ },
                onFraudDetectionClick = { navController.navigate("security/fraud") }
            )
        }
        
        composable("security/fraud") {
            FraudDetectionScreen(
                onBackClick = { navController.popBackStack() },
                onAlertClick = { alertId -> /* Handle alert click */ },
                onTransactionClick = { transactionId -> 
                    navController.navigate("transactions/$transactionId")
                },
                onRuleClick = { ruleId -> /* Handle rule click */ },
                onReportFraudClick = { /* Handle report fraud */ }
            )
        }
        
        // Support screens
        composable("support") {
            SupportScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category -> /* Handle category click */ },
                onArticleClick = { articleId -> /* Handle article click */ },
                onContactClick = { contactType -> 
                    when (contactType.name) {
                        "CHAT" -> navController.navigate("support/chat")
                        "VIDEO_CALL" -> navController.navigate("support/video")
                        else -> { /* Handle other contact types */ }
                    }
                },
                onTicketClick = { ticketId -> /* Handle ticket click */ },
                onCreateTicketClick = { /* Handle create ticket */ }
            )
        }
        
        composable("support/chat") {
            CustomerSupportScreen(
                onBackClick = { navController.popBackStack() },
                onStartChatClick = { topic -> /* Handle start chat */ },
                onCallSupportClick = { /* Handle call support */ },
                onEmailSupportClick = { /* Handle email support */ }
            )
        }
        
        composable("support/video") {
            VideoSupportScreen(
                onBackClick = { navController.popBackStack() },
                onScheduleSessionClick = { /* Handle schedule session */ },
                onJoinSessionClick = { sessionId -> /* Handle join session */ },
                onRescheduleClick = { sessionId -> /* Handle reschedule */ },
                onCancelSessionClick = { sessionId -> /* Handle cancel session */ }
            )
        }
        
        // Settings screens
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onProfileClick = { /* Navigate to profile */ },
                onSecurityClick = { navController.navigate("security") },
                onNotificationsClick = { /* Navigate to notifications */ },
                onPrivacyClick = { /* Navigate to privacy */ },
                onExportDataClick = { navController.navigate("settings/export") },
                onSupportClick = { navController.navigate("support") },
                onAboutClick = { /* Navigate to about */ },
                onSignOutClick = { /* Handle sign out */ }
            )
        }
        
        composable("settings/export") {
            ExportDataScreen(
                onBackClick = { navController.popBackStack() },
                onStartExportClick = { options, format -> /* Handle export start */ },
                onDownloadClick = { requestId -> /* Handle download */ },
                onDeleteRequestClick = { requestId -> /* Handle delete request */ }
            )
        }
        
        // Additional screens
        composable("subscriptions") {
            SubscriptionManagerScreen(
                onBackClick = { navController.popBackStack() },
                onSubscriptionClick = { /* Navigate to subscription details */ },
                onUpgradeClick = { /* Handle plan upgrade */ },
                onCancelClick = { /* Handle subscription cancellation */ },
                onPauseClick = { /* Handle subscription pause */ },
                onBrowsePlansClick = { /* Navigate to browse plans */ }
            )
        }
        
        composable("rewards") {
            RewardsProgramScreen(
                onBackClick = { navController.popBackStack() },
                onRewardClick = { /* Navigate to reward details */ },
                onRedeemClick = { /* Handle reward redemption */ },
                onChallengeClick = { /* Navigate to challenge details */ },
                onPartnerClick = { /* Navigate to partner details */ },
                onViewHistoryClick = { /* Navigate to points history */ }
            )
        }
    }
}

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    // Placeholder onboarding screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Monzo Bank",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your modern banking experience starts here",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onOnboardingComplete
        ) {
            Text("Get Started")
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    
    // Handle login success
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            onLoginSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login to Monzo",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                authViewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Login")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text("Don't have an account? Register")
        }
        
        // Show error if any
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Placeholder register screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRegistrationSuccess,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onNavigateToLogin
        ) {
            Text("Already have an account? Login")
        }
    }
}