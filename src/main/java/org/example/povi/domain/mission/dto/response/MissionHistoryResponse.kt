package org.example.povi.domain.mission.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.mission.entity.UserMission.MissionStatus
import java.time.LocalDate

@Schema(description = "사용자의 하루 미션 기록 응답 DTO")
data class MissionHistoryResponse(
    @field:Schema(
        description = "미션 날짜",
        example = "2025-10-24"
    )
    val missionDate: LocalDate,

    @field:Schema(description = "미션 목록")
    val missions: List<MissionResponse>,

    @field:Schema(
        description = "완료된 미션 개수",
        example = "2"
    )
    val completedCount: Int,

    @field:Schema(
        description = "전체 미션 개수",
        example = "5"
    )
    val totalCount: Int,

    @field:Schema(
        description = "완료율 (%)",
        example = "40.0"
    )
    val completionRate: Double
) {
    constructor(missionDate: LocalDate, missions: List<MissionResponse>) : this(
        missionDate,
        missions,
        calculateCompletedCount(missions),
        missions.size,
        calculateCompletionRate(missions)
    )

    companion object {
        // 완료 미션 개수
        private fun calculateCompletedCount(missions: List<MissionResponse>): Int {
            return missions.count { it.status == MissionStatus.COMPLETED }
        }

        // 완료율
        private fun calculateCompletionRate(missions: List<MissionResponse>): Double {
            val totalCount = missions.size
            if (totalCount == 0) return 0.0
            val completedCount = calculateCompletedCount(missions)
            return (completedCount.toDouble() / totalCount * 100).let { 
                kotlin.math.round(it * 10) / 10.0
            }
        }
    }
}