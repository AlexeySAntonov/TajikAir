package com.aleksejantonov.tajikair.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.util.initDefaultFocusChangeListener
import com.aleksejantonov.tajikair.util.initOnClearSearchListener
import com.aleksejantonov.tajikair.util.initOnSearchListener
import com.aleksejantonov.tajikair.util.initSuggestionsHeightChangeListener
import com.aleksejantonov.tajikair.util.queryChanges
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment(R.layout.fragment_main), MainView {

    private val viewModel by viewModels<MainViewModel> { DI.appComponent.viewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDeparture()
        initDestination()

        search.setOnClickListener { viewModel.onSearchClick() }
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
            viewModel.listenDepartureQueries(queryChanges())
            initDefaultFocusChangeListener(viewModel::departureChanged)
            initOnSearchListener(viewModel::departureChanged)
            initSuggestionsHeightChangeListener(destinationLabel, destinationSearch)
            initOnClearSearchListener(viewModel::departureChanged)
        }
    }

    private fun initDestination() {
        with(destinationSearch) {
            viewModel.listenDestinationQueries(queryChanges())
            initDefaultFocusChangeListener(viewModel::destinationChanged)
            initOnSearchListener(viewModel::destinationChanged)
            initOnClearSearchListener(viewModel::destinationChanged)
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }

}