package com.example.androidweather.di

import android.app.Application
import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.androidweather.BASE_URL
import com.example.androidweather.api.WeatherService
import com.example.androidweather.db.AppDatabase
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
open class AppModule {

  @Singleton
  @Provides
  fun provideApplicationContext(application: Application): Context = application.applicationContext

  @Singleton
  @Provides
  fun provideObjectMapper(): ObjectMapper =
    ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  @Singleton
  @Provides
  fun provideConverterFactory(objectMapper: ObjectMapper): Converter.Factory =
    JacksonConverterFactory.create(objectMapper)

  @Provides
  @Singleton
  fun provideChuckerInterceptor(context: Context): ChuckerInterceptor =
    ChuckerInterceptor.Builder(context).build()

  @Singleton
  @Provides
  fun provideCallAdapterFactory(): CallAdapter.Factory = RxJava3CallAdapterFactory.create()

  @Singleton
  @Provides
  fun provideOkHttpClient(chuckerInterceptor: ChuckerInterceptor): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(chuckerInterceptor)
      .build()

  @Singleton
  @Provides
  fun provideRetrofit(
    callAdapterFactory: CallAdapter.Factory,
    converterFactory: Converter.Factory,
    okHttpClient: OkHttpClient
  ): Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(converterFactory)
    .addCallAdapterFactory(callAdapterFactory)
    .client(okHttpClient)
    .build()

  @Singleton
  @Provides
  fun provideWeatherService(retrofit: Retrofit): WeatherService =
    retrofit.create(WeatherService::class.java)

  @Provides
  fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

  @Singleton
  @Provides
  fun provideLocationManager(application: Application): LocationManager? =
    ContextCompat.getSystemService(application, LocationManager::class.java)

  @Singleton
  @Provides
  fun provideAppDatabase(application: Application) =
    Room
      .databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "android-weather-database"
      )
      .build()

  @Singleton
  @Provides
  fun provideCityDao(appDatabase: AppDatabase) = appDatabase.cityDao()
}