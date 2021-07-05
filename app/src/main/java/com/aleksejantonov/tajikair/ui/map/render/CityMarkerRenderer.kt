package com.aleksejantonov.tajikair.ui.map.render

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.widget.TextView
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.util.getScreenWidth
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class CityMarkerRenderer(
    private val context: Context,
    private val map: GoogleMap,
    private val clusterManager: ClusterManager<CityMarker>
) : DefaultClusterRenderer<CityMarker>(context, map, clusterManager) {

    private val markerGenerator = IconGenerator(context)
    private var iataText: TextView? = null

    init {
        setupClusterManager()
        setupMarker()
    }

    override fun onBeforeClusterItemRendered(item: CityMarker, markerOptions: MarkerOptions) {
        iataText?.text = item.snippet
        markerOptions.apply {
            icon(BitmapDescriptorFactory.fromBitmap(markerGenerator.makeIcon()))
            anchor(0.5f, 0.5f)
            zIndex(2f)
        }
    }

    fun render(cities: List<City>) {
        val boundsBuilder = LatLngBounds.Builder()
        clusterManager.clearItems()
        cities.forEach { city ->
            clusterManager.addItem(CityMarker(city))
            clusterManager.cluster()

            boundsBuilder.include(
                LatLng(
                    requireNotNull(city.latLng?.latitude),
                    requireNotNull(city.latLng?.longitude)
                )
            )
        }
        val bounds = boundsBuilder.build()
        val padding = when {
            context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> context.getScreenWidth() / 4
            else                                                                              -> context.getScreenWidth() / 8
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        map.animateCamera(cameraUpdate)
    }

    private fun setupClusterManager() {
        with(clusterManager) {
            renderer = this@CityMarkerRenderer
            map.setOnCameraIdleListener(this)
            map.setOnMarkerClickListener(this)
            setOnClusterItemClickListener { cityMarker ->
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            requireNotNull(cityMarker.city.latLng?.latitude),
                            requireNotNull(cityMarker.city.latLng?.longitude)
                        ),
                        17F
                    )
                )
                true
            }
        }
    }

    private fun setupMarker() {
        val markerView = LayoutInflater.from(context).inflate(R.layout.view_city_marker, null)
        iataText = markerView.findViewById(R.id.iata)
        markerGenerator.setContentView(markerView)
        markerGenerator.setBackground(null)
    }

}