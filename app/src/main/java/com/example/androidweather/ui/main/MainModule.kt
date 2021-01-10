package com.example.androidweather.ui.main

import android.app.Application
import com.example.androidweather.di.scope.ActivityScoped
import com.example.androidweather.repository.LocationRepository
import com.example.androidweather.repository.SearchHistoryRepository
import com.example.androidweather.repository.WeatherRepository
import com.example.androidweather.viewmodel.MainViewModelFactory
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.Module
import dagger.Provides

@Module
open class MainModule {

  @ActivityScoped
  @Provides
  fun provideMainActionProcessorHolder(
    application: Application,
    locationRepository: LocationRepository,
    rxPermissions: RxPermissions,
    searchHistoryRepository: SearchHistoryRepository,
    weatherRepository: WeatherRepository
  ): MainActionProcessorHolder =
    MainActionProcessorHolder(
      application,
      locationRepository,
      rxPermissions,
      searchHistoryRepository,
      weatherRepository
    )

  @ActivityScoped
  @Provides
  fun provideMainController(): MainController = MainController()

  @ActivityScoped
  @Provides
  fun provideMainViewModelFactory(
    mainActionProcessorHolder: MainActionProcessorHolder
  ): MainViewModelFactory =
    MainViewModelFactory(mainActionProcessorHolder)

  @ActivityScoped
  @Provides
  fun provideRxPermissions(activity: MainActivity): RxPermissions = RxPermissions(activity)
}