package com.example.weatherapi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapi.R
import com.example.weatherapi.databinding.ListItemBinding
import com.example.weatherapi.items.WeatherModel
import com.squareup.picasso.Picasso

class WeatherListAdapter(val listener: Listener?):ListAdapter<WeatherModel,WeatherListAdapter.ViewHolder>(Comparator()) {

    class ViewHolder(view:View, val listener: Listener?):RecyclerView.ViewHolder(view){
        private val binding = ListItemBinding.bind(view)
        var itemTemp: WeatherModel? = null

        init {
            itemView.setOnClickListener{
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        fun bind(item:WeatherModel) = with(binding){
            itemTemp = item
            tvCondition.text = item.condition
            tvDate.text = item.time
            tvTemp.text = item.currentTemp.ifEmpty { "${item.maxTemp}°С / ${item.minTemp}°С" }
            Picasso.get().load("https:"+ item.imageUrl).into(im)
        }
    }

    class Comparator: DiffUtil.ItemCallback<WeatherModel>(){

        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return ViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(item: WeatherModel){

        }
    }


}