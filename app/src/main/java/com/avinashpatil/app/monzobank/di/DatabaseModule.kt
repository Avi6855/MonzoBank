package com.avinashpatil.app.monzobank.di

import android.content.Context
import androidx.room.Room
import com.avinashpatil.app.monzobank.data.local.MonzoBankDatabase
import com.avinashpatil.app.monzobank.data.local.dao.AccountDao
import com.avinashpatil.app.monzobank.data.local.dao.CardDao
import com.avinashpatil.app.monzobank.data.local.dao.TransactionDao
import com.avinashpatil.app.monzobank.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideMonzoBankDatabase(
        @ApplicationContext context: Context
    ): MonzoBankDatabase {
        return Room.databaseBuilder(
            context,
            MonzoBankDatabase::class.java,
            "monzo_bank_database"
        ).build()
    }
    
    @Provides
    fun provideUserDao(database: MonzoBankDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideAccountDao(database: MonzoBankDatabase): AccountDao {
        return database.accountDao()
    }
    
    @Provides
    fun provideTransactionDao(database: MonzoBankDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    @Provides
    fun provideCardDao(database: MonzoBankDatabase): CardDao {
        return database.cardDao()
    }
}