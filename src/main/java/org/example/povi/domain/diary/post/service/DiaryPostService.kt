package org.example.povi.domain.diary.post.service

import lombok.RequiredArgsConstructor
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository
import org.example.povi.domain.diary.enums.Visibility
import org.example.povi.domain.diary.like.repository.DiaryPostLikeRepository
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq
import org.example.povi.domain.diary.post.dto.response.*
import org.example.povi.domain.diary.post.dto.response.DiaryDetailRes.Companion.of
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.diary.post.mapper.DiaryCardAssembler.toCards
import org.example.povi.domain.diary.post.mapper.DiaryQueryMapper.toCountMap
import org.example.povi.domain.diary.post.mapper.DiaryRequestMapper.fromCreateRequest
import org.example.povi.domain.diary.post.mapper.MyDiaryAssembler.build
import org.example.povi.domain.diary.post.policy.DiaryPostAccessPolicy
import org.example.povi.domain.diary.post.repository.DiaryPostRepository
import org.example.povi.domain.user.follow.service.FollowService
import org.example.povi.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.List

@Service
@RequiredArgsConstructor
class DiaryPostService {
    private val diaryPostRepository: DiaryPostRepository? = null
    private val userRepository: UserRepository? = null
    private val followService: FollowService? = null
    private val diaryPostLikeRepository: DiaryPostLikeRepository? = null
    private val diaryCommentRepository: DiaryCommentRepository? = null
    private val postAccessPolicy: DiaryPostAccessPolicy? = null

