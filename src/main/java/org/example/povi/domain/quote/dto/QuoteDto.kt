package org.example.povi.domain.quote.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "오늘의 명언 응답 DTO")
data class QuoteDto (
    @Schema(description = "명언 작성자", example = "알베르트 아인슈타인")
    val author: String,

    @Schema(description = "작성자 프로필 링크", example = "https://en.wikipedia.org/wiki/Albert_Einstein")
    val authorProfile: String,

    @Schema(description = "명언 메시지 내용", example = "삶은 자전거를 타는 것과 같다. 균형을 잡으려면 움직여야 한다.")
    val message: String
)