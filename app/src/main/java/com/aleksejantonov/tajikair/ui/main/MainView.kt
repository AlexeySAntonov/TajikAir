package com.aleksejantonov.tajikair.ui.main

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun setDepartureSearchText(text: String)
    fun setDestinationsSearchText(text: String)
    fun enableSearchButton(enabled: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun applyDepartureResults(locations: List<LocationSuggestion>)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun applyDestinationResults(locations: List<LocationSuggestion>)
}