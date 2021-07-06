package com.aleksejantonov.tajikair.ui.map

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.databinding.FragmentMapBinding
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.ui.map.render.CityMarkerRenderer
import com.aleksejantonov.tajikair.ui.map.render.PlaneMarkerRenderer
import com.aleksejantonov.tajikair.util.dpToPx
import com.aleksejantonov.tajikair.util.getCurvePlaneAnimator
import com.aleksejantonov.tajikair.util.getPivotPoints
import com.aleksejantonov.tajikair.util.getRouteCoordinates
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager

class MapFragment : BaseFragment() {

  private val binding get() = _binding as FragmentMapBinding

  private val viewModel by viewModels<MapViewModel> { DI.appComponent.viewModelFactory() }

  private lateinit var cityRenderer: CityMarkerRenderer
  private lateinit var planeRenderer: PlaneMarkerRenderer

  private val depCity by lazy { requireNotNull(arguments?.getParcelable(DEPARTURE)) as City }
  private val desCity by lazy { requireNotNull(arguments?.getParcelable(DESTINATION)) as City }

  private var animator: Animator? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentMapBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.mapView.onCreate(savedInstanceState)
    prepareMap()
  }

  private fun prepareMap() {
    binding.mapView.getMapAsync { map ->
      map.uiSettings.apply {
        isRotateGesturesEnabled = false
        isIndoorLevelPickerEnabled = false
      }
      context?.let {
        cityRenderer = CityMarkerRenderer(it, map, ClusterManager(it, map))
        planeRenderer = PlaneMarkerRenderer(it, map, ClusterManager(it, map))

        val pivotPoints = getPivotPoints(requireNotNull(depCity.latLng), requireNotNull(desCity.latLng))
        renderRoute(map, pivotPoints)
        renderCities(listOf(depCity, desCity))
        startPlaneAnimation(pivotPoints)
      }
    }
  }

  private fun renderRoute(map: GoogleMap, pivotPoints: Array<Array<Double>>) {
    val routeCoordinates = getRouteCoordinates(pivotPoints).map { LatLng(it.latitude, it.longitude) }
    val polyline = map.addPolyline(
      PolylineOptions()
        .add(*routeCoordinates.toTypedArray())
    )
    polyline.width = dpToPx(6f).toFloat()
    polyline.color = ContextCompat.getColor(requireNotNull(context), R.color.semiTransparentDot)
    polyline.pattern = listOf(Dot(), Gap(dpToPx(6f).toFloat()))
  }

  private fun renderCities(cities: List<City>) {
    cityRenderer.render(cities)
  }

  private fun startPlaneAnimation(pivotPoints: Array<Array<Double>>) {
    animator = getCurvePlaneAnimator(pivotPoints, planeRenderer)
    animator?.start()
  }

  override fun onStart() {
    animator?.resume()
    binding.mapView.onStart()
    super.onStart()
  }

  override fun onResume() {
    binding.mapView.onResume()
    super.onResume()
  }

  override fun onPause() {
    binding.mapView.onPause()
    super.onPause()
  }

  override fun onStop() {
    animator?.pause()
    binding.mapView.onStop()
    super.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    binding.mapView.onLowMemory()
  }

  override fun onDestroyView() {
    animator?.cancel()
    animator = null
    planeRenderer.onRemove()
    binding.mapView.onDestroy()
    super.onDestroyView()
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