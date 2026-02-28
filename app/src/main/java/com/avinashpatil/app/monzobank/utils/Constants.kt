package com.avinashpatil.app.monzobank.utils

/**
 * Application constants
 */
object Constants {
    
    // API Configuration
    const val BASE_URL = "https://api.monzobank.com/api/"
    const val API_TIMEOUT = 30L // seconds
    
    // Database Configuration
    const val DATABASE_NAME = "monzo_bank_db"
    const val DATABASE_VERSION = 1
    
    // SharedPreferences
    const val ENCRYPTED_PREFS_NAME = "monzo_secure_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"
    const val PREF_TOKEN_EXPIRES = "token_expires"
    const val PREF_TOKEN_TYPE = "token_type"
    const val PREF_BIOMETRIC_ENABLED = "biometric_enabled"
    const val PREF_FIRST_LAUNCH = "first_launch"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_LANGUAGE = "language"
    const val PREF_CURRENCY = "currency"
    
    // Biometric Authentication
    const val BIOMETRIC_KEY_NAME = "MonzoBankBiometricKey"
    const val BIOMETRIC_REQUEST_CODE = 1001
    
    // Animation Durations
    const val ANIMATION_DURATION_SHORT = 300L
    const val ANIMATION_DURATION_MEDIUM = 500L
    const val ANIMATION_DURATION_LONG = 1000L
    
    // UI Constants
    const val SPLASH_DELAY = 2000L
    const val DEBOUNCE_DELAY = 500L
    const val PAGINATION_SIZE = 20
    
    // Validation Constants
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 128
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 50
    const val OTP_LENGTH = 6
    const val OTP_EXPIRY_MINUTES = 5
    
    // Currency Codes
    const val CURRENCY_GBP = "GBP"
    const val CURRENCY_USD = "USD"
    const val CURRENCY_EUR = "EUR"
    const val CURRENCY_INR = "INR"
    
    // Account Types
    const val ACCOUNT_TYPE_CURRENT = "CURRENT"
    const val ACCOUNT_TYPE_SAVINGS = "SAVINGS"
    const val ACCOUNT_TYPE_BUSINESS = "BUSINESS"
    const val ACCOUNT_TYPE_JOINT = "JOINT"
    
    // Transaction Categories
    const val CATEGORY_GROCERIES = "Groceries"
    const val CATEGORY_TRANSPORT = "Transport"
    const val CATEGORY_ENTERTAINMENT = "Entertainment"
    const val CATEGORY_BILLS = "Bills"
    const val CATEGORY_SHOPPING = "Shopping"
    const val CATEGORY_RESTAURANTS = "Restaurants"
    const val CATEGORY_HEALTH = "Health"
    const val CATEGORY_EDUCATION = "Education"
    const val CATEGORY_OTHER = "Other"
    
    // Transaction Status
    const val TRANSACTION_STATUS_PENDING = "PENDING"
    const val TRANSACTION_STATUS_COMPLETED = "COMPLETED"
    const val TRANSACTION_STATUS_FAILED = "FAILED"
    const val TRANSACTION_STATUS_CANCELLED = "CANCELLED"
    
    // Card Types
    const val CARD_TYPE_DEBIT = "DEBIT"
    const val CARD_TYPE_CREDIT = "CREDIT"
    const val CARD_TYPE_VIRTUAL = "VIRTUAL"
    
    // KYC Status
    const val KYC_STATUS_PENDING = "PENDING"
    const val KYC_STATUS_VERIFIED = "VERIFIED"
    const val KYC_STATUS_REJECTED = "REJECTED"
    
    // Investment Asset Types
    const val ASSET_TYPE_STOCK = "STOCK"
    const val ASSET_TYPE_ETF = "ETF"
    const val ASSET_TYPE_CRYPTO = "CRYPTO"
    const val ASSET_TYPE_BOND = "BOND"
    
    // Loan Types
    const val LOAN_TYPE_PERSONAL = "PERSONAL"
    const val LOAN_TYPE_MORTGAGE = "MORTGAGE"
    const val LOAN_TYPE_AUTO = "AUTO"
    const val LOAN_TYPE_BUSINESS = "BUSINESS"
    
    // Insurance Types
    const val INSURANCE_TYPE_TRAVEL = "TRAVEL"
    const val INSURANCE_TYPE_PHONE = "PHONE"
    const val INSURANCE_TYPE_HEALTH = "HEALTH"
    const val INSURANCE_TYPE_LIFE = "LIFE"
    
    // Notification Types
    const val NOTIFICATION_TYPE_TRANSACTION = "TRANSACTION"
    const val NOTIFICATION_TYPE_SECURITY = "SECURITY"
    const val NOTIFICATION_TYPE_MARKETING = "MARKETING"
    const val NOTIFICATION_TYPE_SYSTEM = "SYSTEM"
    
    // Error Codes
    const val ERROR_NETWORK = "NETWORK_ERROR"
    const val ERROR_UNAUTHORIZED = "UNAUTHORIZED"
    const val ERROR_VALIDATION = "VALIDATION_ERROR"
    const val ERROR_SERVER = "SERVER_ERROR"
    const val ERROR_BIOMETRIC = "BIOMETRIC_ERROR"
    
    // Date Formats
    const val DATE_FORMAT_API = "yyyy-MM-dd"
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATETIME_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATETIME_FORMAT_DISPLAY = "dd MMM yyyy, HH:mm"
    
    // File Upload
    const val MAX_FILE_SIZE_MB = 10
    const val ALLOWED_IMAGE_TYPES = "image/jpeg,image/png,image/jpg"
    const val ALLOWED_DOCUMENT_TYPES = "application/pdf,image/jpeg,image/png"
    
    // Security
    const val MAX_LOGIN_ATTEMPTS = 5
    const val LOCKOUT_DURATION_MINUTES = 15
    const val SESSION_TIMEOUT_MINUTES = 30
    
    // Regex Patterns
    const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    const val PHONE_PATTERN = "^\\+?[1-9]\\d{1,14}$"
    const val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    
    // Deep Links
    const val DEEP_LINK_SCHEME = "monzobank"
    const val DEEP_LINK_HOST = "app"
    
    // Feature Flags
    const val FEATURE_BIOMETRIC_AUTH = "biometric_auth"
    const val FEATURE_INVESTMENTS = "investments"
    const val FEATURE_BUSINESS_BANKING = "business_banking"
    const val FEATURE_INSURANCE = "insurance"
    
    // Default Values
    const val DEFAULT_CURRENCY = CURRENCY_GBP
    const val DEFAULT_LANGUAGE = "en"
    const val DEFAULT_COUNTRY = "GB"
    const val DEFAULT_CARD_DAILY_LIMIT = 500.00
    const val DEFAULT_CARD_MONTHLY_LIMIT = 2000.00
    
    // Pot Colors (Hex values)
    val POT_COLORS = listOf(
        "#FF5733", // Monzo Coral
        "#4CAF50", // Green
        "#2196F3", // Blue
        "#FF9800", // Orange
        "#9C27B0", // Purple
        "#F44336", // Red
        "#009688", // Teal
        "#795548", // Brown
        "#607D8B", // Blue Grey
        "#E91E63"  // Pink
    )
    
    // Pot Emojis
    val POT_EMOJIS = listOf(
        "🎯", "💰", "🏠", "🚗", "✈️", "🎓", "💍", "🎮", "📱", "👕",
        "🍕", "☕", "🎬", "🏋️", "🎸", "📚", "🎨", "🌟", "💎", "🔥"
    )
}