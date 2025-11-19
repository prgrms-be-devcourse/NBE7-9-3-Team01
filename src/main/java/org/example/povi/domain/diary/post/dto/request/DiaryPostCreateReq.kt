package org.example.povi.domain.diary.post.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility

@Schema(description = "다이어리 게시글 작성 요청 DTO")
@JvmRecord
data class DiaryPostCreateReq(val title: @NotBlank @Size(min = 1, max = 50) String?,
                              val content: @NotBlank @Size(min = 1, max = 3000) String?,
                              val moodEmoji: @NotNull MoodEmoji?,
                              val visibility: @NotNull Visibility?,
                              val imageUrls: @Size(max = 3) MutableList<String>?
) 