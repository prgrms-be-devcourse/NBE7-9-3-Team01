package org.example.povi.domain.weather

import org.example.povi.domain.mission.entity.Mission
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WeatherTypeMapperTest {

    private lateinit var weatherTypeMapper: WeatherTypeMapper

    @BeforeEach
    fun setup() {
        weatherTypeMapper = WeatherTypeMapper()
    }

    @Test
    fun `강수-특수현상 - Thunder`() {
        val result = weatherTypeMapper.decide("Thunderstorm", 20.0, 5.0)
        assertEquals(Mission.WeatherType.THUNDER, result)
    }

    @Test
    fun `강수-특수현상 - Drizzle`() {
        val result = weatherTypeMapper.decide("Drizzle", 15.0, 3.0)
        assertEquals(Mission.WeatherType.DRIZZLE, result)
    }

    @Test
    fun `강수-특수현상 - Rain`() {
        val result = weatherTypeMapper.decide("Rain", 18.0, 4.0)
        assertEquals(Mission.WeatherType.RAINY, result)
    }

    @Test
    fun `강수-특수현상 - Snow`() {
        val result = weatherTypeMapper.decide("Snow", -2.0, 2.0)
        assertEquals(Mission.WeatherType.SNOWY, result)
    }

    @Test
    fun `하늘상태 - Foggy`() {
        val testCases = listOf("Mist", "Fog", "Haze", "Smoke")
        testCases.forEach { main ->
            val result = weatherTypeMapper.decide(main, 15.0, 2.0)
            assertEquals(Mission.WeatherType.FOGGY, result, "Failed for: $main")
        }
    }

    @Test
    fun `하늘상태 - Cloudy`() {
        val result = weatherTypeMapper.decide("Clouds", 20.0, 3.0)
        assertEquals(Mission.WeatherType.CLOUDY, result)
    }

    @Test
    fun `하늘상태 - Clear`() {
        val result = weatherTypeMapper.decide("Clear", 22.0, 2.0)
        assertEquals(Mission.WeatherType.CLEAR, result)
    }

    @Test
    fun `바람 - Windy`() {
        // main이 null이거나 알 수 없는 경우에만 바람 체크
        val result = weatherTypeMapper.decide(null, 20.0, 10.0)
        assertEquals(Mission.WeatherType.WINDY, result)
    }

    @Test
    fun `바람 - Windy 임계값 이상`() {
        // main이 null이거나 알 수 없는 경우에만 바람 체크
        val result = weatherTypeMapper.decide("Unknown", 20.0, 15.0)
        assertEquals(Mission.WeatherType.WINDY, result)
    }

    @Test
    fun `바람 - Windy 임계값 미만`() {
        // main이 null이거나 알 수 없는 경우에도 바람이 임계값 미만이면 ANY
        val result = weatherTypeMapper.decide("Unknown", 20.0, 9.0)
        assertEquals(Mission.WeatherType.ANY, result)
    }

    @Test
    fun `온도 - Hot`() {
        // main이 null이거나 알 수 없는 경우에만 온도 체크
        val result = weatherTypeMapper.decide(null, 28.0, 2.0)
        assertEquals(Mission.WeatherType.HOT, result)
    }

    @Test
    fun `온도 - Hot 임계값 이상`() {
        // main이 null이거나 알 수 없는 경우에만 온도 체크
        val result = weatherTypeMapper.decide("Unknown", 30.0, 2.0)
        assertEquals(Mission.WeatherType.HOT, result)
    }

    @Test
    fun `온도 - Cold`() {
        // main이 null이거나 알 수 없는 경우에만 온도 체크
        val result = weatherTypeMapper.decide(null, 5.0, 2.0)
        assertEquals(Mission.WeatherType.COLD, result)
    }

    @Test
    fun `온도 - Cold 임계값 이하`() {
        // main이 null이거나 알 수 없는 경우에만 온도 체크
        val result = weatherTypeMapper.decide("Unknown", 0.0, 2.0)
        assertEquals(Mission.WeatherType.COLD, result)
    }

    @Test
    fun `우선순위 테스트 - 강수가 하늘상태보다 우선`() {
        // Rain이 Clouds보다 우선되어야 함
        val result = weatherTypeMapper.decide("Rain", 20.0, 2.0)
        assertEquals(Mission.WeatherType.RAINY, result)
    }

    @Test
    fun `우선순위 테스트 - 강수가 온도보다 우선`() {
        // Rain이 Hot보다 우선되어야 함
        val result = weatherTypeMapper.decide("Rain", 30.0, 2.0)
        assertEquals(Mission.WeatherType.RAINY, result)
    }

    @Test
    fun `우선순위 테스트 - 하늘상태가 온도보다 우선`() {
        // Clouds가 Hot보다 우선되어야 함
        val result = weatherTypeMapper.decide("Clouds", 30.0, 2.0)
        assertEquals(Mission.WeatherType.CLOUDY, result)
    }

    @Test
    fun `우선순위 테스트 - 하늘상태가 바람보다 우선`() {
        // Clear가 Windy보다 우선되어야 함 (기존 로직대로)
        val result = weatherTypeMapper.decide("Clear", 20.0, 15.0)
        assertEquals(Mission.WeatherType.CLEAR, result)
    }

    @Test
    fun `매칭 실패 - ANY 반환`() {
        val result = weatherTypeMapper.decide("Unknown", 20.0, 2.0)
        assertEquals(Mission.WeatherType.ANY, result)
    }

    @Test
    fun `null main 처리`() {
        // null이면 빈 문자열이 되고, 바람/온도 체크 후 매칭 실패 시 ANY 반환
        // 바람이 2.0 (< 10.0), 온도가 20.0 (5.0 < temp < 28.0)이므로 ANY
        val result = weatherTypeMapper.decide(null, 20.0, 2.0)
        assertEquals(Mission.WeatherType.ANY, result)
    }

    @Test
    fun `대소문자 무시 테스트`() {
        val result1 = weatherTypeMapper.decide("RAIN", 20.0, 2.0)
        val result2 = weatherTypeMapper.decide("rain", 20.0, 2.0)
        val result3 = weatherTypeMapper.decide("Rain", 20.0, 2.0)

        assertEquals(Mission.WeatherType.RAINY, result1)
        assertEquals(Mission.WeatherType.RAINY, result2)
        assertEquals(Mission.WeatherType.RAINY, result3)
    }

    @Test
    fun `NaN 온도 처리`() {
        val result = weatherTypeMapper.decide("Clear", Double.NaN, 2.0)
        assertEquals(Mission.WeatherType.CLEAR, result)
    }

    @Test
    fun `NaN 바람 처리`() {
        val result = weatherTypeMapper.decide("Clear", 20.0, Double.NaN)
        assertEquals(Mission.WeatherType.CLEAR, result)
    }

    @Test
    fun `무한대 온도 처리`() {
        val result = weatherTypeMapper.decide("Clear", Double.POSITIVE_INFINITY, 2.0)
        assertEquals(Mission.WeatherType.CLEAR, result)
    }
}

