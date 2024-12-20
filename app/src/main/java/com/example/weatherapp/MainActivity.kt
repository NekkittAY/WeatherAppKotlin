package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var weatherTextView: TextView
    private val apiKey = "854075760f35435e8b0141415242012"

    data class WeatherResponse(
        val location: Location,
        val current: Current
    )

    data class Location(
        val name: String,
        val region: String,
        val country: String
    )

    data class Current(
        val temp_c: Float,
        val condition: Condition
    )

    data class Condition(
        val text: String,
        val icon: String
    )

    interface WeatherApi {
        @GET("current.json")
        fun getWeather(
            @Query("key") apiKey: String,
            @Query("q") cityName: String
        ): Call<WeatherResponse>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTextView = findViewById(R.id.weatherTextView)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        val call = weatherApi.getWeather(apiKey, "Moscow")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val weatherInfo = "City: ${it.location.name}\n" +
                                "Temperature: ${it.current.temp_c} °C\n" +
                                "Condition: ${it.current.condition.text}"
                        weatherTextView.text = weatherInfo
                    }
                } else {
                    weatherTextView.text = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherTextView.text = "Failure: ${t.message}"
                Log.e("MainActivity", t.message ?: "Error")
            }
        })
    }
}