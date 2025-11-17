package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * 모든 커스텀 예외의 부모 클래스
 */
open class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)