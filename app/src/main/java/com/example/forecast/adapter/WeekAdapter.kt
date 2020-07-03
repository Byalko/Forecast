package com.example.forecast.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.forecast.R
import com.example.forecast.activity.parseDate
import com.example.forecast.model.WeatherList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row.view.*

class WeekAdapter(private val context: Context,
                  private val weather: List<WeatherList>)
    : RecyclerView.Adapter<WeekAdapter.ViewHolder>(){

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView= LayoutInflater.from(parent.context)
            .inflate(R.layout.row,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = weather.size

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = weather[position]
        with(holder) {
            view.TimeItem.text=parseDate(weather[position].dt_txt,"HH:mm")
            view.DesItem.text=post.weather[0].description
            view.DegreeItem.text="${weather[position].main.temp.toInt()}°C"
            val url= "http://openweathermap.org/img/wn/"+ post.weather[0].icon +"@2x.png"
            Picasso.get()
                .load(url)
                .into(holder.view.ImageItem)

            val isExpandable:Boolean=post.check
            view.ConstLayoutRow.background=
                if(isExpandable)  ContextCompat.getDrawable(context,R.drawable.bg_for_items_clicked)
            else null //ContextCompat.getDrawable(context,R.drawable.bg_for_items)

            view.ConstLayoutRow.setOnClickListener {
                val postC=weather[position]
                postC.check=!postC.check
                notifyItemChanged(position)
            }
        }
    }
}