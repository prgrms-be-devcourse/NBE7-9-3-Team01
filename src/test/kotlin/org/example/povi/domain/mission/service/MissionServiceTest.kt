package org.example.povi.domain.mission.service

import io.mockk.*
import org.example.povi.domain.mission.dto.response.MissionHistoryResponse
import org.example.povi.domain.mission.dto.response.MissionResponse
import org.example.povi.domain.mission.entity.Mission
import org.example.povi.domain.mission.entity.Mission.EmotionType
import org.example.povi.domain.mission.entity.Mission.WeatherType
import org.example.povi.domain.mission.entity.UserMission
import org.example.povi.domain.mission.entity.UserMission.MissionStatus
import org.example.povi.domain.mission.repository.MissionRepository
import org.example.povi.domain.mission.repository.UserMissionRepository
import org.example.povi.domain.user.entity.User
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.domain.weather.OpenWeatherClient
import org.example.povi.domain.weather.WeatherTypeMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class MissionServiceTest {

    private lateinit var userMissionRepository: UserMissionRepository
    private lateinit var missionRepository: MissionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var weatherClient: OpenWeatherClient
    private lateinit var weatherTypeMapper: WeatherTypeMapper

    private lateinit var missionService: MissionService

    @BeforeEach
    fun setup() {
        userMissionRepository = mockk()
        missionRepository = mockk()
        userRepository = mockk()
        weatherClient = mockk()
        weatherTypeMapper = mockk()

        missionService = MissionService(
            userMissionRepository,
            missionRepository,
            userRepository,
            weatherClient,
            weatherTypeMapper
        )
    }

    @Test
    fun `오늘 미션 조회 성공`() {
        val userId = 1L
        val user = createTestUser(userId)
        val today = LocalDate.now()

        val mission1 = createTestMission(1L, "미션 1", "설명 1")
        val mission2 = createTestMission(2L, "미션 2", "설명 2")

        val userMission1 = createTestUserMission(10L, user, mission1, today, MissionStatus.IN_PROGRESS)
        val userMission2 = createTestUserMission(11L, user, mission2, today, MissionStatus.IN_PROGRESS)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every {
            userMissionRepository.findAllByUserAndMissionDateOrderByIdAsc(user, today)
        } returns listOf(userMission1, userMission2)

        val result = missionService.readTodayMissions(userId)

        assertEquals(2, result.size)
        assertEquals(1L, result[0].missionId)
        assertEquals("미션 1", result[0].title)
        assertEquals(MissionStatus.IN_PROGRESS, result[0].status)
        assertEquals(10L, result[0].userMissionId)

        verify { userRepository.findById(userId) }
        verify {
            userMissionRepository.findAllByUserAndMissionDateOrderByIdAsc(user, today)
        }
    }

    @Test
    fun `오늘 미션 조회 - 사용자 없음`() {
        val userId = 999L

        every { userRepository.findById(userId) } returns Optional.empty()

        assertThrows(IllegalArgumentException::class.java) {
            missionService.readTodayMissions(userId)
        }

        verify { userRepository.findById(userId) }
    }

    @Test
    fun `오늘 미션 생성 성공 - 날씨 기반`() {
        val userId = 1L
        val user = createTestUser(userId)
        val today = LocalDate.now()
        val emotionType = EmotionType.HAPPY
        val latitude = 37.5665
        val longitude = 126.9780

        val mission1 = createTestMission(1L, "미션 1", "설명 1", WeatherType.CLEAR)
        val mission2 = createTestMission(2L, "미션 2", "설명 2", WeatherType.CLEAR)
        val mission3 = createTestMission(3L, "미션 3", "설명 3", WeatherType.CLEAR)
        val mission4 = createTestMission(4L, "미션 4", "설명 4", WeatherType.CLEAR)

        val snapshot = OpenWeatherClient.Snapshot("Clear", 25.0, 5.0)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { userMissionRepository.existsByUserAndMissionDate(user, today) } returns false
        every { weatherClient.fetchSnapshot(latitude, longitude) } returns snapshot
        every { weatherTypeMapper.decide("Clear", 25.0, 5.0) } returns WeatherType.CLEAR
        every {
            missionRepository.findByEmotionTypeAndWeatherTypeIn(
                emotionType,
                listOf(WeatherType.CLEAR)
            )
        } returns listOf(mission1, mission2, mission3, mission4)

        every {
            userMissionRepository.saveAll(any<List<UserMission>>())
        } answers {
            val missions = firstArg<List<UserMission>>()
            missions.mapIndexed { index, um ->
                setEntityId(um, 10L + index)
            }
        }

        val result = missionService.createTodayMissions(userId, emotionType, latitude, longitude)

        assertEquals(3, result.size) // DAILY_MISSION_COUNT = 3
        verify { userRepository.findById(userId) }
        verify { userMissionRepository.existsByUserAndMissionDate(user, today) }
        verify { weatherClient.fetchSnapshot(latitude, longitude) }
        verify { weatherTypeMapper.decide("Clear", 25.0, 5.0) }
        verify {
            missionRepository.findByEmotionTypeAndWeatherTypeIn(
                emotionType,
                listOf(WeatherType.CLEAR)
            )
        }
        verify { userMissionRepository.saveAll(any<List<UserMission>>()) }
    }

    @Test
    fun `오늘 미션 생성 - 이미 존재하는 경우`() {
        val userId = 1L
        val user = createTestUser(userId)
        val today = LocalDate.now()
        val emotionType = EmotionType.HAPPY
        val latitude = 37.5665
        val longitude = 126.9780

        val mission1 = createTestMission(1L, "미션 1", "설명 1")
        val userMission1 = createTestUserMission(10L, user, mission1, today, MissionStatus.IN_PROGRESS)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { userMissionRepository.existsByUserAndMissionDate(user, today) } returns true
        every {
            userMissionRepository.findAllByUserAndMissionDateOrderByIdAsc(user, today)
        } returns listOf(userMission1)

        val result = missionService.createTodayMissions(userId, emotionType, latitude, longitude)

        assertEquals(1, result.size)
        verify { userRepository.findById(userId) }
        verify { userMissionRepository.existsByUserAndMissionDate(user, today) }
        verify {
            userMissionRepository.findAllByUserAndMissionDateOrderByIdAsc(user, today)
        }
        verify(exactly = 0) { weatherClient.fetchSnapshot(any(), any()) }
    }

    @Test
    fun `오늘 미션 생성 - 날씨 API 실패 시 ANY로 대체`() {
        val userId = 1L
        val user = createTestUser(userId)
        val today = LocalDate.now()
        val emotionType = EmotionType.HAPPY
        val latitude = 37.5665
        val longitude = 126.9780

        val mission1 = createTestMission(1L, "미션 1", "설명 1", WeatherType.ANY)
        val mission2 = createTestMission(2L, "미션 2", "설명 2", WeatherType.ANY)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { userMissionRepository.existsByUserAndMissionDate(user, today) } returns false
        every { weatherClient.fetchSnapshot(latitude, longitude) } throws RuntimeException("API Error")
        every {
            missionRepository.findByEmotionTypeAndWeatherTypeIn(
                emotionType,
                listOf(WeatherType.ANY)
            )
        } returns listOf(mission1, mission2)

        every {
            userMissionRepository.saveAll(any<List<UserMission>>())
        } answers {
            val missions = firstArg<List<UserMission>>()
            missions.mapIndexed { index, um ->
                setEntityId(um, 10L + index)
            }
        }

        val result = missionService.createTodayMissions(userId, emotionType, latitude, longitude)

        assertEquals(2, result.size)
        verify { userRepository.findById(userId) }
        verify { userMissionRepository.existsByUserAndMissionDate(user, today) }
        verify { weatherClient.fetchSnapshot(latitude, longitude) }
        verify {
            missionRepository.findByEmotionTypeAndWeatherTypeIn(
                emotionType,
                listOf(WeatherType.ANY)
            )
        }
    }

    @Test
    fun `미션 상태 업데이트 - 완료로 변경`() {
        val userId = 1L
        val userMissionId = 10L
        val user = createTestUser(userId)
        val mission = createTestMission(1L, "미션 1", "설명 1")
        val userMission = createTestUserMission(
            userMissionId,
            user,
            mission,
            LocalDate.now(),
            MissionStatus.IN_PROGRESS
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every {
            userMissionRepository.findByIdAndUser(userMissionId, user)
        } returns Optional.of(userMission)

        missionService.updateUserMissionStatus(userId, userMissionId, MissionStatus.COMPLETED)

        assertEquals(MissionStatus.COMPLETED, userMission.status)
        verify { userRepository.findById(userId) }
        verify { userMissionRepository.findByIdAndUser(userMissionId, user) }
    }

    @Test
    fun `미션 상태 업데이트 - 진행중으로 변경`() {
        val userId = 1L
        val userMissionId = 10L
        val user = createTestUser(userId)
        val mission = createTestMission(1L, "미션 1", "설명 1")
        val userMission = createTestUserMission(
            userMissionId,
            user,
            mission,
            LocalDate.now(),
            MissionStatus.COMPLETED
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every {
            userMissionRepository.findByIdAndUser(userMissionId, user)
        } returns Optional.of(userMission)

        missionService.updateUserMissionStatus(userId, userMissionId, MissionStatus.IN_PROGRESS)

        assertEquals(MissionStatus.IN_PROGRESS, userMission.status)
        verify { userRepository.findById(userId) }
        verify { userMissionRepository.findByIdAndUser(userMissionId, user) }
    }

    @Test
    fun `미션 상태 업데이트 - UserMission 없음`() {
        val userId = 1L
        val userMissionId = 999L
        val user = createTestUser(userId)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every {
            userMissionRepository.findByIdAndUser(userMissionId, user)
        } returns Optional.empty()

        assertThrows(IllegalArgumentException::class.java) {
            missionService.updateUserMissionStatus(userId, userMissionId, MissionStatus.COMPLETED)
        }

        verify { userRepository.findById(userId) }
        verify { userMissionRepository.findByIdAndUser(userMissionId, user) }
    }

    @Test
    fun `미션 이력 조회 성공`() {
        val userId = 1L
        val user = createTestUser(userId)
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val mission1 = createTestMission(1L, "미션 1", "설명 1")
        val mission2 = createTestMission(2L, "미션 2", "설명 2")

        val userMission1 = createTestUserMission(10L, user, mission1, today, MissionStatus.COMPLETED)
        val userMission2 = createTestUserMission(11L, user, mission2, today, MissionStatus.IN_PROGRESS)
        val userMission3 = createTestUserMission(12L, user, mission1, yesterday, MissionStatus.COMPLETED)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every {
            userMissionRepository.findAllByUserOrderByMissionDateDesc(user)
        } returns listOf(userMission1, userMission2, userMission3)

        val result = missionService.getMissionHistory(userId)

        assertEquals(2, result.size) // 날짜별로 그룹화
        assertEquals(today, result[0].missionDate) // 최신 날짜부터
        assertEquals(2, result[0].missions.size)
        assertEquals(yesterday, result[1].missionDate)
        assertEquals(1, result[1].missions.size)
        assertEquals(1, result[0].completedCount) // 완료된 미션 1개
        assertEquals(2, result[0].totalCount) // 전체 미션 2개

        verify { userRepository.findById(userId) }
        verify { userMissionRepository.findAllByUserOrderByMissionDateDesc(user) }
    }

    private fun <T : org.example.povi.global.entity.BaseEntity> setEntityId(entity: T, id: Long): T {
        val idField = org.example.povi.global.entity.BaseEntity::class.java.getDeclaredField("id")
        idField.trySetAccessible()
        idField.set(entity, id)
        return entity
    }

    private fun createTestUser(id: Long): User {
        val user = User.builder()
            .email("test@example.com")
            .nickname("tester")
            .provider(org.example.povi.auth.enums.AuthProvider.LOCAL)
            .build()
        setEntityId(user, id)
        return user
    }

    private fun createTestMission(
        id: Long,
        title: String,
        description: String,
        weatherType: WeatherType = WeatherType.CLEAR
    ): Mission {
        val mission = Mission(
            title = title,
            description = description,
            emotionType = EmotionType.HAPPY,
            weatherType = weatherType
        )
        return setEntityId(mission, id)
    }

    private fun createTestUserMission(
        id: Long,
        user: User,
        mission: Mission,
        missionDate: LocalDate,
        status: MissionStatus
    ): UserMission {
        val userMission = UserMission(
            user = user,
            mission = mission,
            missionDate = missionDate,
            status = status
        )
        return setEntityId(userMission, id)
    }
}

