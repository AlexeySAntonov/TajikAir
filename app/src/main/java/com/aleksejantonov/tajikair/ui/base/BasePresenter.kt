package com.aleksejantonov.tajikair.ui.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T : MvpView> : MvpPresenter<T>() {

    private val subscriptions = CompositeDisposable()

    fun Disposable.keepUntilDestroy() = subscriptions.add(this)

    override fun onDestroy() {
        subscriptions.clear()
        super.onDestroy()
    }
}