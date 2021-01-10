package com.example.androidweather.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityManager @Inject constructor() : Application.ActivityLifecycleCallbacks {

  var currentActivity: WeakReference<Activity?> = WeakReference(null)

  override fun onActivityPaused(activity: Activity) {
    /** Do nothing */
  }

  override fun onActivityResumed(activity: Activity) {
    currentActivity = WeakReference(activity)
  }

  override fun onActivityStarted(activity: Activity) {
    /** Do nothing */
  }

  override fun onActivityDestroyed(activity: Activity) {
    /** Do nothing */
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    /** Do nothing */
  }

  override fun onActivityStopped(activity: Activity) {
    /** Do nothing */
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    /** Do nothing */
  }
}