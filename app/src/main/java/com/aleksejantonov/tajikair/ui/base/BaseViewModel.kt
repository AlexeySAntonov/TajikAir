package com.aleksejantonov.tajikair.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

open class BaseViewModel : ViewModel() {

    open val exceptionHandler = CoroutineExceptionHandler { _, e -> Timber.e(e) }

}