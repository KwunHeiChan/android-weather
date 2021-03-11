package com.example.androidweather.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.ui.history.HistoryViewModel
import com.example.androidweather.ui.main.MainViewModel
import com.example.androidweather.viewmodel.WeatherViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(HistoryViewModel::class)
  abstract fun bindHistoryViewModel(historyViewModel: HistoryViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

  @Binds
  abstract fun bindViewModelFactory(factory: WeatherViewModelFactory): ViewModelProvider.Factory
}