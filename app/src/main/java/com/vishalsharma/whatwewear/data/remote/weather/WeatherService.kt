
package com.vishalsharma.whatwewear.data.remote.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class CurrentWeather(
    val temperature: String,
    val description: String
)

object WeatherService {

    private const val JAIPUR_LATITUDE = 26.9124
    private const val JAIPUR_LONGITUDE = 75.7873

    suspend fun getCurrentWeather(): CurrentWeather =
        withContext(Dispatchers.IO) {
            val url = URL(
                "https://api.open-meteo.com/v1/forecast" +
                        "?latitude=$JAIPUR_LATITUDE" +
                        "&longitude=$JAIPUR_LONGITUDE" +
                        "&current=temperature_2m,weather_code" +
                        "&timezone=Asia%2FKolkata"
            )

            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode !in 200..299) {
                    error("Weather service unavailable")
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val current = JSONObject(response)
                    .getJSONObject("current")

                val temperature = current.getDouble("temperature_2m")
                val weatherCode = current.getInt("weather_code")

                CurrentWeather(
                    temperature = "${temperature.toInt()}°C",
                    description = weatherDescription(weatherCode)
                )
            } finally {
                connection.disconnect()
            }
        }

    private fun weatherDescription(code: Int): String =
        when (code) {
            0 -> "Clear sky"
            1 -> "Mostly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast skies"
            45, 48 -> "Foggy conditions"
            51, 53, 55 -> "Light drizzle"
            56, 57 -> "Freezing drizzle"
            61, 63, 65 -> "Rain expected"
            66, 67 -> "Freezing rain"
            71, 73, 75, 77 -> "Snowfall"
            80, 81, 82 -> "Rain showers"
            85, 86 -> "Snow showers"
            95, 96, 99 -> "Thunderstorms"
            else -> "Current weather"
        }
}
