package org.example.povi.domain.transcription.repository

import org.example.povi.domain.transcription.entity.Transcription
import org.example.povi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TranscriptionRepository : JpaRepository<Transcription, Long> {
    fun deleteAllByUser(user: User)

    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long, pageable: Pageable): Page<Transcription>
}
