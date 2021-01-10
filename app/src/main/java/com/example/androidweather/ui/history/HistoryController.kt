package com.example.androidweather.ui.history

import com.airbnb.epoxy.TypedEpoxyController
import com.example.androidweather.epoxy.cityView
import com.example.androidweather.util.KeyedListener
import com.jakewharton.rxrelay3.PublishRelay

class HistoryController : TypedEpoxyController<List<HistoryControllerItem>>() {

  /**
   * The [PublishRelay] of delete city button click listener, which passes the city ID
   * [Int] for subscription outside
   */
  val btnRemoveClickRelay: PublishRelay<Int> = PublishRelay.create<Int>()

  override fun buildModels(data: List<HistoryControllerItem>?) {
    data?.forEach { controllerItem ->
      when (controllerItem) {
        is HistoryControllerItem.CityItem -> {
          cityView {
            id("city_city_${controllerItem.id}")
            cityName(controllerItem.name)
            btnRemoveKeyedOnClickListener(
              KeyedListener.create(
                controllerItem.id,
                {
                  btnRemoveClickRelay.accept(controllerItem.id)
                }
              )
            )
          }
        }
      }
    }
  }
}