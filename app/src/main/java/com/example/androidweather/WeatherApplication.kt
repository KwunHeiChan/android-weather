package com.example.androidweather

import android.content.Context
import androidx.multidex.MultiDex
import com.example.androidweather.di.DaggerAppComponent
import com.example.androidweather.manager.ActivityManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class WeatherApplication : DaggerApplication() {

  @Inject
  lateinit var activityManager: ActivityManager

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    registerActivityLifecycleCallbacks(activityManager)
  }

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
    return DaggerAppComponent.builder().application(this).build()
  }
}