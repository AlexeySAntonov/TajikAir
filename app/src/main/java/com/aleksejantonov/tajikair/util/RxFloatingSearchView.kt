package com.aleksejantonov.tajikair.util

import com.arlib.floatingsearchview.FloatingSearchView
import com.jakewharton.rxbinding2.InitialValueObservable

object RxFloatingSearchView {

    fun queryChanges(view: FloatingSearchView): InitialValueObservable<CharSequence> = QueryObservable(view)
}

fun FloatingSearchView.queryChanges(): InitialValueObservable<CharSequence> = RxFloatingSearchView.queryChanges(this)