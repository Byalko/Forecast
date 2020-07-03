package com.example.forecast.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

import java.util.ArrayList

data class WeatherResponse(
    @SerializedName("cod") val cod: String,
    @SerializedName("message") val message: Int,
    @SerializedName("cnt") val cnt: Int,
    @SerializedName("list") val list: ArrayList<WeatherList>,
    @SerializedName("city") val city: City
) : Serializable

data class WeatherList(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: ArrayList<Weather>,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("rain") val rain: Rain,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("dt_txt") val dt_txt: String,
    var check:Boolean=false
) : Serializable

data class Main (
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feels_like: Double,
    @SerializedName("temp_min") val temp_min: Double,
    @SerializedName("temp_max") val temp_max: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("sea_level") val sea_level: Int,
    @SerializedName("grnd_level") val grnd_level: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("temp_kf") val temp_kf: Double
): Serializable

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
) : Serializable

data class Clouds(
    @SerializedName("all") val all: Int
) : Serializable

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int
) : Serializable

data class Rain(
    @SerializedName("3h") val h: Double
) : Serializable

data class City(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("coord") val coord: Coord,
    @SerializedName("country") val country: String,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
) : Serializable

data class Coord(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
) : Serializable