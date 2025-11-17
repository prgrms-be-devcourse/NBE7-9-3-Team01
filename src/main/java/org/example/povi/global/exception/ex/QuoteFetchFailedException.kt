package org.example.povi.global.exception.ex

/**
 * 외부 서비스나 API에서 명언(quote) 데이터를 가져오지 못했을 때 발생하는 예외
 */
class QuoteFetchFailedException(
    message: String = "명언 데이터를 가져오지 못했습니다.",
    cause: Throwable? = null
) : RuntimeException(message, cause)