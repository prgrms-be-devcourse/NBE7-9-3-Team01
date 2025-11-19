package org.example.povi.domain.diary.like.controller

import io.swagger.v3.oas.annotations.Operation
import org.example.povi.auth.token.jwt.CustomJwtUser
import org.example.povi.domain.diary.like.dto.DiaryPostLikeRes
import org.example.povi.domain.diary.like.service.DiaryPostLikeService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/diary-posts/{postId}/likes")
class DiaryPostLikeController(
    private val diaryPostLikeService: DiaryPostLikeService
) {

    @PostMapping("/toggle")
    @Operation(summary = "좋아요 토글", description = "이미 눌렀다면 취소, 아니면 추가합니다.")
    fun toggleLike(
        @PathVariable postId: Long,
        @AuthenticationPrincipal currentUser: CustomJwtUser
    ): ResponseEntity<DiaryPostLikeRes> {
        val userId = currentUser.id
        val response = diaryPostLikeService.toggle(postId, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    @Operation(summary = "좋아요 여부 조회")
    fun isLiked(
        @PathVariable postId: Long,
        @AuthenticationPrincipal currentUser: CustomJwtUser
    ): ResponseEntity<DiaryPostLikeRes> {
        val userId = currentUser.id
        val response = diaryPostLikeService.me(postId, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/count")
    @Operation(summary = "좋아요 수 조회")
    fun countLikes(@PathVariable postId: Long): ResponseEntity<Long> {
        val count = diaryPostLikeService.count(postId)
        return ResponseEntity.ok(count)
    }
}