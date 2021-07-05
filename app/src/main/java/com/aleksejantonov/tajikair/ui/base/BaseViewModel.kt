package com.aleksejantonov.tajikair.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

open class BaseViewModel : ViewModel() {

    open val exceptionHandler = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    private val subscriptions = CompositeDisposable()

    fun Disposable.keepUntilDestroy() = subscriptions.add(this)

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }

}