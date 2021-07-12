package com.aleksejantonov.tajikair.ui.main.search

import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.api.entity.LatLng

data class CityItem(
  val code: String,
  val fullName: String,
  val latLng: LatLng?,
  val iata: List<String>
) {

  companion object {

    fun from(city: City) = with(city) {
      CityItem(
        code = code,
        fullName = fullName,
        latLng = latLng,
        iata
      )
    }

    fun CityItem.toCity() = City(code, fullName, latLng, iata)
  }
}