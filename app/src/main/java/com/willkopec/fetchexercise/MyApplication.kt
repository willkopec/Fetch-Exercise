package com.willkopec.fetchexercise

import android.app.Application
import com.willkopec.fetchexercise.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}