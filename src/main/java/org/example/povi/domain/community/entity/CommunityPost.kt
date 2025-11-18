package org.example.povi.domain.community.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity
import org.hibernate.annotations.Formula
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener::class)
@AttributeOverride(name = "id", column = Column(name = "post_id"))
class CommunityPost(
        @field:JoinColumn(name = "user_id", nullable = false)
        @field:ManyToOne(fetch = FetchType.LAZY)
        val user: User,

        @field:Column(name = "title", nullable = false, length = 255)
        var title: String,

        @field:Column(name = "content", nullable = false, columnDefinition = "TEXT")
        var content: String,

        @field:Column(name = "emoticon", nullable = false)
        @field:Enumerated(EnumType.STRING)
        var emoticon: CommunityEmoticon,

        @OneToMany(mappedBy = "communityPost", cascade = [CascadeType.ALL], orphanRemoval = true)
        val images: MutableList<CommunityImage> = mutableListOf()


) : BaseEntity() {

    public constructor() : this(
            user = User(), // 기본값 설정 필요
            title = "",
            content = "",
            emoticon = CommunityEmoticon.NORMAL,
            images = mutableListOf()
    )


    @OneToMany(mappedBy = "post", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val likes: Set<PostLike> = HashSet()

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0 // 기본값을 0으로 설정

    @OneToMany(mappedBy = "communityPost", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: List<Comment> = ArrayList()

    @Formula("(SELECT COUNT(1) FROM comments c WHERE c.post_id = post_id)")
    val commentCount = 0






    fun updatePost(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun addLike() {
        likeCount++
    }

    fun removeLike() {
        if (this.likeCount > 0) {
            likeCount--
        }
    }


}
