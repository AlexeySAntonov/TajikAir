package com.aleksejantonov.tajikair.model

import com.aleksejantonov.tajikair.api.YasenHotelService
import com.aleksejantonov.tajikair.api.entity.City
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CitiesRepository(private val service: YasenHotelService) {

    fun searchCities(query: String): Single<List<City>> =
        service.getCities(term = query)
            .map { it.cities.filter { city -> city.iata.isNotEmpty() } }
            .subscribeOn(Schedulers.io())
}