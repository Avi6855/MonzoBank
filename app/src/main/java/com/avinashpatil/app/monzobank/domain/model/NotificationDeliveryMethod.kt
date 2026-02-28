package com.avinashpatil.app.monzobank.domain.model

/**
 * Notification delivery method enumeration
 * Defines the different ways notifications can be delivered
 */
enum class NotificationDeliveryMethod {
    PUSH,           // Push notification
    EMAIL,          // Email notification
    SMS,            // SMS notification
    IN_APP,         // In-app notification
    WEBHOOK,        // Webhook delivery
    SLACK,          // Slack integration
    TEAMS,          // Microsoft Teams integration
    DISCORD,        // Discord integration
    TELEGRAM,       // Telegram bot
    WHATSAPP,       // WhatsApp Business API
    VOICE_CALL,     // Voice call notification
    DESKTOP,        // Desktop notification
    BROWSER,        // Browser notification
    WIDGET,         // Widget notification
    BANNER,         // Banner notification
    POPUP,          // Popup notification
    TOAST,          // Toast notification
    BADGE,          // Badge notification
    SOUND,          // Sound notification
    VIBRATION,      // Vibration notification
    LED,            // LED notification
    SCREEN_ON,      // Screen on notification
    LOCK_SCREEN,    // Lock screen notification
    STATUS_BAR,     // Status bar notification
    NOTIFICATION_CENTER, // Notification center
    WATCH,          // Smartwatch notification
    TV,             // Smart TV notification
    CAR,            // Car integration notification
    IOT_DEVICE,     // IoT device notification
    API_CALLBACK,   // API callback
    DATABASE_LOG,   // Database logging
    FILE_LOG,       // File logging
    ANALYTICS,      // Analytics tracking
    MONITORING,     // System monitoring
    AUDIT_LOG,      // Audit logging
    CUSTOM          // Custom delivery method
}