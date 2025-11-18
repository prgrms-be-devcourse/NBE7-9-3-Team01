package org.example.povi.domain.diary.enums.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "기분 이모지 옵션 응답 DTO")
data class MoodEmojiOptionRes(
    val code: String?,
    val label: String?
) 