package org.example.povi.domain.community.entity

import jakarta.persistence.*
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "post_id"])])
@AttributeOverride(name = "id", column = Column(name = "like_id"))
class PostLike : BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private var user: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private var post: CommunityPost? = null

    protected constructor()

    constructor(user: User?, post: CommunityPost?) {
        this.user = user
        this.post = post
    }
}
