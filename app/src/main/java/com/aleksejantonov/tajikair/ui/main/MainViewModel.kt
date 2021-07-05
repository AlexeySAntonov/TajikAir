package com.aleksejantonov.tajikair.ui.main

import androidx.lifecycle.viewModelScope
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.di.qualifiers.DispatcherDefault
import com.aleksejantonov.tajikair.model.CitiesRepository
import com.aleksejantonov.tajikair.navigation.AppRouter
import com.aleksejantonov.tajikair.ui.Screens.*
import com.aleksejantonov.tajikair.ui.base.BaseViewModel
import com.aleksejantonov.tajikair.util.cityStub
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

  private val _departureLocationData = MutableStateFlow(cityStub())
  val departureLocationData: StateFlow<City> = _departureLocationData

  private val _destinationLocationData = MutableStateFlow(cityStub())
  val destinationLocationData: StateFlow<City> = _destinationLocationData

  private val _enableSearchData = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  val enableSearchData: SharedFlow<Boolean> = _enableSearchData

  private val _departureQueryData = MutableSharedFlow<String>()
  private val _destinationQueryData = MutableSharedFlow<String>()

  val departureSuggestionsData = MutableSharedFlow<List<LocationSuggestion>>()
  val destinationSuggestionsData = MutableSharedFlow<List<LocationSuggestion>>()

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
        .map { cities -> cities.map { LocationSuggestion(it) } }
        .collect { departureSuggestionsData.emit(it) }
    }

    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      _destinationQueryData
        .debounce(400L)
        .flatMapLatest { query -> repository.searchCities(query) }
        .map { cities -> cities.map { LocationSuggestion(it) } }
        .collect { destinationSuggestionsData.emit(it) }
    }

  }

  fun departureQueryChanged(query: String) {
    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
      _departureQueryData.emit(query)
    }
  }

  fun destinationQueryChanged(query: String) {
    viewModelScope.launch(dispatcherDefault + exceptionHandler) {
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
      _departureLocationData.value to _destinationLocationData.value
    )
  }
}