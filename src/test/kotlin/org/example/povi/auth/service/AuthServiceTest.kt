package org.example.povi.auth.service

import io.mockk.*
import org.example.povi.auth.dto.LoginRequestDto
import org.example.povi.auth.enums.AuthProvider
import org.example.povi.auth.mapper.UserMapper
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.auth.token.jwt.RefreshTokenService
import org.example.povi.domain.community.repository.*
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository
import org.example.povi.domain.diary.post.repository.DiaryPostRepository
import org.example.povi.domain.mission.repository.UserMissionRepository
import org.example.povi.domain.transcription.repository.TranscriptionRepository
import org.example.povi.domain.user.entity.User
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.domain.user.follow.repository.FollowRepository
import org.example.povi.global.exception.ex.InvalidPasswordException
import org.example.povi.global.exception.ex.UserNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtProvider: JwtTokenProvider
    private lateinit var refreshTokenService: RefreshTokenService

    // 삭제 관련 저장소
    private lateinit var postLikeRepository: PostLikeRepository
    private lateinit var bookmarkRepository: CommunityBookmarkRepository
    private lateinit var commentRepository: CommentRepository
    private lateinit var diaryCommentRepository: DiaryCommentRepository
    private lateinit var transcriptionRepository: TranscriptionRepository
    private lateinit var userMissionRepository: UserMissionRepository
    private lateinit var followRepository: FollowRepository
    private lateinit var diaryPostRepository: DiaryPostRepository
    private lateinit var communityRepository: CommunityRepository

    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        passwordEncoder = mockk()
        jwtProvider = mockk()
        refreshTokenService = mockk()

        postLikeRepository = mockk()
        bookmarkRepository = mockk()
        commentRepository = mockk()
        diaryCommentRepository = mockk()
        transcriptionRepository = mockk()
        userMissionRepository = mockk()
        followRepository = mockk()
        diaryPostRepository = mockk()
        communityRepository = mockk()

        authService = AuthService(
            userRepository,
            passwordEncoder,
            jwtProvider,
            refreshTokenService,
            postLikeRepository,
            bookmarkRepository,
            commentRepository,
            diaryCommentRepository,
            transcriptionRepository,
            userMissionRepository,
            followRepository,
            diaryPostRepository,
            communityRepository
        )
    }

    @Test
    fun `로그인 성공`() {
        val email = "test@example.com"
        val password = "1234"
        val encoded = "ENCODED_PASSWORD"

        val user = User.builder()
            .email(email)
            .password(encoded)
            .nickname("tester")
            .provider(AuthProvider.LOCAL)
            .isEmailVerified(true)
            .build()

        every { userRepository.findByEmail(email) } returns Optional.of(user)
        every { passwordEncoder.matches(password, encoded) } returns true
        every { jwtProvider.createAccessToken(user.id, user.email) } returns "ACCESS_TOKEN"
        every { jwtProvider.createRefreshToken(user.email) } returns "REFRESH_TOKEN"
        every { refreshTokenService.save(user.email, "REFRESH_TOKEN") } just Runs

        val request = LoginRequestDto(email, password)

        val result = authService.login(request)

        assertEquals("ACCESS_TOKEN", result.accessToken)
        assertEquals("REFRESH_TOKEN", result.refreshToken)
        assertEquals("tester", result.nickname)
    }

    @Test
    fun `로그인 실패 - 이메일 없음`() {
        every { userRepository.findByEmail("none@test.com") } returns Optional.empty()

        val request = LoginRequestDto("none@test.com", "1234")

        assertThrows(UserNotFoundException::class.java) {
            authService.login(request)
        }
    }

    @Test
    fun `로그인 실패 - 비밀번호 불일치`() {
        val email = "test@example.com"

        val user = User.builder()
            .email(email)
            .password("HASHED")
            .nickname("tester")
            .provider(AuthProvider.LOCAL)
            .build()

        every { userRepository.findByEmail(email) } returns Optional.of(user)
        every { passwordEncoder.matches("wrong", any()) } returns false

        val request = LoginRequestDto(email, "wrong")

        assertThrows(InvalidPasswordException::class.java) {
            authService.login(request)
        }
    }
}