package org.example.povi.domain.mission.controller

import jakarta.validation.Valid
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.domain.mission.controller.docs.MissionControllerDocs
import org.example.povi.domain.mission.dto.request.CreateTodayMissionsRequest
import org.example.povi.domain.mission.dto.request.UpdateStatusRequest
import org.example.povi.domain.mission.dto.response.MissionHistoryResponse
import org.example.povi.domain.mission.dto.response.MissionResponse
import org.example.povi.domain.mission.service.MissionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions")
class MissionController(
    private val missionService: MissionService,
    private val jwtTokenProvider: JwtTokenProvider
) : MissionControllerDocs {

    // resolveToken 함수 선언
    private fun resolveToken(bearerToken: String): String {
        return bearerToken.replace("Bearer ", "")
    }

    // 오늘 미션 조회
    @GetMapping("/today")
    override fun getTodayMissions(
        @RequestHeader("Authorization") bearerToken: String
    ): ResponseEntity<List<MissionResponse>> {
        val token = resolveToken(bearerToken)
        val userId = jwtTokenProvider.getUserId(token)
        val list = missionService.readTodayMissions(userId)
        if (list.isEmpty()) return ResponseEntity.noContent().build()
        return ResponseEntity.ok(list)
    }

    // 오늘 미션 생성
    @PostMapping("/today")
    override fun createTodayMissions(
        @RequestHeader("Authorization") bearerToken: String,
        @RequestBody req: CreateTodayMissionsRequest
    ): ResponseEntity<List<MissionResponse>> {
        val token = resolveToken(bearerToken)
        val userId = jwtTokenProvider.getUserId(token)
        val list = missionService.createTodayMissions(userId, req.emotionType, req.latitude, req.longitude)
        return ResponseEntity.status(HttpStatus.CREATED).body(list)
    }

    // 유저 미션 상태 업데이트
    @PatchMapping("/{userMissionId}/status")
    override fun updateStatus(
        @RequestHeader("Authorization") bearerToken: String,
        @PathVariable userMissionId: Long,
        @RequestBody req: @Valid UpdateStatusRequest
    ): ResponseEntity<Void> {
        val token = resolveToken(bearerToken)
        val userId = jwtTokenProvider.getUserId(token)
        missionService.updateUserMissionStatus(userId, userMissionId, req.status)
        return ResponseEntity.noContent().build()
    }

    // 미션 이력 조회
    @GetMapping("/history")
    override fun getMissionHistory(
        @RequestHeader("Authorization") bearerToken: String
    ): ResponseEntity<List<MissionHistoryResponse>> {
        val token = resolveToken(bearerToken)
        val userId = jwtTokenProvider.getUserId(token)
        val history = missionService.getMissionHistory(userId)
        if (history.isEmpty()) return ResponseEntity.noContent().build()
        return ResponseEntity.ok(history)
    }
}