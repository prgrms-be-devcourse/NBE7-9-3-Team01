package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.Comment
import java.time.LocalDateTime

@Schema(description = "마이페이지 내 댓글 목록 조회를 위한 응답 DTO")
data class CommentListResponse(
        // 2. 주 생성자의 속성들을 'val'로 명확히 합니다. (혹은 val/var 생략)
        @Schema(description = "댓글 ID")
        val commentId: Long?, // 3. Nullable로 변경 (Long -> Long?)

        @Schema(description = "댓글 내용")
        val content: String?,

        @Schema(description = "댓글 작성일")
        val createdAt: LocalDateTime?, // 3. Nullable로 변경 (LocalDateTime -> LocalDateTime?)

        @field:Schema(description = "댓글이 달린 원본 게시글 ID")
        val postId: Long?, // 3. Nullable로 변경 (Long -> Long?)

        @Schema(description = "댓글이 달린 원본 게시글 제목")
        val postTitle: String?
) {

    companion object {
        @JvmStatic
        fun from(comment: Comment): CommentListResponse {
            // 5. 코틀린의 null-safe 'let'과 'take'를 사용해 안전하고 간결하게 요약
            val summaryTitle = comment.communityPost.title?.take(20)

            val summaryContent = comment.content?.let {
                if (it.length > 20) it.substring(0, 20) + "..." else it
            }

            return CommentListResponse(
                    comment.id, // 'comment.id' (Long?)를 'commentId' (Long?)에 전달
                    summaryContent,
                    comment.createdAt, // 'comment.createdAt' (LocalDateTime?)을 'createdAt' (LocalDateTime?)에 전달
                    comment.communityPost.id, // 'comment.communityPost.id' (Long?)를 'postId' (Long?)에 전달
                    summaryTitle
            )
        }
    }
}