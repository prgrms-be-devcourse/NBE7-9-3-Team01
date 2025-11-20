package org.example.povi.domain.quote.dto
import io.swagger.v3.oas.annotations.media.Schema
import org.example.povi.domain.quote.entity.Quote

@Schema(description = "명언 응답 DTO")
data class QuoteRes(
    @field:Schema(description = "명언 ID", example = "1") @param:Schema(
        description = "명언 ID",
        example = "1"
    ) val quoteId: Long,

    @field:Schema(description = "작성자", example = "파울로 코엘료") @param:Schema(
        description = "작성자",
        example = "파울로 코엘료"
    ) val author: String,

    @field:Schema(
        description = "명언 메시지",
        example = "당신이 무언가를 간절히 원할 때, 온 우주는 그것을 이루기 위해 움직인다."
    ) @param:Schema(
        description = "명언 메시지",
        example = "당신이 무언가를 간절히 원할 때, 온 우주는 그것을 이루기 위해 움직인다."
    ) val message: String

) {
    constructor(quote: Quote) : this(
        quoteId = quote.id!!,
        author = quote.author,
        message = quote.content
    )
}