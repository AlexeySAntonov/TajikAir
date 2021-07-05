package com.aleksejantonov.tajikair.di

import android.app.Application

object DI {

  @Volatile
  lateinit var appComponent: AppComponent

  fun init(app: Application) {
    if (!this::appComponent.isInitialized) {
      synchronized(this) {
        if (!this::appComponent.isInitialized) {
          appComponent = AppComponent.init(app)
        }
      }
    }
  }

}