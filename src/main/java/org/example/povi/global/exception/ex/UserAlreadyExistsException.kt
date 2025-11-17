package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * 이미 가입된 이메일(중복 사용자)이 존재할 때 발생하는 예외
 */
class UserAlreadyExistsException :
    CustomException(ErrorCode.USER_ALREADY_EXISTS)