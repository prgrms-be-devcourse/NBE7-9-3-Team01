package org.example.povi.domain.weather

import org.example.povi.domain.mission.entity.Mission.WeatherType
import org.springframework.stereotype.Component
import java.util.*

@Component
class WeatherTypeMapper {
    /**
     * OpenWeather 'main', 'temp(°C)', 'wind(m/s)' 기반으로 WeatherType 결정.
     * 1) 강수/특수현상(main) 우선
     * 2) 하늘상태(main)
     * 3) 바람/온도 임계치
     * 4) 매칭 실패 시 ANY
     */
    fun decide(main: String?, tempC: Double, windMs: Double): WeatherType {
        val m = (main.orEmpty()).lowercase(Locale.getDefault())

        // 1) 강수/특수현상
        if (m.contains("thunder")) return WeatherType.THUNDER
        if (m.contains("drizzle")) return WeatherType.DRIZZLE
        if (m.contains("rain")) return WeatherType.RAINY
        if (m.contains("snow")) return WeatherType.SNOWY

        // 2) 하늘상태
        if (m.contains("mist") || m.contains("fog") || m.contains("haze") || m.contains("smoke")) return WeatherType.FOGGY
        if (m.contains("cloud")) return WeatherType.CLOUDY
        if (m.contains("clear")) return WeatherType.CLEAR

        // 3) 바람/온도
        if (windMs.isFinite() && windMs >= WINDY_THRESHOLD_MS) return WeatherType.WINDY

        if (tempC.isFinite()) {
            if (tempC >= HOT_C_THRESHOLD_C) return WeatherType.HOT
            if (tempC <= COLD_C_THRESHOLD_C) return WeatherType.COLD
        }

        // 4) 매칭 실패
        return WeatherType.ANY
    }

    companion object {
        private const val HOT_C_THRESHOLD_C = 28.0 // >= 덥다
        private const val COLD_C_THRESHOLD_C = 5.0 // <= 춥다
        private const val WINDY_THRESHOLD_MS = 10.0 // >= 강풍
    }
}