package org.example.povi.domain.diary.like.mapper

import org.example.povi.domain.diary.like.dto.DiaryPostLikeRes

object DiaryPostLikeMapper {
    /** 좋아요 상태 + 카운트 → Response DTO  */
    fun toResponse(liked: Boolean, count: Long): DiaryPostLikeRes {
        return DiaryPostLikeRes.Companion.of(liked, count)
    }
}