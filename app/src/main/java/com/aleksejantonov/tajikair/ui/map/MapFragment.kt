package com.aleksejantonov.tajikair.ui.map

import android.animation.Animator
import android.os.Bundle
import android.view.View
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.ui.map.render.CityMarkerRenderer
import com.aleksejantonov.tajikair.ui.map.render.DotMarkerRenderer
import com.aleksejantonov.tajikair.ui.map.render.PlaneMarkerRenderer
import com.aleksejantonov.tajikair.util.getCurvePlaneAnimator
import com.aleksejantonov.tajikair.util.getPivotPoints
import com.aleksejantonov.tajikair.util.renderRoute
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : BaseFragment(), MapView {

    private lateinit var cityRenderer: CityMarkerRenderer
    private lateinit var planeRenderer: PlaneMarkerRenderer
    private lateinit var dotRenderer: DotMarkerRenderer

    private val depCity by lazy { arguments?.getParcelable(DEPARTURE) as City }
    private val desCity by lazy { arguments?.getParcelable(DESTINATION) as City }

    private var animator: Animator? = null

    override val layoutId = R.layout.fragment_map

    @InjectPresenter
    lateinit var presenter: MapPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        prepareMap()
    }

    private fun prepareMap() {
        mapView.getMapAsync { map ->
            map.uiSettings.apply {
                isRotateGesturesEnabled = false
                isIndoorLevelPickerEnabled = false
            }
            context?.let {
                cityRenderer = CityMarkerRenderer(it, map, ClusterManager(it, map))
                planeRenderer = PlaneMarkerRenderer(it, map, ClusterManager(it, map))
                dotRenderer = DotMarkerRenderer(it, map, ClusterManager(it, map))

                renderCities(listOf(depCity, desCity))
                renderRoute()
                startPlaneAnimation()
            }
        }
    }

    private fun renderRoute() {
        val pivotPoints = getPivotPoints(requireNotNull(depCity.latLng), requireNotNull(desCity.latLng))
        renderRoute(pivotPoints, dotRenderer)
    }

    private fun startPlaneAnimation() {
        val pivotPoints = getPivotPoints(requireNotNull(depCity.latLng), requireNotNull(desCity.latLng))
        animator = getCurvePlaneAnimator(pivotPoints, planeRenderer)
        animator?.start()
    }

    override fun onStart() {
        animator?.resume()
        mapView.onStart()
        super.onStart()
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        animator?.pause()
        mapView.onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        planeRenderer.onRemove()
        mapView.onDestroy()
        super.onDestroyView()
    }

    private fun renderCities(cities: List<City>) {
        cityRenderer.render(cities)
    }

    companion object {
        private const val DEPARTURE = "DEPARTURE"
        private const val DESTINATION = "DESTINATION"

        fun newInstance(depCity: City, desCity: City) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DEPARTURE, depCity)
                    putParcelable(DESTINATION, desCity)
                }
            }
    }

}