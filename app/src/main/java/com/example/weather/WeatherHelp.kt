package com.example.weather

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapAPI {
    @GET("data/2.5/forecast?units=metric&APPID=5487f42686c6152c4dd0c37ed9f9cc37")
    fun getOpenWeatherMapForecast(@Query("q") q: String): Call<OpenWeatherMapQuery>
}

class OpenWeatherMapQuery(val city: OpenWeatherMapCity, val list: List<OpenWeatherMapList>)
class OpenWeatherMapCity(val name: String, val country: String)
class OpenWeatherMapList(
    val dt: Long,
    val main: OpenWeatherTemps,
    val weather: List<OpenWeatherDescription>
)

class OpenWeatherTemps(val temp: Double, val temp_min: Double, val temp_max: Double)
class OpenWeatherDescription(val main: String, val description: String)

class OpenWeatherMapRetriever {
    private val service: OpenWeatherMapAPI

    init {
        val retrofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        service = retrofit.create(OpenWeatherMapAPI::class.java)
    }

    fun getOpenWeatherMapForecast(callback: Callback<OpenWeatherMapQuery>, searchTerm: String) {

        var searchT: String = searchTerm
        if (searchT == "") {
            searchT = "Athens, GR"
        }
        val call = service.getOpenWeatherMapForecast(searchT)
        call.enqueue(callback)
    }
}