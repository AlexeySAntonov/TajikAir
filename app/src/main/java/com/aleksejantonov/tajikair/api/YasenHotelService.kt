package com.aleksejantonov.tajikair.api

import com.aleksejantonov.tajikair.api.config.Constants.LANGUAGE
import com.aleksejantonov.tajikair.api.config.Constants.TERM
import com.aleksejantonov.tajikair.api.config.Methods.AUTOCOMPLETE
import com.aleksejantonov.tajikair.api.entity.CitiesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YasenHotelService {

    @GET(AUTOCOMPLETE)
    suspend fun getCities(
        @Query(TERM) term: String,
        @Query(LANGUAGE) language: String = "ru"
    ): CitiesResponse
}