package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.community.entity.Comment
import org.example.povi.domain.community.entity.CommunityEmoticon
import org.example.povi.domain.community.entity.CommunityImage
import org.example.povi.domain.community.entity.CommunityPost
import java.time.LocalDateTime
import java.util.stream.Collectors

@Schema(description = "게시글 상세 조회 응답 DTO")
class PostDetailResponse(@field:Schema(description = "게시글 ID", example = "101")
                         @param:Schema(description = "게시글 ID", example = "101")
                         val postId: Long,

                         @field:Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
                         @param:Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
                         val title: String,

                         @field:Schema(description = "게시글 본문", example = "집에만 있기 아까운 날씨입니다. 다들 즐거운 하루 보내세요.")
                         val content: String,

                         @Schema(description = "감정 이모티콘", example = "HAPPY")
                         val emoticon: CommunityEmoticon,

                         @Schema(description = "작성자 닉네임", example = "행복한개발자")
                         val authorNickname: String,

                         @Schema(description = "작성일시")
                         val createdAt: LocalDateTime,

                         @Schema(description = "댓글 목록")
                         val comments: List<CommentResponse>,

                         @field:Schema(description = "첨부된 사진 URL 목록")
                         @param:Schema(description = "첨부된 사진 URL 목록")
                         val photoUrls: List<String>

) {
    companion object {
        @JvmStatic
        fun from(post: CommunityPost): PostDetailResponse {
            val urls: List<String> = post.images.stream()
                    .map(CommunityImage::imageUrl) // Photo 엔티티에 getFileUrl()이 있다고 가정
                    .collect(Collectors.toList())

            return PostDetailResponse(
                    post.id!!,
                    post.title,
                    post.content,
                    post.emoticon,
                    post.user.nickname,
                    post.createdAt!!,
                    post.comments.stream()
                            .map { comment: Comment -> CommentResponse.Companion.from(comment) }
                            .collect(Collectors.toList()),
                    urls
            )
        }
    }
}
