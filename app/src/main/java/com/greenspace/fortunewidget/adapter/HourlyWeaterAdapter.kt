package com.greenspace.fortunewidget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenspace.fortunewidget.MainActivity
import com.greenspace.fortunewidget.R
import com.greenspace.fortunewidget.model.HourlyWeather
import com.greenspace.fortunewidget.model.WeatherDTO
import com.greenspace.fortunewidget.model.WeatherItem
import kotlinx.android.synthetic.main.item_hourly_weather.view.*
import java.lang.NumberFormatException

class HourlyWeaterAdapter(private val context: Context, private val datas: WeatherDTO) :
    RecyclerView.Adapter<HourlyWeaterAdapter.ViewHolder>() {
    private val items = datas.response.body.items.item
    private lateinit var mListener: OnItemClickListener
//    private lateinit var firstItem: HourlyWeather

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_hourly_weather, parent, false)
        view.layoutParams.width = (parent.width / 3.7f).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourlyList = ArrayList<WeatherItem>()
        for (i in position until items.size step 6) {
            hourlyList.add(items[i])
        }
        val hourlyWeather = HourlyWeather(hourlyList)
        if(position == 0) (context as MainActivity).setDetailWeather(hourlyWeather)
        holder.bind(hourlyWeather)
    }

    override fun getItemCount(): Int = 6

    interface OnItemClickListener {
        fun onItemClick(v : View, item: HourlyWeather)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: HourlyWeather) {
            itemView.textViewFcstTime.text = item.getFcstTime()
            itemView.textViewTemperature.text = item.getTemperature()
            itemView.textViewPrecipitation.text = item.getPrecipitation()
            itemView.textViewSky.text = item.getCloudState()
            itemView.textViewHumidity.text = item.getHumidity()
            if(item.precipitationType == 0) {
                itemView.textViewPrecipitationType.visibility = View.GONE
            }
            else {
                itemView.textViewPrecipitationType.text = item.getPrecipitationType()
            }
            itemView.textViewLightning.text = item.getLightning()
            itemView.textViewWindDirection.text = item.getWindDirection()
            itemView.textViewWindSpeed.text = item.getWindSpeed()
            itemView.setOnClickListener {
                mListener.onItemClick(it, item)
            }

        }
    }
}