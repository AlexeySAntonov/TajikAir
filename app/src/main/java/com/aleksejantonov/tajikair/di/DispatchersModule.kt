package com.aleksejantonov.tajikair.di

import com.aleksejantonov.tajikair.di.qualifiers.DispatcherDefault
import com.aleksejantonov.tajikair.di.qualifiers.DispatcherIO
import com.aleksejantonov.tajikair.di.qualifiers.DispatcherMain
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class DispatchersModule {

  @Provides
  @Singleton
  @DispatcherMain
  fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

  @Provides
  @Singleton
  @DispatcherIO
  fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Singleton
  @DispatcherDefault
  fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}