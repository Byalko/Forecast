package com.example.forecast.activity

import WeekAdapter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forecast.R
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


class WeekActivity : Navigation(1) {
    private val TAG="WeekActivity"
    private lateinit var weatherCity: TextView
    private lateinit var api: ApiService
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var myCompositeDisposable: CompositeDisposable? = null

    private var lon:Double?=null
    private var lat:Double?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week)
        setupBottomNavigation()
        weatherCity = findViewById(R.id.city)
        myCompositeDisposable = CompositeDisposable()
        fusedLocationClient = LocationServices . getFusedLocationProviderClient ( this )

    }

    override fun onStart() {
        super.onStart()
        getLastLocation()
        //weatherCity.text=lon.toString()

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
        weatherCity.text=response.city.name
        val weath:List<WeatherList>
        weath= response.list
        RecyclerWeek.adapter = WeekAdapter(weath)
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

