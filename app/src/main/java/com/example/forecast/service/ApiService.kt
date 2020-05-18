package com.example.forecast.service

import com.example.forecast.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("data/2.5/forecast?")
    fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String,
                              @Query("units") units:String, @Query("APPID") app_id: String):
            io.reactivex.Observable<WeatherResponse>
}