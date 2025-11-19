package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.Comment
import java.time.LocalDateTime

@Schema(description = "댓글 응답 DTO")
data class CommentResponse(
    val commentId: Long,
    val authorName: String,
    var content: String,
    var likeCount: Int,
    var createdAt: LocalDateTime
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                    comment.id!!,
                    comment.user.nickname,
                    comment.content,
                    comment.likeCount, // assuming Comment has a likeCount field
                    comment.createdAt!!
            )
        }
    }
}

