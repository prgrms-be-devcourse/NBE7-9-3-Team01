package org.example.povi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO.
 */
@Schema(description = "로그인 성공 응답 DTO")
data class LoginResponseDto(

    @field:Schema(
        description = "Access Token (JWT)",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0..."
    )
    @param:Schema(
        description = "Access Token (JWT)",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0..."
    )
    val accessToken: String,

    @field:Schema(
        description = "Refresh Token (JWT)",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2..."
    )
    @param:Schema(
        description = "Refresh Token (JWT)",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2..."
    )
    val refreshToken: String,

    @field:Schema(
        description = "사용자 닉네임",
        example = "홍길동"
    )
    @param:Schema(
        description = "사용자 닉네임",
        example = "홍길동"
    )
    val nickname: String
)