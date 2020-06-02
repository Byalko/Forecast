package com.example.forecast.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forecast.R
import com.example.forecast.adapter.ContainerAdapter
import com.example.forecast.model.RecyclerViewSection
import com.example.forecast.model.WeatherList
import com.example.forecast.model.WeatherResponse
import com.example.forecast.service.ApiService
import com.example.forecast.service.RetrofitClient
import com.example.forecast.utils.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_week.*
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.android.synthetic.main.activity_week.*



class WeekActivity : Navigation(1) {
    private val TAG="WeekActivity"
    private lateinit var api: ApiService
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var myCompositeDisposable: CompositeDisposable? = null

    private var lon:Double?=null
    private var lat:Double?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week)
        setupBottomNavigation()
        myCompositeDisposable = CompositeDisposable()
        fusedLocationClient = LocationServices . getFusedLocationProviderClient ( this )

    }

    override fun onStart() {
        super.onStart()
        getLastLocation()

    }

    override fun onResume() {
        super.onResume()
        getLastLocation()

    }

    override fun onDestroy() {
        super.onDestroy()
        myCompositeDisposable?.clear()

    }

    private fun onFailure(t: Throwable) {
        Toast.makeText(this,t.message, Toast.LENGTH_SHORT).show()
    }

    private fun onResponse(response: WeatherResponse) {
        city.text=response.city.name
        val weath:List<WeatherList>
        weath= response.list
        var size=-1
        val date = SimpleDateFormat("yyyy-MM-dd").parse("${weath[0].dt_txt}")

        for (i in 0..8){
            if (SimpleDateFormat("yyyy-MM-dd").parse("${weath[i].dt_txt}")==date){
                size += 1
            }
        }
//        var time = android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("${weath[0].dt_txt}")
//
//        weatherCity.text=time.toString()

        val sections = mutableListOf<RecyclerViewSection>()
        val items1 = mutableListOf<WeatherList>()
        for (i in 0..size) {
            items1.add(weath[i])
        }
        val section = RecyclerViewSection("TODAY", items1)
        sections.add(section)

            for (i in 1..4) {
            val items = mutableListOf<WeatherList>()

            for (i in size+1..size+8) {
                items.add(weath[i])
            }
            val date = SimpleDateFormat("yyyy-MM-dd").parse("${weath[size+8].dt_txt}")
            val datee = ZonedDateTime
                .parse(date.toString(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH))
                .toLocalDate()
            size+=8

            val section = RecyclerViewSection("${datee.dayOfWeek}", items)
            sections.add(section)
        }

        RecyclerWeek.adapter = ContainerAdapter(this,sections)
        RecyclerWeek.layoutManager = LinearLayoutManager(this)

    }

    private fun getLastLocation() {
        fusedLocationClient . lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                //We have a location
                lon=location.longitude
                lat=location.latitude
                getCurrentData()

            } else {
                Log.d(TAG, "onSuccess: Location was null...")
            }
        }
    }

    private fun getCurrentData() {
        val retrofit = RetrofitClient.instance
        api = retrofit.create(ApiService::class.java)

        myCompositeDisposable?.add(
            api.getCurrentWeatherData(
                "${lat.toString()}",
                "${lon.toString()}",
                "metric",
                "67901cf7da944ab97cf90b4656ad27b4"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        )
    }


}

