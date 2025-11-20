package org.example.povi.domain.transcription.entity

import jakarta.persistence.*
import org.example.povi.domain.quote.entity.Quote
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity

@Entity
@AttributeOverride(name = "id", column = Column(name = "transcription_id"))
@Table(
    name = "transcriptions",
    uniqueConstraints = [UniqueConstraint(name = "uk_user_quote", columnNames = ["user_id", "quote_id"])]
)
class Transcription : BaseEntity {
    @Column
    var content: String = ""
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
     var quote: Quote
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
        protected set
    constructor(content: String, quote: Quote, user: User) {
        this.content = content
        this.quote = quote
        this.user = user
    }
}