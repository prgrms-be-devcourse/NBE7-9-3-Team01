package org.example.povi.domain.community.service

import lombok.RequiredArgsConstructor
import org.example.povi.domain.community.dto.request.CommentCreateRequest
import org.example.povi.domain.community.dto.request.PostCreateRequest
import org.example.povi.domain.community.dto.request.PostUpdateRequest
import org.example.povi.domain.community.dto.response.*
import org.example.povi.domain.community.dto.response.CommentCreateResponse.Companion.from
import org.example.povi.domain.community.entity.*
import org.example.povi.domain.community.repository.*
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.domain.community.repository.CommunityRepository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
@RequiredArgsConstructor
open class CommunityService (
    private val communityRepository: CommunityRepository,
    private val communityImageRepository: CommunityImageRepository,
    private val userRepository: UserRepository,
    private val fileUploadService: FileUploadService,
    private val commentRepository: CommentRepository,
    private val bookmarkRepository: CommunityBookmarkRepository,
    private val likeRepository: PostLikeRepository,
    ) {
    @Transactional
    open fun createPost(userId: Long, request: PostCreateRequest): PostCreateResponse {
        val user = userRepository.findById(userId).orElse(null)
                ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        val post = request.toEntity(user)
        val savedPost = communityRepository.save(post)

        if (!request.imageUrls.isNullOrEmpty()) {
            if (request.imageUrls != null) {
                val images = request.imageUrls.map { url ->
                    CommunityImage(
                            imageUrl = url,
                            communityPost = savedPost
                    )
                }
                communityImageRepository.saveAll(images)
            }
        }

        return PostCreateResponse(savedPost.id!!, "게시글이 성공적으로 생성되었습니다.")
    }


    @Transactional
    fun deletePost(userId: Long, postId: Long): PostDeleteResponse {
        // 3. '!!' 제거. orElseThrow가 이미 non-null을 보장합니다.
        val post = communityRepository.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

        if (post == null) {
            throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
        }

        if (post.user.id !!.equals(userId).not()) {
            throw SecurityException("삭제 권한이 없는 사용자입니다.")
        }

        communityRepository.delete(post)

        return PostDeleteResponse(postId, "게시글이 성공적으로 삭제되었습니다.")
    }


    @Transactional
    fun updatePost(userId: Long, postId: Long, request: PostUpdateRequest, images: List<MultipartFile>): PostUpdateResponse {
        val post = communityRepository!!.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }!!

        if (post.user.id != userId) {
            throw SecurityException("수정 권한이 없는 사용자입니다.")
        }

        post.updatePost(request.title, request.content)
        deleteExistingImages(post)
        val newImageUrls = uploadNewImages(post, images)

        return PostUpdateResponse(postId, post.title, post.content, newImageUrls, "게시글이 성공적으로 수정되었습니다.")
    }


    /**
     * 기존 이미지 파일을 삭제하고 DB에서도 제거합니다.
     */
    private fun deleteExistingImages(post: CommunityPost) {

        val images = post.images
        if (images.isNotEmpty()) {

            images.forEach { image ->
                fileUploadService.deleteFile(image.imageUrl)
            }

            communityImageRepository.deleteAllByCommunityPost(post)

            // 7. DB와 동기화된 엔티티의 컬렉션 상태도 비워줍니다. (JPA 세션 유지를 위해)
            images.clear()
        }
    }

    /**
     * 새로 업로드된 파일을 저장하고 DB에 CommunityImage로 매핑하여 저장합니다.
     */
    private fun uploadNewImages(post: CommunityPost, multipartFiles: List<MultipartFile>?): List<String> {
        // 1. '스마트 캐스트'를 위해 파라미터를 로컬 변수로 복사 (이전 'saveAll' 오류 해결)
        val files = multipartFiles

        // 2. 코틀린 확장 함수: 'files == null || files.isEmpty()' 대신 .isNullOrEmpty() 사용
        if (files.isNullOrEmpty()) {
            // 3. 코틀린 표준 라이브러리: new ArrayList() 대신 emptyList() 사용
            return emptyList()
        }

        // 4. '!!' 제거: fileUploadService가 non-null임을 보장
        //    'files' 변수는 if문 덕분에 non-null로 스마트 캐스트됨
        val imageUrls = fileUploadService.uploadFiles(files)

        // 5. Java Stream -> 코틀린 .map()으로 변경 (훨씬 간결)
        // 6. 빌더 패턴(.builder()) -> 코틀린 생성자 직접 호출로 변경 (이전 'builder' 오류 해결)
        // 7. <Any> 및 Function<> 등 불필요한 타입 캐스팅 모두 제거
        val newImages = imageUrls.map { url ->
            CommunityImage(
                    imageUrl = url,
                    communityPost = post
            )
        }

        // 8. '!!' 제거: communityImageRepository가 non-null임을 보장
        communityImageRepository.saveAll(newImages)

        // 9. 프로퍼티 접근: .getImages() 대신 .images 사용
        post.images.addAll(newImages)

        return imageUrls
    }

    // 'open' 키워드나 'all-open' 플러그인이 클래스에 적용되어 있어야 합니다.
