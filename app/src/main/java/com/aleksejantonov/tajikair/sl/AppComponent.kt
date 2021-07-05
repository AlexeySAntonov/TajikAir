package com.aleksejantonov.tajikair.sl

import com.aleksejantonov.tajikair.api.YasenHotelService
import com.aleksejantonov.tajikair.api.config.Constants
import com.aleksejantonov.tajikair.model.CitiesRepository
import com.aleksejantonov.tajikair.navigation.AppRouter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AppComponent {

    private val client by lazy {
        OkHttpClient.Builder()
            .build()
    }

    val router by lazy { AppRouter() }

    val service by lazy {
        Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(YasenHotelService::class.java)
    }

    val citiesRepository by lazy { CitiesRepository(service) }
}
