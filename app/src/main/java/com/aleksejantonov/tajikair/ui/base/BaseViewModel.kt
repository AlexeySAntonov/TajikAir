package com.aleksejantonov.tajikair.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {

    private val subscriptions = CompositeDisposable()

    fun Disposable.keepUntilDestroy() = subscriptions.add(this)

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }

}