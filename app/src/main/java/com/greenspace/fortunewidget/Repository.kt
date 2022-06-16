package com.greenspace.fortunewidget

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonPrimitive
import com.greenspace.fortunewidget.model.WeatherDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {


    fun getUltraSrtFcst(querys : MutableMap<String, String>,
                        myResponse : MutableLiveData<WeatherDTO>)
    {
        RetrofitInstance.api.getUltraSrtFcst(querys).enqueue(object : Callback<WeatherDTO> {
            override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
                Log.w("태그Repository", response.body().toString())

                if(response.isSuccessful)
                    myResponse.postValue(response.body() as WeatherDTO)
            }

            override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
                t.stackTrace
            }
        })

    }
}