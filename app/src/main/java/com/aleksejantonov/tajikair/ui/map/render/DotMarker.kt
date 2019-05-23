package com.aleksejantonov.tajikair.ui.map.render

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class DotMarker(private val latLng: com.aleksejantonov.tajikair.api.entity.LatLng) : ClusterItem {

    override fun getSnippet() = ""

    override fun getTitle() = ""

    override fun getPosition() = LatLng(
        requireNotNull(latLng.latitude),
        requireNotNull(latLng.longitude)
    )
}