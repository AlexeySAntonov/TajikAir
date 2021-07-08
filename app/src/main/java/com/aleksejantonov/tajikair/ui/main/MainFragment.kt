package com.aleksejantonov.tajikair.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aleksejantonov.tajikair.databinding.FragmentMainBinding
import com.aleksejantonov.tajikair.di.DI
import com.aleksejantonov.tajikair.ui.base.BaseFragment
import com.aleksejantonov.tajikair.ui.main.CityItem.Companion.toCity
import com.aleksejantonov.tajikair.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainFragment : BaseFragment() {

  private val binding get() = _binding as FragmentMainBinding

  private val viewModel by viewModels<MainViewModel> { DI.appComponent.viewModelFactory() }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initDeparture()
    initDestination()
    binding.search.setOnClickListener { viewModel.onSearchClick() }
    lifecycleScope.launch { viewModel.enableSearchData.collect { enableSearchButton(it) } }
  }

  override fun onStatusBarHeight(statusBarHeight: Int) {
    binding.root.setPaddings(top = statusBarHeight)
  }

  override fun onNavigationBarHeight(navBarHeight: Int) {
    binding.root.setPaddings(bottom = navBarHeight)
  }

  private fun setDepartureSearchText(text: String) {
    safePostDelayed({ binding.departureSearch.setSearchText(text) }, 100L)
  }

  private fun setDestinationsSearchText(text: String) {
    safePostDelayed({ binding.destinationSearch.setSearchText(text) }, 100L)
  }

  private fun enableSearchButton(enabled: Boolean) {
    binding.search.isEnabled = enabled
    binding.search.alpha = if (enabled) 1f else 0.5f
  }

  private fun applyDepartureResults(cities: List<CityItem>) {
    binding.departureSearch.swapSuggestions(cities)
  }

  private fun applyDestinationResults(cities: List<CityItem>) {
    binding.destinationSearch.swapSuggestions(cities)
  }

  private fun initDeparture() {
    with(binding.departureSearch) {
      setType(SuggestionsSearchView.Type.DEPARTURE)
      onQueryChanged { newQuery -> viewModel.departureQueryChanged(newQuery) }
      onSuggestionClicked { suggestion -> viewModel.departureChanged(suggestion.toCity()) }
      onCleared { viewModel.departureChanged(cityStub()) }
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
    with(binding.destinationSearch) {
      setType(SuggestionsSearchView.Type.DESTINATION)
      onQueryChanged { newQuery -> viewModel.destinationQueryChanged(newQuery) }
      onSuggestionClicked { suggestion -> viewModel.destinationChanged(suggestion.toCity()) }
      onCleared { viewModel.destinationChanged(cityStub()) }
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