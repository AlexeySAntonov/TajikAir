package com.aleksejantonov.tajikair.ui.main

import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.sl.SL
import com.aleksejantonov.tajikair.ui.Screens.*
import com.aleksejantonov.tajikair.ui.base.BasePresenter
import com.aleksejantonov.tajikair.util.cityStub
import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxbinding2.InitialValueObservable
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import java.util.concurrent.TimeUnit

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    private val router = SL.componentManager().appComponent.router
    private val repository = SL.componentManager().appComponent.citiesRepository

    private val departureLocationRelay = BehaviorRelay.createDefault(cityStub())
    private val destinationLocationRelay = BehaviorRelay.createDefault(cityStub())

    override fun onFirstViewAttach() {
        Observables
            .combineLatest(
                departureLocationRelay,
                destinationLocationRelay
            ) { depCity, desCity -> depCity.fullName.isNotBlank() && desCity.fullName.isNotBlank() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { viewState.enableSearchButton(it) }
            .keepUntilDestroy()

        departureLocationRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { viewState.setDepartureSearchText(it.fullName) }
            .keepUntilDestroy()

        destinationLocationRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { viewState.setDestinationsSearchText(it.fullName) }
            .keepUntilDestroy()
    }

    fun departureChanged(city: City?) = departureLocationRelay.accept(requireNotNull(city))
    fun destinationChanged(city: City?) = destinationLocationRelay.accept(requireNotNull(city))

    fun listenDepartureQueries(observable: InitialValueObservable<CharSequence>) {
        observable
            .debounce(400, TimeUnit.MILLISECONDS)
            .switchMapSingle { query -> repository.searchCities(query.toString()) }
            .map { cities -> cities.map { LocationSuggestion(it) } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = viewState::applyDepartureResults,
                onError = Timber::e
            )
            .keepUntilDestroy()
    }

    fun listenDestinationQueries(observable: InitialValueObservable<CharSequence>) {
        observable
            .debounce(400, TimeUnit.MILLISECONDS)
            .switchMapSingle { query -> repository.searchCities(query.toString()) }
            .map { cities -> cities.map { LocationSuggestion(it) } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = viewState::applyDestinationResults,
                onError = Timber::e
            )
            .keepUntilDestroy()
    }

    fun onSearchClick() {
        router.forward(
            MAP_FRAGMENT,
            departureLocationRelay.value to destinationLocationRelay.value
        )
    }
}