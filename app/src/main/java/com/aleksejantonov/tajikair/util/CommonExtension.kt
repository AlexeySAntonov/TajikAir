package com.aleksejantonov.tajikair.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import com.aleksejantonov.tajikair.api.entity.City

fun cityStub() = City("", "", null, emptyList())

fun hasOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun View.drawBitmap(x: Int = 0, y: Int = 0, w: Int, h: Int): Bitmap {
  val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  draw(canvas)
  return Bitmap.createBitmap(bitmap, x, y, w, h)
}