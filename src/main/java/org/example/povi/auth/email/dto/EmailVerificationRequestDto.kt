package org.example.povi.auth.email.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 이메일 인증 요청 DTO
 */
@Schema(description = "이메일 인증 요청 DTO")
data class EmailVerificationRequestDto(

    @field:Schema(
        description = "이메일 주소",
        example = "user@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @param:Schema(
        description = "이메일 주소",
        example = "user@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수 입력값입니다.")
    val email: String?
)