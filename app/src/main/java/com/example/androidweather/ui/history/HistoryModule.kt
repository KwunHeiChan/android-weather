package com.example.androidweather.ui.history

import com.example.androidweather.di.scope.ActivityScoped
import com.example.androidweather.repository.SearchHistoryRepository
import com.example.androidweather.viewmodel.HistoryViewModelFactory
import dagger.Module
import dagger.Provides

@Module
open class HistoryModule {

  @ActivityScoped
  @Provides
  fun provideHistoryActionProcessorHolder(
    searchHistoryRepository: SearchHistoryRepository
  ): HistoryActionProcessorHolder = HistoryActionProcessorHolder(searchHistoryRepository)

  @ActivityScoped
  @Provides
  fun provideHistoryController(): HistoryController = HistoryController()

  @ActivityScoped
  @Provides
  fun provideHistoryViewModelFactory(
    historyActionProcessorHolder: HistoryActionProcessorHolder
  ): HistoryViewModelFactory = HistoryViewModelFactory(historyActionProcessorHolder)
}