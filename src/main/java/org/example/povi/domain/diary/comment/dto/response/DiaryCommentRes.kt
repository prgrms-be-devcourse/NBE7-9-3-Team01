package org.example.povi.domain.diary.comment.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "다이어리 댓글 응답 DTO")
@JvmRecord
data class DiaryCommentRes(
    val commentId: Long,
    val authorId: Long,
    val authorName: String,
    val content: String,
    val createdAt: LocalDateTime,
    val isMine: Boolean
) {
    companion object {
        /**
         * Entity → DTO (단건 변환)
         */
        @JvmStatic
        fun from(comment: DiaryComment, currentUserId: Long): DiaryCommentRes {
            val aid = comment.author.id!!

            return DiaryCommentRes(
                commentId = comment.id!!,
                authorId = aid,
                authorName = comment.author.nickname!!,
                content = comment.content,
                createdAt = comment.createdAt!!,
                isMine = (aid == currentUserId)
            )
        }

        /**
         * Entity List → DTO List (목록 변환)
         */
        @JvmStatic
        fun fromList(comments: List<DiaryComment>, currentUserId: Long): List<DiaryCommentRes> {
            return comments.map { from(it, currentUserId) }
        }
    }
}