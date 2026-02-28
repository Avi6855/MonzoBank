package com.avinashpatil.app.monzobank.di

import com.avinashpatil.app.monzobank.data.repository.*
import com.avinashpatil.app.monzobank.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository
    
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository
    
    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ): CardRepository
    
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
    
    @Binds
    @Singleton
    abstract fun bindSecurityRepository(
        securityRepositoryImpl: SecurityRepositoryImpl
    ): SecurityRepository
    
    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        analyticsRepositoryImpl: AnalyticsRepositoryImpl
    ): AnalyticsRepository
    
    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository
    
    @Binds
    @Singleton
    abstract fun bindBiometricRepository(
        biometricRepositoryImpl: BiometricRepositoryImpl
    ): BiometricRepository
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
    
    @Binds
    @Singleton
    abstract fun bindContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository
    
    @Binds
    @Singleton
    abstract fun bindFileRepository(
        fileRepositoryImpl: FileRepositoryImpl
    ): FileRepository
    
    @Binds
    @Singleton
    abstract fun bindCacheRepository(
        cacheRepositoryImpl: CacheRepositoryImpl
    ): CacheRepository
    
    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository
    
    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        backupRepositoryImpl: BackupRepositoryImpl
    ): BackupRepository
    
    @Binds
    @Singleton
    abstract fun bindExternalServiceRepository(
        externalServiceRepositoryImpl: ExternalServiceRepositoryImpl
    ): ExternalServiceRepository
    
    @Binds
    @Singleton
    abstract fun bindComplianceRepository(
        complianceRepositoryImpl: ComplianceRepositoryImpl
    ): ComplianceRepository
    
    @Binds
    @Singleton
    abstract fun bindFraudRepository(
        fraudRepositoryImpl: FraudRepositoryImpl
    ): FraudRepository
    
    @Binds
    @Singleton
    abstract fun bindAuditRepository(
        auditRepositoryImpl: AuditRepositoryImpl
    ): AuditRepository
    
    @Binds
    @Singleton
    abstract fun bindReportingRepository(
        reportingRepositoryImpl: ReportingRepositoryImpl
    ): ReportingRepository
    
    @Binds
    @Singleton
    abstract fun bindConfigurationRepository(
        configurationRepositoryImpl: ConfigurationRepositoryImpl
    ): ConfigurationRepository
    
    @Binds
    @Singleton
    abstract fun bindFeatureFlagRepository(
        featureFlagRepositoryImpl: FeatureFlagRepositoryImpl
    ): FeatureFlagRepository
    
    @Binds
    @Singleton
    abstract fun bindHealthCheckRepository(
        healthCheckRepositoryImpl: HealthCheckRepositoryImpl
    ): HealthCheckRepository
    
    @Binds
    @Singleton
    abstract fun bindMetricsRepository(
        metricsRepositoryImpl: MetricsRepositoryImpl
    ): MetricsRepository
    
    @Binds
    @Singleton
    abstract fun bindLoggingRepository(
        loggingRepositoryImpl: LoggingRepositoryImpl
    ): LoggingRepository
    
    @Binds
    @Singleton
    abstract fun bindCrashReportingRepository(
        crashReportingRepositoryImpl: CrashReportingRepositoryImpl
    ): CrashReportingRepository
    
    @Binds
    @Singleton
    abstract fun bindPerformanceRepository(
        performanceRepositoryImpl: PerformanceRepositoryImpl
    ): PerformanceRepository
    
    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository
    
    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository
    
    @Binds
    @Singleton
    abstract fun bindEncryptionRepository(
        encryptionRepositoryImpl: EncryptionRepositoryImpl
    ): EncryptionRepository
    
    @Binds
    @Singleton
    abstract fun bindKeyManagementRepository(
        keyManagementRepositoryImpl: KeyManagementRepositoryImpl
    ): KeyManagementRepository
    
    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository
    
    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository
    
    @Binds
    @Singleton
    abstract fun bindRateLimitRepository(
        rateLimitRepositoryImpl: RateLimitRepositoryImpl
    ): RateLimitRepository
    
    @Binds
    @Singleton
    abstract fun bindThrottleRepository(
        throttleRepositoryImpl: ThrottleRepositoryImpl
    ): ThrottleRepository
    
    @Binds
    @Singleton
    abstract fun bindQueueRepository(
        queueRepositoryImpl: QueueRepositoryImpl
    ): QueueRepository
    
    @Binds
    @Singleton
    abstract fun bindSchedulerRepository(
        schedulerRepositoryImpl: SchedulerRepositoryImpl
    ): SchedulerRepository
    
    @Binds
    @Singleton
    abstract fun bindWorkManagerRepository(
        workManagerRepositoryImpl: WorkManagerRepositoryImpl
    ): WorkManagerRepository
    
    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository
    
    @Binds
    @Singleton
    abstract fun bindDocumentRepository(
        documentRepositoryImpl: DocumentRepositoryImpl
    ): DocumentRepository
    
    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository
    
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository
    
    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        recommendationRepositoryImpl: RecommendationRepositoryImpl
    ): RecommendationRepository
    
    @Binds
    @Singleton
    abstract fun bindPersonalizationRepository(
        personalizationRepositoryImpl: PersonalizationRepositoryImpl
    ): PersonalizationRepository
    
    @Binds
    @Singleton
    abstract fun bindMachineLearningRepository(
        machineLearningRepositoryImpl: MachineLearningRepositoryImpl
    ): MachineLearningRepository
    
    @Binds
    @Singleton
    abstract fun bindAIRepository(
        aiRepositoryImpl: AIRepositoryImpl
    ): AIRepository
    
    @Binds
    @Singleton
    abstract fun bindChatbotRepository(
        chatbotRepositoryImpl: ChatbotRepositoryImpl
    ): ChatbotRepository
    
    @Binds
    @Singleton
    abstract fun bindVoiceRepository(
        voiceRepositoryImpl: VoiceRepositoryImpl
    ): VoiceRepository
    
    @Binds
    @Singleton
    abstract fun bindAccessibilityRepository(
        accessibilityRepositoryImpl: AccessibilityRepositoryImpl
    ): AccessibilityRepository
    
    @Binds
    @Singleton
    abstract fun bindInternationalizationRepository(
        internationalizationRepositoryImpl: InternationalizationRepositoryImpl
    ): InternationalizationRepository
    
    @Binds
    @Singleton
    abstract fun bindLocalizationRepository(
        localizationRepositoryImpl: LocalizationRepositoryImpl
    ): LocalizationRepository
    
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
    
    @Binds
    @Singleton
    abstract fun bindExchangeRateRepository(
        exchangeRateRepositoryImpl: ExchangeRateRepositoryImpl
    ): ExchangeRateRepository
    
    @Binds
    @Singleton
    abstract fun bindTaxRepository(
        taxRepositoryImpl: TaxRepositoryImpl
    ): TaxRepository
    
    @Binds
    @Singleton
    abstract fun bindRegulatoryRepository(
        regulatoryRepositoryImpl: RegulatoryRepositoryImpl
    ): RegulatoryRepository
    
    @Binds
    @Singleton
    abstract fun bindKYCRepository(
        kycRepositoryImpl: KYCRepositoryImpl
    ): KYCRepository
    
    @Binds
    @Singleton
    abstract fun bindAMLRepository(
        amlRepositoryImpl: AMLRepositoryImpl
    ): AMLRepository
    
    @Binds
    @Singleton
    abstract fun bindSanctionsRepository(
        sanctionsRepositoryImpl: SanctionsRepositoryImpl
    ): SanctionsRepository
    
    @Binds
    @Singleton
    abstract fun bindRiskRepository(
        riskRepositoryImpl: RiskRepositoryImpl
    ): RiskRepository
    
    @Binds
    @Singleton
    abstract fun bindCreditRepository(
        creditRepositoryImpl: CreditRepositoryImpl
    ): CreditRepository
    
    @Binds
    @Singleton
    abstract fun bindLoanRepository(
        loanRepositoryImpl: LoanRepositoryImpl
    ): LoanRepository
    
    @Binds
    @Singleton
    abstract fun bindInvestmentRepository(
        investmentRepositoryImpl: InvestmentRepositoryImpl
    ): InvestmentRepository
    
    @Binds
    @Singleton
    abstract fun bindInsuranceRepository(
        insuranceRepositoryImpl: InsuranceRepositoryImpl
    ): InsuranceRepository
    
    @Binds
    @Singleton
    abstract fun bindMortgageRepository(
        mortgageRepositoryImpl: MortgageRepositoryImpl
    ): MortgageRepository
    
    @Binds
    @Singleton
    abstract fun bindSavingsRepository(
        savingsRepositoryImpl: SavingsRepositoryImpl
    ): SavingsRepository
    
    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository
    
    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository
    
    @Binds
    @Singleton
    abstract fun bindRewardRepository(
        rewardRepositoryImpl: RewardRepositoryImpl
    ): RewardRepository
    
    @Binds
    @Singleton
    abstract fun bindLoyaltyRepository(
        loyaltyRepositoryImpl: LoyaltyRepositoryImpl
    ): LoyaltyRepository
    
    @Binds
    @Singleton
    abstract fun bindCashbackRepository(
        cashbackRepositoryImpl: CashbackRepositoryImpl
    ): CashbackRepository
    
    @Binds
    @Singleton
    abstract fun bindReferralRepository(
        referralRepositoryImpl: ReferralRepositoryImpl
    ): ReferralRepository
    
    @Binds
    @Singleton
    abstract fun bindPromotionRepository(
        promotionRepositoryImpl: PromotionRepositoryImpl
    ): PromotionRepository
    
    @Binds
    @Singleton
    abstract fun bindOfferRepository(
        offerRepositoryImpl: OfferRepositoryImpl
    ): OfferRepository
    
    @Binds
    @Singleton
    abstract fun bindMarketingRepository(
        marketingRepositoryImpl: MarketingRepositoryImpl
    ): MarketingRepository
    
    @Binds
    @Singleton
    abstract fun bindCampaignRepository(
        campaignRepositoryImpl: CampaignRepositoryImpl
    ): CampaignRepository
    
    @Binds
    @Singleton
    abstract fun bindSurveyRepository(
        surveyRepositoryImpl: SurveyRepositoryImpl
    ): SurveyRepository
    
    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(
        feedbackRepositoryImpl: FeedbackRepositoryImpl
    ): FeedbackRepository
    
    @Binds
    @Singleton
    abstract fun bindSupportRepository(
        supportRepositoryImpl: SupportRepositoryImpl
    ): SupportRepository
    
    @Binds
    @Singleton
    abstract fun bindHelpRepository(
        helpRepositoryImpl: HelpRepositoryImpl
    ): HelpRepository
    
    @Binds
    @Singleton
    abstract fun bindFAQRepository(
        faqRepositoryImpl: FAQRepositoryImpl
    ): FAQRepository
    
    @Binds
    @Singleton
    abstract fun bindTutorialRepository(
        tutorialRepositoryImpl: TutorialRepositoryImpl
    ): TutorialRepository
    
    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl
    ): OnboardingRepository
    
    @Binds
    @Singleton
    abstract fun bindWalkthroughRepository(
        walkthroughRepositoryImpl: WalkthroughRepositoryImpl
    ): WalkthroughRepository
    
    @Binds
    @Singleton
    abstract fun bindTipsRepository(
        tipsRepositoryImpl: TipsRepositoryImpl
    ): TipsRepository
    
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository
    
    @Binds
    @Singleton
    abstract fun bindUpdateRepository(
        updateRepositoryImpl: UpdateRepositoryImpl
    ): UpdateRepository
    
    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(
        maintenanceRepositoryImpl: MaintenanceRepositoryImpl
    ): MaintenanceRepository
    
    @Binds
    @Singleton
    abstract fun bindStatusRepository(
        statusRepositoryImpl: StatusRepositoryImpl
    ): StatusRepository
    
    @Binds
    @Singleton
    abstract fun bindVersionRepository(
        versionRepositoryImpl: VersionRepositoryImpl
    ): VersionRepository
    
    @Binds
    @Singleton
    abstract fun bindMigrationRepository(
        migrationRepositoryImpl: MigrationRepositoryImpl
    ): MigrationRepository
    
    @Binds
    @Singleton
    abstract fun bindCleanupRepository(
        cleanupRepositoryImpl: CleanupRepositoryImpl
    ): CleanupRepository
    
    @Binds
    @Singleton
    abstract fun bindOptimizationRepository(
        optimizationRepositoryImpl: OptimizationRepositoryImpl
    ): OptimizationRepository
}