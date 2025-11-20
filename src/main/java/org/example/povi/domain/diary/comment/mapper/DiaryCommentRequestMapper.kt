@file:JvmName("DiaryCommentRequestMapper")
package org.example.povi.domain.diary.comment.mapper

import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq
import org.example.povi.domain.diary.comment.entity.DiaryComment
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.user.entity.User

/**
 * DiaryCommentCreateReq → DiaryComment 엔티티 변환
 */
fun toEntity(
    req: DiaryCommentCreateReq,
    author: User,
    post: DiaryPost
): DiaryComment {
    return DiaryComment(
        author = author,
        post = post,
        content = req.content.trim()
    )
}

/**
 * 댓글 수정 요청 적용 (엔티티 변경)
 * - Dirty Checking으로 자동 반영됨
 */
fun updateEntity(comment: DiaryComment, req: DiaryCommentUpdateReq) {
    comment.updateContent(req.content.trim()
    )
}