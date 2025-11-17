package org.example.povi.domain.mission.repository

import org.example.povi.domain.mission.entity.Mission
import org.example.povi.domain.mission.entity.Mission.EmotionType
import org.example.povi.domain.mission.entity.Mission.WeatherType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MissionRepository : JpaRepository<Mission, Long> {
    // 감정 + 날씨 조회
    fun findByEmotionTypeAndWeatherTypeIn(
        emotionType: EmotionType,
        weatherTypes: Collection<WeatherType>
    ): List<Mission>
}
