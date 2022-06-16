package com.greenspace.fortunewidget

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.greenspace.fortunewidget.adapter.HourlyWeaterAdapter
import com.greenspace.fortunewidget.model.HourlyWeather
import com.greenspace.fortunewidget.model.WeatherItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_hourly_weather.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var hourlyWeaterAdapter: HourlyWeaterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        requestLocationPermission()

        val current = LocalDateTime.now()
        val week =
            when(current.dayOfWeek.value) {
                1 -> "월"
                2 -> "화"
                3 -> "수"
                4 -> "목"
                5 -> "금"
                6 -> "토"
                7 -> "일"
                else -> "정보없음"
            }
        textClockDetailNow.format12Hour = "MM월 dd일($week) hh:mm"
        textClockDetailNow.format24Hour = "MM월 dd일($week) hh:mm"

        viewModel.getResponse().observe(this, Observer {
            if (recyclerViewHourlyWeather.adapter == null) {
                hourlyWeaterAdapter = HourlyWeaterAdapter(this, it)
                hourlyWeaterAdapter.setOnItemClickListener(object : HourlyWeaterAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, item: HourlyWeather) {
                        setDetailWeather(item)
                    }
                })
                recyclerViewHourlyWeather.adapter = hourlyWeaterAdapter
            }
            hourlyWeaterAdapter.notifyDataSetChanged()
        })

        textViewLocation.setOnClickListener {
            onLocationListener()
        }
    }

    fun setDetailWeather(item: HourlyWeather) {
        CoroutineScope(Dispatchers.Main).launch {
            textViewDetailFcstTime.text = item.getFcstTime()
            textViewDetailTemperature.text = item.getTemperature()
            textViewDetailPrecipitation.text = item.getPrecipitation()
            textViewDetailSky.text = item.getCloudState()
            textViewDetailHumidity.text = item.getHumidity()
            if(item.precipitationType == 0) {
                textViewDetailPrecipitationType.visibility = View.GONE
            }
            else {
                textViewDetailPrecipitationType.text = item.getPrecipitationType()
            }
            textViewDetailLightning.text = item.getLightning()
            textViewDetailWindDirection.text = item.getWindDirection()
            textViewDetailWindSpeed.text = item.getWindSpeed()
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val geocoder = Geocoder(this)
                        val cityList = geocoder.getFromLocation(latitude, longitude, 10)
                        if(cityList != null && cityList.size != 0) {
                            textViewLocation.text = cityList[0].thoroughfare
                        }

                        viewModel.setPoint(latitude, longitude)
                    }
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "현재 위치의 날씨정보를 받아오기 위해선 위치권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }

    private fun onLocationListener() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val geocoder = Geocoder(this)
                        val cityList = geocoder.getFromLocation(latitude, longitude, 10)
                        if(cityList != null && cityList.size != 0) {
                            textViewLocation.text = cityList[0].thoroughfare
                        }

                        viewModel.setPoint(latitude, longitude)
                    }
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "현재 위치의 날씨정보를 받아오기 위해선 위치권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
            else -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("위치 권한 설정")
                    .setMessage("현재 위치의 날씨정보를 받아오기 위해선 위치권한이 필요합니다.")
                    .setPositiveButton("권한 허용하러 가기") { dialog, which ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:$packageName"))
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            startActivity(intent)
                        }
                    }
                    .setNegativeButton("취소하기", null)
                    .create()
                    .show()
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions())
    { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                requestLocationPermission()
            }
            else -> {
                textViewLocation.text = "신림동"
                viewModel.getUltraSrtFcst()
            }
        }
    }

    var mBackWait: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackWait >= 1000) {
            mBackWait = System.currentTimeMillis()
        } else {
            finishAndRemoveTask()
            System.runFinalization()
            exitProcess(0)
        }
    }
}