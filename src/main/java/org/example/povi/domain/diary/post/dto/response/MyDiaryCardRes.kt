package org.example.povi.domain.diary.post.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.mapper.DiaryPreviewMapper.buildPreviewText
import org.example.povi.domain.diary.post.mapper.DiaryPreviewMapper.firstImageUrl
import org.example.povi.domain.diary.post.view.PostViewStats
import java.time.LocalDate

@Schema(description = "내 다이어리 게시글 카드 응답 DTO")
class MyDiaryCardRes(val postId: Long?,
                     val title: String,
                     preview: String,
                     moodEmoji: MoodEmoji,
                     thumbnailUrl: String?,
                     visibility: Visibility,
                     createdDate: LocalDate,
                     liked: Boolean,
                     likeCount: Long,
                     commentCount: Long
) {

    companion object {
        fun from(post: DiaryPost, stats: PostViewStats): MyDiaryCardRes {
            return MyDiaryCardRes(
                    post.id,
                    post.title,
                    buildPreviewText(post.content, 100),
                    post.moodEmoji,
                    firstImageUrl(post),
                    post.visibility,
                    post.createdAt!!.toLocalDate(),
                    stats.likedByMe,
                    stats.likeCount,
                    stats.commentCount
            )
        }
    }
}