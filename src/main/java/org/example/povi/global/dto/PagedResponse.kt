package org.example.povi.global.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공통 페이지네이션 응답 DTO")
data class PagedResponse<T>(
    @field:Schema(description = "현재 페이지의 데이터 목록", example = "[ ... ]")
    val content: List<T>,
    @field:Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    val pageNumber: Int,
    @field:Schema(description = "페이지당 데이터 개수", example = "20")
    val pageSize: Int,
    @field:Schema(description = "전체 데이터 개수", example = "100")
    val totalElements: Long,
    @field:Schema(description = "전체 페이지 수", example = "5")
    val totalPages: Int,
    @field:Schema(description = "첫 페이지 여부", example = "true")
    val isFirst: Boolean,
    @field:Schema(description = "마지막 페이지 여부", example = "false")
    val isLast: Boolean
) {
    companion object {

        @JvmStatic
        fun <T> of(
            content: List<T>,
            pageNumber: Int,
            pageSize: Int,
            totalElements: Long,
            totalPages: Int,
            isFirst: Boolean,
            isLast: Boolean
        ): PagedResponse<T> {
            return PagedResponse(
                content, pageNumber, pageSize,
                totalElements, totalPages,
                isFirst, isLast
            )
        }
    }
}