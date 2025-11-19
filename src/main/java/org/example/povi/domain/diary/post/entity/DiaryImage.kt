package org.example.povi.domain.diary.post.entity

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import org.example.povi.global.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@Getter
@AllArgsConstructor
@Table(name = "diary_images")
class DiaryImage(@field:JoinColumn(name = "diary_id", nullable = false)
                 @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
                 var post: DiaryPost, @field:Column(name = "image_url", length = 2048, nullable = false)
                 var imageUrl: String) : BaseEntity() {
    @Column(name = "deleted_at")
    private var deletedAt: LocalDateTime? = null

    fun setDiaryPost(diaryPost: DiaryPost) {
        this.post = diaryPost
    }
}