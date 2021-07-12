package com.aleksejantonov.tajikair.di

import androidx.lifecycle.ViewModel
import com.aleksejantonov.tajikair.di.qualifiers.ViewModelKey
import com.aleksejantonov.tajikair.ui.map.FlyComputationViewModel
import com.aleksejantonov.tajikair.ui.main.MainViewModel
import com.aleksejantonov.tajikair.ui.map.MapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  abstract fun bindsMainViewModel(viewModel: MainViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(MapViewModel::class)
  abstract fun bindsMapViewModel(viewModel: MapViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(FlyComputationViewModel::class)
  abstract fun bindsFlyComputationViewModel(viewModel: FlyComputationViewModel): ViewModel
}