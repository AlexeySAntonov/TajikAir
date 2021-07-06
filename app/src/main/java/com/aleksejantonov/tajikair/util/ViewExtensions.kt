package com.aleksejantonov.tajikair.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.ui.main.LocationSuggestion
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlin.math.roundToInt


fun Fragment.statusBarHeight(): Int {
    return activity?.statusBarHeight() ?: 0
}

fun Activity.statusBarHeight(): Int {
    val rectangle = Rect()
    val window = window
    window.decorView.getWindowVisibleDisplayFrame(rectangle)
    return if (rectangle.top > 0) rectangle.top else statusBarHeightFromResources()
}

fun Activity.statusBarHeightFromResources(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId)
    }
    return 0
}

fun Fragment.navBarHeight(): Int = requireContext().navBarHeight()

fun Context.navBarHeight(isLandscapeMode: Boolean = false): Int {
    if (hasSoftBottomBar(isLandscapeMode)) {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
    }
    return 0
}

fun Context.hasSoftBottomBar(isLandscapeMode: Boolean = false): Boolean {
    val bottomBarHeight = dpToPx(48f) // 48 is bottom bar height on Nexus 6 at least
    val screenSize = screenSize(this)
    val fullSize = fullScreenSize(this)
    return if (!isLandscapeMode) fullSize.y - screenSize.y >= bottomBarHeight
    else fullSize.x - screenSize.x >= bottomBarHeight
}

fun Context.dpToPx(dp: Float): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).roundToInt()
}

fun Fragment.dpToPx(dp: Float): Int {
    val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
}

fun screenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display
    } else {
        windowManager.defaultDisplay
    }
    val size = Point()
    display?.getSize(size)
    return size
}

fun fullScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display
    } else {
        windowManager.defaultDisplay
    }
    val size = Point()
    display?.getRealSize(size)
    return size
}

fun Context.getScreenHeight(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowManager.maximumWindowMetrics.bounds.bottom
    } else {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        size.y
    }
}

fun Context.getScreenWidth(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowManager.maximumWindowMetrics.bounds.right
    } else {
        val size = Point()
        (this as Activity).windowManager.defaultDisplay.getSize(size)
        size.x
    }
}

fun View.setPaddings(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    setPadding(
        left ?: paddingLeft,
        top ?: paddingTop,
        right ?: paddingRight,
        bottom ?: paddingBottom
    )
}

fun FloatingSearchView.initDefaultFocusChangeListener(action: (City) -> Unit) {
    setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
        override fun onFocusCleared() = action.invoke(cityStub())
        override fun onFocus() = Unit
    })
}

fun FloatingSearchView.initOnSearchListener(onSuggestionClickAction: (City?) -> Unit) {
    setOnSearchListener(object : FloatingSearchView.OnSearchListener {
        override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
            clearFocus()
            onSuggestionClickAction.invoke((searchSuggestion as LocationSuggestion).city)
        }

        override fun onSearchAction(currentQuery: String?) = Unit
    })
}

fun FloatingSearchView.initSuggestionsHeightChangeListener(vararg dependentViews: View) {
    setOnSuggestionsListHeightChanged { newHeight ->
        dependentViews.forEach {
            it.translationY = newHeight
        }
    }
}

fun FloatingSearchView.initOnClearSearchListener(onClearAction: (City) -> Unit) {
    setOnClearSearchActionListener { onClearAction.invoke(cityStub()) }
}