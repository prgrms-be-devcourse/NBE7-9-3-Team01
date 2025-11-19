package org.example.povi.domain.diary.like.repository

import org.example.povi.domain.diary.like.entity.DiaryPostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DiaryPostLikeRepository : JpaRepository<DiaryPostLike, Long> {
    // 특정 게시글에 대해 사용자가 좋아요를 눌렀는지 여부 확인
    fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean

    // 특정 게시글의 전체 좋아요 개수
    fun countByPostId(postId: Long): Long

    // 특정 게시글 + 사용자 조합으로 좋아요 엔티티 조회 (토글용)
    fun findByPostIdAndUserId(postId: Long, userId: Long): DiaryPostLike?

    // 특정 게시글 + 사용자 조합으로 좋아요 삭제
    fun deleteByPostIdAndUserId(postId: Long, userId: Long)

    // 여러 게시글에 대한 좋아요 수를 한 번에 집계
    @Query("select l.post.id, count(l) from DiaryPostLike l where l.post.id in :postIds group by l.post.id")
    fun countByPostIds(@Param("postIds") postIds: List<Long>): List<Array<Any>>

    // 특정 사용자(userId)가 좋아요한 게시글 ID 목록 조회
    @Query("select l.post.id from DiaryPostLike l where l.user.id = :userId and l.post.id in :postIds")
    fun findPostIdsLikedByUser(
        @Param("postIds") postIds: List<Long>,
        @Param("userId") userId: Long
    ): List<Long>
}