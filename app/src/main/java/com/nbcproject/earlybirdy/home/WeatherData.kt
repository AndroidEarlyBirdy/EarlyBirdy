package com.nbcproject.earlybirdy.home

import android.util.Log
import com.nbcproject.earlybirdy.R
import org.json.JSONException
import org.json.JSONObject

class WeatherData {
    lateinit var tempString: String
    lateinit var icon: String
    lateinit var weatherType: String
    private var weatherId: Int = 0
    private var tempInt: Int = 0
    lateinit var iconUrl: String

    fun fromJson(jsonObject: JSONObject?): WeatherData? {
        try {
            val currentWeather = jsonObject?.getJSONObject("current")
            var weatherData = WeatherData()
            weatherData.weatherId = currentWeather?.getInt("weather_code") ?: 0
            weatherData.weatherType = currentWeather?.getString("weather_descriptions") ?: "Unknown"
            weatherData.icon = updateWeatherIcon(weatherData.weatherId).toString()
            weatherData.tempString = currentWeather?.getString("temperature") ?: "0"

            val isDay = currentWeather?.getString("is_day") != "no"

//            // 아이콘 URL 설정
//            weatherData.iconUrl = "https://openweathermap.org/img/wn/${weatherData.icon}${if(isDay){"d"}else{"n"}}@2x.png"
//            Log.d("weather_icon_url", weatherData.iconUrl)
//            weatherData.iconUrl = currentWeather?.getJSONArray("weather_icons")!![0].toString()
            //weatherData.iconUrl = "https://openweathermap.org/img/wn/02d@4x.png"
            //weatherData.iconUrl = "https://weatherstack.com/static/img/weather/${weatherData.icon}.png"
            return weatherData
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
    }

    private fun updateWeatherIcon(condition: Int): Int {
        Log.d("weather_code", condition.toString())
        return when (condition) {
            113 -> R.drawable.ic_sunny  // 맑음
            116 -> R.drawable.ic_partly_cloudy  // 약간 흐림
            119 -> R.drawable.ic_cloud // 흐림
            122 -> R.drawable.ic_cloud// 흐림
            143 -> R.drawable.ic_mist  // 안개
            176 -> R.drawable.ic_rainy_light  // 가벼운 비
            263 -> R.drawable.ic_rainy  // 가벼운 소나기
            296 -> R.drawable.ic_rainy_light  // 가벼운 비
            308 -> R.drawable.ic_rainy_heavy  // 강한 비
            356 -> R.drawable.ic_snow // 가벼운 눈
            359 -> R.drawable.ic_snow   // 가벼운 눈
            386 -> R.drawable.ic_thunderstorm  // 천둥 번개
            else -> R.drawable.ic_none   // 알 수 없는 날씨 조건에 대한 기본 아이콘
        }
//        return when (condition) {
//            113 -> "clear"  // 맑음
//            116 -> "partly_cloudy"  // 약간 흐림
//            119 -> "cloudy"  // 흐림
//            143 -> "mist"  // 안개
//            176 -> "light_rain"  // 가벼운 비
//            263 -> "light_showers"  // 가벼운 소나기
//            296 -> "light_rain"  // 가벼운 비
//            308 -> "heavy_rain"  // 강한 비
//            356 -> "light_snow"  // 가벼운 눈
//            359 -> "light_snow"  // 가벼운 눈
//            386 -> "thunderstorm"  // 천둥 번개
//            else -> "unknown"  // 알 수 없는 날씨 조건에 대한 기본 아이콘
//        }
    }
}

