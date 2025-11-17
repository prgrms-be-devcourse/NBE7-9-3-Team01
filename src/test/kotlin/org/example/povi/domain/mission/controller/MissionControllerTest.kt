package org.example.povi.domain.mission.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.domain.mission.dto.request.CreateTodayMissionsRequest
import org.example.povi.domain.mission.dto.request.UpdateStatusRequest
import org.example.povi.domain.mission.dto.response.MissionHistoryResponse
import org.example.povi.domain.mission.dto.response.MissionResponse
import org.example.povi.domain.mission.entity.Mission
import org.example.povi.domain.mission.entity.UserMission
import org.example.povi.domain.mission.service.MissionService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate

class MissionControllerTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper()

    private val missionService: MissionService = mockk()
    private val jwtTokenProvider: JwtTokenProvider = mockk()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(MissionController(missionService, jwtTokenProvider))
            .build()
    }

    @Test
    fun `오늘 미션 조회 성공`() {
        val userId = 1L
        val token = "test-token"
        val bearerToken = "Bearer $token"

        val mission1 = createTestMission(1L, "미션 1", "설명 1")
        val mission2 = createTestMission(2L, "미션 2", "설명 2")

        val responses = listOf(
            MissionResponse(mission1, UserMission.MissionStatus.IN_PROGRESS, 10L),
            MissionResponse(mission2, UserMission.MissionStatus.IN_PROGRESS, 11L)
        )

        every { jwtTokenProvider.getUserId(token) } returns userId
        every { missionService.readTodayMissions(userId) } returns responses

        mockMvc.perform(
            get("/api/missions/today")
                .header("Authorization", bearerToken)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].missionId").value(1L))
            .andExpect(jsonPath("$[0].title").value("미션 1"))
            .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$[1].missionId").value(2L))
            .andExpect(jsonPath("$[1].title").value("미션 2"))

        verify { jwtTokenProvider.getUserId(token) }
        verify { missionService.readTodayMissions(userId) }
    }

    @Test
    fun `오늘 미션 조회 - 미션 없음`() {
        val userId = 1L
        val token = "test-token"
        val bearerToken = "Bearer $token"

        every { jwtTokenProvider.getUserId(token) } returns userId
        every { missionService.readTodayMissions(userId) } returns emptyList()

        mockMvc.perform(
            get("/api/missions/today")
                .header("Authorization", bearerToken)
        )
            .andExpect(status().isNoContent)

        verify { jwtTokenProvider.getUserId(token) }
        verify { missionService.readTodayMissions(userId) }
    }

    @Test
    fun `오늘 미션 생성 성공`() {
        val userId = 1L
        val token = "test-token"
        val bearerToken = "Bearer $token"

        val request = CreateTodayMissionsRequest(
            emotionType = Mission.EmotionType.HAPPY,
            latitude = 37.5665,
            longitude = 126.9780
        )

        val mission1 = createTestMission(1L, "미션 1", "설명 1")
        val mission2 = createTestMission(2L, "미션 2", "설명 2")

        val responses = listOf(
            MissionResponse(mission1, UserMission.MissionStatus.IN_PROGRESS, 10L),
            MissionResponse(mission2, UserMission.MissionStatus.IN_PROGRESS, 11L)
        )

        every { jwtTokenProvider.getUserId(token) } returns userId
        every {
            missionService.createTodayMissions(
                userId,
                request.emotionType,
                request.latitude,
                request.longitude
            )
        } returns responses

        mockMvc.perform(
            post("/api/missions/today")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$[0].missionId").value(1L))
            .andExpect(jsonPath("$[0].title").value("미션 1"))
            .andExpect(jsonPath("$[1].missionId").value(2L))

        verify { jwtTokenProvider.getUserId(token) }
        verify {
            missionService.createTodayMissions(
                userId,
                request.emotionType,
                request.latitude,
                request.longitude
            )
        }
    }

    @Test
    fun `미션 상태 업데이트 성공`() {
        val userId = 1L
        val userMissionId = 10L
        val token = "test-token"
        val bearerToken = "Bearer $token"

        val request = UpdateStatusRequest(
            status = UserMission.MissionStatus.COMPLETED
        )

        every { jwtTokenProvider.getUserId(token) } returns userId
        every {
            missionService.updateUserMissionStatus(userId, userMissionId, request.status)
        } returns Unit

        mockMvc.perform(
            patch("/api/missions/$userMissionId/status")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNoContent)

        verify { jwtTokenProvider.getUserId(token) }
        verify {
            missionService.updateUserMissionStatus(userId, userMissionId, request.status)
        }
    }

    @Test
    fun `미션 이력 조회 성공`() {
        val userId = 1L
        val token = "test-token"
        val bearerToken = "Bearer $token"

        val mission1 = createTestMission(1L, "미션 1", "설명 1")
        val mission2 = createTestMission(2L, "미션 2", "설명 2")

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val responses = listOf(
            MissionHistoryResponse(
                missionDate = today,
                missions = listOf(
                    MissionResponse(mission1, UserMission.MissionStatus.COMPLETED, 10L),
                    MissionResponse(mission2, UserMission.MissionStatus.IN_PROGRESS, 11L)
                )
            ),
            MissionHistoryResponse(
                missionDate = yesterday,
                missions = listOf(
                    MissionResponse(mission1, UserMission.MissionStatus.COMPLETED, 12L)
                )
            )
        )

        every { jwtTokenProvider.getUserId(token) } returns userId
        every { missionService.getMissionHistory(userId) } returns responses

        mockMvc.perform(
            get("/api/missions/history")
                .header("Authorization", bearerToken)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].missionDate[0]").value(today.year))
            .andExpect(jsonPath("$[0].missionDate[1]").value(today.monthValue))
            .andExpect(jsonPath("$[0].missionDate[2]").value(today.dayOfMonth))
            .andExpect(jsonPath("$[0].missions[0].title").value("미션 1"))
            .andExpect(jsonPath("$[0].missions[0].status").value("COMPLETED"))
            .andExpect(jsonPath("$[0].completedCount").value(1))
            .andExpect(jsonPath("$[0].totalCount").value(2))
            .andExpect(jsonPath("$[1].missionDate[0]").value(yesterday.year))
            .andExpect(jsonPath("$[1].missionDate[1]").value(yesterday.monthValue))
            .andExpect(jsonPath("$[1].missionDate[2]").value(yesterday.dayOfMonth))

        verify { jwtTokenProvider.getUserId(token) }
        verify { missionService.getMissionHistory(userId) }
    }

    @Test
    fun `미션 이력 조회 - 이력 없음`() {
        val userId = 1L
        val token = "test-token"
        val bearerToken = "Bearer $token"

        every { jwtTokenProvider.getUserId(token) } returns userId
        every { missionService.getMissionHistory(userId) } returns emptyList()

        mockMvc.perform(
            get("/api/missions/history")
                .header("Authorization", bearerToken)
        )
            .andExpect(status().isNoContent)

        verify { jwtTokenProvider.getUserId(token) }
        verify { missionService.getMissionHistory(userId) }
    }

    private fun <T : org.example.povi.global.entity.BaseEntity> setEntityId(entity: T, id: Long): T {
        val idField = org.example.povi.global.entity.BaseEntity::class.java.getDeclaredField("id")
        idField.trySetAccessible()
        idField.set(entity, id)
        return entity
    }

    private fun createTestMission(id: Long, title: String, description: String): Mission {
        val mission = Mission(
            title = title,
            description = description,
            emotionType = Mission.EmotionType.HAPPY,
            weatherType = Mission.WeatherType.CLEAR
        )
        return setEntityId(mission, id)
    }
}

