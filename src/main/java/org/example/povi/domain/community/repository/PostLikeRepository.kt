package org.example.povi.domain.community.repository

import jakarta.persistence.LockModeType
import org.example.povi.domain.community.entity.CommunityPost
import org.example.povi.domain.community.entity.PostLike
import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface PostLikeRepository : JpaRepository<PostLike?, Long?> {
    fun deleteAllByUser(user: User?)


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByUserIdAndPostId(userId: Long?, postId: Long?): Optional<PostLike?>?

    @Query("SELECT pl.post FROM PostLike pl WHERE pl.user.id = :userId ORDER BY pl.createdAt DESC")
    fun findLikedPostsByUserId(@Param("userId") userId: Long?, pageable: Pageable?): Page<CommunityPost?>?
}
