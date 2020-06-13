package com.example.forecast.service

import com.example.forecast.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("data/2.5/forecast?")
    fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String,
                              @Query("units") units:String, @Query("APPID") app_id: String):
            io.reactivex.Observable<WeatherResponse>

    companion object RetrofitClient {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.openweathermap.org/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}