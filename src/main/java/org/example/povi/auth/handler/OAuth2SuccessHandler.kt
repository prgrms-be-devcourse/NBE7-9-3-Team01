package org.example.povi.auth.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.povi.auth.oauthinfo.CustomOAuth2User
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2SuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    @Value("\${app.oauth2.redirect-uri}")
    private val frontendRedirectUrl: String
) : AuthenticationSuccessHandler {

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        val oauthUser = authentication.principal as? CustomOAuth2User
            ?: throw RuntimeException("OAuth2 사용자 정보를 가져올 수 없습니다.")

        val user = userRepository.findByEmail(oauthUser.email)
            .orElseThrow { RuntimeException("사용자를 찾을 수 없습니다: ${oauthUser.email}") }

        // JWT 발급
        val accessToken = jwtTokenProvider.createAccessToken(user.id, user.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email)

        // url encoding
        val redirectUrl =
            "$frontendRedirectUrl?accessToken=${URLEncoder.encode(accessToken, StandardCharsets.UTF_8)}" +
                    "&refreshToken=${URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)}"

        response.sendRedirect(redirectUrl)
    }
}