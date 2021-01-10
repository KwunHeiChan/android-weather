package com.example.androidweather.repository

import com.example.androidweather.db.City
import com.example.androidweather.db.CityDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(private val cityDao: CityDao) {

  fun getLastSearchedCity(): Observable<City> {
    return cityDao.getLastSearchedCity().toObservable().subscribeOn(Schedulers.io())
  }

  fun getSearchHistory(): Observable<List<City>> {
    return cityDao.getAll().toObservable().subscribeOn(Schedulers.io())
  }

  fun insert(id: Int, name: String) {
    cityDao.insert(
      City(
        id = id,
        name = name,
        lastSearchedAt = Calendar.getInstance().timeInMillis
      )
    )
  }

  fun remove(id: Int): Observable<Int> {
    return cityDao.deleteCityById(id).toObservable().subscribeOn(Schedulers.io())
  }
}