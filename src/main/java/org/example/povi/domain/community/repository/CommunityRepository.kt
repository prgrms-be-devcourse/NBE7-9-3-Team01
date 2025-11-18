package org.example.povi.domain.community.repository

import org.example.povi.domain.community.entity.CommunityPost
import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityRepository : JpaRepository<CommunityPost, Long> {
    fun deleteAllByUser(user: User)

    fun findAllByUserId(userId: Long, pageable: Pageable): Page<CommunityPost>
}
