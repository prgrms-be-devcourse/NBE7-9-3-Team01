package org.example.povi.domain.diary.post.view

/**
 * 게시글 단위의 "뷰 통계" 정보를 표현합니다.
 * - 좋아요 여부
 * - 좋아요 수
 * - 댓글 수
 */
@JvmRecord
data class PostViewStats(@JvmField val likedByMe: Boolean, @JvmField val likeCount: Long, @JvmField val commentCount: Long) {
    companion object {
        fun of(
                likedPostIds: Set<Long?>,
                likeCountByPostId: Map<Long, Long>,
                commentCountByPostId: Map<Long, Long>,
                postId: Long?
        ): PostViewStats {
            return PostViewStats(
                    likedPostIds.contains(postId),
                    likeCountByPostId.getOrDefault(postId, 0L),
                    commentCountByPostId.getOrDefault(postId, 0L)
            )
        }
    }
}