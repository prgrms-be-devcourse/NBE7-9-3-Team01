package org.example.povi.domain.diary.post.entity

import jakarta.persistence.*
import org.example.povi.domain.diary.comment.entity.DiaryComment
import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.like.entity.DiaryPostLike
import org.example.povi.domain.user.entity.User
import org.example.povi.global.entity.BaseEntity


@Entity
@Table(name = "diary_posts")
@AttributeOverride(name = "id", column = Column(name = "post_id"))
// 1. @Getter, @NoArgsConstructor 제거
class DiaryPost(
        // 2. 'user'는 생성 시점에 반드시 필요하므로 주 생성자로 이동
        @JoinColumn(name = "user_id", nullable = false)
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        val user: User

) : BaseEntity() {

    // 3. [중요] private 제거 -> lateinit var로 변경 (create에서 주입)
    @Column(name = "title", length = 50, nullable = false)
    lateinit var title: String

    // 4. [중요] private 제거 -> lateinit var로 변경 (create에서 주입)
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    lateinit var content: String

    // 5. [중요] private 제거 -> var로 변경 (기본값 설정)
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_emoji", nullable = false)
    var moodEmoji = MoodEmoji.NEUTRAL

    // 6. [중요] private 제거 -> var로 변경 (기본값 설정)
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    var visibility = Visibility.PRIVATE

    // 7. [중요] private 제거 + 코틀린 스타일 초기화
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id ASC")
    val images: MutableList<DiaryImage> = mutableListOf()

    // 8. [중요] private 제거 + List -> MutableList로 변경
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("createdAt ASC")
    val comments: MutableList<DiaryComment> = mutableListOf()

    // 9. [중요] private 제거 + 코틀린 스타일 초기화
    @OneToMany(mappedBy = "post", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val likes: MutableSet<DiaryPostLike> = mutableSetOf()

    // 10. JPA Plugin이 기본 생성자를 만들어주므로,
    //     JPA를 위한 protected 생성자만 명시 (선택 사항이지만 권장)
    protected constructor() : this(User()) // 'User'의 기본 생성자 필요

    // 11. init 블록 제거 (기본값으로 이미 처리됨)

    fun renameTo(newTitle: String?) {
        val t = (newTitle?.trim() ?: "")
        require(t.isNotEmpty()) { "제목은 공백만으로 수정할 수 없습니다." }
        this.title = t
    }

    fun rewriteContent(newContent: String?) {
        val c = (newContent?.trim() ?: "")
        require(c.isNotEmpty()) { "내용은 공백만으로 수정할 수 없습니다." }
        this.content = c
    }

    fun changeMood(newMood: MoodEmoji?) {
        requireNotNull(newMood) { "이모지는 null 일 수 없습니다." }
        this.moodEmoji = newMood
    }

    fun changeVisibility(newVisibility: Visibility?) {
        requireNotNull(newVisibility) { "공개범위는 null 일 수 없습니다." }
        this.visibility = newVisibility
    }

    fun addImage(image: DiaryImage?) {
        if (image == null) return
        images.add(image)
        if (image.post !== this) image.setDiaryPost(this)
    }

    fun replaceImages(urls: List<String?>?) {
        images.clear()

        // 12. [중요] Java Stream -> Kotlin Collection 함수로 변경
        urls?.mapNotNull { it?.trim() } // null 제거 및 trim
                ?.filter { it.isNotEmpty() } // 빈 문자열 제거
                ?.distinct() // 중복 제거
                ?.forEach { url -> // 각 url에 대해
                    addImage(DiaryImage(this, url))
                }
    }

    companion object {
        // 13. 'create' 메서드가 주 생성자를 호출하고 속성을 설정하도록 변경
        fun create(author: User, title: String?, content: String?,
                   mood: MoodEmoji?, vis: Visibility?, imageUrls: List<String?>?): DiaryPost {

            val p = DiaryPost(author) // 주 생성자 호출

            // lateinit var 속성 설정
            p.renameTo(title)
            p.rewriteContent(content)

            // 기본값 덮어쓰기
            p.moodEmoji = mood ?: MoodEmoji.NEUTRAL
            p.visibility = vis ?: Visibility.PRIVATE

            // 이미지 설정
            p.replaceImages(imageUrls)

            return p
        }
    }
}