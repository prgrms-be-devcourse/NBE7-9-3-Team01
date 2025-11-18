package org.example.povi.domain.community.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.example.povi.global.entity.BaseEntity

@Entity
@Table(name = "community_images")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = Column(name = "image_id"))
class CommunityImage @Builder constructor(
        @field:Column(name = "image_url", nullable = false)
        var imageUrl: String, // 이 이미지가 속한 게시글 (N:1 관계)
        @field:JoinColumn(name = "post_id", nullable = false)
        @field:ManyToOne(fetch = FetchType.LAZY)
        val communityPost: CommunityPost) : BaseEntity()

// 기본 생성자
{
    protected constructor() : this(
            imageUrl = "",
            communityPost = CommunityPost() // 기본값 설정 필요
    )
}

