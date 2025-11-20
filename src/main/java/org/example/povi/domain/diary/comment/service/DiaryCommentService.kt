package org.example.povi.domain.diary.comment.service

import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentUpdateRes
import org.example.povi.domain.diary.comment.mapper.toEntity
import org.example.povi.domain.diary.comment.mapper.toPagedResponse
import org.example.povi.domain.diary.comment.mapper.updateEntity
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.policy.DiaryPostAccessPolicy
import org.example.povi.domain.diary.post.repository.DiaryPostRepository
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.dto.PagedResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class DiaryCommentService (
    private val diaryCommentRepository: DiaryCommentRepository,
    private val diaryPostRepository: DiaryPostRepository,
    private val userRepository: UserRepository,
    private val postAccessPolicy: DiaryPostAccessPolicy
) {
    /**
     * 댓글 생성
     * - 로그인 필수
     * - 대상 포스트에 대한 읽기 권한 필요(공개/친구/본인)
     */
    @Transactional
    fun createDiaryComment(
        postId: Long,
        req: DiaryCommentCreateReq,
        currentUserId: Long
    ): DiaryCommentCreateRes {

        requireLogin(currentUserId)

        val commenter = userRepository.findByIdOrNull(currentUserId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다.")

        val post = diaryPostRepository.findByIdOrNull(postId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")

        assertReadable(currentUserId, post, "댓글을 작성할 권한이 없습니다.")

        val saved = diaryCommentRepository.save(
            toEntity(req, commenter, post)
        )
        return DiaryCommentCreateRes.from(saved)
    }


    /**
     * 댓글 목록 조회
     * - 비로그인 허용(공개글 조건은 Policy에서 처리)
     */
    @Transactional(readOnly = true)
    fun getCommentsByPost(
        postId: Long,
        pageable: Pageable,
        currentUserId: Long
    ): PagedResponse<DiaryCommentRes> {

        requireLogin(currentUserId)

        val post = diaryPostRepository.findByIdOrNull(postId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")

        assertReadable(currentUserId, post, "이 게시글에 접근할 수 없습니다.")

        val page : Page<DiaryComment> = diaryCommentRepository.findByPostId(postId, pageable)
        return toPagedResponse(page, currentUserId)
    }

    /**
     * 댓글 수정
     * - 로그인 필수
     * - 대상 포스트 읽기 가능해야 함
     * - 댓글 작성자 본인만 수정 가능
     */
    @Transactional
    fun updateDiaryComment(
        postId: Long,
        commentId: Long,
        req: DiaryCommentUpdateReq,
        currentUserId: Long
    ): DiaryCommentUpdateRes {

        requireLogin(currentUserId)

        val comment: DiaryComment = diaryCommentRepository.findByIdAndPostId(commentId, postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
            )

        val post = comment.post
        assertReadable(currentUserId, post, "이 게시글에 접근할 수 없습니다.")

        if (comment.author.id != currentUserId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 수정할 수 있습니다.")
        }

        updateEntity(comment, req)
        return DiaryCommentUpdateRes.from(comment)
    }

    /**
     * 댓글 삭제
     * - 로그인 필수
     * - 대상 포스트 읽기 가능해야 함
     * - 댓글 작성자 또는 게시글 작성자만 삭제 가능
     */
    @Transactional
    fun deleteDiaryComment(
        postId: Long,
        commentId: Long,
        currentUserId: Long
    ) {

        requireLogin(currentUserId)

        val comment: DiaryComment = diaryCommentRepository.findByIdAndPostId(commentId, postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
            )

        val post = comment.post
        assertReadable(currentUserId, post, "이 게시글에 접근할 수 없습니다.")

        val authorId = comment.author.id
        val postAuthorId = post.user.id
        if (currentUserId != authorId && currentUserId != postAuthorId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자 또는 게시글 작성자만 삭제할 수 있습니다.")
        }

        diaryCommentRepository.delete(comment)
    }


    /** 로그인 필수 동작에서 userId null 방지  */
    private fun requireLogin(userId: Long?) {
        if (userId == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.")
        }
    }

    /** 포스트 읽기 권한 검사: 본인/공개/친구(상호팔로우)  */
    private fun assertReadable(userId: Long?, post: DiaryPost, forbiddenMsg: String) {
        if (!postAccessPolicy.hasReadPermission(userId, post)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, forbiddenMsg)
        }
    }

    /** 댓글 존재 + 특정 포스트에 속하는지 매칭 검증  */
    private fun getCommentOr404(commentId: Long, postId: Long): DiaryComment {
        return diaryCommentRepository.findByIdAndPostId(commentId, postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
                )
    }
}
