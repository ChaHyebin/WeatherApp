package com.greenspace.fortunewidget

import android.graphics.Point
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonPrimitive
import com.greenspace.fortunewidget.model.WeatherDTO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*


class MainViewModel(private val repository: Repository) : ViewModel() {
    val myResponse : MutableLiveData<WeatherDTO> = MutableLiveData()
    var point = Point(59,125)

    fun getUltraSrtFcst() {
        val querys : MutableMap<String, String> = mutableMapOf()
        querys["serviceKey"] = Contents.SERVICE_KEY
        querys["dataType"] = "JSON"
        var current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        if(current.minute < 45) {
            current = current.minusHours(1)
        }

        querys["base_date"] = current.format(formatter)
        querys["base_time"] = "${current.hour}30"
        Log.w("태그", querys["base_date"] + querys["base_time"])
        querys["numOfRows"] = "60"
        querys["nx"] = point.x.toString()
        querys["ny"] = point.y.toString()

        viewModelScope.launch {
            repository.getUltraSrtFcst(querys, myResponse)
        }
    }

    fun getResponse() : MutableLiveData<WeatherDTO> {
        return myResponse
    }

    fun setPoint(lat: Double, lon: Double) {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        val DEGRAD = Math.PI / 180.0
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD
        var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
        var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = sf.pow(sn) * cos(slat1) / sn
        var ro = tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / ro.pow(sn)

        var ra = tan(Math.PI * 0.25 + lat * DEGRAD * 0.5)
        ra = re * sf / ra.pow(sn)
        var theta = lon * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        point.x = floor(ra * sin(theta) + XO + 0.5).toInt()
        point.y = floor(ro - ra * cos(theta) + YO + 0.5).toInt()

        Log.w("태그", "${point.x} ${point.y}")
        getUltraSrtFcst()
    }
}