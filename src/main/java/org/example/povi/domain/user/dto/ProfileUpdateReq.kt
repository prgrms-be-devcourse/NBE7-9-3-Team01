package org.example.povi.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "프로필 수정 요청 DTO")
data class ProfileUpdateReq(
    @get:Schema(
        description = "닉네임",
        example = "행복한코끼리",
        minLength = 2,
        maxLength = 50)
    @field: NotBlank(message = "닉네임은 필수항목입니다.") @field: Size(min = 2, max = 50)
    val nickname: String,

    @get:Schema(
        description = "자기소개 (bio)",
        example = "소소한 행복을 기록하는 개발자입니다.")
    val bio: String?
)