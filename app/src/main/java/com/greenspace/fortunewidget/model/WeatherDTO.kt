package com.greenspace.fortunewidget.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherItem(
    @SerializedName("baseDate")
    var baseDate: String = "",
    @SerializedName("baseTime")
    var baseTime: String = "",
    @SerializedName("category")
    var category: String = "",
    @SerializedName("fcstDate")
    var fcstDate: String = "",
    @SerializedName("fcstTime")
    var fcstTime: String = "",
    @SerializedName("fcstValue")
    var fcstValue: String = "",
    @SerializedName("nx")
    var nx: Int = 0,
    @SerializedName("ny")
    var ny: Int = 0
)

data class WeatherDTO (val response : RESPONSE)
data class RESPONSE(val header : HEADER, val body : BODY)
data class HEADER(val resultCode : Int, val resultMsg : String)
data class BODY(val dataType : String, val items : ITEMS, val totalCount : Int)
data class ITEMS(val item : List<WeatherItem>)