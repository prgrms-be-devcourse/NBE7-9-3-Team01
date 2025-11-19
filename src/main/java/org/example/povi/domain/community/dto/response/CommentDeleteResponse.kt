package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "댓글 삭제 응답 DTO")
data class CommentDeleteResponse(
        val commentId: Long,
        val message: String
)
