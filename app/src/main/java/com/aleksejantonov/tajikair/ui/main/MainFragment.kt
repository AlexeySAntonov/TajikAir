package com.aleksejantonov.tajikair.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.util.initDefaultFocusChangeListener
import com.aleksejantonov.tajikair.util.initOnClearSearchListener
import com.aleksejantonov.tajikair.util.initOnSearchListener
import com.aleksejantonov.tajikair.util.initSuggestionsHeightChangeListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainFragment : BaseFragment(R.layout.fragment_main) {

  private val viewModel by viewModels<MainViewModel> { DI.appComponent.viewModelFactory() }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initDeparture()
    initDestination()
    search.setOnClickListener { viewModel.onSearchClick() }
    lifecycleScope.launch { viewModel.enableSearchData.collect { enableSearchButton(it) } }
  }

  private fun setDepartureSearchText(text: String) {
    departureSearch.setSearchText(text)
  }

  private fun setDestinationsSearchText(text: String) {
    destinationSearch.setSearchText(text)
  }

  private fun enableSearchButton(enabled: Boolean) {
    search.isEnabled = enabled
    search.alpha = if (enabled) 1f else 0.5f
  }

  private fun applyDepartureResults(locations: List<LocationSuggestion>) {
    departureSearch.swapSuggestions(locations)
  }

  private fun applyDestinationResults(locations: List<LocationSuggestion>) {
    destinationSearch.swapSuggestions(locations)
  }

  private fun initDeparture() {
    with(departureSearch) {
      setOnQueryChangeListener { _, newQuery -> viewModel.departureQueryChanged(newQuery) }
      initDefaultFocusChangeListener(viewModel::departureChanged)
      initOnSearchListener(viewModel::departureChanged)
      initSuggestionsHeightChangeListener(destinationLabel, destinationSearch)
      initOnClearSearchListener(viewModel::departureChanged)
      lifecycleScope.launchWhenCreated {
        viewModel.departureLocationData.collect { city ->
          setDepartureSearchText(city.fullName)
        }
      }
      lifecycleScope.launchWhenCreated {
        viewModel.departureSuggestionsData.collect { suggestions ->
          applyDepartureResults(suggestions)
        }
      }
    }
  }

  private fun initDestination() {
    with(destinationSearch) {
      setOnQueryChangeListener { _, newQuery -> viewModel.destinationQueryChanged(newQuery) }
      initDefaultFocusChangeListener(viewModel::destinationChanged)
      initOnSearchListener(viewModel::destinationChanged)
      initOnClearSearchListener(viewModel::destinationChanged)
      lifecycleScope.launchWhenCreated {
        viewModel.destinationLocationData.collect { city ->
          setDestinationsSearchText(city.fullName)
        }
      }
      lifecycleScope.launchWhenCreated {
        viewModel.destinationSuggestionsData.collect { suggestions ->
          applyDestinationResults(suggestions)
        }
      }
    }
  }

  companion object {
    fun newInstance() = MainFragment()
  }

}