package org.example.povi.domain.weather

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openweather")
class OpenWeatherProperties(
    val baseUrl: String?,
    val path: String?,
    val apiKey: String?,
    val units: String? = "metric",
    val exclude: String? = "minutely,hourly,alerts"
)
