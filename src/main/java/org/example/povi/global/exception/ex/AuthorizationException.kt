package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * 403 Forbidden - 권한 없음 예외
 */
class AuthorizationException(
    override val message: String? = ErrorCode.UNAUTHORIZED.message  // 기본 메시지 사용
) : CustomException(ErrorCode.UNAUTHORIZED)