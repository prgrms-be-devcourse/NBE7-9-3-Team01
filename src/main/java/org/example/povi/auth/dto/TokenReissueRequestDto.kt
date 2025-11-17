package org.example.povi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * AccessToken 재발급 요청 DTO
 */
@Schema(description = "AccessToken 재발급 요청 DTO")
data class TokenReissueRequestDto(

    @field:Schema(
        description = "만료되었거나 만료 예정인 Access Token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @param:Schema(
        description = "만료되었거나 만료 예정인 Access Token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    val accessToken: String,

    @field:Schema(
        description = "유효한 Refresh Token",
        example = "dGhpcyBpcyBhIHZhbGlkIHJlZnJlc2ggdG9rZW4="
    )
    @param:Schema(
        description = "유효한 Refresh Token",
        example = "dGhpcyBpcyBhIHZhbGlkIHJlZnJlc2ggdG9rZW4="
    )
    val refreshToken: String
)