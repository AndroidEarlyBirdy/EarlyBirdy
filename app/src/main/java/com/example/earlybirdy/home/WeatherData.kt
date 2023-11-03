package com.example.earlybirdy.home

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
            weatherData.icon = updateWeatherIcon(weatherData.weatherId)
            weatherData.tempString = currentWeather?.getString("temperature") ?: "0"

            val isDay = currentWeather?.getString("is_day") != "no"

            // 아이콘 URL 설정
            weatherData.iconUrl = "https://openweathermap.org/img/wn/${weatherData.icon}${if(isDay){"d"}else{"n"}}@2x.png"
//            weatherData.iconUrl = currentWeather?.getJSONArray("weather_icons")!![0].toString()
            //weatherData.iconUrl = "https://openweathermap.org/img/wn/02d@4x.png"
            //weatherData.iconUrl = "https://weatherstack.com/static/img/weather/${weatherData.icon}.png"
            return weatherData
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
    }

    private fun updateWeatherIcon(condition: Int): String {
        return when (condition) {
            113 -> "01"  // 맑음
            116 -> "02"  // 약간 흐림
            119 -> "03"  // 흐림
            122 -> "03" // 흐림
            143 -> "50"  // 안개
            176 -> "10"  // 가벼운 비
            263 -> "09"  // 가벼운 소나기
            296 -> "10"  // 가벼운 비
            308 -> "10"  // 강한 비
            356 -> "13"  // 가벼운 눈
            359 -> "13"  // 가벼운 눈
            386 -> "11"  // 천둥 번개
            else -> "01"  // 알 수 없는 날씨 조건에 대한 기본 아이콘
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

