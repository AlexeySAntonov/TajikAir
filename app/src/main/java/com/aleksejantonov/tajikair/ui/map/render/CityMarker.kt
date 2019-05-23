package com.aleksejantonov.tajikair.ui.map.render

import com.aleksejantonov.tajikair.api.entity.City
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class CityMarker(val city: City) : ClusterItem {

    override fun getSnippet() = city.iata.first()

    override fun getTitle() = ""

    override fun getPosition() = LatLng(
        requireNotNull(city.latLng?.latitude),
        requireNotNull(city.latLng?.longitude)
    )
}