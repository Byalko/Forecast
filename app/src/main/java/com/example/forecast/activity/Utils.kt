package com.example.forecast.activity

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun ParseDate(date:String, pattern:String): String {
    val parser =  SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH)
    val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
    return formatter.format(parser.parse(date)!!)
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    // API 23
    /* val capabilities =
         connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
     if (capabilities != null) {
         if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
             return true
         } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
             return true
         }
     }*/
    //API 21
    val activeNetwork=connectivityManager.activeNetworkInfo
    if (activeNetwork!=null && activeNetwork.isConnectedOrConnecting){
        return true
    }

    return false
}

fun isOnlineGps(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    return gps_enabled
}

fun check2(context: Context):Boolean{
    return if (isOnlineGps(context)){
        if (isOnline(context)){
            true
        }else{
            Toast.makeText(context, "Internet is't active!!!", Toast.LENGTH_SHORT).show()
            false
        }
    } else{
        Toast.makeText(context, "Gps is'n active!!!", Toast.LENGTH_SHORT).show()
        false
    }
}