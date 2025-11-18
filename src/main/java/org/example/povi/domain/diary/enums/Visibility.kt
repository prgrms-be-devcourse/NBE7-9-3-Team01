package org.example.povi.domain.diary.enums

import org.example.povi.domain.user.follow.service.FollowService


enum class Visibility {
    PUBLIC,
    FRIEND,
    PRIVATE;

    fun canAccess(viewerId: Long?, ownerId: Long?, followService: FollowService): Boolean {
        return when (this) {
            Visibility.PUBLIC -> true
            Visibility.FRIEND -> followService.isMutualFollow(viewerId, ownerId)
            Visibility.PRIVATE -> false
        }
    }
}
