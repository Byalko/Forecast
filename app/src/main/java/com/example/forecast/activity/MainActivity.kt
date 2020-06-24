package com.example.forecast.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.forecast.R
import com.example.forecast.model.WeatherResponse
import com.example.forecast.service.ApiService
import com.example.forecast.utils.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Navigation(0) {
    private var compas:String=""
    private var lon:Double?=null
    private var lat:Double?=null
    private var txtShare:String?=null
    private var weatherToWeek:WeatherResponse?=null
    private lateinit var api:ApiService

    private var locationRequestCode = 10001
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var myCompositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        myCompositeDisposable = CompositeDisposable()
        api = ApiService.create()
        //btn_share
        cancel_button.setOnClickListener{
            shareData()
        }
    }

    private fun shareData(){
        if (txtShare!=null){
            val intent = Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,txtShare)
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Select app:"))
        }
        else{
            Toast.makeText(this,"No data to share!!!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun check():Boolean{
        return if (getAndAsk()){
            true
        }else{
            Toast.makeText(this, "Permission is't granted!!!", Toast.LENGTH_SHORT).show()
            askLocationPermission()
            false
        }
    }

    override fun onStart() {
        super.onStart()
        if(check()){
            if (check2(this)){
                getLastLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (check2(this)){
            getLastLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myCompositeDisposable?.clear()

    }

    private fun getCurrentData() {
        myCompositeDisposable?.add(
            api.getCurrentWeatherData(
                lat.toString(), lon.toString(),
                "metric", "67901cf7da944ab97cf90b4656ad27b4")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { response -> onResponse(response) },{ error ->
                Toast.makeText(this, error.message,Toast.LENGTH_SHORT).show()
                Log.d("Get", error.message!!)
            } )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun onResponse(response: WeatherResponse) {
        weatherToWeek=response
        val url= "http://openweathermap.org/img/wn/${response.list[0].weather[0].icon}@2x.png"
        Picasso.get()
            .load(url)
            .into(icon_wheat)
        val strCity = response.city.name+", "+response.city.country
        city.text = strCity
        gradusy.text="${response.list[0].main.temp.toInt()} °C | ${response.list[0].weather[0].main}"
        txt_rainfall.text="${response.list[0].main.humidity}%"
        //txt_water.text=response.list[0].rain.h.toString()+" mm"
        txt_degree.text="${response.list[0].main.pressure} hPa"
        txt_wind.text="${((response.list[0].wind.speed)*3.6).toInt()} km/h"
        direction(response.list[0].wind.deg)
        txt_compass.text=compas
        txtShare= "$strCity\n Degrees: ${response.list[0].main.temp.toInt()} °C | ${response.list[0].weather[0].main}\n " +
                "Humidity: ${response.list[0].main.humidity}%\n " +
                "Atmospheric pressure: ${response.list[0].main.pressure} hPa\n " +
                "Wind speed: ${((response.list[0].wind.speed)*3.6).toInt()} km/h\n Wind direction: $compas"
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

    private fun getAndAsk(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    /*private fun getAndAsk(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            return false
            //askLocationPermission()
        }
    }*/

    private fun getLastLocation() {
        val locationTask: Task<Location> = fusedLocationProviderClient!!.lastLocation
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                //We have a location
                lon=location.longitude
                lat=location.latitude
                getCurrentData()

            } else {
                Log.d("Loc", "onSuccess: Location was null...")
            }
        }
        locationTask.addOnFailureListener { e ->
            Log.e("Loc", "onFailure: " + e.localizedMessage)
        }
    }

    private fun askLocationPermission(): Boolean {
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
                Log.d("Gps", "askLocationPermission: you should show an alert dialog...")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationRequestCode
                )
                return true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationRequestCode
                )
                return false
            }
        }
        return true
    }

    /*override fun onRequestPermissionsResult(
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

}
