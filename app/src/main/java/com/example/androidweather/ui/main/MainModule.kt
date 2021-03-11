package com.example.androidweather.ui.main

import com.example.androidweather.di.scope.ActivityScoped
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.Module
import dagger.Provides

@Module
open class MainModule {

  @ActivityScoped
  @Provides
  fun provideMainController(): MainController = MainController()

  @ActivityScoped
  @Provides
  fun provideRxPermissions(activity: MainActivity): RxPermissions = RxPermissions(activity)
}