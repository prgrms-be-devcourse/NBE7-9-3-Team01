package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * 로그인 시 비밀번호가 일치하지 않을 때 발생하는 예외
 */
class InvalidPasswordException :
    CustomException(ErrorCode.INVALID_PASSWORD)