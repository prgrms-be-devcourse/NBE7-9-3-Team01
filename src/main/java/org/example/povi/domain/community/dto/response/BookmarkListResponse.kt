package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.CommunityEmoticon
import org.example.povi.domain.community.entity.CommunityPost
import java.time.LocalDateTime

@Schema(description = "북마크한 커뮤니티 게시글 목록 응답 DTO")
@JvmRecord
data class BookmarkListResponse(val postId: Long?,
                                val postTitle: String?,
                                val content: String?,
                                val postAuthorNickname: String,
                                val emoticon: CommunityEmoticon,
                                val imageUrls: List<String> = emptyList(),
                                val postCreatedAt: LocalDateTime
) {
    companion object {
        @JvmStatic
        fun from(post: CommunityPost): BookmarkListResponse {
            var summaryTitle: String = post.title
            var summaryContent: String = post.content

            if (summaryTitle != null && summaryTitle.length > 20) {
                summaryTitle = summaryTitle.substring(0, 20) + "..."
            }
            if (summaryContent != null && summaryContent.length > 20) {
                summaryContent = summaryContent.substring(0, 20) + "..."
            }
            return BookmarkListResponse(
                    post.id,
                    summaryTitle,
                    summaryContent,
                    post.user.nickname,
                    post.emoticon,
                    post.images.map { it.imageUrl },
                    post.createdAt!!
            )
        }
    }
}
