package org.example.povi.global.exception.ex

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 특정 작업(예: 이메일 인증 요청 등)이 지정된 횟수를 초과했을 때 발생하는 예외
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class RateLimitExceededException(
    message: String = "요청 제한을 초과했습니다. 잠시 후 다시 시도해주세요."
) : RuntimeException(message)