@file:JvmName("DiaryCommentMapper")
package org.example.povi.domain.diary.comment.mapper

import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes
import org.example.povi.global.dto.PagedResponse
import org.springframework.data.domain.Page

/**
     * Page<DiaryComment> → PagedResponse<DiaryCommentRes>
     * - 엔티티 페이지를 DTO 페이지 응답 형태로 변환
    </DiaryCommentRes></DiaryComment> */
fun toPagedResponse(commentPage: Page<DiaryComment>, currentUserId: Long): PagedResponse<DiaryCommentRes> {
    val items = DiaryCommentRes.fromList(commentPage.content, currentUserId)

    return PagedResponse.of(
        items,
        commentPage.number,
        commentPage.size,
        commentPage.totalElements,
        commentPage.totalPages,
        commentPage.isFirst,
        commentPage.isLast
    )
}