package org.example.povi.domain.mission.service

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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MissionService(
    private val userMissionRepository: UserMissionRepository,
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository,
    private val weatherClient: OpenWeatherClient,
    private val weatherTypeMapper: WeatherTypeMapper
) {
    private val log = LoggerFactory.getLogger(MissionService::class.java)

    /** 오늘 저장본 조회  */
    @Transactional(readOnly = true)
    fun readTodayMissions(userId: Long): List<MissionResponse> {
        val user = findUser(userId)
        val today = LocalDate.now()

        return userMissionRepository
            .findAllByUserAndMissionDateOrderByIdAsc(user, today)
            .map { um -> MissionResponse(um.mission, um.status, um.id) }
    }

    /** 오늘 최초 생성 (감정/위경도 필수)  */
    @Transactional
    fun createTodayMissions(
        userId: Long,
        emotionType: EmotionType,
        latitude: Double,
        longitude: Double
    ): List<MissionResponse> {
        val user = findUser(userId)
        val today = LocalDate.now()

        if (userMissionRepository.existsByUserAndMissionDate(user, today)) {
            return readTodayMissions(userId)
        }

        val candidates: List<Mission>
        try {
            // 1) 정상 경로: 실시간 날씨 결정 → 정확 매칭만
            val snap = weatherClient.fetchSnapshot(latitude, longitude)
            val decided = weatherTypeMapper.decide(snap.weatherMain, snap.temperatureC, snap.windMs)

            candidates = missionRepository.findByEmotionTypeAndWeatherTypeIn(
                emotionType, listOf(decided)
            )

            // 정확 매칭 정책: 후보가 0이면 데이터 보강 필요를 바로 알림
            check(candidates.isNotEmpty()) {
                "해당 감정/날씨 미션이 없습니다: emotion=$emotionType, weather=$decided — data.sql 보강 필요"
            }
        } catch (e: Exception) {
            // 2) 비상 경로: API 실패/매핑 불가 시에만 감정+ANY로 대체
            log.warn("[OW] weather fetch/mapping failed, fallback to ANY: {}", e.toString())
            val fallbackCandidates = missionRepository.findByEmotionTypeAndWeatherTypeIn(
                emotionType, listOf(WeatherType.ANY)
            )

            check(fallbackCandidates.isNotEmpty()) { "비상 대체(ANY) 미션도 없습니다: emotion=$emotionType" }
            return createMissionsFromCandidates(fallbackCandidates, user, today)
        }

        return createMissionsFromCandidates(candidates, user, today)
    }

    private fun createMissionsFromCandidates(
        candidates: List<Mission>,
        user: User,
        today: LocalDate
    ): List<MissionResponse> {
        val shuffled = candidates.shuffled()
        val picked = shuffled.take(DAILY_MISSION_COUNT)

        val saved = picked.map { m -> UserMission(user, m, today) }
        userMissionRepository.saveAll(saved)

        return saved.map { um -> MissionResponse(um.mission, um.status, um.id) }
    }

    private fun findUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다. userId=$userId") }
    }

    @Transactional
    fun updateUserMissionStatus(userId: Long, userMissionId: Long, status: MissionStatus) {
        val user = findUser(userId)
        val userMission = userMissionRepository.findByIdAndUser(userMissionId, user)
            .orElseThrow { IllegalArgumentException("UserMission을 찾을 수 없습니다. userMissionId=$userMissionId") }

        when (status) {
            MissionStatus.COMPLETED -> userMission.completeMission()
            MissionStatus.IN_PROGRESS -> userMission.inProgressMission()
        }
    }

    /** 미션 이력 조회  */
    @Transactional(readOnly = true)
    fun getMissionHistory(userId: Long): List<MissionHistoryResponse> {
        val user = findUser(userId)
        val userMissions = userMissionRepository.findAllByUserOrderByMissionDateDesc(user)

        // 날짜별로 그룹화
        val missionsByDate = userMissions.groupBy { it.missionDate }

        return missionsByDate.entries
            .map { (date, missionList) ->
                val missionDetails = missionList.map { um ->
                    MissionResponse(um.mission, um.status, um.id)
                }
                MissionHistoryResponse(date, missionDetails)
            }
            .sortedByDescending { it.missionDate }  // 최신 날짜부터
    }

    companion object {
        private const val DAILY_MISSION_COUNT = 3
    }
}