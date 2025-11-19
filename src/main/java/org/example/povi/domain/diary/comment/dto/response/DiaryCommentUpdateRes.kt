package org.example.povi.domain.diary.comment.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "다이어리 댓글 수정 응답 DTO")
@JvmRecord
data class DiaryCommentUpdateRes(
    val commentId: Long,
    val postId: Long,
    val authorId: Long,
    val authorName: String,
    val content: String,
    val updatedAt: LocalDateTime
) {
    companion object {
        @JvmStatic
        fun from(entity: DiaryComment): DiaryCommentUpdateRes {
            return DiaryCommentUpdateRes(
                commentId = entity.id!!,
                postId = entity.post.id!!,
                authorId = entity.author.id!!,
                authorName = entity.author.nickname!!,
                content = entity.content,
                updatedAt = entity.updatedAt!!
            )
        }
    }
}