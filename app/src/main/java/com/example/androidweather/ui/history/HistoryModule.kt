package com.example.androidweather.ui.history

import com.example.androidweather.di.scope.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
open class HistoryModule {

  @ActivityScoped
  @Provides
  fun provideHistoryController(): HistoryController = HistoryController()
}