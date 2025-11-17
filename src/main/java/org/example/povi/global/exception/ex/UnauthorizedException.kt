package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * 인증되지 않은 사용자(401 Unauthorized) 예외
 */
class UnauthorizedException :
    CustomException(ErrorCode.UNAUTHORIZED)