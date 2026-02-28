package com.avinashpatil.app.monzobank.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.avinashpatil.app.monzobank.data.local.dao.*
import com.avinashpatil.app.monzobank.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        TransactionEntity::class,
        CardEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MonzoBankDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun cardDao(): CardDao
    
    companion object {
        @Volatile
        private var INSTANCE: MonzoBankDatabase? = null
        
        fun getDatabase(context: Context): MonzoBankDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MonzoBankDatabase::class.java,
                    "monzo_bank_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}