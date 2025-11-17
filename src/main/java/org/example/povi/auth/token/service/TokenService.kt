package org.example.povi.auth.token.service

import org.example.povi.auth.dto.TokenReissueRequestDto
import org.example.povi.auth.dto.TokenReissueResponseDto
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.auth.token.jwt.RefreshTokenService
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.exception.error.ErrorCode
import org.example.povi.global.exception.ex.CustomException
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val userRepository: UserRepository
) {

    fun reissueAccessToken(requestDto: TokenReissueRequestDto): TokenReissueResponseDto {
        val refreshToken = requestDto.refreshToken

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val email = jwtTokenProvider.getUserEmail(refreshToken)
        val savedToken = refreshTokenService.getByEmail(email)

        if (refreshToken != savedToken.refreshToken) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val user = userRepository.findByEmail(email)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val newAccessToken = jwtTokenProvider.createAccessToken(
            user.id,
            user.email!!
        )

        return TokenReissueResponseDto(newAccessToken, refreshToken)
    }
}