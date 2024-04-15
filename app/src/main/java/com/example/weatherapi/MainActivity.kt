package com.example.weatherapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapi.databinding.ActivityMainBinding
import com.example.weatherapi.fragments.MainFragment

const val API_KEY = "64163e5d2ad64006a9f161629240802"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, MainFragment.newInstance())
            .commit()


    }

//    private fun getResult(name:String){
//        val url = "https://api.weatherapi.com/v1/current.json" +
//                "?key=${API_KEY}&q=$name&aqi=no"
//        val queue = Volley.newRequestQueue(this)
//        val stringRequest = StringRequest(Request.Method.GET,
//            url,
//            {
//                responce ->
//                val obj = JSONObject(responce)
//                val temp = obj.getJSONObject("current")
//                Log.d("MyLog", "Responce: ${temp.getString("temp_c")}")
//            },
//            {
//                Log.d("MyLog","Volley Error: $it")
//            }
//        )
//        queue.add(stringRequest)
//    }
}