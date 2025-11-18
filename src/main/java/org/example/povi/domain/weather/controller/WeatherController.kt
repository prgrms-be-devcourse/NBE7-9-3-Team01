package org.example.povi.domain.weather.controller

import org.example.povi.domain.weather.OpenWeatherClient
import org.example.povi.domain.weather.dto.WeatherResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale
import kotlin.math.roundToInt

@RestController
@RequestMapping("/api/weather")
class WeatherController(
    private val weatherClient: OpenWeatherClient
) {

    @GetMapping
    fun getWeather(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double
    ): ResponseEntity<WeatherResponse> {
        return try {
            val snapshot = weatherClient.fetchSnapshot(latitude, longitude)
            val weatherMain = snapshot.weatherMain ?: "Clear"
            val response = WeatherResponse(
                weatherMain,
                "${weatherMain.lowercase(Locale.getDefault())} weather",
                snapshot.temperatureC.roundToInt(),
                snapshot.windMs.roundToInt()
            )
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            // 날씨 정보 가져오기 실패 시 기본값 반환
            val response = WeatherResponse(
                "Clear",
                "clear weather",
                20,
                0
            )
            ResponseEntity.ok(response)
        }
    }
}
