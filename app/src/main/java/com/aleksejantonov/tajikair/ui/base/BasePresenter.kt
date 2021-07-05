package com.aleksejantonov.tajikair.ui.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T> {

    private val subscriptions = CompositeDisposable()

    fun Disposable.keepUntilDestroy() = subscriptions.add(this)

    fun onDestroy() {
        subscriptions.clear()
    }
}