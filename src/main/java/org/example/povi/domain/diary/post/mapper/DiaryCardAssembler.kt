package org.example.povi.domain.diary.post.mapper

import org.example.povi.domain.diary.post.dto.response.DiaryPostCardRes
import org.example.povi.domain.diary.post.dto.response.MyDiaryCardRes
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.view.PostViewStats

object DiaryCardAssembler {
    private const val PREVIEW_MAX = 100

    /**
     * 단일 카드 변환 (나의 다이어리용)
     */
    fun toMyCard(
            post: DiaryPost,
            liked: Boolean,
            likeCount: Long,
            commentCount: Long
    ): MyDiaryCardRes {
        val preview = DiaryPreviewMapper.buildPreviewText(post.content, PREVIEW_MAX)
        val thumbnail = DiaryPreviewMapper.firstImageUrl(post)

        return MyDiaryCardRes(
                post.id,
                post.title,
                preview,
                post.moodEmoji,
                thumbnail,
                post.visibility,
                post.createdAt!!.toLocalDate(),
                liked,
                likeCount,
                commentCount
        )
    }

    /**
     * 여러 게시글을 한 번에 DTO 리스트로 변환 (PostViewStats와 함께)
     */
    fun toCards(
            posts: List<DiaryPost>,
            likedSet: Set<Long>,
            likeCnt: Map<Long, Long>,
            commentCnt: Map<Long, Long>
    ): List<DiaryPostCardRes> {
        return posts.stream()
                .map<DiaryPostCardRes> { p: DiaryPost ->
                    DiaryPostCardRes.from(
                            p,
                            PostViewStats.of(likedSet, likeCnt, commentCnt, p.id)
                    )
                }
                .toList()
    }
}