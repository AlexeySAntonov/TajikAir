package com.aleksejantonov.tajikair.ui.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.location.Location
import com.aleksejantonov.tajikair.ui.base.BaseViewModel
import com.aleksejantonov.tajikair.util.MAX_LONGITUDE
import com.aleksejantonov.tajikair.util.MIN_LONGITUDE
import com.aleksejantonov.tajikair.util.WHOLE_PATH_ANIMATION_DURATION
import com.aleksejantonov.tajikair.util.getCoef
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.pow

class FlyComputationViewModel @Inject constructor() : BaseViewModel() {

  fun getSimpleRoutePivotPoints(dep: LatLng, dest: LatLng): Array<Array<Double>> {
    val deltaLat = abs(dep.latitude - dest.latitude)
    val deltaLng = abs(dep.longitude - dest.longitude) / 2 // In visual presentation 1 Lat == 2 Lng

    val startPoint = arrayOf(dep.latitude, dep.longitude)
    val endPoint = arrayOf(dest.latitude, dest.longitude)
    val firstPivotPoint: Array<Double>
    val secondPivotPoint: Array<Double>
    when {
      // Shift pivot points in case of big difference between Lat and Lng
      deltaLat > deltaLng * 2 -> {
        val fpLng = if (dest.longitude > dep.longitude) dest.longitude + (deltaLat - deltaLng) / 2
        else dest.longitude - (deltaLat - deltaLng) / 2
        val fpLat = if (dest.latitude > dep.latitude)  dep.latitude + deltaLat / 4
        else dep.latitude - deltaLat / 4
        firstPivotPoint = arrayOf(fpLat, fpLng)

        val spLng = if (dest.longitude > dep.longitude) dep.longitude - (deltaLat - deltaLng) / 2
        else dep.longitude + (deltaLat - deltaLng) / 2
        val spLat = if (dest.latitude > dep.latitude) dest.latitude - deltaLat / 4
        else dest.latitude + deltaLat / 4
        secondPivotPoint = arrayOf(spLat, spLng)
      }
      deltaLng > deltaLat * 2 -> {
        val fpLat = if (dest.latitude > dep.latitude) dep.latitude - (deltaLng - deltaLat) / 2
        else dep.latitude + (deltaLng - deltaLat) / 2
        val fpLng = if (dest.longitude > dep.longitude) dest.longitude - deltaLng / 2
        else dest.longitude + deltaLng / 2
        firstPivotPoint = arrayOf(fpLat, fpLng)

        val spLat = if (dest.latitude > dep.latitude) dest.latitude + (deltaLng - deltaLat) / 2
        else dest.latitude - (deltaLng - deltaLat) / 2
        val spLng = if (dest.longitude > dep.longitude) dep.longitude + deltaLng / 2
        else dep.longitude - deltaLng / 2
        secondPivotPoint = arrayOf(spLat, spLng)
      }
      else -> {
        firstPivotPoint = arrayOf(dep.latitude, dest.longitude)
        secondPivotPoint = arrayOf(dest.latitude, dep.longitude)
      }
    }

    return arrayOf(
      startPoint,
      firstPivotPoint,
      secondPivotPoint,
      endPoint
    )
  }

  fun getComplexRoutePivotPoints(dep: LatLng, dest: LatLng): Pair<Array<Array<Double>>, Array<Array<Double>>> {
    val closestBreakLongitude = if (dep.longitude >= 0) MAX_LONGITUDE else MIN_LONGITUDE
    val middleLatitude = dep.latitude - (dep.latitude - dest.latitude) / 2
    val firstPath = arrayOf(
      arrayOf(dep.latitude, dep.longitude),
      arrayOf(middleLatitude, dep.longitude),
      arrayOf(dep.latitude, closestBreakLongitude),
      arrayOf(middleLatitude, closestBreakLongitude),
    )
    val secondPath = arrayOf(
      arrayOf(middleLatitude, closestBreakLongitude * -1),
      arrayOf(dest.latitude, closestBreakLongitude * -1),
      arrayOf(middleLatitude, dest.longitude),
      arrayOf(dest.latitude, dest.longitude)
    )
    return firstPath to secondPath
  }

