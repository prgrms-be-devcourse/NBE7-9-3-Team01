package org.example.povi.auth.util

import org.example.povi.auth.token.jwt.CustomJwtUser
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {

    /**
     * 현재 인증된 사용자 정보 조회
     * - 인증 안 되어 있으면 UnauthorizedException 발생
     * - CustomJwtUser 타입이 아니어도 UnauthorizedException 발생
     */
    val currentUserOrThrow: CustomJwtUser
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw UnauthorizedException()

            if (!authentication.isAuthenticated) {
                throw UnauthorizedException()
            }

            val principal = authentication.principal
            if (principal !is CustomJwtUser) {
                throw UnauthorizedException()
            }

            return principal
        }

    class UnauthorizedException :
        RuntimeException("인증되지 않은 사용자입니다.")
}