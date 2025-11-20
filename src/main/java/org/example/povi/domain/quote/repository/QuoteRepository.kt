package org.example.povi.domain.quote.repository

import org.example.povi.domain.quote.entity.Quote
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface QuoteRepository : JpaRepository<Quote, Long> {
    fun findFirstByCreatedAtBetweenOrderByCreatedAtDesc(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Quote?
}
