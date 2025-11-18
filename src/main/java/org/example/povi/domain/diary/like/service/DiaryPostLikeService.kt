package org.example.povi.domain.diary.like.service

import org.example.povi.domain.diary.like.dto.DiaryPostLikeRes
import org.example.povi.domain.diary.like.entity.DiaryPostLike
import org.example.povi.domain.diary.like.repository.DiaryPostLikeRepository
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.repository.DiaryPostRepository
import org.example.povi.domain.user.entity.User
import org.example.povi.domain.user.follow.service.FollowService
import org.example.povi.domain.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class DiaryPostLikeService(
    private val diaryPostLikeRepository: DiaryPostLikeRepository,
    private val diaryPostRepository: DiaryPostRepository,
    private val userRepository: UserRepository,
    private val followService: FollowService
) {

    /**
     * 좋아요 토글: 결과 DTO 반환 (liked=true: 추가, false: 취소)
     */
    @Transactional
    fun toggle(postId: Long, userId: Long): DiaryPostLikeRes {
        val post = checkAccessOrThrow(postId, userId)
        val user = findUserOrThrow(userId)

        val liked = if (diaryPostLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            diaryPostLikeRepository.deleteByPostIdAndUserId(postId, userId)
            false
        } else {
            diaryPostLikeRepository.save(DiaryPostLike.of(post, user))
            true
        }

        val count = countLikesByPostId(postId)
        return DiaryPostLikeRes(liked, count)
    }

    /**
     * 내 좋아요 여부 + 현재 좋아요 수
     */
    @Transactional(readOnly = true)
    fun me(postId: Long, userId: Long): DiaryPostLikeRes {
        checkAccessOrThrow(postId, userId)
        val liked = diaryPostLikeRepository.existsByPostIdAndUserId(postId, userId)
        val count = countLikesByPostId(postId)

        return DiaryPostLikeRes(liked, count)
    }

    /**
     * 좋아요 수
     */
    @Transactional(readOnly = true)
    fun count(postId: Long): Long {
        if (!diaryPostRepository.existsById(postId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")
        }
        return countLikesByPostId(postId)
    }


    private fun countLikesByPostId(postId: Long): Long {
        return diaryPostLikeRepository.countByPostId(postId)
    }

    /** 존재/가시성 검증 후 게시글 반환  */
    private fun checkAccessOrThrow(postId: Long, viewerId: Long): DiaryPost {
        val post = diaryPostRepository.findById(postId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "게시글이 존재하지 않습니다."
                )
            }
        if (!canAccessPost(viewerId, post)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "이 게시글에 접근할 수 없습니다.")
        }
        return post
    }

    private fun findUserOrThrow(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "사용자가 존재하지 않습니다."
                )
            }
    }

    /**
     * 댓글 서비스와 동일한 접근 권한 로직
     * - 본인 글: 허용
     * - PUBLIC: 허용
     * - FRIEND: 맞팔만 허용
     * - PRIVATE: 불허
     */
    private fun canAccessPost(viewerId: Long, post: DiaryPost): Boolean {
        val ownerId = post.user.id
        if (viewerId == ownerId) return true

        return post.visibility.canAccess(viewerId, ownerId, followService)
    }
}

