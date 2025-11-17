package org.example.povi.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.example.povi.auth.dto.*
import org.example.povi.auth.service.AuthService
import org.example.povi.auth.token.jwt.CustomJwtUser
import org.example.povi.auth.token.service.TokenService
import org.example.povi.auth.util.SecurityUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    // 완전한 mock 서비스
    private val authService: AuthService = mockk()
    private val tokenService: TokenService = mockk()

    private lateinit var customUser: CustomJwtUser

    @BeforeEach
    fun setup() {
        mockkObject(SecurityUtil)

        customUser = CustomJwtUser(
            id = 1L,
            email = "test@example.com",
            nickname = "tester"
        )

        // StandaloneSetup → Spring Context 완전히 배제
        mockMvc = MockMvcBuilders
            .standaloneSetup(AuthController(authService, tokenService))
            .build()
    }

    @Test
    fun `회원가입 성공`() {
        val request = SignupRequestDto(
            email = "test@example.com",
            password = "1234",
            nickname = "tester",
            provider = "LOCAL",
            providerId = "none"
        )

        every { authService.signup(any()) } returns Unit

        mockMvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

        verify { authService.signup(any()) }
    }

    @Test
    fun `로그인 성공`() {
        val request = LoginRequestDto("test@example.com", "1234")

        val response = LoginResponseDto(
            accessToken = "ACCESS",
            refreshToken = "REFRESH",
            nickname = "tester"
        )

        every { authService.login(any()) } returns response

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("ACCESS"))
            .andExpect(jsonPath("$.refreshToken").value("REFRESH"))
            .andExpect(jsonPath("$.nickname").value("tester"))
    }

    @Test
    fun `내 정보 조회 성공`() {
        every { SecurityUtil.currentUserOrThrow } returns customUser

        mockMvc.perform(get("/auth/me"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.nickname").value("tester"))
    }

    @Test
    fun `토큰 재발급 성공`() {
        val request = TokenReissueRequestDto(
            accessToken = "OLD_ACCESS",
            refreshToken = "REFRESH_TOKEN"
        )

        val response = TokenReissueResponseDto(
            accessToken = "NEW_ACCESS",
            refreshToken = "NEW_REFRESH"
        )

        every { tokenService.reissueAccessToken(any()) } returns response

        mockMvc.perform(
            post("/auth/token/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("NEW_ACCESS"))
            .andExpect(jsonPath("$.refreshToken").value("NEW_REFRESH"))
    }

    @Test
    fun `로그아웃 성공`() {
        every { SecurityUtil.currentUserOrThrow } returns customUser
        every { authService.logout(any()) } returns Unit

        mockMvc.perform(post("/auth/logout"))
            .andExpect(status().isOk)

        verify { authService.logout(1L) }
    }

    @Test
    fun `회원 탈퇴 성공`() {
        every { SecurityUtil.currentUserOrThrow } returns customUser
        every { authService.withdraw(any()) } returns Unit

        mockMvc.perform(delete("/auth/withdraw"))
            .andExpect(status().isNoContent)

        verify { authService.withdraw(1L) }
    }
}