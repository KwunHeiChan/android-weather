package com.example.androidweather.di

import com.example.androidweather.di.scope.ActivityScoped
import com.example.androidweather.ui.history.HistoryActivity
import com.example.androidweather.ui.history.HistoryModule
import com.example.androidweather.ui.main.MainActivity
import com.example.androidweather.ui.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

  @ActivityScoped
  @ContributesAndroidInjector(modules = [MainModule::class])
  internal abstract fun contributeMainActivity(): MainActivity

  @ActivityScoped
  @ContributesAndroidInjector(modules = [HistoryModule::class])
  internal abstract fun contributeHistoryActivity(): HistoryActivity
}