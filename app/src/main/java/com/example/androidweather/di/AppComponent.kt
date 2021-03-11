package com.example.androidweather.di

import android.app.Application
import com.example.androidweather.WeatherApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
  AndroidSupportInjectionModule::class,
  AppModule::class,
  ActivityBindingModule::class,
  ViewModelModule::class
])
interface AppComponent : AndroidInjector<WeatherApplication> {

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun build(): AppComponent
  }
}