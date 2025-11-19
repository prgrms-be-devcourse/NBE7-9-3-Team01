package org.example.povi.domain.weather.dto

data class WeatherResponse(
    val main: String?,
    val description: String?,
    val temp: Int,
    val windSpeed: Int
)
