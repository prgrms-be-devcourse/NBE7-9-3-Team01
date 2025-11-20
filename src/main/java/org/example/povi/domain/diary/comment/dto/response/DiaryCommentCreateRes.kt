package org.example.povi.domain.diary.comment.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "다이어리 댓글 작성 응답 DTO")
@JvmRecord
data class DiaryCommentCreateRes(
    val commentId: Long,
    val postId: Long,
    val authorId: Long,
    val authorName: String?,
    val content: String,
    val createdAt: LocalDateTime?
) {
    companion object {
        @JvmStatic
        fun from(comment: DiaryComment): DiaryCommentCreateRes {
            val post = comment.post
            val author = comment.author

            return DiaryCommentCreateRes(
                commentId = comment.id!!,
                postId = comment.post.id!!,
                authorId = comment.author.id!!,
                authorName = comment.author.nickname,
                content = comment.content,
                createdAt = comment.createdAt
            )
        }
    }
}