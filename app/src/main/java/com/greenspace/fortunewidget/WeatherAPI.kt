package com.greenspace.fortunewidget

import com.google.gson.JsonPrimitive
import com.greenspace.fortunewidget.model.WeatherDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WeatherAPI {
    @GET("getUltraSrtFcst")
    fun getUltraSrtFcst(@QueryMap querys : MutableMap<String, String>) : Call<WeatherDTO>
}