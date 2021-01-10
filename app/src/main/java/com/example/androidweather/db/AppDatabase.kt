package com.example.androidweather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [
    City::class
  ],
  version = 1
)
@TypeConverters
abstract class AppDatabase : RoomDatabase() {
  abstract fun cityDao(): CityDao
}