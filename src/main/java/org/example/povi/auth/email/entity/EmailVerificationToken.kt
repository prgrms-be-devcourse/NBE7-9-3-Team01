package org.example.povi.auth.email.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 이메일 인증 토큰 엔티티
 */
@Entity
@Table(name = "email_verification_token")
class EmailVerificationToken(

    /**
     * PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    /**
     * 토큰 발급 대상 이메일
     */
    @Column(nullable = false)
    var email: String? = null,

    /**
     * 인증용 고유 토큰 (UUID 문자열)
     */
    @Column(nullable = false, unique = true)
    var token: String? = null,

    /**
     * 만료 시각 (기준: 발급 후 60분)
     */
    @Column(nullable = false)
    var expiresAt: LocalDateTime? = null,

    /**
     * 인증 여부 플래그
     */
    @Column(nullable = false)
    var verified: Boolean = false

) {

    // JPA 기본 생성자
    protected constructor() : this(
        id = null,
        email = null,
        token = null,
        expiresAt = null,
        verified = false
    )

    /**
     * 토큰 만료 여부 확인
     */
    val isExpired: Boolean
        get() = expiresAt?.let { LocalDateTime.now().isAfter(it) } ?: true

    /**
     * 인증 완료 처리
     */
    fun markAsVerified() {
        this.verified = true
    }

    /**
     * 새 토큰으로 갱신 (재요청 시)
     */
    fun updateToken(newToken: String?, newExpiresAt: LocalDateTime?) {
        this.token = newToken
        this.expiresAt = newExpiresAt
        this.verified = false
    }
}