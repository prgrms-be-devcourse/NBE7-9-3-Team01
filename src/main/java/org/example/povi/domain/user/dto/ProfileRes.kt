package org.example.povi.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 프로필 응답 DTO")
data class ProfileRes(
    @get:Schema(
        description = "사용자 닉네임",
        example = "행복한코끼리")
    val nickname: String,

    @get:Schema(
        description = "프로필 이미지 URL",
        example = "https://example.com/images/profile.jpg")
    val profileImgUrl: String?,

    @get:Schema(
        description = "자기소개 (bio)",
        example = "하루하루 성장하는 개발자입니다.")
    val bio: String?
)