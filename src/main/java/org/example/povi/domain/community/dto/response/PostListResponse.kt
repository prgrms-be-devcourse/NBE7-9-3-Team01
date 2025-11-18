package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.CommunityEmoticon
import org.example.povi.domain.community.entity.CommunityPost
import java.time.LocalDateTime

@Schema(description = "게시글 목록 응답 DTO")
class PostListResponse(@field:Schema(description = "게시글 ID", example = "101")
                       @param:Schema(description = "게시글 ID", example = "101")
                       val postId: Long,

                       @field:Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
                       @param:Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
                       val title: String,

                       @param:Schema(description = "게시글 본문 미리보기", example = "집에만 있기 아까운 날씨입니다. 다들 즐거운 하루 보내세요.")
                       val content: String?,

                       @Schema(description = "작성자 닉네임", example = "행복한개발자")
                       val authorNickname: String,

                       @Schema(description = "작성일시")
                       val createdAt: LocalDateTime,

                       @Schema(description = "감정 이모티콘", example = "HAPPY")
                       val emoticon: CommunityEmoticon,

                       @Schema(description = "좋아요 수", example = "25")
                       val likeCount: Int,

                       @Schema(description = "댓글 수", example = "10")
                       val commentCount: Int


) {
    companion object {
        @JvmStatic
        fun from(post: CommunityPost): PostListResponse {
            var summaryContent = post.content
            // 내용이 20자보다 클 경우, "..."을 붙임
            if (summaryContent != null && summaryContent.length > 20) {
                summaryContent = summaryContent.substring(0, 20) + "..."
            }

            return PostListResponse(
                    post.id!!,
                    post.title,
                    summaryContent,
                    post.user.nickname,
                    post.createdAt!!,
                    post.emoticon,
                    post.likeCount,
                    post.comments.size
            )
        }
    }
}
