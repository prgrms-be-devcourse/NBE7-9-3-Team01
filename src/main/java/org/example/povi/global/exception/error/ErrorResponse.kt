package org.example.povi.global.exception.error

import java.time.LocalDateTime

/**
 * 예외 발생 시 반환되는 표준 에러 응답 DTO
 */
data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String?
) {
    companion object {
        /**
         * ErrorResponse 생성 팩토리 메서드
         */
        fun of(status: Int, error: String, message: String?): ErrorResponse {
            return ErrorResponse(
                timestamp = LocalDateTime.now(),
                status = status,
                error = error,
                message = message
            )
        }
    }
}