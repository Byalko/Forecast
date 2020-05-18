package com.example.forecast.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.forecast.R
import com.example.forecast.activity.MainActivity
import com.example.forecast.activity.WeekActivity
import kotlinx.android.synthetic.main.activity_main.*

abstract class Navigation(val navNumb:Int): AppCompatActivity() {
    fun setupBottomNavigation(){
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val next_activity=
                when(it.itemId){
                    R.id.bottom_navigation_today -> MainActivity::class.java
                    R.id.bottom_navigation_week -> WeekActivity::class.java
                    else->{null}
                }
            if(next_activity!=null){
                val intent = Intent(this,next_activity)
                intent.flags= Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                true
            } else {false}

        }
        bottom_navigation_view.menu.getItem(navNumb).isChecked=true
    }
}