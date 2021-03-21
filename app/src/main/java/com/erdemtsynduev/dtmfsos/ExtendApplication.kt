package com.erdemtsynduev.dtmfsos

import android.app.Application
import timber.log.Timber

class ExtendApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Add logging app
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}