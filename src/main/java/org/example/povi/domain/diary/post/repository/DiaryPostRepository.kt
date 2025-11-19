package org.example.povi.domain.diary.post.repository

import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DiaryPostRepository : JpaRepository<DiaryPost?, Long?> {
    fun deleteAllByUser(user: User?)

    // =========================================================
    // 나의 다이어리 (월별 페이징 / 주간 비페이징)
    //  - 월별 카드 목록: Page
    //  - 주간 통계 집계용: List
    // =========================================================
    fun findByUserIdAndCreatedAtBetween(
            userId: Long?,
            startAt: LocalDateTime?,
            endAt: LocalDateTime?,
            pageable: Pageable?
    ): Page<DiaryPost>

    fun findByUserIdAndCreatedAtBetween(
            userId: Long?,
            startAt: LocalDateTime?,
            endAt: LocalDateTime?
    ): List<DiaryPost>

    // =========================================================
    // 친구 피드 (페이징)
    //  - 맞팔: FRIEND + PUBLIC
    //  - 단방향: PUBLIC
    //  - hasMutual / hasOneWay 플래그로 조건 단순화 (빈 컬렉션 처리 회피)
    // =========================================================
    @Query("""
select p
from DiaryPost p
where (
        (:hasMutual = true and p.user.id in :mutualIds and p.visibility in :friendVisible)
     or (:hasOneWay = true and p.user.id in :oneWayIds and p.visibility = :publicVis)
)
order by p.createdAt desc
""")
    fun findFriendFeedPaged(
            @Param("mutualIds") mutualIds: Collection<Long?>?,
            @Param("friendVisible") friendVisible: Collection<Visibility?>?,
            @Param("oneWayIds") oneWayIds: Collection<Long?>?,
            @Param("publicVis") publicVis: Visibility?,
            @Param("hasMutual") hasMutual: Boolean,
            @Param("hasOneWay") hasOneWay: Boolean,
            pageable: Pageable?
    ): Page<DiaryPost>

    // =========================================================
    // 모두의 다이어리 Explore (기간 + 가시성 규칙, 페이징)
    //  - viewer 제외
    //  - 최근 7일 기간 필터
    //  - 맞팔: FRIEND + PUBLIC, 그 외: PUBLIC
    // =========================================================
    @Query("""
        select p
        from DiaryPost p
        where p.user.id <> :viewerId
          and p.createdAt >= :startAt
          and p.createdAt < :endAt
          and (
                (p.user.id in :mutualIds and p.visibility in :friendVisible)
             or (p.user.id not in :mutualIds and p.visibility = :publicVis)
          )
        order by p.createdAt desc
        
        """)
    fun findExploreFeedWithMutualsInPeriodPaged(
            @Param("viewerId") viewerId: Long?,
            @Param("mutualIds") mutualIds: Collection<Long?>?,
            @Param("friendVisible") friendVisible: Collection<Visibility?>?,
            @Param("publicVis") publicVis: Visibility?,
            @Param("startAt") startAt: LocalDateTime?,
            @Param("endAt") endAt: LocalDateTime?,
            pageable: Pageable?
    ): Page<DiaryPost>

    @Query("""
        select p
        from DiaryPost p
        where p.user.id <> :viewerId
          and p.createdAt >= :startAt
          and p.createdAt < :endAt
          and p.visibility = :publicVis
        order by p.createdAt desc
        
        """)
    fun findExploreFeedPublicOnlyInPeriodPaged(
            @Param("viewerId") viewerId: Long?,
            @Param("publicVis") publicVis: Visibility?,
            @Param("startAt") startAt: LocalDateTime?,
            @Param("endAt") endAt: LocalDateTime?,
            pageable: Pageable?
    ): Page<DiaryPost>

    // ======================================================================
    // 집계/통계
    // ======================================================================
    @Query("""
            select c.post.id as postId, count(c) as cnt
            from DiaryComment c
            where c.post.id in :postIds
            group by c.post.id
            
            """)
    fun countCommentsInPostIds(@Param("postIds") postIds: List<Long?>?): List<Array<Any>>?

    fun countByUserId(userId: Long?): Long
}