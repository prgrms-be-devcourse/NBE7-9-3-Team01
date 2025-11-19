package org.example.povi.domain.community.repository

import jakarta.persistence.LockModeType
import org.example.povi.domain.community.entity.Comment
import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.*

interface CommentRepository : JpaRepository<Comment?, Long?> {
    fun deleteAllByUser(user: User?)

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findById(id: Long): Optional<Comment?>

    fun findAllByUserId(userId: Long?, pageable: Pageable?): Page<Comment?>?
}
