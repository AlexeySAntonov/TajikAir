package com.aleksejantonov.tajikair.model

import com.aleksejantonov.tajikair.api.YasenHotelService
import com.aleksejantonov.tajikair.api.entity.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CitiesRepository(private val service: YasenHotelService) {

    fun searchCities(query: String): Flow<List<City>> = flow {
        val response = service.getCities(term = query)
        emit(response.cities.filter { city -> city.iata.isNotEmpty() })
    }

}