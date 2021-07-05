package com.aleksejantonov.tajikair.ui.main

import android.os.Bundle
import android.view.View
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.util.initDefaultFocusChangeListener
import com.aleksejantonov.tajikair.util.initOnClearSearchListener
import com.aleksejantonov.tajikair.util.initOnSearchListener
import com.aleksejantonov.tajikair.util.initSuggestionsHeightChangeListener
import com.aleksejantonov.tajikair.util.queryChanges
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment(), MainView {

    override val layoutId = R.layout.fragment_main

//    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDeparture()
        initDestination()

        search.setOnClickListener { presenter.onSearchClick() }
    }

    override fun setDepartureSearchText(text: String) {
        departureSearch.setSearchText(text)
    }

    override fun setDestinationsSearchText(text: String) {
        destinationSearch.setSearchText(text)
    }

    override fun enableSearchButton(enabled: Boolean) {
        search.isEnabled = enabled
        search.alpha = if (enabled) 1f else 0.5f
    }

    override fun applyDepartureResults(locations: List<LocationSuggestion>) {
        departureSearch.swapSuggestions(locations)
    }

    override fun applyDestinationResults(locations: List<LocationSuggestion>) {
        destinationSearch.swapSuggestions(locations)
    }

    private fun initDeparture() {
        with(departureSearch) {
            presenter.listenDepartureQueries(queryChanges())
            initDefaultFocusChangeListener(presenter::departureChanged)
            initOnSearchListener(presenter::departureChanged)
            initSuggestionsHeightChangeListener(destinationLabel, destinationSearch)
            initOnClearSearchListener(presenter::departureChanged)
        }
    }

    private fun initDestination() {
        with(destinationSearch) {
            presenter.listenDestinationQueries(queryChanges())
            initDefaultFocusChangeListener(presenter::destinationChanged)
            initOnSearchListener(presenter::destinationChanged)
            initOnClearSearchListener(presenter::destinationChanged)
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }

}