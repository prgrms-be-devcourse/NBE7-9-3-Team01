package org.example.povi.domain.community.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity

@Entity
@Table(name = "comments")
@Getter
@AttributeOverride(name = "id", column = Column(name = "comment_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Comment @Builder constructor(
        @field:Column(nullable = false, length = 1500)
        var content: String, @field:JoinColumn(name = "user_id", nullable = false) @field:ManyToOne(fetch = FetchType.LAZY)
        val user: User, @field:JoinColumn(name = "post_id", nullable = false) @field:ManyToOne(fetch = FetchType.LAZY)
        val communityPost: CommunityPost) : BaseEntity() {

            //생성자
    protected constructor() : this(
            content = "",
            user = User(), // 기본값 설정 필요
            communityPost = CommunityPost( // 기본값 설정 필요
                    user = User(),
                    title = "",
                    content = "",
                    emoticon = CommunityEmoticon.NORMAL
            )
    )

    @Column(nullable = false)
    var likeCount: Int = 0
        private set

    fun addLike() {
        likeCount++
    }

    fun removeLike() {
        if (this.likeCount > 0) {
            likeCount--
        }
    }
}
