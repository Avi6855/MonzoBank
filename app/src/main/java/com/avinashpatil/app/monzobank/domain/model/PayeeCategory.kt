package com.avinashpatil.app.monzobank.domain.model

/**
 * Enum representing different categories of payees
 */
enum class PayeeCategory {
    UTILITIES,
    RENT_MORTGAGE,
    INSURANCE,
    LOANS,
    CREDIT_CARDS,
    SUBSCRIPTIONS,
    HEALTHCARE,
    EDUCATION,
    GOVERNMENT,
    CHARITY,
    FAMILY_FRIENDS,
    BUSINESS,
    INVESTMENT,
    OTHER
}

/**
 * Extension function to get display name for PayeeCategory
 */
fun PayeeCategory.displayName(): String {
    return when (this) {
        PayeeCategory.UTILITIES -> "Utilities"
        PayeeCategory.RENT_MORTGAGE -> "Rent & Mortgage"
        PayeeCategory.INSURANCE -> "Insurance"
        PayeeCategory.LOANS -> "Loans"
        PayeeCategory.CREDIT_CARDS -> "Credit Cards"
        PayeeCategory.SUBSCRIPTIONS -> "Subscriptions"
        PayeeCategory.HEALTHCARE -> "Healthcare"
        PayeeCategory.EDUCATION -> "Education"
        PayeeCategory.GOVERNMENT -> "Government"
        PayeeCategory.CHARITY -> "Charity"
        PayeeCategory.FAMILY_FRIENDS -> "Family & Friends"
        PayeeCategory.BUSINESS -> "Business"
        PayeeCategory.INVESTMENT -> "Investment"
        PayeeCategory.OTHER -> "Other"
    }
}

/**
 * Extension function to get icon name for PayeeCategory
 */
fun PayeeCategory.iconName(): String {
    return when (this) {
        PayeeCategory.UTILITIES -> "zap"
        PayeeCategory.RENT_MORTGAGE -> "home"
        PayeeCategory.INSURANCE -> "shield"
        PayeeCategory.LOANS -> "credit-card"
        PayeeCategory.CREDIT_CARDS -> "credit-card"
        PayeeCategory.SUBSCRIPTIONS -> "repeat"
        PayeeCategory.HEALTHCARE -> "heart"
        PayeeCategory.EDUCATION -> "book"
        PayeeCategory.GOVERNMENT -> "building"
        PayeeCategory.CHARITY -> "heart"
        PayeeCategory.FAMILY_FRIENDS -> "users"
        PayeeCategory.BUSINESS -> "briefcase"
        PayeeCategory.INVESTMENT -> "trending-up"
        PayeeCategory.OTHER -> "more-horizontal"
    }
}