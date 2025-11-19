package org.example.povi.domain.user.follow.service

import lombok.RequiredArgsConstructor
import org.example.povi.domain.user.follow.repository.FollowRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class FollowService {
    private val followRepository: FollowRepository? = null

    /**
     * 내가 팔로우 중인 사용자 ID 목록 조회
     */
    @Transactional(readOnly = true)
    fun getFollowingUserIds(userId: Long?): Set<Long?>? {
        return followRepository!!.findFollowingIds(userId)
    }

    /**
     * 맞팔(서로 팔로우 중인) 사용자 ID 목록 조회
     */
    @Transactional(readOnly = true)
    fun getMutualUserIds(userId: Long?): Set<Long?>? {
        return followRepository!!.findMutualFriendIds(userId)
    }

    /**
     * 두 사용자가 맞팔인지 여부
     */
    @Transactional(readOnly = true)
    fun isMutualFollow(userId1: Long?, userId2: Long?): Boolean {
        if (userId1 == null || userId2 == null) return false
        if (userId1 == userId2) return true
        return followRepository!!.findMutualFriendIds(userId1)!!.contains(userId2)
    }
}
