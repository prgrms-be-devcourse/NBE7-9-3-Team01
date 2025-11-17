package org.example.povi.global.exception.ex

/**
 * 동일한 음성/텍스트를 중복으로 전사하려고 할 때 발생하는 예외
 */
class DuplicateTranscriptionException(
    message: String = "이미 전사된 데이터입니다."
) : RuntimeException(message)