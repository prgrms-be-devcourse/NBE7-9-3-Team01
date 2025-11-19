package org.example.povi.domain.quote.controller

import org.example.povi.domain.quote.controller.docs.QuoteControllerDocs
import org.example.povi.domain.quote.dto.QuoteRes
import org.example.povi.domain.quote.service.QuoteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quotes")
class QuoteController(
    private val quoteService: QuoteService
): QuoteControllerDocs {
    @GetMapping("/today")
    override fun getTodayQuote(): ResponseEntity<QuoteRes> {
        return quoteService.getTodayQuote()?.let { quote ->
            ResponseEntity.ok(quote)
        } ?: ResponseEntity.notFound().build()
    }
}
