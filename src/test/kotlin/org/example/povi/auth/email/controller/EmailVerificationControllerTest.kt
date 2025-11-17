package org.example.povi.auth.email.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.example.povi.auth.email.dto.EmailVerificationRequestDto
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto
import org.example.povi.auth.email.service.EmailVerificationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import jakarta.validation.Validation
import org.springframework.validation.beanvalidation.SpringValidatorAdapter

class EmailVerificationControllerTest {

    private lateinit var mockMvc: MockMvc

    // ✔ FIXED: Jackson Kotlin module 사용
    private val objectMapper = jacksonObjectMapper()

    private val emailService: EmailVerificationService = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        val jakartaValidator = Validation.buildDefaultValidatorFactory().validator
        val springValidator = SpringValidatorAdapter(jakartaValidator)

        mockMvc = MockMvcBuilders
            .standaloneSetup(EmailVerificationController(emailService))
            .setValidator(springValidator)
            .build()
    }

    @Test
    fun `인증 이메일 전송 성공`() {
        val request = EmailVerificationRequestDto("test@example.com")

        every { emailService.sendVerificationEmail(any()) } returns Unit

        mockMvc.perform(
            post("/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `이메일 인증 성공`() {
        every { emailService.verifyEmail(any()) } returns true

        mockMvc.perform(
            get("/auth/email/verify")
                .param("token", "abc123")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `이메일 인증 실패`() {
        every { emailService.verifyEmail(any()) } returns false

        mockMvc.perform(
            get("/auth/email/verify")
                .param("token", "abc123")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `이메일 인증 상태 조회 성공`() {
        every {
            emailService.checkVerificationStatus("test@example.com")
        } returns EmailVerificationStatusResponseDto("test@example.com", true)

        mockMvc.perform(
            get("/auth/email/status")
                .param("email", "test@example.com")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.verified").value(true))
    }
}