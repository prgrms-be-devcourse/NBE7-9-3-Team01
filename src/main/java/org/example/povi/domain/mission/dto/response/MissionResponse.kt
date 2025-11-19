package org.example.povi.domain.mission.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.mission.entity.Mission
import org.example.povi.domain.mission.entity.UserMission.MissionStatus

@Schema(description = "미션 응답 DTO")
data class MissionResponse(
    @field:Schema(
        description = "미션 ID",
        example = "1"
    )
    val missionId: Long?,

    @field:Schema(
        description = "미션 제목",
        example = "밖에서 산책하기"
    )
    val title: String,

    @field:Schema(
        description = "미션 설명",
        example = "햇빛을 쬐며 산책하면 기분이 좋아져요."
    )
    val description: String,

    @field:Schema(
        description = "미션 상태",
        example = "IN_PROGRESS"
    )
    val status: MissionStatus,

    @field:Schema(
        description = "미션 개인화 ID",
        example = "1"
    )
    val userMissionId: Long?
) {
    constructor(mission: Mission, status: MissionStatus, userMissionId: Long?) : this(
        mission.id,
        mission.title,
        mission.description,
        status,
        userMissionId
    )
}