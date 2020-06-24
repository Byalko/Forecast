package com.example.forecast.utils

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.forecast.R
import com.example.forecast.activity.MainActivity
import com.example.forecast.activity.WeekActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.absoluteValue

abstract class Navigation(private val navNumb:Int): AppCompatActivity() {
    fun setupBottomNavigation(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.parseColor("#DBD7D2")
        }

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity=
                when(it.itemId){
                    R.id.bottom_navigation_today -> MainActivity::class.java
                    R.id.bottom_navigation_week -> WeekActivity::class.java
                    else->{null}
                }
            if(nextActivity!=null){
                val intent = Intent(this,nextActivity)
                intent.flags= Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0,0)
                true
            } else {false}

        }
    }

    override fun onResume() {
        super.onResume()
        if (bottom_navigation_view!=null){
            bottom_navigation_view.menu.getItem(navNumb).isChecked=true
        }
    }

}