package org.example.povi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 회원가입 요청 DTO
 */
@Schema(description = "회원가입 요청을 위한 데이터 전송 객체")
data class SignupRequestDto(

    @field:Schema(description = "사용자 이메일", example = "user@example.com")
    @param:Schema(description = "사용자 이메일", example = "user@example.com")
    val email: String,

    @field:Schema(description = "사용자 비밀번호", example = "secureP@ssw0rd")
    @param:Schema(description = "사용자 비밀번호", example = "secureP@ssw0rd")
    val password: String,

    @field:Schema(description = "사용자 닉네임", example = "홍길동")
    @param:Schema(description = "사용자 닉네임", example = "홍길동")
    val nickname: String,

    @field:Schema(description = "OAuth 제공자 (ex: google, kakao)", example = "google")
    @param:Schema(description = "OAuth 제공자 (ex: google, kakao)", example = "google")
    val provider: String,

    @field:Schema(description = "OAuth 제공자 고유 ID", example = "1037246634527")
    @param:Schema(description = "OAuth 제공자 고유 ID", example = "1037246634527")
    val providerId: String
)