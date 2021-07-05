package com.aleksejantonov.tajikair

import android.app.Application
import com.aleksejantonov.tajikair.di.DI
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DI.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}