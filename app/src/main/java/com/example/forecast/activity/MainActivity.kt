package com.example.forecast.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.forecast.utils.Navigation
import com.example.forecast.R
import com.example.forecast.model.WeatherResponse
import com.example.forecast.service.ApiService
import com.example.forecast.service.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


open class MainActivity : Navigation(0) {
    private lateinit var weatherCity: TextView
    private lateinit var weatherGrad: TextView
    private lateinit var weatherRain: TextView
    private lateinit var weatherWater: TextView
    private lateinit var weatherDegree: TextView
    private lateinit var weatherSpeed: TextView
    private lateinit var weatherCompass: TextView
    private lateinit var image: ImageView
    private lateinit var navigat: BottomNavigationView
    private var compas:String=""
    private var lon:Double?=null
    private var lat:Double?=null
    private lateinit var api:ApiService
    private var myCompositeDisposable: CompositeDisposable? = null

    private val TAG = "MainActivity"
    var LOCATION_REQUEST_CODE = 10001
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()
        weatherCity = findViewById(R.id.city)
        weatherGrad = findViewById(R.id.gradusy)
        weatherRain = findViewById(R.id.txt_rainfall)
        weatherWater = findViewById(R.id.txt_water)
        weatherDegree = findViewById(R.id.txt_degree)
        weatherSpeed = findViewById(R.id.txt_wind)
        weatherCompass = findViewById(R.id.txt_compass)
        image=findViewById(R.id.icon_wheat)
        //weaLoc = findViewById(R.id.loc)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //getCurrentData()
        navigat=findViewById(R.id.bottom_navigation_view)
        navigat.setOnNavigationItemReselectedListener {  }

    }

    private fun getCurrentData() {
        val retrofit=RetrofitClient.instance
        api=retrofit.create(ApiService::class.java)
        //weatherCity.text=lon.toString() + " ? "+lat.toString()

        val observable = api.getCurrentWeatherData("${lat.toString()}", "${lon.toString()}","metric", "67901cf7da944ab97cf90b4656ad27b4")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { response -> onResponse(response) },{ error ->
                Toast.makeText(this, error.message,Toast.LENGTH_SHORT).show()
                Log.d(TAG, error.message)
            } )

    }

    private fun onResponse(response: WeatherResponse) {
        val url= "http://openweathermap.org/img/wn/"+ "${response.list[0].weather[0].icon}" +"@2x.png"
        Picasso.get()
            .load(url)
            .into(image)
        val strCity = response.city.name+", "+response.city.country
        weatherCity.text = strCity
        weatherGrad.text=response.list[0].main.temp.toInt().toString()+"°C | "+response.list[0].weather[0].main
        weatherRain.text=response.list[0].main.humidity.toString()+"%"
        //weatherWater.text=response.list[0].rain.h.toString()+" mm"
        weatherDegree.text=response.list[0].main.pressure.toString()+" hPa"
        weatherSpeed.text=((response.list[0].wind.speed)*3.6).toInt().toString()+" km/h"
        direction(response.list[0].wind.deg)
        weatherCompass.text=compas
    }

    private fun direction(deg: Int) {
        if(deg in 0..23 || deg in 339..360 )  this.compas ="N"
        if(deg in 24..68) this.compas="NE"
        if(deg in 69..113)  this.compas="E"
        if(deg in 114..158)  this.compas="SE"
        if(deg in 158..203)  this.compas="S"
        if(deg in 204..248)  this.compas="SW"
        if(deg in 249..293)  this.compas="W"
        if(deg in 294..338)  this.compas="NW"
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            askLocationPermission()
        }
    }

    private fun getLastLocation() {
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

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Log.d(
                    TAG,
                    "askLocationPermission: you should show an alert dialog..."
                )
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
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
    }

}
