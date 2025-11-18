package org.example.povi.domain.community.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.povi.domain.community.entity.CommunityEmoticon
import org.example.povi.domain.community.entity.CommunityPost
import org.example.povi.domain.user.entity.User

@Schema(description = "게시글 작성 요청 DTO")
@JvmRecord
data class PostCreateRequest(val title: @NotBlank(message = "제목은 필수 입력 항목입니다.") @Size(max = 50, message = "제목은 50자 초과할 수 없습니다.") String?,

                             val content: @NotBlank(message = "내용은 필수 입력 항목입니다.") @Size(max = 1000, message = "내용은 1000자 초과할 수 없습니다.") String?,

                             val userId: Long,
                             val emoticon: CommunityEmoticon,
                             val imageUrls: @Size(max = 3, message = "이미지는 최대 3장까지 첨부할 수 있습니다.") MutableList<String>?
) {
    fun toEntity(user: User): CommunityPost {
        return CommunityPost(
            title = requireNotNull(title) { "제목은 필수 입력 항목입니다." },
            content = requireNotNull(content) { "내용은 필수 입력 항목입니다." },
            user = user,
            emoticon = emoticon
        )
    }
}
