package com.avinashpatil.app.monzobank.di

import com.avinashpatil.app.monzobank.BuildConfig
import com.avinashpatil.app.monzobank.data.remote.api.*
import com.avinashpatil.app.monzobank.data.remote.interceptor.AuthInterceptor
import com.avinashpatil.app.monzobank.data.remote.interceptor.ErrorInterceptor
import com.avinashpatil.app.monzobank.data.remote.interceptor.LoggingInterceptor
import com.avinashpatil.app.monzobank.data.remote.interceptor.NetworkInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://api.monzo.com/"
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: com.avinashpatil.app.monzobank.data.local.TokenManager
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideErrorInterceptor(): ErrorInterceptor {
        return ErrorInterceptor()
    }
    
    @Provides
    @Singleton
    fun provideNetworkInterceptor(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): NetworkInterceptor {
        return NetworkInterceptor(context)
    }
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }
    
    @Provides
    @Singleton
    @PublicClient
    fun providePublicOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        errorInterceptor: ErrorInterceptor,
        networkInterceptor: NetworkInterceptor,
        loggingInterceptor: LoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(errorInterceptor as Interceptor)
            .addInterceptor(networkInterceptor as Interceptor)
            .addInterceptor(loggingInterceptor as Interceptor)
            .addInterceptor(httpLoggingInterceptor as Interceptor)
            .build()
    }
    
    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        networkInterceptor: NetworkInterceptor,
        loggingInterceptor: LoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor as Interceptor)
            .addInterceptor(errorInterceptor as Interceptor)
            .addInterceptor(networkInterceptor as Interceptor)
            .addInterceptor(loggingInterceptor as Interceptor)
            .addInterceptor(httpLoggingInterceptor as Interceptor)
            .build()
    }
    
    @Provides
    @Singleton
    @PublicClient
    fun providePublicRetrofit(
        @PublicClient okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedRetrofit(
        @AuthenticatedClient okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    // API Services
    
    @Provides
    @Singleton
    fun provideAuthApiService(
        @PublicClient retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideUserApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAccountApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): AccountApiService {
        return retrofit.create(AccountApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideTransactionApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): TransactionApiService {
        return retrofit.create(TransactionApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCardApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): CardApiService {
        return retrofit.create(CardApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun providePaymentApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideNotificationApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideSecurityApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): SecurityApiService {
        return retrofit.create(SecurityApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAnalyticsApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): AnalyticsApiService {
        return retrofit.create(AnalyticsApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideExternalApiService(
        @AuthenticatedClient retrofit: Retrofit
    ): ExternalApiService {
        return retrofit.create(ExternalApiService::class.java)
    }
}