package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 수정 응답 DTO")
@JvmRecord
data class PostUpdateResponse(@field:Schema(description = "수정된 게시글 ID", example = "1") @param:Schema(description = "수정된 게시글 ID", example = "1") val postId: Long,

                              @field:Schema(description = "수정된 제목", example = "수정된 제목입니다") @param:Schema(description = "수정된 제목", example = "수정된 제목입니다") val title: String,

                              @field:Schema(description = "수정된 내용", example = "새롭게 수정한 내용입니다.") @param:Schema(description = "수정된 내용", example = "새롭게 수정한 내용입니다.") val content: String,

                              @field:Schema(description = "새롭게 등록된 사진 URL 목록", example = "[\"url1.jpg\", \"url2.jpg\"]") @param:Schema(description = "새롭게 등록된 사진 URL 목록", example = "[\"url1.jpg\", \"url2.jpg\"]") val photoUrls: List<String>,

                              @field:Schema(description = "응답 메시지", example = "게시글이 성공적으로 수정되었습니다.") @param:Schema(description = "응답 메시지", example = "게시글이 성공적으로 수정되었습니다.") val message: String
)
