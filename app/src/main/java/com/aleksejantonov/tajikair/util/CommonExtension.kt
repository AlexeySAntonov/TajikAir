package com.aleksejantonov.tajikair.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.aleksejantonov.tajikair.api.entity.City

fun cityStub() = City("", "", null, emptyList())

fun hasOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun View.drawBitmap(x: Int = 0, y: Int = 0, w: Int, h: Int): Bitmap {
  val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  draw(canvas)
  return Bitmap.createBitmap(bitmap, x, y, w, h)
}

fun EditText.showKeyboard(delay: Long = 0L) {
  if (delay == 0L) {
    showKeyboardImmediately()
  } else {
    postDelayed({ showKeyboardImmediately() }, delay)
  }
}

private fun EditText.showKeyboardImmediately() {
  requestFocus()
  val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.hideKeyboard(delay: Long = 0L) {
  view?.let { activity?.hideKeyboard(it, delay) }
}

fun Activity.hideKeyboard(delay: Long = 0L) {
  hideKeyboard(currentFocus ?: View(this), delay)
}

fun Context.hideKeyboard(view: View, delay: Long = 0) {
  if (delay == 0L) {
    hideKeyBoardImmediately(view)
  } else {
    view.postDelayed({ hideKeyBoardImmediately(view) }, delay)
  }
}

private fun Context.hideKeyBoardImmediately(view: View) {
  val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}