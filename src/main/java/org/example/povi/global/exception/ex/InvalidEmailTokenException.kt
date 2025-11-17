package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * 유효하지 않은 이메일 인증 토큰 예외
 */
class InvalidEmailTokenException :
    CustomException(ErrorCode.INVALID_EMAIL_TOKEN)