// open class CommunityService(...) {

    @Transactional(readOnly = true)
    open fun getPostList(pageable: Pageable?): Page<PostListResponse> {
        val posts = communityRepository.findAll(pageable)

        // 1. '::from' 대신 람다 '{...}'를 사용합니다.
        val dtoPage = posts.map { post ->
            // 2. 'post'는 여기서 'CommunityPost?' 타입입니다.
            // 3. 'post!!'를 사용해 non-null임을 보증하고 'from'에 전달합니다.
            PostListResponse.from(post!!)
        }
        return dtoPage
    }

    @Transactional(readOnly = true)
    // 4. 함수에 'open' 추가
    open fun getMyPostList(userId: Long, pageable: Pageable): Page<PostListResponse> {
        // 5. 파라미터가 nullable(Long?)이므로, repository에 전달하기 전에
        //    null이 아님을 보장하는 것이 안전합니다.
        val nonNullUserId = userId ?: throw IllegalArgumentException("사용자 ID가 필요합니다.")
        val posts = communityRepository.findAllByUserId(nonNullUserId, pageable)

        return posts?.map { post -> PostListResponse.from(post!!) } ?: Page.empty()
    }

    @Transactional(readOnly = true)
    open fun getPostDetail(postId: Long): PostDetailResponse {
        val post = communityRepository.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

        return PostDetailResponse.from(post!!)
    }

    @Transactional
    fun createComment(userId: Long, postId: Long, request: CommentCreateRequest): CommentCreateResponse {
        val user = userRepository!!.findById(userId)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다. ID: $userId") }
        val post = communityRepository!!.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다. ID: $postId") }!!

        val comment = Comment(
                content = request.content,
                user = user,
                communityPost = post
        )

        val savedComment = commentRepository!!.save(comment)
        return from(savedComment)
    }

    @Transactional
    fun deleteComment(userId: Long, commentId: Long): CommentDeleteResponse {
        val comment = commentRepository!!.findById(commentId)
                .orElseThrow { IllegalArgumentException("댓글을 찾을 수 없습니다. ID: $commentId") }!!

        val commentAuthorId = comment.user.id
        val postAuthorId = comment.communityPost.user.id

        if (userId != commentAuthorId && userId != postAuthorId) {
            throw SecurityException("댓글을 삭제할 권한이 없습니다.")
        }

        commentRepository.delete(comment)
        return CommentDeleteResponse(commentId, "댓글이 성공적으로 삭제되었습니다.")
    }

    @Transactional
    fun addLikeToComment(commentId: Long): LikeResponse {
        val comment = commentRepository!!.findById(commentId)
                .orElseThrow { IllegalArgumentException("댓글을 찾을 수 없습니다. ID: $commentId") }!!
        comment.addLike()
        return LikeResponse(commentId, comment.likeCount)
    }

    @Transactional
    fun removeLikeFromComment(commentId: Long): LikeResponse {
        val comment = commentRepository!!.findById(commentId)
                .orElseThrow { IllegalArgumentException("댓글을 찾을 수 없습니다. ID: $commentId") }!!
        comment.removeLike()
        return LikeResponse(commentId, comment.likeCount)
    }

    @Transactional
    fun addLikeToPost(userId: Long, postId: Long): LikeResponse {
        val user = userRepository!!.findById(userId)
                .orElseThrow { IllegalArgumentException("유저를 찾을 수 없습니다. ID: $userId") }
        val post = communityRepository!!.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다. ID: $postId") }!!
        post.addLike()

        check(!likeRepository!!.findByUserIdAndPostId(userId, postId)!!.isPresent) { "이미 좋아요를 누른 게시글입니다." }
        val newLike = PostLike(user, post)
        likeRepository.save(newLike)

        post.addLike()

        return LikeResponse(postId, post.likeCount)
    }

    @Transactional
    fun removeLikeFromPost(userId: Long?, postId: Long): LikeResponse {
        val post = communityRepository!!.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다. ID: $postId") }!!
        val like = likeRepository.findByUserIdAndPostId(userId, postId)!!
                .orElseThrow { IllegalStateException("좋아요 내역을 찾을 수 없습니다.") }

        likeRepository.delete(like)

        post.removeLike()

        return LikeResponse(postId, post.likeCount)
    }

    @Transactional(readOnly = true)
    // 1. 'open' 추가
    open fun getMyLikedPosts(userId: Long?, pageable: Pageable?): Page<LikeListResponse> {
        // 2. Nullable 파라미터 처리
        val nonNullUserId = userId ?: throw IllegalArgumentException("사용자 ID가 필요합니다.")

        // 3. '!!' 제거
        val likedPostPage = likeRepository.findLikedPostsByUserId(nonNullUserId, pageable)

        return likedPostPage?.map { post ->
            LikeListResponse.from(post!!)
        } ?: Page.empty()
    }

    @Transactional
    open fun addBookmark(userId: Long, postId: Long): PostBookmarkResponse {
        val user = userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        // 1. 'post'를 찾을 때 .orElseThrow()를 사용합니다.
        val post = communityRepository.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

        // 2. 이렇게 하면 'post'는 'CommunityPost' (non-null) 타입이 됩니다.

        require(!bookmarkRepository.existsByUserAndCommunityPost(user, post)) { "이미 북마크한 게시글입니다." }

        // 3. 'post'가 non-null이므로 생성자에서 오류가 사라집니다.
        val bookmark = CommunityBookmark(
                user = user,
                communityPost = post!!
        )

        bookmarkRepository.save(bookmark)

        return PostBookmarkResponse(postId, "게시글을 북마크했습니다.")
    }

    @Transactional
    open fun removeBookmark(userId: Long, postId: Long): PostBookmarkResponse {
        val user = userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val post = communityRepository.findById(postId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

        // 'findByUserAndCommunityPost'가 Optional?를 반환하므로 '!!'로 단언
        val bookmark = bookmarkRepository.findByUserAndCommunityPost(user, post)!!
                .orElseThrow { IllegalArgumentException("북마크하지 않은 게시글입니다.") }

        bookmarkRepository.delete(bookmark)

        return PostBookmarkResponse(postId, "북마크를 취소했습니다.")
    }

    @Transactional(readOnly = true)
    // 12. 'open' 추가
    open fun getMyBookmarkedPosts(userId: Long?, pageable: Pageable?): Page<BookmarkListResponse> {
        // 13. Nullable 파라미터 처리
        val nonNullUserId = userId ?: throw IllegalArgumentException("사용자 ID가 필요합니다.")

        // 14. '!!' 제거
        val bookmarkedPosts = bookmarkRepository.findBookmarkedPostsByUserId(nonNullUserId, pageable)

        // 15. .map() 로직 수정
        return bookmarkedPosts?.map { post ->
            // 2. 'post'가 null일 수 있으므로 '!! (non-null 단언)' 사용
            BookmarkListResponse.from(post!!)
        } ?: Page.empty()
        }

    @Transactional(readOnly = true)
    // 16. 'open' 추가
    open fun getMyComments(userId: Long?, pageable: Pageable?): Page<CommentListResponse> {
        // 17. Nullable 파라미터 처리
        val nonNullUserId = userId ?: throw IllegalArgumentException("사용자 ID가 필요합니다.")

        // 18. '!!' 제거
        val comments = commentRepository.findAllByUserId(nonNullUserId, pageable)

        // 19. .map() 로직 수정
        return comments?.map { comment ->
            CommentListResponse.from(comment!!)
        } ?: Page.empty()
    }
}
