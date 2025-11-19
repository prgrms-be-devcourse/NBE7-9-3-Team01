package org.example.povi.domain.community.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import org.example.povi.domain.community.entity.CommunityEmoticon

@Schema(description = "게시글 수정 요청 DTO")
@JvmRecord
data class PostUpdateRequest(@field:Schema(description = "게시글 제목", example = "수정된 제목") @param:Schema(description = "게시글 제목", example = "수정된 제목") val title: @NotBlank(message = "제목은 필수 입력 항목입니다.") String,
                             @field:Schema(description = "수정할 게시글 본문", example = "새롭게 수정한 내용입니다.") @param:Schema(description = "수정할 게시글 본문", example = "새롭게 수정한 내용입니다.") val content: @NotBlank(message = "내용은 비워둘 수 없습니다.") String,
                             @field:Schema(description = "수정할 사진 URL 목록", example = "[\"url1.jpg\", \"url2.jpg\"]") @param:Schema(description = "수정할 사진 URL 목록", example = "[\"url1.jpg\", \"url2.jpg\"]") val photoUrls: List<String>,
                             val emoticon: CommunityEmoticon

)
