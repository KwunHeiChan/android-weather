package com.example.androidweather.epoxy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.example.androidweather.databinding.ViewCityBinding
import com.example.androidweather.util.KeyedListener

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CityView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  private var binding = ViewCityBinding.inflate(LayoutInflater.from(context), this)

  @TextProp
  fun setCityName(cityName: CharSequence) {
    binding.tvCityName.text = cityName
  }

  @ModelProp(ModelProp.Option.NullOnRecycle)
  fun setBtnRemoveKeyedOnClickListener(listener: KeyedListener<*, () -> Unit>?) {
    binding.btnRemove.setOnClickListener {
      listener?.callback?.invoke()
    }
  }
}