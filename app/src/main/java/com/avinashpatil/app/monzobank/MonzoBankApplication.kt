package com.avinashpatil.app.monzobank

import android.app.Application
import com.avinashpatil.app.monzobank.BuildConfig
// import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

// @HiltAndroidApp
class MonzoBankApplication : Application() {
    
    companion object {
        const val SESSION_TIMEOUT_MINUTES = 15
        const val BIOMETRIC_TIMEOUT_SECONDS = 30
        const val MAX_LOGIN_ATTEMPTS = 5
        const val LOCKOUT_DURATION_MINUTES = 30
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("MonzoBankApplication initialized")
    }
}