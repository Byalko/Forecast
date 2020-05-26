package com.example.forecast.adapter

import WeekAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forecast.R
import com.example.forecast.model.RecyclerViewSection
import kotlinx.android.synthetic.main.activity_week.*
import kotlinx.android.synthetic.main.item_container.*

class ContainerAdapter(private val context: Context,
                       private val sections : List<RecyclerViewSection>)
    : RecyclerView.Adapter<ContainerAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(context : Context,
                 section : RecyclerViewSection) {
            val tvName = view.findViewById(R.id.SectionName) as TextView
            val recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView

            tvName.text = section.label
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            val adapter =
                WeekAdapter(
                    section.items
                )
            val layoutManager = LinearLayoutManager( context)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = sections.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(context,
            section)
    }
}