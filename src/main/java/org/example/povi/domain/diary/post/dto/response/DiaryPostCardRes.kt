package org.example.povi.domain.diary.post.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.mapper.DiaryPreviewMapper.buildPreviewText
import org.example.povi.domain.diary.post.mapper.DiaryPreviewMapper.firstImageUrl
import org.example.povi.domain.diary.post.view.PostViewStats
import java.time.LocalDate

@Schema(description = "다이어리 게시글 카드 응답 DTO")
@JvmRecord
data class DiaryPostCardRes(val postId: Long?,
                            val authorId: Long?,
                            val authorName: String,
                            val title: String,
                            val preview: String,
                            val thumbnailUrl: String?,
                            val moodEmoji: MoodEmoji,
                            val visibility: Visibility,
                            val createdDate: LocalDate,
                            val liked: Boolean,
                            val likeCount: Long,
                            val commentCount: Long

) {
    companion object {
        fun from(post: DiaryPost, stats: PostViewStats): DiaryPostCardRes {
            return DiaryPostCardRes(
                    post.id,
                    post.user.id,
                    post.user.nickname,
                    post.title,
                    buildPreviewText(post.content, 100),
                    firstImageUrl(post),
                    post.moodEmoji,
                    post.visibility,
                    post.createdAt!!.toLocalDate(),
                    stats.likedByMe,
                    stats.likeCount,
                    stats.commentCount
            )
        }
    }
}