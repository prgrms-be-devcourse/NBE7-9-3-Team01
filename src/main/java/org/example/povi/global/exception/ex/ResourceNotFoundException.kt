package org.example.povi.global.exception.ex

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 요청한 리소스를 찾을 수 없을 때 발생하는 예외 (404 Not Found)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(
    message: String = "요청한 리소스를 찾을 수 없습니다."
) : RuntimeException(message)