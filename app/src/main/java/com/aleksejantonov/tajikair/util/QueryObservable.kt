package com.aleksejantonov.tajikair.util

import com.arlib.floatingsearchview.FloatingSearchView
import com.jakewharton.rxbinding2.InitialValueObservable

import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class QueryObservable(private val view: FloatingSearchView) : InitialValueObservable<CharSequence>() {

    override fun subscribeListener(observer: Observer<in CharSequence>) {
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnQueryChangeListener(listener)
    }

    override fun getInitialValue(): CharSequence = view.query

    internal class Listener(
        private val view: FloatingSearchView,
        private val observer: Observer<in CharSequence>
    ) : MainThreadDisposable(), FloatingSearchView.OnQueryChangeListener {

        override fun onSearchTextChanged(oldQuery: String, newQuery: String?) {
            if (!isDisposed && newQuery != null) observer.onNext(newQuery)
        }

        override fun onDispose(): Unit = view.setOnQueryChangeListener(null)
    }
}
