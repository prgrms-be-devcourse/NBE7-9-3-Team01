package org.example.povi.domain.diary.like.dto

@JvmRecord
data class DiaryPostLikeRes(
    val liked: Boolean,
    val likeCount: Long
) {
    companion object {
        fun of(liked: Boolean, likeCount: Long): DiaryPostLikeRes {
            return DiaryPostLikeRes(liked, likeCount)
        }
    }
}