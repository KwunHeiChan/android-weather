package com.example.androidweather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface CityDao {

  @Query("SELECT * FROM city ORDER BY last_searched_at DESC LIMIT 1")
  fun getLastSearchedCity(): Single<City>

  @Query("SELECT * FROM city ORDER BY name DESC")
  fun getAll(): Single<List<City>>

  @Query("DELETE FROM city WHERE id = :id")
  fun deleteCityById(id: Int): Single<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(city: City)
}