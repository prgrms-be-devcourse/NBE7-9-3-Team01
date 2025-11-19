package org.example.povi.domain.community.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.domain.community.dto.request.CommentCreateRequest
import org.example.povi.domain.community.dto.request.PostCreateRequest
import org.example.povi.domain.community.dto.request.PostUpdateRequest
import org.example.povi.domain.community.dto.response.*
import org.example.povi.domain.community.service.CommunityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
class CommunityController (private val communityService: CommunityService,
                           private val jwtUtil: JwtTokenProvider)


{


    @Operation(summary = "커뮤니티 글 작성", description = "커뮤니티에 익명 글을 작성합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "작성 성공", content = [Content(schema = Schema(implementation = PostCreateResponse::class))]), ApiResponse(responseCode = "400", description = "요청 실패")])
    @PostMapping
    fun createPost(
            @RequestHeader("Authorization") bearerToken: String,
            @RequestBody @Valid request: PostCreateRequest
    ): ResponseEntity<PostCreateResponse> {

        // "Bearer " 접두사를 제거하는 더 안전하고 관용적인 방법입니다.
        val rawToken = bearerToken.removePrefix("Bearer ")

        // 'uerId' -> 'userId' 오타 수정, 'Long' 타입 추론
        val userId = jwtUtil.getUserId(rawToken)

        val response = communityService.createPost(userId, request)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "커뮤니티 글 삭제", description = "커뮤니티에 작성한 익명 글을 삭제합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "삭제 성공", content = [Content(schema = Schema(implementation = PostDeleteResponse::class))]), ApiResponse(responseCode = "400", description = "요청 실패"), ApiResponse(responseCode = "403", description = "권한 없음"), ApiResponse(responseCode = "404", description = "게시글 없음")])
    @DeleteMapping("/{postId}")
     fun deletePost(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long): ResponseEntity<PostDeleteResponse> {
        val rawToken = bearerToken.replace("Bearer ", "")
        val userId = jwtUtil!!.getUserId(rawToken)

        val response = communityService!!.deletePost(userId, postId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "커뮤니티 글 수정", description = "커뮤니티에 작성한 익명 글을 수정합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "수정 성공", content = [Content(schema = Schema(implementation = PostUpdateResponse::class))]), ApiResponse(responseCode = "400", description = "요청 실패"), ApiResponse(responseCode = "403", description = "권한 없음"), ApiResponse(responseCode = "404", description = "게시글 없음")])
    @PutMapping(value = ["/{postId}"], consumes = ["multipart/form-data"])
     fun updatePost(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long,
            @RequestPart("request") request: @Valid PostUpdateRequest?,
            @RequestPart(value = "photos", required = false) photos: List<MultipartFile>): ResponseEntity<PostUpdateResponse> {
        val rawToken = bearerToken.replace("Bearer ", "")
        val userId = jwtUtil!!.getUserId(rawToken)

        val response = communityService!!.updatePost(userId, postId, request!!, photos)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "커뮤니티 글 목록 조회", description = "커뮤니티 글 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "조회 성공", content = [Content(array = ArraySchema(schema = Schema(implementation = PostListResponse::class)))]), ApiResponse(responseCode = "400", description = "요청 실패")])
    @GetMapping
     fun getPostList(
            @PageableDefault(size = 10, sort = ["createdAt,desc"]) pageable: Pageable): ResponseEntity<Page<PostListResponse>> {
        val postList = communityService!!.getPostList(pageable)
        return ResponseEntity.ok(postList)
    }


    @Operation(summary = "내가 작성한 글 목록 조회", description = "내가 작성한 커뮤니티 글 목록을 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "조회 성공", content = [Content(array = ArraySchema(schema = Schema(implementation = PostListResponse::class)))]), ApiResponse(responseCode = "401", description = "인증 실패")])
    @GetMapping("/me")
     fun getMyPostList(
            @RequestHeader("Authorization") bearerToken: String,
            @PageableDefault(size = 4, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<PostListResponse>> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val postList = communityService!!.getMyPostList(userId, pageable)
        return ResponseEntity.ok(postList)
    }


    @Operation(summary = "커뮤니티 글 상세보기", description = "커뮤니티에 익명 글을 클릭했을때 .")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "성공", content = [Content(mediaType = "application/json", array = ArraySchema(schema = Schema(implementation = PostDetailResponse::class)))]), ApiResponse(responseCode = "400", description = "실패")])
    @GetMapping("/{postId}")
     fun getPostDetail(@PathVariable postId: Long): ResponseEntity<PostDetailResponse> {
        val postDetail = communityService!!.getPostDetail(postId)
        return ResponseEntity.ok(postDetail)
    }

    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "작성 성공", content = [Content(schema = Schema(implementation = CommentCreateResponse::class))]), ApiResponse(responseCode = "401", description = "인증 실패"), ApiResponse(responseCode = "404", description = "게시글 없음")])
    @PostMapping("/{postId}/comments")
     fun createComment(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long,
            @RequestBody request: @Valid CommentCreateRequest?): ResponseEntity<CommentCreateResponse> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.createComment(userId, postId, request!!)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "삭제 성공"), ApiResponse(responseCode = "401", description = "인증 실패"), ApiResponse(responseCode = "403", description = "권한 없음"), ApiResponse(responseCode = "404", description = "댓글 없음")])
    @DeleteMapping("/comments/{commentId}")
     fun deleteComment(bearerToken: String, commentId: Long): ResponseEntity<CommentDeleteResponse> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        communityService!!.deleteComment(userId, commentId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "내가 작성한 댓글 목록", description = "내가 작성한 댓글 목록을 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "조회 성공", content = [Content(array = ArraySchema(schema = Schema(implementation = CommentListResponse::class)))]), ApiResponse(responseCode = "401", description = "인증 실패")])
    @GetMapping("/me/comments")
     fun getMyComments(
            @RequestHeader("Authorization") bearerToken: String,
            @PageableDefault(size = 2, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CommentListResponse>> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.getMyComments(userId, pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "댓글 좋아요 추가", description = "특정 댓글에 좋아요를 추가합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "성공", content = [Content(schema = Schema(implementation = LikeResponse::class))]), ApiResponse(responseCode = "404", description = "댓글 없음")])
    @PostMapping("/comments/{commentId}/like")
     fun addLikeToComment(@PathVariable commentId: Long): ResponseEntity<LikeResponse> {
        val response = communityService!!.addLikeToComment(commentId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "댓글 좋아요 삭제", description = "특정 댓글의 좋아요를 삭제합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "성공", content = [Content(schema = Schema(implementation = LikeResponse::class))]), ApiResponse(responseCode = "404", description = "댓글 없음")])
    @DeleteMapping("/comments/{commentId}/like")
     fun removeLikeFromComment(@PathVariable commentId: Long): ResponseEntity<LikeResponse> {
        val response = communityService!!.removeLikeFromComment(commentId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "게시글 좋아요 추가", description = "특정 게시글에 좋아요를 추가합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "성공", content = [Content(schema = Schema(implementation = LikeResponse::class))]), ApiResponse(responseCode = "401", description = "인증 실패"), ApiResponse(responseCode = "404", description = "게시글 없음"), ApiResponse(responseCode = "409", description = "이미 좋아요 누름")])
    @PostMapping("/{postId}/like")
     fun addLikeToPost(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long): ResponseEntity<LikeResponse> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.addLikeToPost(userId, postId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "게시글 좋아요 삭제", description = "특정 게시글의 좋아요를 삭제합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "성공", content = [Content(schema = Schema(implementation = LikeResponse::class))]), ApiResponse(responseCode = "401", description = "인증 실패"), ApiResponse(responseCode = "404", description = "좋아요/게시글 없음")])
    @DeleteMapping("/{postId}/like")
     fun removeLikeFromPost(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long): ResponseEntity<LikeResponse> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.removeLikeFromPost(userId, postId)
        return ResponseEntity.ok(response)
    }

    // 내가 좋아요 누른 게시글
    @Operation(summary = "내가 좋아요 누른 게시글 목록", description = "내가 좋아요를 누른 게시글 목록을 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "조회 성공", content = [Content(array = ArraySchema(schema = Schema(implementation = LikeListResponse::class)))]), ApiResponse(responseCode = "401", description = "인증 실패")])
    @GetMapping("/me/likes")
     fun getMyLikedPosts(
            @RequestHeader("Authorization") bearerToken: String,
            @PageableDefault(size = 2, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable): ResponseEntity<Page<LikeListResponse>> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.getMyLikedPosts(userId, pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "게시글 북마크 추가", description = "특정 게시글을 북마크합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "북마크 성공", content = [Content(schema = Schema(implementation = PostBookmarkResponse::class))]), ApiResponse(responseCode = "401", description = "인증 실패"), ApiResponse(responseCode = "404", description = "게시글 없음"), ApiResponse(responseCode = "409", description = "이미 북마크됨")])
    @PostMapping("/{postId}/bookmark")
     fun addBookmark(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long): ResponseEntity<PostBookmarkResponse> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.addBookmark(userId, postId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "게시글 북마크 삭제", description = "특정 게시글의 북마크를 삭제합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "삭제 성공", content = [Content(schema = Schema(implementation = PostBookmarkResponse::class))]), ApiResponse(responseCode = "401", description = "인증 실패"), ApiResponse(responseCode = "404", description = "북마크/게시글 없음")])
    @DeleteMapping("/{postId}/bookmark")
     fun removeBookmark(
            @RequestHeader("Authorization") bearerToken: String,
            @PathVariable postId: Long): ResponseEntity<PostBookmarkResponse> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.removeBookmark(userId, postId)
        return ResponseEntity.ok(response)
    }

    // 내가 북마크한 글 목록
    @Operation(summary = "내가 북마크한 글 목록", description = "내가 북마크한 게시글 목록을 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "조회 성공", content = [Content(array = ArraySchema(schema = Schema(implementation = BookmarkListResponse::class)))]), ApiResponse(responseCode = "401", description = "인증 실패")])
    @GetMapping("/me/bookmarks")
     fun getMyBookmarks(
            @RequestHeader("Authorization") bearerToken: String,
            @PageableDefault(size = 4, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<BookmarkListResponse>> {
        val userId = jwtUtil!!.getUserId(bearerToken.replace("Bearer ", ""))
        val response = communityService!!.getMyBookmarkedPosts(userId, pageable)
        return ResponseEntity.ok(response)
    }
}
