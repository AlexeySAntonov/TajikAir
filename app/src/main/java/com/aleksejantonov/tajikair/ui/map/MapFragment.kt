package com.aleksejantonov.tajikair.ui.map

import android.animation.AnimatorSet
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.api.entity.LatLng.CREATOR.toMapLatLng
import com.aleksejantonov.tajikair.databinding.FragmentMapBinding
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.math.abs


class MapFragment : BaseFragment() {

  private val binding get() = _binding as FragmentMapBinding

  private val viewModel by viewModels<MapViewModel> { DI.appComponent.viewModelFactory() }
  private val flyComputationViewModel by viewModels<FlyComputationViewModel> { DI.appComponent.viewModelFactory() }

  private val depCity by lazy { requireNotNull(arguments?.getParcelable(DEPARTURE)) as City }
  private val desCity by lazy { requireNotNull(arguments?.getParcelable(DESTINATION)) as City }

  private var animatorSet: AnimatorSet? = null
  private var currentPlaneMarker: Marker? = null

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
        val depLatLng = requireNotNull(depCity.latLng?.toMapLatLng())
        val destLatLng = requireNotNull(desCity.latLng?.toMapLatLng())
        renderCities(map, depLatLng, destLatLng)

        if (abs(depLatLng.longitude - destLatLng.longitude) <= MAX_LONGITUDE) {
          // Simple route without map breaks
          val pivotPoints = flyComputationViewModel.getSimpleRoutePivotPoints(depLatLng, destLatLng)
          renderRoute(map, pivotPoints)
          renderPivotPoints(map, pivotPoints)
          startPlaneAnimation(map, pivotPoints)
        } else {
          val (pivotPointsA, pivotPointsB) = flyComputationViewModel.getComplexRoutePivotPoints(depLatLng, destLatLng)
          renderRoute(map, pivotPointsA)
          renderRoute(map, pivotPointsB)
          startPlaneAnimation(map, pivotPointsA, pivotPointsB)
        }
      }
    }
  }

  // Just for info
  private fun renderPivotPoints(map: GoogleMap, pivotPoints: Array<Array<Double>>) {
    val a = LatLng(pivotPoints[1][0], pivotPoints[1][1])
    val b = LatLng(pivotPoints[2][0], pivotPoints[2][1])
    map.addMarker(
      MarkerOptions().apply {
        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pivot_point))
        position(a)
        anchor(0.5f, 0.5f)
        zIndex(2f)
      }
    )
    map.addMarker(
      MarkerOptions().apply {
        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pivot_point))
        position(b)
        anchor(0.5f, 0.5f)
        zIndex(2f)
      }
    )
  }

  private fun renderRoute(map: GoogleMap, pivotPoints: Array<Array<Double>>) {
    val routeCoordinates = flyComputationViewModel.getRouteCoordinates(pivotPoints).map { LatLng(it.latitude, it.longitude) }
    val polyline = map.addPolyline(
      PolylineOptions()
        .add(*routeCoordinates.toTypedArray())
    )
    polyline.width = dpToPx(6f).toFloat()
    polyline.color = ContextCompat.getColor(requireNotNull(context), R.color.semiTransparentDot)
    polyline.pattern = listOf(Dot(), Gap(dpToPx(6f).toFloat()))
  }

  private fun renderCities(map: GoogleMap, depLatLng: LatLng, destLatLng: LatLng) {
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

  private fun startPlaneAnimation(map: GoogleMap, pivotPointsA: Array<Array<Double>>, pivotPointsB: Array<Array<Double>>? = null) {
    animatorSet?.cancel()
    animatorSet = AnimatorSet().apply {
      pivotPointsB?.let {
        val (durationA, durationB) = flyComputationViewModel.complexRoutePathsDurations(pivotPointsA, it)
        playSequentially(
          flyComputationViewModel.getCurvePlaneAnimator(pivotPointsA) { latLng, angle -> renderPlane(map, latLng, angle) }.apply { duration = durationA },
          flyComputationViewModel.getCurvePlaneAnimator(it) { latLng, angle -> renderPlane(map, latLng, angle) }.apply { duration = durationB }
        )
      } ?: run {
        play(flyComputationViewModel.getCurvePlaneAnimator(pivotPointsA) { latLng, angle -> renderPlane(map, latLng, angle) })
        duration = WHOLE_PATH_ANIMATION_DURATION
      }
      interpolator = LinearInterpolator()
      if (hasOreo()) currentPlayTime = viewModel.currentPlayTime
      start()
    }
  }

  private fun renderPlane(map: GoogleMap, latLng: LatLng, angle: Float) {
    val tempMarker = map.addMarker(
      MarkerOptions().apply {
        icon(BitmapDescriptorFactory.fromBitmap(getPlaneMarkerBitmap()))
        position(latLng)
        rotation(angle)
        anchor(0.5f, 0.5f)
        zIndex(3f)
      }
    )
    currentPlaneMarker?.remove()
    currentPlaneMarker = tempMarker
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

  private fun getPlaneMarkerBitmap(): Bitmap {
    val customMarkerView: ImageView = layoutInflater.inflate(R.layout.view_plane_marker, null) as ImageView
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