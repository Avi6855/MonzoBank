package com.avinashpatil.app.monzobank.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.avinashpatil.app.monzobank.domain.model.Address
import com.avinashpatil.app.monzobank.domain.model.KYCStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Room entity for User table
 * Based on the technical architecture database schema
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phone"], unique = true),
        Index(value = ["kyc_status"])
    ]
)
@TypeConverters(UserConverters::class)
data class UserEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "phone")
    val phone: String,
    
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    
    @ColumnInfo(name = "first_name")
    val firstName: String,
    
    @ColumnInfo(name = "last_name")
    val lastName: String,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: LocalDate,
    
    @ColumnInfo(name = "address")
    val address: Address,
    
    @ColumnInfo(name = "kyc_status")
    val kycStatus: KYCStatus = KYCStatus.PENDING,
    
    @ColumnInfo(name = "biometric_enabled")
    val biometricEnabled: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)

/**
 * Type converters for Room database
 */
class UserConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromAddress(address: Address): String {
        return gson.toJson(address)
    }
    
    @TypeConverter
    fun toAddress(addressString: String): Address {
        val type = object : TypeToken<Address>() {}.type
        return gson.fromJson(addressString, type)
    }
    
    @TypeConverter
    fun fromKycStatus(status: KYCStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toKycStatus(status: String): KYCStatus {
        return KYCStatus.valueOf(status)
    }
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it) }
    }
}

/**
 * Entity for storing authentication tokens locally
 */
@Entity(
    tableName = "auth_tokens",
    indices = [Index(value = ["user_id"], unique = true)]
)
data class AuthTokenEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "access_token")
    val accessToken: String,
    
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,
    
    @ColumnInfo(name = "expires_at")
    val expiresAt: LocalDateTime,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
)

/**
 * Entity for storing biometric authentication data
 */
@Entity(
    tableName = "biometric_auth",
    indices = [Index(value = ["user_id"], unique = true)]
)
data class BiometricAuthEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "biometric_key")
    val biometricKey: String,
    
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "last_used")
    val lastUsed: LocalDateTime?
)