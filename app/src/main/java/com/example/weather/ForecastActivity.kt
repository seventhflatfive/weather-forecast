package com.example.weather

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_forecast.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class ForecastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)


        val retrieverOpenWeatherMap = OpenWeatherMapRetriever()
        val callbackOpenWeatherMap = object : Callback<OpenWeatherMapQuery> {

            override fun onFailure(call: Call<OpenWeatherMapQuery>, t: Throwable) {
                println(">>> SOMETHING'S WRONG")
                println(t)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<OpenWeatherMapQuery>,
                response: Response<OpenWeatherMapQuery>
            ) {
                println(">>> ALL GOOD")
                if (response.body()?.city?.name != null) {
                    title = "Weather in ".plus(response.body()?.city?.name).plus(", ")
                        .plus(response.body()?.city?.country)
                    val forecasts = response.body()?.list
                    val forecastsString = mutableListOf<String>()
                    if (forecasts != null) {
                        for (forecast in forecasts) {

                            // convert UNIX timestamp to something readable
                            val correctTime = java.time.format.DateTimeFormatter.ISO_INSTANT.format(
                                java.time.Instant.ofEpochSecond(forecast.dt)
                            )
                            val timeFormatted = OffsetDateTime.parse("$correctTime")
                                .toLocalDateTime()
                                .format(
                                    DateTimeFormatter
                                        .ofLocalizedDateTime(FormatStyle.SHORT)
                                        .withLocale(Locale.UK)
                                )

                            // display date/time and temps
                            val newString =
                                "$timeFormatted -  Temp: ${forecast.main.temp_max} / ${forecast.main.temp_min} - ${forecast.weather.first().main}"
                            forecastsString.add(newString)
                        }
                        val adapter = ArrayAdapter(
                            this@ForecastActivity,
                            android.R.layout.simple_list_item_1,
                            forecastsString
                        )
                        forecastListView.adapter = adapter
                    }
                } else {
                    val t: Toast = Toast.makeText(
                        this@ForecastActivity,
                        "No such city found... :(",
                        Toast.LENGTH_LONG
                    )
                    t.show()
                    finish()
                }
            }

        }

        val searchTerm = intent.extras?.getString("searchTerm")

        retrieverOpenWeatherMap.getOpenWeatherMapForecast(callbackOpenWeatherMap, searchTerm!!)
    }


}
