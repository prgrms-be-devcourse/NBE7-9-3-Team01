package org.example.povi.domain.diary.post.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.post.entity.DiaryImage
import org.example.povi.domain.diary.post.entity.DiaryPost
import java.time.LocalDateTime

@Schema(description = "다이어리 게시글 작성 응답 DTO")
class DiaryPostCreateRes(title: String,
                         content: String,
                         moodEmoji: MoodEmoji,
                         visibility: Visibility,
                         imageUrls: List<String>,
                         createdAt: LocalDateTime?
) {

    companion object {
        @JvmStatic
        fun from(post: DiaryPost): DiaryPostCreateRes {
            return DiaryPostCreateRes(
                    post.title,
                    post.content,
                    post.moodEmoji,
                    post.visibility,
                    post.images.stream()
                            .map(DiaryImage::imageUrl)
                            .toList(),
                    post.createdAt
            )
        }
    }
}