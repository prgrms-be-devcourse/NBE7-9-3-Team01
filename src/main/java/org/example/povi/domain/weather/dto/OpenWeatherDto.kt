package org.example.povi.domain.weather.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenWeatherDto(
    @field:JsonProperty("weather")
    @param:JsonProperty("weather")
    val weather: List<Weather?>?,

    @field:JsonProperty("main")
    @param:JsonProperty("main")
    val main: Main?,

    @field:JsonProperty("wind")
    @param:JsonProperty("wind")
    val wind: Wind?
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Weather(
        @field:JsonProperty("main")
        @param:JsonProperty("main")
        val main: String?,
        @field:JsonProperty("description")
        @param:JsonProperty("description")
        val description: String?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Main(
        @field:JsonProperty("temp")
        @param:JsonProperty("temp")
        val temp: Double?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Wind(
        @field:JsonProperty("speed")
        @param:JsonProperty("speed")
        val speed: Double? // m/s
    )
}