package org.example.povi.domain.diary.comment.repository

import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DiaryCommentRepository : JpaRepository<DiaryComment, Long> {
    fun deleteAllByAuthor(user: User)

    /**
     * 댓글 ID와 게시글 ID로 특정 댓글 조회
     */
    fun findByIdAndPostId(commentId: Long, postId: Long): DiaryComment?

    fun findByPostId(postId: Long, pageable: Pageable): Page<DiaryComment>

    // 여러 게시글 댓글 수 한 번에
    @Query("select c.post.id, count(c) from DiaryComment c where c.post.id in :postIds group by c.post.id")
    fun countByPostIds(@Param("postIds") postIds: List<Long>): List<Array<Any>>

    //특정 게시글의 댓글 개수
    fun countByPostId(postId: Long): Long
}


