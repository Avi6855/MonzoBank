package com.avinashpatil.app.monzobank.data.local

import androidx.room.TypeConverter
import com.avinashpatil.app.monzobank.data.local.entity.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.Date

class Converters {
    
    private val gson = Gson()
    
    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    // BigDecimal converters
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }
    
    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }
    
    // AccountType converters
    @TypeConverter
    fun fromAccountType(accountType: AccountType?): String? {
        return accountType?.name
    }
    
    @TypeConverter
    fun toAccountType(accountType: String?): AccountType? {
        return accountType?.let { AccountType.valueOf(it) }
    }
    
    // TransactionStatus converters
    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toTransactionStatus(status: String?): TransactionStatus? {
        return status?.let { TransactionStatus.valueOf(it) }
    }
    
    // TransactionType converters
    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }
    
    @TypeConverter
    fun toTransactionType(type: String?): TransactionType? {
        return type?.let { TransactionType.valueOf(it) }
    }
    
    // CardType converters
    @TypeConverter
    fun fromCardType(cardType: CardType?): String? {
        return cardType?.name
    }
    
    @TypeConverter
    fun toCardType(cardType: String?): CardType? {
        return cardType?.let { CardType.valueOf(it) }
    }
    
    // DeliveryStatus converters
    @TypeConverter
    fun fromDeliveryStatus(status: DeliveryStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toDeliveryStatus(status: String?): DeliveryStatus? {
        return status?.let { DeliveryStatus.valueOf(it) }
    }
    
    // JSON object converters for complex types
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return value?.let {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    // List converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}