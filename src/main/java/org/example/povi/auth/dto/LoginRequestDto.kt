package org.example.povi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 로그인 요청 DTO - 이메일과 비밀번호를 포함합니다.
 */
@Schema(description = "로그인 요청 DTO")
data class LoginRequestDto(

    @field:Schema(
        description = "사용자 이메일",
        example = "user@example.com"
    )
    @param:Schema(
        description = "사용자 이메일",
        example = "user@example.com"
    )
    val email: String,

    @field:Schema(
        description = "사용자 비밀번호",
        example = "p@ssW0rd123"
    )
    @param:Schema(
        description = "사용자 비밀번호",
        example = "p@ssW0rd123"
    )
    val password: String
)