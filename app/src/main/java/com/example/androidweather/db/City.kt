package com.example.androidweather.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
  @PrimaryKey
  @ColumnInfo(name = "id")
  val id: Int,
  @ColumnInfo(name = "name")
  val name: String,
  @ColumnInfo(name = "last_searched_at")
  val lastSearchedAt: Long
)