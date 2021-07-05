package com.aleksejantonov.tajikair.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.aleksejantonov.tajikair.api.entity.LatLng
import com.aleksejantonov.tajikair.ui.map.render.DotMarkerRenderer
import com.aleksejantonov.tajikair.ui.map.render.PlaneMarkerRenderer
import com.google.android.gms.maps.GoogleMap
import java.math.BigInteger
import kotlin.math.pow

fun getPivotPoints(depLatLng: LatLng, desLatLng: LatLng): Array<Array<Double>> {
    return when {
        depLatLng.latitude < desLatLng.latitude
                && depLatLng.longitude < desLatLng.longitude
                || depLatLng.latitude > desLatLng.latitude
                && depLatLng.longitude > desLatLng.longitude -> {
            arrayOf(
                arrayOf(depLatLng.latitude, depLatLng.longitude),
                arrayOf(desLatLng.latitude, depLatLng.longitude),
                arrayOf(depLatLng.latitude, desLatLng.longitude),
                arrayOf(desLatLng.latitude, desLatLng.longitude)
            )
        }
        else                                                 -> arrayOf(
            arrayOf(depLatLng.latitude, depLatLng.longitude),
            arrayOf(depLatLng.latitude, desLatLng.longitude),
            arrayOf(desLatLng.latitude, depLatLng.longitude),
            arrayOf(desLatLng.latitude, desLatLng.longitude)
        )
    }
}

fun renderRoute(xy: Array<Array<Double>>, renderer: DotMarkerRenderer) {
    val dots = mutableListOf<LatLng>()
    var t = 0.0
    val count = 41.0
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

    renderer.render(dots)
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
                Math.toDegrees(Math.atan(((nextY - currentY) / (nextX - currentX)))) - 90
            }
            nextX > currentX && nextY < currentY -> {
                Math.toDegrees(Math.atan(((nextX - currentX) / (currentY - nextY)))) - 180
            }
            nextX < currentX && nextY > currentY -> {
                Math.toDegrees(Math.atan(((currentX - nextX) / (nextY - currentY))))
            }
            else                                 -> { // nextX < currentX && nextY < currentY
                Math.toDegrees(Math.atan(((currentY - nextY) / (currentX - nextX)))) + 90
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
