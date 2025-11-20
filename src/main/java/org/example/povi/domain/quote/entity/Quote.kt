package org.example.povi.domain.quote.entity

import jakarta.persistence.*
import org.example.povi.domain.transcription.entity.Transcription
import org.example.povi.global.entity.BaseEntity

@Entity
@AttributeOverride(name = "id", column = Column(name = "quote_id"))
@Table(name = "quotes")
class Quote(
    @Column(nullable = false)
    var author: String,

    @Column(
        columnDefinition = "TEXT",
        nullable = false )
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var dataSource : DataSource = DataSource.GITHUB,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var quoteSource : QuoteSource = QuoteSource.QUOTE,

    @OneToMany(mappedBy = "quote", cascade = [CascadeType.ALL])
    val transcriptions: MutableList<Transcription> = mutableListOf()

) : BaseEntity() {

    enum class DataSource {
        GITHUB,
        AI
    }

    enum class QuoteSource {
        QUOTE,  // 명언
        BOOK,  // 책구절
        MOVIE // 영화
    }
}