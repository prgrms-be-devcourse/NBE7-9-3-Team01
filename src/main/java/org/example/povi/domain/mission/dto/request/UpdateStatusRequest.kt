package org.example.povi.domain.mission.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.example.povi.domain.mission.entity.UserMission.MissionStatus

@Schema(description = "유저 미션 상태 변경 요청 DTO")
data class UpdateStatusRequest(
    @field:Schema(
        description = "미션 상태",
        example = "COMPLETED",
        implementation = MissionStatus::class
    )
    @field:NotNull
    val status: MissionStatus
) 