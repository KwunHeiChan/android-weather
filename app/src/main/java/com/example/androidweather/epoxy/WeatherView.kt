package com.example.androidweather.epoxy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.bumptech.glide.Glide
import com.example.androidweather.databinding.ViewWeatherBinding

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class WeatherView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  private var binding = ViewWeatherBinding.inflate(LayoutInflater.from(context), this)

  @TextProp
  fun setCityName(cityName: CharSequence) {
    binding.tvCityName.text = cityName
  }

  @ModelProp
  fun setWeatherIconUrl(weatherIconUrl: String) {
    Glide.with(context)
      .load(weatherIconUrl)
      .into(binding.ivWeather)
  }

  @TextProp
  fun setWeatherDescription(weatherDescription: CharSequence) {
    binding.tvWeatherDescription.text = weatherDescription
  }

  @TextProp
  fun setTemp(temp: CharSequence) {
    binding.tvTemp.text = temp
  }

  @TextProp
  fun setFeelsLike(feelsLike: CharSequence) {
    binding.tvFeelsLike.text = feelsLike
  }

  @TextProp
  fun setHighTemp(highTemperature: CharSequence) {
    binding.tvHighTemp.text = highTemperature
  }

  @TextProp
  fun setLowTemp(lowTemperature: CharSequence) {
    binding.tvLowTemp.text = lowTemperature
  }
}