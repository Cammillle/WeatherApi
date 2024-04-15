package com.example.weatherapi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.weatherapi.R
import com.example.weatherapi.adapters.WeatherListAdapter
import com.example.weatherapi.databinding.FragmentDaysBinding
import com.example.weatherapi.items.WeatherModel
import com.example.weatherapi.models.MainViewModel


class DaysFragment : Fragment(), WeatherListAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: WeatherListAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        model.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(it.subList(1,it.size))
        }
    }

    private fun init(){
        adapter = WeatherListAdapter(this)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value = item

    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}