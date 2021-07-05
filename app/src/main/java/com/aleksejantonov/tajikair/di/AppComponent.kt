package com.aleksejantonov.tajikair.di

import android.app.Application
import com.aleksejantonov.tajikair.navigation.AppRouter
import com.aleksejantonov.tajikair.ui.base.ViewModelFactoryProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    AppModule::class,
    ViewModelModule::class,
    DispatchersModule::class,
  ]
)
interface AppComponent : ViewModelFactoryProvider {

  fun appRouter(): AppRouter

  @Component.Builder
  interface Builder {

    @BindsInstance
    fun application(app: Application): Builder

    fun build(): AppComponent
  }

  companion object {

    fun init(app: Application): AppComponent {
      return DaggerAppComponent.builder()
        .application(app)
        .build()
    }
  }
}
