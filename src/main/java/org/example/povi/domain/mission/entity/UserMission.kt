package org.example.povi.domain.mission.entity

import jakarta.persistence.*
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity
import java.time.LocalDate

@Entity
@Table(
    name = "user_missions",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "mission_date", "mission_id"])]
)
@AttributeOverride(name = "id", column = Column(name = "user_mission_id"))
class UserMission(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    var mission: Mission,

    @Column(name = "mission_date", nullable = false)
    var missionDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: MissionStatus = MissionStatus.IN_PROGRESS
) : BaseEntity() {

    // JPA 기본 생성자
    protected constructor() : this(
        user = User.builder().build(),
        mission = Mission(),
        missionDate = LocalDate.now()
    )

    enum class MissionStatus {
        IN_PROGRESS,  // 진행중
        COMPLETED // 완료
    }

    // 미션 완료
    fun completeMission() {
        this.status = MissionStatus.COMPLETED
    }

    // 미션 진행중으로 되돌리기
    fun inProgressMission() {
        this.status = MissionStatus.IN_PROGRESS
    }
}