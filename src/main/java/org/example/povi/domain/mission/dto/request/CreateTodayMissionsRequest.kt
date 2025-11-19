package org.example.povi.domain.mission.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.example.povi.domain.mission.entity.Mission.EmotionType

@Schema(description = "오늘 미션 생성을 위한 요청 DTO")
data class CreateTodayMissionsRequest(
    @field:Schema(
        description = "사용자의 감정 상태",
        example = "HAPPY"
    )
    @field:NotNull
    val emotionType: EmotionType,

    @field:Schema(
        description = "위도",
        example = "37.5665"
    )
    @field:NotNull
    val latitude: Double,

    @field:Schema(
        description = "경도",
        example = "126.9780"
    )
    @field:NotNull
    val longitude: Double
) 