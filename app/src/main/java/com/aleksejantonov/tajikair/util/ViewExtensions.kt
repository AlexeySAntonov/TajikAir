package com.aleksejantonov.tajikair.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.ui.main.LocationSuggestion
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion


fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Context.getScreenWidth(): Int {
    val size = Point()
    (this as Activity).windowManager.defaultDisplay.getSize(size)
    return size.x
}

fun Context.getScreenHeight(): Int {
    val size = Point()
    (this as Activity).windowManager.defaultDisplay.getSize(size)
    return size.y - getStatusBarHeight()
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
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

fun cityStub() = City("", "", null, emptyList())