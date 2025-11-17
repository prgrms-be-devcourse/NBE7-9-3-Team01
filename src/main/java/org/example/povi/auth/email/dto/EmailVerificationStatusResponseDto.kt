package org.example.povi.auth.email.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 이메일 인증 상태 응답 DTO입니다.
 */
@Schema(description = "이메일 인증 상태 응답 DTO")
data class EmailVerificationStatusResponseDto(

    @field:Schema(
        description = "인증 대상 이메일 주소",
        example = "user@example.com"
    )
    @param:Schema(
        description = "인증 대상 이메일 주소",
        example = "user@example.com"
    )
    val email: String,

    @field:Schema(
        description = "이메일 인증 완료 여부",
        example = "true"
    )
    @param:Schema(
        description = "이메일 인증 완료 여부",
        example = "true"
    )
    val verified: Boolean
)