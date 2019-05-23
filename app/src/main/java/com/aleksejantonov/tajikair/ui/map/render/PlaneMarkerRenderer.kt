package com.aleksejantonov.tajikair.ui.map.render

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class PlaneMarkerRenderer(
    private val context: Context,
    private val map: GoogleMap,
    private val clusterManager: ClusterManager<PlaneMarker>
) : DefaultClusterRenderer<PlaneMarker>(context, map, clusterManager) {

    private val markerGenerator = IconGenerator(context)
    private var plane: ImageView? = null
    private var angle: Float = 0f

    init {
        setupClusterManager()
        setupMarker()
    }

    override fun onBeforeClusterItemRendered(item: PlaneMarker, markerOptions: MarkerOptions) {
        plane?.rotation = angle
        markerOptions.apply {
            icon(BitmapDescriptorFactory.fromBitmap(markerGenerator.makeIcon()))
            anchor(0.5f, 0.5f)
            zIndex(1f)
        }

    }

    fun render(latLng: LatLng, angle: Float) {
        this.angle = angle
        clusterManager.clearItems()
        clusterManager.addItem(
            PlaneMarker(LatLng(latLng.latitude, latLng.longitude))
        )
        clusterManager.cluster()
    }

    private fun setupClusterManager() {
        with(clusterManager) {
            renderer = this@PlaneMarkerRenderer
            map.setOnCameraIdleListener(this)
            map.setOnMarkerClickListener(this)
        }
    }

    private fun setupMarker() {
        val markerView = LayoutInflater.from(context).inflate(R.layout.view_plane_marker, null)
        plane = markerView.findViewById(R.id.plane)
        markerGenerator.setContentView(markerView)
        markerGenerator.setBackground(null)
    }

}