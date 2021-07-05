package com.aleksejantonov.tajikair.di

import com.aleksejantonov.tajikair.api.YasenHotelService
import com.aleksejantonov.tajikair.api.config.Constants
import com.aleksejantonov.tajikair.model.CitiesRepository
import com.aleksejantonov.tajikair.navigation.AppRouter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

  @Provides
  @Singleton
  fun providesRouter(): AppRouter = AppRouter()

  @Provides
  @Singleton
  fun providesHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .build()
  }

  @Provides
  @Singleton
  fun providesService(client: OkHttpClient): YasenHotelService {
    return Retrofit.Builder()
      .client(client)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl(Constants.BASE_URL)
      .build()
      .create(YasenHotelService::class.java)
  }

  @Provides
  @Singleton
  fun providesCitiesRepository(service: YasenHotelService): CitiesRepository {
    return CitiesRepository(service)
  }

}