  fun getRouteCoordinates(xy: Array<Array<Double>>): List<LatLng> {
    val dots = mutableListOf<LatLng>()
    var t = 0.0
    val count = 100.0
    for (i in 0 until count.toInt()) {
      val b = t
      val a = 1 - b

      var nextX = 0.0
      var nextY = 0.0

      val s = xy.size
      for (j in 0 until s) {
        val coef = getCoef(s - 1, j)
        nextX += coef * a.pow(s - 1 - j) * b.pow(j) * xy[j][0]
        nextY += coef * a.pow(s - 1 - j) * b.pow(j) * xy[j][1]
      }

      dots.add(LatLng(nextX, nextY))
      t += (1.0 / count)
    }
    val destinationDot = LatLng(xy[xy.size - 1][0], xy[xy.size - 1][1])
    return dots.apply { add(destinationDot) }
  }

  fun getCurvePlaneAnimator(xy: Array<Array<Double>>, renderBlock: (LatLng, Float) -> Unit): Animator {
    var currentX = 0.0
    var currentY = 0.0
    val animator = ValueAnimator.ofFloat(0f, 1f)
    animator.addUpdateListener {
      val b = it.animatedValue as Float
      val a = 1 - b

      var nextX = 0.0
      var nextY = 0.0

      val s = xy.size
      for (i in 0 until s) {
        val coef = getCoef(s - 1, i)
        nextX += coef * a.pow(s - 1 - i) * b.pow(i) * xy[i][0]
        nextY += coef * a.pow(s - 1 - i) * b.pow(i) * xy[i][1]
      }

      val angle = when {
        nextX > currentX && nextY > currentY -> {
          Math.toDegrees(atan(((nextY - currentY) / (nextX - currentX)))) - 90
        }
        nextX > currentX && nextY < currentY -> {
          Math.toDegrees(atan(((nextX - currentX) / (currentY - nextY)))) - 180
        }
        nextX < currentX && nextY > currentY -> {
          Math.toDegrees(atan(((currentX - nextX) / (nextY - currentY))))
        }
        else -> { // nextX < currentX && nextY < currentY
          Math.toDegrees(atan(((currentY - nextY) / (currentX - nextX)))) + 90
        }
      }

      currentX = nextX
      currentY = nextY

      renderBlock.invoke(LatLng(nextX, nextY), angle.toFloat())
    }
    return animator
  }

  fun complexRoutePathsDurations(pivotPointsA: Array<Array<Double>>, pivotPointsB: Array<Array<Double>>): Pair<Long, Long> {
    val aDep = LatLng(pivotPointsA[0][0], pivotPointsA[0][1])
    val aDest = LatLng(pivotPointsA[pivotPointsA.size - 1][0], pivotPointsA[pivotPointsA.size - 1][1])
    val bDep = LatLng(pivotPointsB[0][0], pivotPointsB[0][1])
    val bDest = LatLng(pivotPointsB[pivotPointsB.size - 1][0], pivotPointsB[pivotPointsB.size - 1][1])
    val results = FloatArray(1)
    Location.distanceBetween(aDep.latitude, aDep.longitude, aDest.latitude, aDest.longitude, results)
    val aDistance = results[0]
    Location.distanceBetween(bDep.latitude, bDep.longitude, bDest.latitude, bDest.longitude, results)
    val bDistance = results[0]
    val aDuration = aDistance * WHOLE_PATH_ANIMATION_DURATION / (aDistance + bDistance)
    val bDuration = WHOLE_PATH_ANIMATION_DURATION - aDuration
    return aDuration.toLong() to bDuration.toLong()
  }

}