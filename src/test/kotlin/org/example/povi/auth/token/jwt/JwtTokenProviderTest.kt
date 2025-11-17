package org.example.povi.auth.token.jwt

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private lateinit var jwtProvider: JwtTokenProvider

    @BeforeEach
    fun setup() {
        jwtProvider = JwtTokenProvider(
            secretKey = "your-test-secret-key-your-test-secret-key-your-test",
            accessTokenExpireTime = 3600000L,       // 1시간
            refreshTokenExpireTime = 1209600000L    // 2주
        )

        jwtProvider.init()
    }

    @Test
    fun `AccessToken 생성 성공`() {
        // given
        val userId = 123L
        val email = "test@example.com"

        val token = jwtProvider.createAccessToken(userId, email)

        assertTrue(jwtProvider.validateToken(token))
        assertEquals(userId, jwtProvider.getUserId(token))
        assertEquals(email, jwtProvider.getUserEmail(token))
    }

    @Test
    fun `만료되었거나 조작된 토큰 - validate 실패`() {
        val invalidToken = "invalid.token.value"
        assertFalse(jwtProvider.validateToken(invalidToken))
    }
}