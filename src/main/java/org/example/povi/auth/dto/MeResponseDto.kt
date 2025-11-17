package org.example.povi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.auth.token.jwt.CustomJwtUser

/**
 * 로그인한 사용자의 정보를 반환하는 응답 DTO.
 */
@Schema(description = "로그인한 사용자의 정보를 담는 응답 DTO")
data class MeResponseDto(

    @field:Schema(description = "사용자 ID", example = "1")
    @param:Schema(description = "사용자 ID", example = "1")
    val id: Long,

    @field:Schema(description = "사용자 이메일", example = "user@example.com")
    @param:Schema(description = "사용자 이메일", example = "user@example.com")
    val email: String,

    @field:Schema(description = "사용자 닉네임", example = "홍길동")
    @param:Schema(description = "사용자 닉네임", example = "홍길동")
    val nickname: String
) {
    companion object {

        /**
         * CustomJwtUser → MeResponseDto 변환
         */
        fun from(user: CustomJwtUser): MeResponseDto =
            MeResponseDto(
                id = user.id,
                email = user.email,
                nickname = user.nickname
            )
    }
}