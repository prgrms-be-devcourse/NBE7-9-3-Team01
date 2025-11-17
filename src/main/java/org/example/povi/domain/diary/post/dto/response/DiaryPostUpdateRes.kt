package org.example.povi.domain.diary.post.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.post.entity.DiaryImage
import org.example.povi.domain.diary.post.entity.DiaryPost
import java.time.LocalDateTime

@Schema(description = "다이어리 게시글 수정 응답 DTO")
@JvmRecord
data class DiaryPostUpdateRes(val postId: Long?,
                              val title: String,
                              val content: String,
                              val moodEmoji: MoodEmoji,
                              val visibility: Visibility,
                              val imageUrls: List<String>,
                              val createdAt: LocalDateTime?,
                              val updatedAt: LocalDateTime?
) {
    companion object {
        @JvmStatic
        fun from(post: DiaryPost): DiaryPostUpdateRes {
            return DiaryPostUpdateRes(
                    post.id,
                    post.title,
                    post.content,
                    post.moodEmoji,
                    post.visibility,
                    post.images.stream()
                            .map(DiaryImage::imageUrl)
                            .toList(),
                    post.createdAt,
                    post.updatedAt
            )
        }
    }
}

