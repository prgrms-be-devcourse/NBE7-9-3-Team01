package org.example.povi.auth.email.repository

import org.example.povi.auth.email.entity.EmailVerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*

/**
 * 이메일 인증 토큰 관련 JPA Repository
 */
interface EmailVerificationTokenRepository : JpaRepository<EmailVerificationToken, Long> {

    /**
     * 토큰 값으로 인증 토큰 조회
     */
    fun findByToken(token: String?): Optional<EmailVerificationToken>

    /**
     * 이메일로 인증 토큰 조회
     */
    fun findByEmail(email: String?): Optional<EmailVerificationToken>

    /**
     * 토큰 값으로 삭제
     */
    fun deleteByToken(token: String?)

    /**
     * 만료된 이메일 인증 토큰 일괄 삭제
     */
    fun deleteAllByExpiresAtBefore(now: LocalDateTime?)
}