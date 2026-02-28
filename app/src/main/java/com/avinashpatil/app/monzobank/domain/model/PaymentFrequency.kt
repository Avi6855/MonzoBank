package com.avinashpatil.app.monzobank.domain.model

/**
 * Payment frequency enumeration
 * Defines how often recurring payments should occur
 */
enum class PaymentFrequency {
    // Standard frequencies
    ONCE,              // One-time payment (not recurring)
    DAILY,             // Every day
    WEEKLY,            // Every week
    FORTNIGHTLY,       // Every two weeks (bi-weekly)
    MONTHLY,           // Every month
    QUARTERLY,         // Every three months
    SEMI_ANNUALLY,     // Every six months
    ANNUALLY,          // Every year
    
    // Business frequencies
    BUSINESS_DAILY,    // Every business day (Mon-Fri)
    BUSINESS_WEEKLY,   // Every business week
    BUSINESS_MONTHLY,  // Every business month
    
    // Specific day frequencies
    FIRST_OF_MONTH,    // First day of every month
    LAST_OF_MONTH,     // Last day of every month
    FIFTEENTH_OF_MONTH, // 15th of every month
    
    // Custom intervals
    EVERY_2_DAYS,      // Every 2 days
    EVERY_3_DAYS,      // Every 3 days
    EVERY_2_WEEKS,     // Every 2 weeks (same as FORTNIGHTLY)
    EVERY_3_WEEKS,     // Every 3 weeks
    EVERY_4_WEEKS,     // Every 4 weeks
    EVERY_2_MONTHS,    // Every 2 months (bi-monthly)
    EVERY_3_MONTHS,    // Every 3 months (same as QUARTERLY)
    EVERY_4_MONTHS,    // Every 4 months
    EVERY_6_MONTHS,    // Every 6 months (same as SEMI_ANNUALLY)
    EVERY_2_YEARS,     // Every 2 years (bi-annually)
    
    // Specific weekday frequencies
    EVERY_MONDAY,      // Every Monday
    EVERY_TUESDAY,     // Every Tuesday
    EVERY_WEDNESDAY,   // Every Wednesday
    EVERY_THURSDAY,    // Every Thursday
    EVERY_FRIDAY,      // Every Friday
    EVERY_SATURDAY,    // Every Saturday
    EVERY_SUNDAY,      // Every Sunday
    
    // Multiple times per period
    TWICE_DAILY,       // Twice per day
    TWICE_WEEKLY,      // Twice per week
    TWICE_MONTHLY,     // Twice per month
    TWICE_YEARLY,      // Twice per year
    
    // Irregular frequencies
    ON_DEMAND,         // When requested
    EVENT_TRIGGERED,   // Triggered by specific events
    CONDITIONAL,       // Based on conditions
    
    // Custom frequency
    CUSTOM             // Custom frequency defined by user
}

/**
 * Extension function to get display name for PaymentFrequency
 */
fun PaymentFrequency.displayName(): String {
    return when (this) {
        PaymentFrequency.ONCE -> "One-time"
        PaymentFrequency.DAILY -> "Daily"
        PaymentFrequency.WEEKLY -> "Weekly"
        PaymentFrequency.FORTNIGHTLY -> "Fortnightly"
        PaymentFrequency.MONTHLY -> "Monthly"
        PaymentFrequency.QUARTERLY -> "Quarterly"
        PaymentFrequency.SEMI_ANNUALLY -> "Semi-annually"
        PaymentFrequency.ANNUALLY -> "Annually"
        PaymentFrequency.BUSINESS_DAILY -> "Business Daily"
        PaymentFrequency.BUSINESS_WEEKLY -> "Business Weekly"
        PaymentFrequency.BUSINESS_MONTHLY -> "Business Monthly"
        PaymentFrequency.FIRST_OF_MONTH -> "First of Month"
        PaymentFrequency.LAST_OF_MONTH -> "Last of Month"
        PaymentFrequency.FIFTEENTH_OF_MONTH -> "15th of Month"
        PaymentFrequency.EVERY_2_DAYS -> "Every 2 Days"
        PaymentFrequency.EVERY_3_DAYS -> "Every 3 Days"
        PaymentFrequency.EVERY_2_WEEKS -> "Every 2 Weeks"
        PaymentFrequency.EVERY_3_WEEKS -> "Every 3 Weeks"
        PaymentFrequency.EVERY_4_WEEKS -> "Every 4 Weeks"
        PaymentFrequency.EVERY_2_MONTHS -> "Every 2 Months"
        PaymentFrequency.EVERY_3_MONTHS -> "Every 3 Months"
        PaymentFrequency.EVERY_4_MONTHS -> "Every 4 Months"
        PaymentFrequency.EVERY_6_MONTHS -> "Every 6 Months"
        PaymentFrequency.EVERY_2_YEARS -> "Every 2 Years"
        PaymentFrequency.EVERY_MONDAY -> "Every Monday"
        PaymentFrequency.EVERY_TUESDAY -> "Every Tuesday"
        PaymentFrequency.EVERY_WEDNESDAY -> "Every Wednesday"
        PaymentFrequency.EVERY_THURSDAY -> "Every Thursday"
        PaymentFrequency.EVERY_FRIDAY -> "Every Friday"
        PaymentFrequency.EVERY_SATURDAY -> "Every Saturday"
        PaymentFrequency.EVERY_SUNDAY -> "Every Sunday"
        PaymentFrequency.TWICE_DAILY -> "Twice Daily"
        PaymentFrequency.TWICE_WEEKLY -> "Twice Weekly"
        PaymentFrequency.TWICE_MONTHLY -> "Twice Monthly"
        PaymentFrequency.TWICE_YEARLY -> "Twice Yearly"
        PaymentFrequency.ON_DEMAND -> "On Demand"
        PaymentFrequency.EVENT_TRIGGERED -> "Event Triggered"
        PaymentFrequency.CONDITIONAL -> "Conditional"
        PaymentFrequency.CUSTOM -> "Custom"
    }
}