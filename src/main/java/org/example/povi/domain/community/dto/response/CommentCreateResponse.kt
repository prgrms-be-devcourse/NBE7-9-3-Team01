package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.Comment
import java.time.LocalDateTime

@Schema(description = "댓글 작성 완료 응답 DTO")
data class CommentCreateResponse(
        @Schema(description = "생성된 댓글 ID")
        val commentId: Long?, // 1. Long -> Long? (Nullable로 변경)

        @Schema(description = "댓글 내용")
        val content: String,

        @Schema(description = "작성자 닉네임")
        val authorNickname: String,

        @Schema(description = "작성 시각")
        val createdAt: LocalDateTime? // 2. LocalDateTime -> LocalDateTime? (Nullable로 변경)
) {

    companion object {
        @JvmStatic
        fun from(comment: Comment): CommentCreateResponse {
            return CommentCreateResponse(
                    comment.id, // 3. '!!' 없이 'comment.id' (Long?)를 전달
                    comment.content,
                    comment.user.nickname,
                    comment.createdAt // 4. '!!' 없이 'comment.createdAt' (LocalDateTime?)을 전달
            )
        }
    }
}
