package com.example.forecast.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forecast.R
import com.example.forecast.model.RecyclerViewSection
import kotlinx.android.synthetic.main.item_container.view.*

class ContainerAdapter(private val context: Context,
                       private val sections : List<RecyclerViewSection>)
    : RecyclerView.Adapter<ContainerAdapter.ViewHolder>() {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        fun bind(context : Context,
                 section : RecyclerViewSection) {

            view.SectionName.text = section.label
            view.recyclerView.setHasFixedSize(true)
            view.recyclerView.isNestedScrollingEnabled = false
            val adapter =
                WeekAdapter(
                    section.items
                )
            val layoutManager = LinearLayoutManager( context)
            view.recyclerView.layoutManager = layoutManager
            view.recyclerView.adapter = adapter
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