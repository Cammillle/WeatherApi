package com.example.weatherapi.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapi.adapters.VpAdapter
import com.example.weatherapi.databinding.FragmentMainBinding
import com.example.weatherapi.items.WeatherModel
import com.example.weatherapi.models.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "64163e5d2ad64006a9f161629240802"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val listOfFragments = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private val listOfTabs = listOf("HOURS", "DAYS")

    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        initViewPagerAdapter()
        getLocation()

        model.liveDataCurrent.observe(viewLifecycleOwner){
            val maxMinTemp = "${it.maxTemp}°С / ${it.minTemp}°С"

            tvCity.text = it.city
            tvData.text = it.time
            tvCurrentTemp.text = it.currentTemp
            tvCondition.text = it.condition
            tvMaxMinTemp.text = maxMinTemp
            Picasso.get().load("https:"+it.imageUrl).into(imWeather)

        }

    }

    private fun initViewPagerAdapter() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, listOfFragments)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, position ->
            tab.text = listOfTabs[position]
        }.attach()

        bSync.setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation(){
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, ct.token )
            .addOnCompleteListener{
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }



    private fun permissionListener() {
        launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&" +
                "days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, url, { result ->
                parseWeatherData(result)
            },
            { error ->
                Log.d("MyLog", "Error: $error")
            })
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDaysData(mainObject)
        parseCurrentData(mainObject,list[0])

    }

    private fun parseDaysData(mainObject: JSONObject) : List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val city = mainObject.getJSONObject("location").getString("name")

        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

        for(i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                city,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
                )
            list.add(item)
        }
        model.liveDataList.value = list
        return list

    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem:WeatherModel){
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours
        )
        model.liveDataCurrent.value = item

    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()

    }

}