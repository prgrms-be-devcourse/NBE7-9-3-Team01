package org.example.povi.domain.diary.post.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility

@Schema(description = "다이어리 게시글 수정 요청 DTO")
@JvmRecord
data class DiaryPostUpdateReq(@JvmField val title: @Size(min = 2, max = 50) String?,
                              @JvmField val content: @Size(min = 10, max = 3000) String?,
                              @JvmField val moodEmoji: MoodEmoji,
                              @JvmField val visibility: Visibility,
                              @JvmField val imageUrls: @Size(max = 3) MutableList<String>?

)
