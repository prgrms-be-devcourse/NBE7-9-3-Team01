package org.example.povi.domain.weather

import org.example.povi.domain.weather.dto.OpenWeatherDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class OpenWeatherClientTest {

    private lateinit var openWeatherClient: OpenWeatherClient
    private val webClient: WebClient = WebClient.builder().build()
    private val props: OpenWeatherProperties = OpenWeatherProperties(
        baseUrl = "https://api.openweathermap.org",
        path = "/data/2.5/weather",
        apiKey = "test-api-key",
        units = "metric",
        exclude = "minutely,hourly,alerts"
    )

    @BeforeEach
    fun setup() {
        openWeatherClient = OpenWeatherClient(webClient, props)
    }

    private fun createTestDto(
        weather: List<OpenWeatherDto.Weather?>? = null,
        main: OpenWeatherDto.Main? = null,
        wind: OpenWeatherDto.Wind? = null
    ) = OpenWeatherDto(weather, main, wind)

    @Test
    fun `extractWeatherMain - 정상 케이스`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = OpenWeatherDto.Main(temp = 25.0),
            wind = OpenWeatherDto.Wind(speed = 3.0)
        )

        val result = openWeatherClient.extractWeatherMain(dto)
        assertEquals("Clear", result)
    }

    @Test
    fun `extractWeatherMain - weather가 null인 경우`() {
        val dto = createTestDto(
            weather = null,
            main = OpenWeatherDto.Main(temp = 25.0),
            wind = OpenWeatherDto.Wind(speed = 3.0)
        )

        val result = openWeatherClient.extractWeatherMain(dto)
        assertEquals("Clear", result)
    }

    @Test
    fun `extractWeatherMain - weather가 비어있는 경우`() {
        val dto = createTestDto(
            weather = emptyList(),
            main = OpenWeatherDto.Main(temp = 25.0),
            wind = OpenWeatherDto.Wind(speed = 3.0)
        )

        val result = openWeatherClient.extractWeatherMain(dto)
        assertEquals("Clear", result)
    }

    @Test
    fun `extractTemperature - 정상 케이스`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = OpenWeatherDto.Main(temp = 25.5),
            wind = OpenWeatherDto.Wind(speed = 3.0)
        )

        val result = openWeatherClient.extractTemperature(dto)
        assertEquals(25.5, result)
    }

    @Test
    fun `extractTemperature - main이 null인 경우`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = null,
            wind = OpenWeatherDto.Wind(speed = 3.0)
        )

        val result = openWeatherClient.extractTemperature(dto)
        assertTrue(result.isNaN())
    }

    @Test
    fun `extractTemperature - temp가 null인 경우`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = OpenWeatherDto.Main(temp = null),
            wind = OpenWeatherDto.Wind(speed = 3.0)
        )

        val result = openWeatherClient.extractTemperature(dto)
        assertTrue(result.isNaN())
    }

    @Test
    fun `extractWindSpeed - 정상 케이스`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = OpenWeatherDto.Main(temp = 25.0),
            wind = OpenWeatherDto.Wind(speed = 5.5)
        )

        val result = openWeatherClient.extractWindSpeed(dto)
        assertEquals(5.5, result)
    }

    @Test
    fun `extractWindSpeed - wind가 null인 경우`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = OpenWeatherDto.Main(temp = 25.0),
            wind = null
        )

        val result = openWeatherClient.extractWindSpeed(dto)
        assertEquals(0.0, result)
    }

    @Test
    fun `extractWindSpeed - speed가 null인 경우`() {
        val dto = createTestDto(
            weather = listOf(OpenWeatherDto.Weather(main = "Clear", description = "clear sky")),
            main = OpenWeatherDto.Main(temp = 25.0),
            wind = OpenWeatherDto.Wind(speed = null)
        )

        val result = openWeatherClient.extractWindSpeed(dto)
        assertEquals(0.0, result)
    }
}

