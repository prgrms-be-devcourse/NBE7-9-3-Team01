package org.example.povi.domain.diary.like.entity

import jakarta.persistence.*
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "diaryPost_likes")
@AttributeOverride(name = "id", column = Column(name = "post_like_id"))
class DiaryPostLike : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    lateinit var post: DiaryPost
        private set

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
        private set

    @Column(name = "liked_at", nullable = false, updatable = false)
    var likedAt: LocalDateTime = LocalDateTime.now()
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiaryPostLike) return false
        return post == other.post && user == other.user
    }

    override fun hashCode(): Int = Objects.hash(post, user)

    companion object {
        fun of(post: DiaryPost, user: User): DiaryPostLike =
            DiaryPostLike().apply {
                this.post = post
                this.user = user
                this.likedAt = LocalDateTime.now()
            }
    }
}