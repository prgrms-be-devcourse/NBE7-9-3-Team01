package org.example.povi.domain.diary.comment.entity

import jakarta.persistence.*
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity

@Entity
@Table(name = "diary_comments")
@AttributeOverride(name = "id", column = Column(name = "comment_id"))
class DiaryComment (
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    var post: DiaryPost,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String
) : BaseEntity() {
    fun updateContent(newContent: String) {
        this.content = newContent
    }
}
