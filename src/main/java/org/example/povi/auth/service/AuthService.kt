package org.example.povi.auth.service

import jakarta.transaction.Transactional
import org.example.povi.auth.dto.LoginRequestDto
import org.example.povi.auth.dto.LoginResponseDto
import org.example.povi.auth.dto.SignupRequestDto
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
import org.example.povi.domain.user.follow.repository.FollowRepository
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.exception.error.ErrorCode
import org.example.povi.global.exception.ex.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,

    private val postLikeRepository: PostLikeRepository,
    private val bookmarkRepository: CommunityBookmarkRepository,
    private val commentRepository: CommentRepository,
    private val diaryCommentRepository: DiaryCommentRepository,
    private val transcriptionRepository: TranscriptionRepository,
    private val userMissionRepository: UserMissionRepository,
    private val followRepository: FollowRepository,

    private val diaryPostRepository: DiaryPostRepository,
    private val communityRepository: CommunityRepository
) {

    /**
     * 회원가입 처리
     */
    fun signup(requestDto: SignupRequestDto?) {
        val dto = requestDto ?: throw CustomException(ErrorCode.INVALID_REQUEST)

        if (userRepository.existsByEmail(dto.email)) {
            throw UserAlreadyExistsException()
        }

        val provider = try {
            AuthProvider.valueOf(dto.provider.uppercase(Locale.getDefault()))
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INVALID_AUTH_PROVIDER)
        }

        // 이메일 중복이지만 인증 전이면 가입 불가
        userRepository.findByEmail(dto.email).ifPresent { existing ->
            if (!existing.isEmailVerified) {
                throw CustomException(ErrorCode.EMAIL_NOT_VERIFIED)
            }
        }

        // local 회원가입 시 비밀번호 없는 요청 차단
        if (provider == AuthProvider.LOCAL && dto.password.isBlank()) {
            throw CustomException(ErrorCode.INVALID_PASSWORD)
        }

        val user = UserMapper.toEntity(dto, provider, passwordEncoder)
        userRepository.save(user)
    }

    /**
     * 로그인 처리
     */
    fun login(requestDto: LoginRequestDto): LoginResponseDto {
        val user = userRepository.findByEmail(requestDto.email)
            .orElseThrow { UserNotFoundException() }

        if (!passwordEncoder.matches(requestDto.password, user.password)) {
            throw InvalidPasswordException()
        }

        val accessToken = jwtTokenProvider.createAccessToken(user.id, user.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email)

        refreshTokenService.save(user.email, refreshToken)

        return LoginResponseDto(accessToken, refreshToken, user.nickname)
    }

    /**
     * 로그아웃 — Refresh Token 제거
     */
    fun logout(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        refreshTokenService.delete(user.email)
    }

    /**
     * 회원 탈퇴 처리
     */
    @Transactional
    fun withdraw(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        // 수동 삭제
        postLikeRepository.deleteAllByUser(user)
        bookmarkRepository.deleteAllByUser(user)
        commentRepository.deleteAllByUser(user)
        diaryCommentRepository.deleteAllByAuthor(user)
        transcriptionRepository.deleteAllByUser(user)
        userMissionRepository.deleteAllByUser(user)
        followRepository.deleteAllByFollowerOrFollowing(user, user)

        // cascade 삭제 처리되는 엔티티
        diaryPostRepository.deleteAllByUser(user)
        communityRepository.deleteAllByUser(user)

        // 사용자 삭제
        userRepository.delete(user)
    }
}