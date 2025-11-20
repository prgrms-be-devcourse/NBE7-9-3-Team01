package org.example.povi.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.transcription.dto.TranscriptionPageRes

@Schema(description = "마이페이지 응답 DTO")
data class MyPageRes(
    @get:Schema(description = "사용자 프로필 정보")
    val profileRes: ProfileRes,

    @get:Schema(
        description = "사용자가 작성한 일기 개수",
        example = "12")
    val diaryCount: Long,

    @get:Schema(description = "사용자가 작성한 필사 목록")
    val transcriptionPageRes: TranscriptionPageRes

)