    /**
     * 다이어리 생성 (로그인 필수)
     */
    @Transactional
    fun createDiaryPost(req: DiaryPostCreateReq?, currentUserId: Long): DiaryPostCreateRes {
        requireLogin(currentUserId)

        val author = userRepository!!.findById(currentUserId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.") }

        try {
            val post = fromCreateRequest(req!!, author) // 엔티티가 자체 정제/검증
            val saved = diaryPostRepository!!.save(post)
            return DiaryPostCreateRes.from(saved)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    /**
     * 단일 상세 (로그인 + 접근권한)
     * - 좋아요 여부/수, 댓글 수 포함
     */
    @Transactional(readOnly = true)
    fun getDiaryPostDetail(postId: Long, currentUserId: Long?): DiaryDetailRes {
        requireLogin(currentUserId)

        val post = diaryPostRepository!!.findById(postId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다.") }!!

        if (!postAccessPolicy!!.hasReadPermission(currentUserId, post)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "열람 권한이 없습니다.")
        }

        val liked = diaryPostLikeRepository!!.existsByPostIdAndUserId(postId, currentUserId)
        val likeCount = diaryPostLikeRepository.countByPostId(postId)
        val commentCount = diaryCommentRepository!!.countByPostId(postId)

        return of(post, liked, likeCount, commentCount)
    }

    /**
     * 내 다이어리: 월별 카드(페이징) + 이번 주 통계
     */
    @Transactional(readOnly = true)
    fun getMyDiaryPostsWithMonthlyFilter(
            year: Int?,
            month: Int?,
            pageable: Pageable?,
            currentUserId: Long?
    ): MyDiaryListRes {
        requireLogin(currentUserId)

        // 기준일 계산 (파라미터 없으면 오늘 기준)
        val today = LocalDate.now()
        val y = if ((year == null)) today.year else year
        val m = if ((month == null)) today.monthValue else month

        // 월별 경계 [YYYY-MM-01 00:00, 다음달 1일 00:00)
        val startOfMonth = LocalDate.of(y, m, 1).atStartOfDay()
        val startOfNextMonth = startOfMonth.plusMonths(1)

        // 이번 주 경계 [이번주 월요일 00:00, 다음주 월요일 00:00)
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val startOfWeek = monday.atStartOfDay()
        val startOfNextWeek = startOfWeek.plusDays(7)

        // 월별 카드 조회 (페이징)
        val cardPage = diaryPostRepository!!.findByUserIdAndCreatedAtBetween(
                currentUserId, startOfMonth, startOfNextMonth, pageable
        )

        val cleanContent = cardPage.content.filterNotNull()

// Create a new, clean Page with the non-null content
        val cleanCardPage = PageImpl(
                cleanContent,
                cardPage.pageable,
                cardPage.totalElements
        )

        // 주간 통계 조회 (비페이징)
        val thisWeekPosts = diaryPostRepository.findByUserIdAndCreatedAtBetween(
                currentUserId, startOfWeek, startOfNextWeek
        )
        val cleanThisWeekPosts = thisWeekPosts.filterNotNull()

        // 집계 데이터 준비 (좋아요/댓글/내가 누른 글)
        val postIds = cardPage!!.content.mapNotNull { it?.id }
        val likeCnt = if (postIds.isEmpty()) java.util.Map.of() else toCountMap(diaryPostLikeRepository!!.countByPostIds(postIds))
        val commentCnt = if (postIds.isEmpty()) java.util.Map.of() else toCountMap(diaryCommentRepository!!.countByPostIds(postIds))
        val likedSet: Set<Long> = if (postIds.isEmpty()) setOf<Long>() else HashSet(diaryPostLikeRepository!!.findPostIdsLikedByUser(postIds, currentUserId))

        return build(
                cleanCardPage,       // This is now Page<DiaryPost>
                cleanThisWeekPosts,
                likedSet,
                likeCnt,
                commentCnt
        )
    }

    /**
     * 친구 피드 (로그인) - 페이징
     * - 맞팔: FRIEND+PUBLIC, 단방향: PUBLIC
     */
    @Transactional(readOnly = true)
    fun listFriendDiaries(currentUserId: Long?, pageable: Pageable?): Page<DiaryPostCardRes> {
        requireLogin(currentUserId)

        val followingIds = followService!!.getFollowingUserIds(currentUserId)
        val mutualIds = followService.getMutualUserIds(currentUserId)
        val oneWayIds: MutableSet<Long?> = HashSet(followingIds)
        oneWayIds.removeAll(mutualIds)

        val hasMutual = !mutualIds.isEmpty()
        val hasOneWay = !oneWayIds.isEmpty()

        val mutualParam = if (hasMutual) mutualIds else List.of(-1L)
        val oneWayParam = if (hasOneWay) oneWayIds else List.of(-1L)

        val page = diaryPostRepository!!.findFriendFeedPaged(
                mutualParam,
                List.of(Visibility.FRIEND, Visibility.PUBLIC),
                oneWayParam,
                Visibility.PUBLIC,
                hasMutual,
                hasOneWay,
                pageable
        )

        if (page!!.isEmpty) return Page.empty(pageable)

        // 현재 페이지 집계 (좋아요/댓글/내가 누른 글)
        val postIds = page.content.stream().map<Long>(DiaryPost::id).toList()
        val commentCnt: Map<Long, Long> = toCountMap(diaryPostRepository.countCommentsInPostIds(postIds))
        val likeCnt: Map<Long, Long> = toCountMap(diaryPostLikeRepository!!.countByPostIds(postIds))
        val likedSet: Set<Long> = HashSet(diaryPostLikeRepository.findPostIdsLikedByUser(postIds, currentUserId))

        // DTO 변환
        val cards = toCards(page.content, likedSet, likeCnt, commentCnt)
        return PageImpl(cards, pageable, page.totalElements)
    }

    /**
     * 모두의 다이어리(Explore) (로그인)
     * - 기간: 최근 7일
     * - 맞팔: FRIEND+PUBLIC, 그 외: PUBLIC
     */
    @Transactional(readOnly = true)
    fun listExploreFeed(currentUserId: Long?, pageable: Pageable?): Page<DiaryPostCardRes> {
        requireLogin(currentUserId)

        // 최근 7일 고정: [오늘-6일 00:00, 내일 00:00)
        val today = LocalDate.now()
        val startAt = today.minusDays(6).atStartOfDay()
        val endAt = today.plusDays(1).atStartOfDay()

        val mutualIds = followService!!.getMutualUserIds(currentUserId)

        val page = if (mutualIds.isEmpty()
        ) diaryPostRepository!!.findExploreFeedPublicOnlyInPeriodPaged(
                currentUserId, Visibility.PUBLIC, startAt, endAt, pageable)
        else diaryPostRepository!!.findExploreFeedWithMutualsInPeriodPaged(
                currentUserId, mutualIds,
                List.of(Visibility.FRIEND, Visibility.PUBLIC),
                Visibility.PUBLIC, startAt, endAt, pageable)

        if (page!!.isEmpty) return Page.empty(pageable)

        // 현재 페이지 집계
        val postIds = page.content.stream().map<Long?>(DiaryPost::id).toList()
        val commentCnt: Map<Long, Long> = toCountMap(diaryPostRepository.countCommentsInPostIds(postIds))
        val likeCnt: Map<Long, Long> = toCountMap(diaryPostLikeRepository!!.countByPostIds(postIds))
        val likedSet: Set<Long> = HashSet(diaryPostLikeRepository.findPostIdsLikedByUser(postIds, currentUserId))

        val cards = toCards(page.content, likedSet, likeCnt, commentCnt)
        return PageImpl(cards, pageable, page.totalElements)
    }


    /**
     * 다이어리 부분 수정 (로그인 + 소유자)
     * - null 필드는 미변경
     */
    @Transactional
    fun updateDiaryPost(postId: Long, req: DiaryPostUpdateReq, currentUserId: Long): DiaryPostUpdateRes {
        requireLogin(currentUserId)

        val post = getOwnedDiaryPostOrThrow(postId, currentUserId)

        try {
            if (req.title != null) post.renameTo(req.title)
            if (req.content != null) post.rewriteContent(req.content)
            if (req.moodEmoji != null) post.changeMood(req.moodEmoji)
            if (req.visibility != null) post.changeVisibility(req.visibility)
            if (req.imageUrls != null) post.replaceImages(req.imageUrls)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        return DiaryPostUpdateRes.from(post)
    }

    /**
     * 다이어리 삭제 (로그인 + 소유자)
     */
    @Transactional
    fun deleteDiaryPost(postId: Long, currentUserId: Long) {
        requireLogin(currentUserId)
        val post = getOwnedDiaryPostOrThrow(postId, currentUserId)
        diaryPostRepository!!.delete(post)
    }

    // =========================================================
    // Private Helpers
    // =========================================================
    /**
     * 로그인 필수 동작에서 userId null 방지
     */
    private fun requireLogin(userId: Long?) {
        if (userId == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.")
        }
    }

    /**
     * 소유자 검증을 포함한 단건 조회
     */
    private fun getOwnedDiaryPostOrThrow(postId: Long, currentUserId: Long): DiaryPost {
        val post = diaryPostRepository!!.findById(postId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다.") }!!
        if (post.user.id != currentUserId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 접근할 수 있습니다.")
        }
        return post
    }

    /**
     * 사용자별 다이어리 개수
     */
    @Transactional(readOnly = true)
    fun getDiaryPostCountForUser(userId: Long?): Long {
        return diaryPostRepository!!.countByUserId(userId)
    }
}