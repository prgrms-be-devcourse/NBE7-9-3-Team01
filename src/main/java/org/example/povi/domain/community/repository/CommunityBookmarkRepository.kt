package org.example.povi.domain.community.repository

import org.example.povi.domain.community.entity.CommunityBookmark
import org.example.povi.domain.community.entity.CommunityPost
import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CommunityBookmarkRepository : JpaRepository<CommunityBookmark?, Long?> {
    fun deleteAllByUser(user: User?)

    fun findByUserAndCommunityPost(user: User?, communityPost: CommunityPost?): Optional<CommunityBookmark?>?

    fun existsByUserAndCommunityPost(user: User?, communityPost: CommunityPost?): Boolean

    @Query("SELECT b.communityPost FROM CommunityBookmark b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    fun findBookmarkedPostsByUserId(@Param("userId") userId: Long?, pageable: Pageable?): Page<CommunityPost?>?
}
