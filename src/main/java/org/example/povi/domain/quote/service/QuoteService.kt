package org.example.povi.domain.quote.service

import org.example.povi.domain.quote.dto.QuoteDto
import org.example.povi.domain.quote.dto.QuoteRes
import org.example.povi.domain.quote.entity.Quote
import org.example.povi.domain.quote.repository.QuoteRepository
import org.example.povi.global.exception.ex.QuoteFetchFailedException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class QuoteService (
    private val quoteRepository: QuoteRepository,
    private val restTemplate: RestTemplate
) {
    fun getQuote() {
        try {
            restTemplate.getForObject(QUOTE_API_URL, QuoteDto::class.java)?.let { quoteDto ->

                val author = "${quoteDto.author} ${quoteDto.authorProfile}"
                val message = quoteDto.message

                if (message.isNotEmpty()) {
                    val quote = Quote(author, message)
                    quoteRepository.save(quote)
                }
            }

        } catch (e: RestClientException) {
            throw QuoteFetchFailedException("외부 명언 API 호출에 실패했습니다.", e)
        }
    }

    fun getTodayQuote(): QuoteRes? {
        // 오늘의 시작과 끝 시간 설정
        val startOfDay = LocalDate.now().atStartOfDay() // 오늘 00:00:00
        val endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX) // 오늘 23:59:59.99...

        // Repository를 통해 DB에서 오늘의 명언 조회
        val quote = quoteRepository.findFirstByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay)

        // Entity를 DTO로 변환하여 반환
        return quote?.let {
            QuoteRes(it)
        }
    }

    companion object {
        private const val QUOTE_API_URL = "https://korean-advice-open-api.vercel.app/api/advice"
    }
}
