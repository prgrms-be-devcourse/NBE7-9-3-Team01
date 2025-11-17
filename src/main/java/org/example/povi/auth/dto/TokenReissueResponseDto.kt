package org.example.povi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * AccessToken 재발급 응답 DTO
 */
@Schema(description = "AccessToken 재발급 응답 DTO")
data class TokenReissueResponseDto(

    @field:Schema(
        description = "재발급된 Access Token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @param:Schema(
        description = "재발급된 Access Token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    val accessToken: String,

    @field:Schema(
        description = "재발급된 Refresh Token",
        example = "dGhpcyBpcyBhIHZhbGlkIHJlZnJlc2ggdG9rZW4="
    )
    @param:Schema(
        description = "재발급된 Refresh Token",
        example = "dGhpcyBpcyBhIHZhbGlkIHJlZnJlc2ggdG9rZW4="
    )
    val refreshToken: String
)