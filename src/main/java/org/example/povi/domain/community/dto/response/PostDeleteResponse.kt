package org.example.povi.domain.community.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 삭제 응답 DTO")
@JvmRecord
data class PostDeleteResponse(
        val postId: Long,
        val message: String) {

}