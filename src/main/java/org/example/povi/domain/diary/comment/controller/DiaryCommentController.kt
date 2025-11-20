package org.example.povi.domain.diary.comment.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.example.povi.auth.token.jwt.CustomJwtUser
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentUpdateRes
import org.example.povi.domain.diary.comment.service.DiaryCommentService
import org.example.povi.global.dto.PagedResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/diary-posts/{postId}/comments")
class DiaryCommentController (
    val diaryCommentService: DiaryCommentService
): DiaryCommentControllerDocs {
    @PostMapping
    override fun createDiaryComment(
        @PathVariable postId: Long,
        @RequestBody createReq: @Valid DiaryCommentCreateReq,
        @AuthenticationPrincipal currentUser: CustomJwtUser
    ): ResponseEntity<DiaryCommentCreateRes> {
        val res = diaryCommentService.createDiaryComment(postId, createReq, currentUser.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @GetMapping
    override fun getComments(
        @PathVariable postId: Long,
        @PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable,
        @AuthenticationPrincipal currentUser: CustomJwtUser
    ): ResponseEntity<PagedResponse<DiaryCommentRes>> {
        val res = diaryCommentService.getCommentsByPost(
            postId, pageable, currentUser.id
        )
        return ResponseEntity.ok(res)
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정")
    override fun updateDiaryComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestBody updateReq: @Valid DiaryCommentUpdateReq,
        @AuthenticationPrincipal currentUser: CustomJwtUser
    ): ResponseEntity<DiaryCommentUpdateRes> {
        val res = diaryCommentService.updateDiaryComment(
            postId, commentId, updateReq, currentUser.id
        )
        return ResponseEntity.ok(res)
    }

    @DeleteMapping("/{commentId}")
    override fun deleteDiaryComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal currentUser: CustomJwtUser
    ): ResponseEntity<Void> {
        diaryCommentService.deleteDiaryComment(postId, commentId, currentUser.id)
        return ResponseEntity.noContent().build()
    }
}
