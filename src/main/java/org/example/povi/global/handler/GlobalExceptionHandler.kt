package org.example.povi.global.handler

import org.example.povi.global.exception.error.ErrorResponse
import org.example.povi.global.exception.ex.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "잘못된 요청입니다.",
            ex.message
        )
        return ResponseEntity.badRequest().body(error)
    }

    /**
     * Bean Validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_REQUEST",
            "요청 형식이 올바르지 않습니다."
        )
        return ResponseEntity.badRequest().body(error)
    }

    /**
     * 중복(Conflict) 예외
     */
    @ExceptionHandler(DuplicateTranscriptionException::class)
    fun handleDuplicateTranscription(ex: DuplicateTranscriptionException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ex.message)
    }

    /**
     * 리소스 없음(404)
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.message)
    }

    /**
     * 권한 없음(403)
     */
    @ExceptionHandler(AuthorizationException::class)
    fun handleAuthorizationException(ex: AuthorizationException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ex.message)
    }

    /**
     * 인증 실패(401)
     */
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            ex.errorCode.name,
            ex.errorCode.message
        )
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(error)
    }

    /**
     * 프로젝트에서 사용하는 CustomException 처리
     */
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "요청 처리 실패",
            ex.errorCode.message
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error)
    }

    /**
     * 처리되지 않은 모든 예외 (500)
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "서버 내부 오류",
            ex.message
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error)
    }
}