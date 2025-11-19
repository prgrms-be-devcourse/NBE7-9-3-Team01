package org.example.povi.domain.weather.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.povi.domain.weather.OpenWeatherClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.math.roundToInt

class WeatherControllerTest {

    private lateinit var mockMvc: MockMvc
    private val weatherClient: OpenWeatherClient = mockk()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(WeatherController(weatherClient))
            .build()
    }

    @Test
    fun `날씨 조회 성공`() {
        val latitude = 37.5665
        val longitude = 126.9780

        val snapshot = OpenWeatherClient.Snapshot(
            weatherMain = "Clear",
            temperatureC = 25.5,
            windMs = 3.2
        )

        every { weatherClient.fetchSnapshot(latitude, longitude) } returns snapshot

        mockMvc.perform(
            get("/api/weather")
                .param("latitude", latitude.toString())
                .param("longitude", longitude.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.main").value("Clear"))
            .andExpect(jsonPath("$.description").value("clear weather"))
            .andExpect(jsonPath("$.temp").value(26))
            .andExpect(jsonPath("$.windSpeed").value(3))

        verify { weatherClient.fetchSnapshot(latitude, longitude) }
    }

    @Test
    fun `날씨 조회 실패 시 기본값 반환`() {
        val latitude = 37.5665
        val longitude = 126.9780

        every { weatherClient.fetchSnapshot(latitude, longitude) } throws RuntimeException("API Error")

        mockMvc.perform(
            get("/api/weather")
                .param("latitude", latitude.toString())
                .param("longitude", longitude.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.main").value("Clear"))
            .andExpect(jsonPath("$.description").value("clear weather"))
            .andExpect(jsonPath("$.temp").value(20))
            .andExpect(jsonPath("$.windSpeed").value(0))

        verify { weatherClient.fetchSnapshot(latitude, longitude) }
    }

    @Test
    fun `다양한 날씨 타입 테스트`() {
        val latitude = 37.5665
        val longitude = 126.9780

        val testCases = listOf(
            Triple("Rain", 20.0, 5.0),
            Triple("Clouds", 15.0, 2.0),
            Triple("Snow", -5.0, 1.0)
        )

        testCases.forEach { (weatherMain, temp, wind) ->
            val snapshot = OpenWeatherClient.Snapshot(
                weatherMain = weatherMain,
                temperatureC = temp,
                windMs = wind
            )

            every { weatherClient.fetchSnapshot(latitude, longitude) } returns snapshot

            mockMvc.perform(
                get("/api/weather")
                    .param("latitude", latitude.toString())
                    .param("longitude", longitude.toString())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.main").value(weatherMain))
                .andExpect(jsonPath("$.description").value("${weatherMain.lowercase()} weather"))
                .andExpect(jsonPath("$.temp").value(temp.roundToInt()))
                .andExpect(jsonPath("$.windSpeed").value(wind.roundToInt()))
        }
    }
}

