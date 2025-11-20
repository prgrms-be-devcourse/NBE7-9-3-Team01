package org.example.povi.domain.transcription.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.transcription.entity.Transcription
import java.time.LocalDateTime

@Schema(description = "필사 생성 응답 DTO")
data class TranscriptionRes(
    @get:Schema(
        description = "필사 ID",
        example = "1")
    val transcriptionId: Long,

    @get:Schema(
        description = "필사 내용",
        example = "오늘 하루도 잘 해냈어. 수고했어."
    )
    val content: String,

    @get:Schema(
        description = "작성 시각",
        example = "2025-10-24T21:32:00")
    val createdAt: LocalDateTime
) {
    constructor(transcription: Transcription) : this(
        transcriptionId = transcription.id!!,
        content = transcription.content,
        createdAt = transcription.createdAt!!
    )
}