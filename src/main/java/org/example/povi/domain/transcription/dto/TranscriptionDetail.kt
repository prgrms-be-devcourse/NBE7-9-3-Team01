package org.example.povi.domain.transcription.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.transcription.entity.Transcription
import java.time.LocalDateTime

@Schema(description = "필사 상세 응답 DTO")
data class TranscriptionDetail(
    @get:Schema(
        description = "필사 ID",
        example = "1")
    val transcriptionId: Long?,

    @get:Schema(
        description = "필사한 문장 내용",
        example = "내가 제일 좋아하는 문장이에요.")
    val content: String?,

    @get:Schema(
        description = "명언 원문 내용",
        example = "삶은 자전거를 타는 것과 같다. 균형을 잡으려면 움직여야 한다.")
    val quoteContent: String,

    @get:Schema(
        description = "명언 저자",
        example = "알베르트 아인슈타인")
    val quoteAuthor: String,

    @get:Schema(
        description = "작성 일시",
        example = "2025-10-24T14:30:00")
    val createdAt: LocalDateTime?

) {
    constructor(transcription: Transcription) : this (
        transcriptionId = transcription.id,
        content = transcription.content,
        quoteContent = transcription.quote.content,
        quoteAuthor = transcription.quote.author,
        createdAt = transcription.createdAt
    )
}