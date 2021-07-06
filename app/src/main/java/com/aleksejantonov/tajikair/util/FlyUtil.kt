package com.aleksejantonov.tajikair.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.aleksejantonov.tajikair.api.entity.LatLng
import com.aleksejantonov.tajikair.ui.map.render.PlaneMarkerRenderer
import timber.log.Timber
import com.google.android.gms.maps.model.LatLng as MapsLatLng
import java.math.BigInteger
import kotlin.math.atan
import kotlin.math.pow

fun getPivotPoints(dep: LatLng, dest: LatLng): Array<Array<Double>> {
  return when {
    dep.latitude < dest.latitude
        && dep.longitude < dest.longitude
        || dep.latitude > dest.latitude
        && dep.longitude > dest.longitude -> {
      arrayOf(
        arrayOf(dep.latitude, dep.longitude),
        arrayOf(dest.latitude, dep.longitude),
        arrayOf(dep.latitude, dest.longitude),
        arrayOf(dest.latitude, dest.longitude)
      )
    }
    else -> arrayOf(
      arrayOf(dep.latitude, dep.longitude),
      arrayOf(dep.latitude, dest.longitude),
      arrayOf(dest.latitude, dep.longitude),
      arrayOf(dest.latitude, dest.longitude)
    )
  }
}

fun getRouteCoordinates(xy: Array<Array<Double>>): List<MapsLatLng> {
  val dots = mutableListOf<MapsLatLng>()
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

    dots.add(MapsLatLng(nextX, nextY))
    Timber.e("Latitude = $nextX, Longtitude = $nextY $")
    t += (1.0 / count)
  }
  val destinationDot = MapsLatLng(xy[xy.size - 1][0], xy[xy.size - 1][1])
  return dots.apply { add(destinationDot) }
}

fun getCurvePlaneAnimator(xy: Array<Array<Double>>, renderer: PlaneMarkerRenderer): Animator {
  var currentX = 0.0
  var currentY = 0.0
  val animator = ValueAnimator.ofFloat(0f, 1f)
  animator.duration = 20000
  animator.interpolator = AccelerateDecelerateInterpolator()
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

    renderer.render(LatLng(nextX, nextY), angle.toFloat())
  }
  return animator
}

fun getCoef(n: Int, k: Int): Int {
  return (factorial(n) / (factorial(k) * factorial(n - k))).toInt()
}

fun factorial(n: Int): BigInteger {
  if (n == 0) return BigInteger.ONE
  return BigInteger.valueOf(n.toLong()) * factorial(n - 1)
}
