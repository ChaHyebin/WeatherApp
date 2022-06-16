package com.greenspace.fortunewidget.model

import kotlinx.android.synthetic.main.item_hourly_weather.view.*

class HourlyWeather {
    var fcstTime: Int? = null
    var temperature: Int? = null
    var precipitation: Float? = null
    var cloudState: Int? = null
    var humidity: Int? = null
    var precipitationType: Int? = null
    var lightning: Float? = null
    var windDirection: Float? = null
    var windSpeed: Int? = null

    constructor(items: List<WeatherItem>) {
        try {
            fcstTime = items[0].fcstTime.substring(0, 2).toInt()
        } catch (e: Exception) { e.stackTrace }

        val t1h = items.filter { it.category == "T1H" }
        if (t1h.isNotEmpty()) {
            temperature = t1h[0].fcstValue.toIntOrNull()
        }

        val rn1 = items.filter { it.category == "RN1" }
        if (rn1.isNotEmpty()) {
            precipitation = if(rn1[0].fcstValue == "강수없음") {
                0f
            }
            else {
                val strRn1 = rn1[0].fcstValue.replace("mm", "")
                strRn1.toFloatOrNull()
            }
        }

        val sky = items.filter { it.category == "SKY" }
        if (sky.isNotEmpty()) {
            cloudState = sky[0].fcstValue.toIntOrNull()
        }

        val reh = items.filter { it.category == "REH" }
        if (reh.isNotEmpty()) {
            humidity = reh[0].fcstValue.toIntOrNull()
        }

        val pty = items.filter { it.category == "PTY" }
        if (pty.isNotEmpty()) {
            precipitationType = pty[0].fcstValue.toIntOrNull()
        }

        val lgt = items.filter { it.category == "LGT" }
        if (lgt.isNotEmpty()) {
            lightning = lgt[0].fcstValue.toFloatOrNull()
        }

        val vec = items.filter { it.category == "VEC" }
        if (vec.isNotEmpty()) {
            windDirection = vec[0].fcstValue.toFloatOrNull()
        }

        val wsd = items.filter { it.category == "WSD" }
        if (wsd.isNotEmpty()) {
            windSpeed = wsd[0].fcstValue.toIntOrNull()
        }
    }

    fun getFcstTime(): String {
        return if(fcstTime != null) "${fcstTime}시" else "불러오기 실패"
    }

    fun getTemperature(): String {
        return if(temperature != null) "${temperature}℃" else "불러오기 실패"
    }

    fun getPrecipitation(): String {
        return when {
            (precipitation == null) -> "불러오기 실패"
            (precipitation!! == 0f) -> "강수없음"
            (precipitation!! < 1f) -> "1.0mm 미만"
            (precipitation!! >= 1f && precipitation!! < 30f) -> "${String.format("%.1f", precipitation)}mm"
            (precipitation!! >= 30f && precipitation!! < 50f) -> "30.0~50.0mm"
            (precipitation!! >= 50f) -> "50.0mm 미만"
            else -> "불러오기 실패"
        }
    }

    fun getCloudState(): String {
        return if (cloudState != null) {
            when (cloudState) {
                1 -> "맑음"
                3 -> "구름많음"
                4 -> "흐림"
                else -> "불러오기 실패"
            }
        } else "불러오기 실패"
    }

    fun getHumidity(): String {
        return if (humidity != null) "${humidity}%" else "불러오기 실패"
    }

    fun getLightning(): String {
        return if(lightning != null) "${lightning}KA/km²" else "불러오기 실패"
    }

    fun getPrecipitationType(): String {
        return if(precipitationType != null) {
            when (precipitationType) {
                0 -> "없음"
                1 -> "비"
                2 -> "비/눈"
                3 -> "눈"
                5 -> "빗방울"
                6 -> "빗방울눈날림"
                7 -> "눈날림"
                else -> "불러오기 실패"
            }
        } else "불러오기 실패"
    }

    fun getWindDirection(): String {
        return if (windDirection != null) {
            val windDir = ((windDirection!! + 22.5f * 0.5f) / 22.5f).toInt()

            return when (windDir) {
                0 -> "북풍(N)"
                1 -> "북동풍(NNE)"
                2 -> "북동풍(NE)"
                3 -> "북동풍(EME)"
                4 -> "동풍(E)"
                5 -> "남동풍(ESE)"
                6 -> "남동풍(SE)"
                7 -> "남동풍(SSE)"
                8 -> "남풍(S)"
                9 -> "남서풍(SSW)"
                10 -> "남서풍(SW)"
                11 -> "남서풍(WSW)"
                12 -> "서풍(W)"
                13 -> "북서풍(WNW)"
                14 -> "북서풍(NW)"
                15 -> "북서풍(NNW)"
                16 -> "북풍(N)"
                else -> "불러오기 실패"
            }
        } else "불러오기 실패"
    }

    fun getWindSpeed(): String {
        return when {
            (windSpeed == null) -> "불러오기 실패"
            (windSpeed!! < 4) -> "${windSpeed}m/s(약한바람)"
            (windSpeed!! in 4..8) -> "${windSpeed}m/s(약간강한바람)"
            (windSpeed!! in 9..13) -> "${windSpeed}m/s(강한바람)"
            (windSpeed!! >= 14) -> "${windSpeed}m/s(매우강한바람)"
            else -> "불러오기 실패"
        }
    }
}