package com.example.forecast.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forecast.R
import com.example.forecast.adapter.ContainerAdapter
import com.example.forecast.model.RecyclerViewSection
import com.example.forecast.model.WeatherList
import com.example.forecast.model.WeatherResponse
import com.example.forecast.service.ApiService
import com.example.forecast.utils.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_week.*


class WeekActivity : Navigation(1) {
    private lateinit var api: ApiService
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var myCompositeDisposable: CompositeDisposable? = null

    private var lon:Double?=null
    private var lat:Double?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week)
        setupBottomNavigation()
        api = ApiService.create()
        myCompositeDisposable = CompositeDisposable()
        fusedLocationClient = LocationServices . getFusedLocationProviderClient ( this )

    }

    override fun onStart() {
        super.onStart()
        if (check2(this)){
            getLastLocation()
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

    private fun onFailure(t: Throwable) {
        Toast.makeText(this,t.message, Toast.LENGTH_SHORT).show()
    }

    private fun onResponse(response: WeatherResponse) {
        city.text=response.city.name
        val weath:List<WeatherList>
        weath= response.list

        RecyclerWeek.adapter = ContainerAdapter(this,inicializeSections( weath = weath))
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
                Log.d("Loc", "onSuccess: Location was null...")
            }
        }
    }

    private fun getCurrentData() {
        myCompositeDisposable?.add(
            api.getCurrentWeatherData(
                lat.toString(),
                lon.toString(),
                "metric",
                "67901cf7da944ab97cf90b4656ad27b4"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        )
    }

    /*private fun ParseDate(date:String, pattern:String): String {
        val parser =  SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH)
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return formatter.format(parser.parse(date)!!)
    }*/

    private fun inicializeSections(weath:List<WeatherList>): MutableList<RecyclerViewSection> {
        var size=0
        for (i in 1..7){
            if (parseDate(weath[0].dt_txt,"dd.MM.yyyy")==parseDate(weath[i].dt_txt,"dd.MM.yyyy")){
                size += 1
            }
        }

        val sections = mutableListOf<RecyclerViewSection>()
        val items1 = mutableListOf<WeatherList>()
        for (i in 0..size) {
            items1.add(weath[i])
        }
        val section1 = RecyclerViewSection("TODAY", items1)
        sections.add(section1)

        for (i in 1..4) {
            val items = mutableListOf<WeatherList>()

            for (ii in size+1..size+8) {
                items.add(weath[ii])
            }
            val dayOfWeek=parseDate(weath[size+8].dt_txt,"E")
            size+=8

            val section = RecyclerViewSection(dayOfWeek(dayOfWeek), items)
            sections.add(section)
        }
        return sections
    }

     private fun dayOfWeek(day:String): String {
        return when(day){
            "Mon"->"MONDAY"
            "Tue"->"TUESDAY"
            "Wed"->"WEDNESDAY"
            "Thu"->"THURSDAY"
            "Fri"->"FRIDAY"
            "Sat"->"SATURDAY"
            else-> "SUNDAY"
        }
    }
}

