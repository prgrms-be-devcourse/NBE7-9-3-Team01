package org.example.povi.domain.mission.entity

import jakarta.persistence.*
import org.example.povi.global.entity.BaseEntity

@Entity
@Table(name = "missions")
@AttributeOverride(name = "id", column = Column(name = "mission_id"))
class Mission(
    @Column(nullable = false, length = 100)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type", nullable = false, length = 30)
    var emotionType: EmotionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_type")
    var weatherType: WeatherType? = null
) : BaseEntity() {

    // JPA 기본 생성자
    internal constructor() : this(
        title = "",
        description = "",
        emotionType = EmotionType.NEUTRAL
    )

    enum class EmotionType {
        HAPPY, JOYFUL, CALM, NEUTRAL, DEPRESSED, SAD, TIRED, ANGRY
    }

    enum class WeatherType {
        CLEAR,  // 맑음
        CLOUDY,  // 구름
        RAINY,  // 비
        SNOWY,  // 눈
        THUNDER,  // 뇌우
        DRIZZLE,  // 이슬비
        FOGGY,  // 안개/연무
        WINDY,  // 바람 강함
        HOT,  // 덥다 (예: T >= 28°C)
        COLD,  // 춥다 (예: T <= 5°C)
        ANY // 어떤 날씨에도 추천 가능
    }
}