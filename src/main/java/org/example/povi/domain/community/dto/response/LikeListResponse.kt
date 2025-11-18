package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.CommunityEmoticon
import org.example.povi.domain.community.entity.CommunityPost
import java.time.LocalDateTime

@Schema(description = "좋아요한 커뮤니티 게시글 목록 응답 DTO")
data class LikeListResponse(
        val postId: Long,
        val postTitle: String?,
        val content: String?,
        val postAuthorNickname: String,
        val emoticon: CommunityEmoticon,
        val postCreatedAt: LocalDateTime
) {

    companion object {
        @JvmStatic
        fun from(post: CommunityPost): LikeListResponse {
            var summaryTitle = post.title
            var summaryContent = post.content

            if (summaryTitle != null && summaryTitle.length > 20) {
                summaryTitle = summaryTitle.substring(0, 20) + "..."
            }
            if (summaryContent != null && summaryContent.length > 20) {
                summaryContent = summaryContent.substring(0, 20) + "..."
            }
            return LikeListResponse(
                    post.id!!,
                    summaryTitle,
                    summaryContent,
                    post.user.nickname,
                    post.emoticon,
                    post.createdAt!!
            )
        }
    }
}
