package com.example.algartech.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeatherByCity(@Query("q") city: String, @Query("APPID") apiKey: String): Call<WeatherResponse>
}