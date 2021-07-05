package com.aleksejantonov.tajikair.ui.main

//@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView {
    fun setDepartureSearchText(text: String)
    fun setDestinationsSearchText(text: String)
    fun enableSearchButton(enabled: Boolean)

//    @StateStrategyType(OneExecutionStateStrategy::class)
    fun applyDepartureResults(locations: List<LocationSuggestion>)
//    @StateStrategyType(OneExecutionStateStrategy::class)
    fun applyDestinationResults(locations: List<LocationSuggestion>)
}