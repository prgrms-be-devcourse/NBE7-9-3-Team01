package org.example.povi.auth.email.mapper

import org.example.povi.auth.email.entity.EmailVerificationToken
import java.time.LocalDateTime

/**
 * 이메일 인증 토큰 생성/갱신 매퍼
 */
object EmailVerificationTokenMapper {

    fun createOrUpdate(
        existingToken: EmailVerificationToken?,
        email: String?,
        token: String?,
        expiresAt: LocalDateTime?
    ): EmailVerificationToken {

        return if (existingToken != null) {
            existingToken.updateToken(token, expiresAt)
            existingToken
        } else {
            EmailVerificationToken(
                id = null,
                email = email,
                token = token,
                expiresAt = expiresAt,
                verified = false
            )
        }
    }
}