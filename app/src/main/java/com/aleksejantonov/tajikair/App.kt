package com.aleksejantonov.tajikair

import android.app.Application
import com.aleksejantonov.tajikair.sl.SL
import com.facebook.stetho.Stetho
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SL.init()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }
}