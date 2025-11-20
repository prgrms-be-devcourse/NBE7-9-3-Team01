package org.example.povi.domain.transcription.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.transcription.entity.Transcription
import org.springframework.data.domain.Page

@Schema(description = "사용자 필사 목록 응답 DTO")
data class TranscriptionPageRes(
    @get:Schema(description = "필사 상세 목록")
    val content: List<TranscriptionDetail>,

    @get:Schema(description = "전체 필사 개수")
    val totalElements: Long,

    @get:Schema(description = "전체 페이지 수")
    val totalPages: Int, // 전체 페이지 수

    @get:Schema(description = "현재 페이지 번호 (0부터 시작)")
    val number: Int, // 현재 페이지 번호

    @get:Schema(description = "페이지 크기")
    val size: Int // 페이지 크기
    ) {
    constructor(page: Page<Transcription>) : this(
        content = page.content.map { TranscriptionDetail(it) },
        totalElements = page.totalElements,
        totalPages = page.totalPages,
        number = page.number,
        size = page.size
    )
}