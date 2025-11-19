package org.example.povi.domain.mission.repository

import org.example.povi.domain.mission.entity.UserMission
import org.example.povi.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface UserMissionRepository : JpaRepository<UserMission, Long> {
    fun deleteAllByUser(user: User)

    fun existsByUserAndMissionDate(user: User, missionDate: LocalDate): Boolean

    // 일일미션을 화면에 항상 같은 순서로 보여주기 위해 정렬
    fun findAllByUserAndMissionDateOrderByIdAsc(user: User, missionDate: LocalDate): List<UserMission>

    fun findByIdAndUser(id: Long, user: User): Optional<UserMission>

    // 미션 이력 조회 (날짜 내림차순)
    fun findAllByUserOrderByMissionDateDesc(user: User): List<UserMission>
}
