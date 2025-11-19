package org.example.povi.domain.community.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity
import org.example.povi.domain.community.entity.CommunityPost

@Entity
@Table(name = "bookmarks", uniqueConstraints = [UniqueConstraint(name = "bookmark_uk", columnNames = ["user_id", "post_id"])])
@AttributeOverride(name = "id", column = Column(name = "bookmark_id"))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class CommunityBookmark(
        @field:JoinColumn(name = "user_id", nullable = false)
        @field:ManyToOne(fetch = FetchType.LAZY)
        val user: User, @field:JoinColumn(name = "post_id", nullable = false)

        @field:ManyToOne(fetch = FetchType.LAZY)
        val communityPost: CommunityPost) : BaseEntity()
{
    // 기본 생성자
    protected constructor() : this(
            user = User(), // 기본값 설정 필요
            communityPost = null as CommunityPost // 기본값 설정 필요
    )
}



