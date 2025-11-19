package org.example.povi.domain.diary.post.policy

import lombok.RequiredArgsConstructor
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.user.follow.service.FollowService
import org.springframework.stereotype.Component


@Component
@RequiredArgsConstructor
class DiaryPostAccessPolicy {
    private val followService: FollowService? = null

    fun hasReadPermission(currentUserId: Long?, post: DiaryPost): Boolean {
        val authorId = post.user.id
        val visibility = post.visibility

        // 비로그인 → 접근 불가
        if (currentUserId == null) {
            return false
        }

        // 본인 → 항상 허용
        if (currentUserId == authorId) {
            return true
        }

        // 가시성 규칙에 따른 접근 허용
        return isVisibleToUser(currentUserId, authorId, visibility)
    }

    /**
     * 가시성 규칙에 따른 접근 가능 여부를 판단합니다.
     */
    private fun isVisibleToUser(userId: Long, authorId: Long?, visibility: Visibility): Boolean {
        return when (visibility) {
            Visibility.PUBLIC -> true
            Visibility.FRIEND -> followService!!.isMutualFollow(userId, authorId)
            Visibility.PRIVATE -> false
        }
    }
}