package com.avinashpatil.app.monzobank.domain.model

import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import java.time.LocalDateTime

data class User(
    val id: String,
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String? = null,
    val profileImageUrl: String? = null,
    val address: Address? = null,
    val kycStatus: KYCStatus = KYCStatus.PENDING,
    val accountType: AccountType = AccountType.CURRENT,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastLoginAt: LocalDateTime? = null,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val preferences: UserPreferences? = null,
    val securitySettings: SecuritySettings? = null,
    val notificationSettings: NotificationSettings? = null
) {
    val fullName: String
        get() = "$firstName $lastName"
    
    val initials: String
        get() = "${firstName.firstOrNull()?.uppercase()}${lastName.firstOrNull()?.uppercase()}"
    
    val isVerified: Boolean
        get() = isEmailVerified && isPhoneVerified && kycStatus == KYCStatus.APPROVED
}

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)



data class SecurityQuestion(
    val id: String,
    val question: String,
    val answerHash: String
)

enum class KYCStatus {
    PENDING,
    IN_PROGRESS,
    APPROVED,
    REJECTED,
    EXPIRED,
    DOCUMENTS_REQUIRED
}