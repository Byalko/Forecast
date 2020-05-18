package com.example.forecast.activity

import WeekAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forecast.utils.Navigation
import com.example.forecast.R
import com.example.forecast.model.WeatherList
import com.example.forecast.model.WeatherResponse
import com.example.forecast.service.ApiService
import com.example.forecast.service.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_week.*


class WeekActivity : Navigation(1) {
    private val TAG="WeekActivity"
    private lateinit var weatherCity: TextView
    private var myCompositeDisposable: CompositeDisposable? = null
    private lateinit var api: ApiService

    private var lon:Double?=null
    private var lat:Double?=null
    var LOCATION_REQUEST_CODE = 10001
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week)
        setupBottomNavigation()
        weatherCity = findViewById(R.id.city)
        myCompositeDisposable = CompositeDisposable()


    }

    override fun onStart() {
        super.onStart()
        getCurrentData()
        /*if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {

        }*/
    }

/*    private fun getLastLocation() {
        val locationTask: Task<Location> = fusedLocationProviderClient!!.lastLocation
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                //We have a location
                lon=location.longitude
                lat=location.latitude
                getCurrentData()

            } else {
                Log.d(TAG, "onSuccess: Location was null...")
            }
        }
        locationTask.addOnFailureListener { e ->
            Log.e(
                TAG,
                "onFailure: " + e.localizedMessage
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation()
            } else {
                //Permission not granted
            }
        }
    }*/


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

    private fun getCurrentData() {
        val retrofit = RetrofitClient.instance
        api = retrofit.create(ApiService::class.java)

        val observable=
            api.getCurrentWeatherData(
                "55.164436",
                "30.222306",
                "metric",
                "67901cf7da944ab97cf90b4656ad27b4"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })

    }


    override fun onDestroy() {
        super.onDestroy()
        myCompositeDisposable?.clear()

    }

}

