package org.example.povi.domain.diary.post.mapper

import org.example.povi.domain.diary.enums.MoodEmoji
import org.example.povi.domain.diary.post.dto.response.MoodSummaryRes
import org.example.povi.domain.diary.post.dto.response.MyDiaryCardRes
import org.example.povi.domain.diary.post.dto.response.MyDiaryListRes
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.view.PostViewStats
import org.springframework.data.domain.Page

/**
 * 내 다이어리 목록 조립기
 * - 카드 리스트: 월별 조회 결과(Page)
 * - 통계 데이터: 이번 주 리스트 기반
 */
object MyDiaryAssembler {
    /** 월별 카드(Page) + 이번주 통계 전용 리스트(thisWeekPosts) 분리 입력  */
    fun build(
            cardPage: Page<DiaryPost>,
            thisWeekPosts: List<DiaryPost>,
            likedSetForCards: Set<Long>,
            likeCntForCards: Map<Long, Long>,
            cmtCntForCards: Map<Long, Long>
    ): MyDiaryListRes {
        // 1) 카드: 월별 필터 결과만 사용
        val cards = cardPage.content.stream()
                .map { p: DiaryPost ->
                    MyDiaryCardRes.from(
                            p,
                            PostViewStats.of(likedSetForCards, likeCntForCards, cmtCntForCards, p.id)
                    )
                }
                .toList()

        // 2) 통계: 이번 주 리스트에서만 계산
        val weeklyCount = thisWeekPosts.size.toLong()
        val avgValence = thisWeekPosts.stream()
                .mapToInt { p: DiaryPost -> p.moodEmoji.valence() }
                .average().orElse(0.0)

        return MyDiaryListRes(
                cardPage.totalElements,
                weeklyCount,
                MoodSummaryRes(avgValence, MoodEmoji.fromValence(avgValence)),
                cards
        )
    }
}