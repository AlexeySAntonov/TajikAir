package com.aleksejantonov.tajikair.ui.map.render

import android.content.Context
import android.view.LayoutInflater
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class DotMarkerRenderer(
    private val context: Context,
    private val map: GoogleMap,
    private val clusterManager: ClusterManager<DotMarker>
) : DefaultClusterRenderer<DotMarker>(context, map, clusterManager) {

    private val markerGenerator = IconGenerator(context)

    init {
        setupClusterManager()
        setupMarker()
        minClusterSize = 100
    }

    override fun onBeforeClusterItemRendered(item: DotMarker, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerGenerator.makeIcon()))
        markerOptions.anchor(0.5f,0.5f)
    }

    fun render(dots: List<LatLng>) {
        clusterManager.clearItems()
        dots.forEach { dot ->
            clusterManager.addItem(
                DotMarker(LatLng(dot.latitude, dot.longitude))
            )
            clusterManager.cluster()
        }
    }

    private fun setupClusterManager() {
        with(clusterManager) {
            renderer = this@DotMarkerRenderer
            map.setOnCameraIdleListener(this)
            map.setOnMarkerClickListener(this)
        }
    }

    private fun setupMarker() {
        val markerView = LayoutInflater.from(context).inflate(R.layout.view_dot_marker, null)
        markerGenerator.setContentView(markerView)
        markerGenerator.setBackground(null)
    }

}