package com.example.androidweather.repository

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.androidweather.R
import com.example.androidweather.testing.OpenForTesting
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class LocationRepository @Inject constructor(
  private val application: Application,
  private val locationManager: LocationManager?
) {

  @Suppress("DEPRECATION")
  @SuppressLint("MissingPermission")
  fun getCurrentLocation(): Observable<Location> {
    return Observable.create<Location> { emitter ->
      when {
        locationManager == null -> {
          emitter.onError(Exception(application.getString(R.string.service_not_supported)))
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
          locationManager.getCurrentLocation(
            LocationManager.NETWORK_PROVIDER,
            null,
            ContextCompat.getMainExecutor(application),
            { emitter.onNext(it) }
          )
        }
        else -> {
          locationManager.requestSingleUpdate(
            LocationManager.NETWORK_PROVIDER,
            { emitter.onNext(it) },
            null
          )
        }
      }
    }
  }
}