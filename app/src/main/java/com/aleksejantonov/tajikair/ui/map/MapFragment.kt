package com.aleksejantonov.tajikair.ui.map

import android.animation.AnimatorSet
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.databinding.FragmentMapBinding
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.ui.map.render.PlaneMarkerRenderer
import com.aleksejantonov.tajikair.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import kotlin.math.abs


class MapFragment : BaseFragment() {

  private val binding get() = _binding as FragmentMapBinding

  private val viewModel by viewModels<MapViewModel> { DI.appComponent.viewModelFactory() }

  private lateinit var planeRenderer: PlaneMarkerRenderer

  private val depCity by lazy { requireNotNull(arguments?.getParcelable(DEPARTURE)) as City }
  private val desCity by lazy { requireNotNull(arguments?.getParcelable(DESTINATION)) as City }

  private var animatorSet: AnimatorSet? = null

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
        planeRenderer = PlaneMarkerRenderer(it, map, ClusterManager(it, map))

        renderCities(map)

        val dep = requireNotNull(depCity.latLng)
        val dest = requireNotNull(desCity.latLng)
        if (abs(dep.longitude - dest.longitude) <= MAX_LONGITUDE) {
          // Simple route without map breaks
          val pivotPoints = getSimpleRoutePivotPoints(dep, dest)
          renderRoute(map, pivotPoints)
          startPlaneAnimation(pivotPoints)
        } else {
          val (pivotPointsA, pivotPointsB) = getComplexRoutePivotPoints(dep, dest)
          renderRoute(map, pivotPointsA)
          renderRoute(map, pivotPointsB)
          startPlaneAnimation(pivotPointsA, pivotPointsB)
        }
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

  private fun renderCities(map: GoogleMap) {
    val depLatLng = requireNotNull(depCity.latLng)
    val destLatLng = requireNotNull(desCity.latLng)
    map.addMarker(
      MarkerOptions().apply {
        icon(BitmapDescriptorFactory.fromBitmap(getCityMarkerBitmap(depCity.iata.first())))
        position(LatLng(depLatLng.latitude, depLatLng.longitude))
        anchor(0.5f, 0.5f)
        zIndex(2f)
      }
    )
    map.addMarker(
      MarkerOptions().apply {
        icon(BitmapDescriptorFactory.fromBitmap(getCityMarkerBitmap(desCity.iata.first())))
        position(LatLng(destLatLng.latitude, destLatLng.longitude))
        anchor(0.5f, 0.5f)
        zIndex(2f)
      }
    )

    val boundsBuilder = LatLngBounds.Builder()
      .include(LatLng(depLatLng.latitude, depLatLng.longitude))
      .include(LatLng(destLatLng.latitude, destLatLng.longitude))
    val bounds = boundsBuilder.build()
    val padding = when (resources.configuration.orientation) {
      Configuration.ORIENTATION_PORTRAIT -> binding.root.context.getScreenWidth() / 4
      else -> binding.root.context.getScreenWidth() / 8
    }
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
    map.animateCamera(cameraUpdate)
  }

  private fun startPlaneAnimation(pivotPointsA: Array<Array<Double>>, pivotPointsB: Array<Array<Double>>? = null) {
    animatorSet?.cancel()
    animatorSet = AnimatorSet().apply {
      pivotPointsB?.let {
        val (durationA, durationB) = complexRoutePathsDurations(pivotPointsA, it)
        playSequentially(
          getCurvePlaneAnimator(pivotPointsA, planeRenderer).apply { duration = durationA },
          getCurvePlaneAnimator(it, planeRenderer).apply { duration = durationB }
        )
      } ?: run {
        play(getCurvePlaneAnimator(pivotPointsA, planeRenderer))
        duration = WHOLE_PATH_ANIMATION_DURATION
      }
      interpolator = LinearInterpolator()
      if (hasOreo()) currentPlayTime = viewModel.currentPlayTime
      start()
    }
  }

  override fun onStart() {
    animatorSet?.resume()
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
    animatorSet?.pause()
    binding.mapView.onStop()
    super.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    binding.mapView.onLowMemory()
  }

  override fun onDestroyView() {
    if (hasOreo()) viewModel.currentPlayTime = animatorSet?.currentPlayTime ?: 0L
    animatorSet?.cancel()
    animatorSet = null
    planeRenderer.onRemove()
    binding.mapView.onDestroy()
    super.onDestroyView()
  }

  private fun getCityMarkerBitmap(iata: String): Bitmap {
    val customMarkerView: TextView = layoutInflater.inflate(R.layout.view_city_marker, null) as TextView
    customMarkerView.text = iata
    customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
    return customMarkerView.drawBitmap(w = customMarkerView.measuredWidth, h = customMarkerView.measuredHeight)
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