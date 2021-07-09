package com.aleksejantonov.tajikair.ui.main

import androidx.lifecycle.viewModelScope
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.di.qualifiers.DispatcherDefault
import com.aleksejantonov.tajikair.model.CitiesRepository
import com.aleksejantonov.tajikair.navigation.AppRouter
import com.aleksejantonov.tajikair.ui.Screens.*
import com.aleksejantonov.tajikair.ui.base.BaseViewModel
import com.aleksejantonov.tajikair.util.value
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
  private val router: AppRouter,
  private val repository: CitiesRepository,
  @DispatcherDefault private val dispatcherDefault: CoroutineDispatcher,
) : BaseViewModel() {

  private val _departureLocationData = MutableSharedFlow<City>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  val departureLocationData: SharedFlow<City> = _departureLocationData

  private val _destinationLocationData = MutableSharedFlow<City>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  val destinationLocationData: SharedFlow<City> = _destinationLocationData

  private val _enableSearchData = MutableStateFlow(false)
  val enableSearchData: StateFlow<Boolean> = _enableSearchData

  private val _departureQueryData = MutableSharedFlow<String>()
  private val _destinationQueryData = MutableSharedFlow<String>()

  private val _departureSuggestionsData = MutableSharedFlow<List<CityItem>>()
  val departureSuggestionsData: SharedFlow<List<CityItem>> = _departureSuggestionsData

  private val _destinationSuggestionsData = MutableSharedFlow<List<CityItem>>()
  val destinationSuggestionsData: SharedFlow<List<CityItem>> = _destinationSuggestionsData

  init {
    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      combine(
        _departureLocationData,
        _destinationLocationData
      ) { depCity, desCity ->
        depCity.fullName.isNotBlank() && desCity.fullName.isNotBlank()
      }.collect {
        _enableSearchData.emit(it)
      }
    }

    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      _departureQueryData
        .debounce(400L)
        .flatMapLatest { query -> repository.searchCities(query) }
        .map { cities -> cities.filter { it.iata != _destinationLocationData.value()?.iata }.map { CityItem.from(it) } }
        .collect { _departureSuggestionsData.emit(it) }
    }

    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      _destinationQueryData
        .debounce(400L)
        .flatMapLatest { query -> repository.searchCities(query) }
        .map { cities -> cities.filter { it.iata != _departureLocationData.value()?.iata }.map { CityItem.from(it) } }
        .collect { _destinationSuggestionsData.emit(it) }
    }

  }

  fun departureQueryChanged(query: String) {
    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      _enableSearchData.emit(false)
      _departureQueryData.emit(query)
    }
  }

  fun destinationQueryChanged(query: String) {
    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      _enableSearchData.emit(false)
      _destinationQueryData.emit(query)
    }
  }

  fun departureChanged(city: City?) {
    city?.let {
      viewModelScope.launch(dispatcherDefault + exceptionHandler) {
        _departureLocationData.emit(it)
      }
    }
  }
  fun destinationChanged(city: City?) {
    city?.let {
      viewModelScope.launch(dispatcherDefault + exceptionHandler) {
        _destinationLocationData.emit(it)
      }
    }
  }

  fun onSearchClick() {
    router.forward(
      MAP_FRAGMENT,
      _departureLocationData.value() to _destinationLocationData.value()
    )
  }
}