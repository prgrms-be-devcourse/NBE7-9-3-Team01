package org.example.povi.auth.email.service

import io.mockk.*
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import org.example.povi.auth.email.dto.EmailVerificationRequestDto
import org.example.povi.auth.email.entity.EmailVerificationToken
import org.example.povi.auth.email.limiter.EmailVerificationRateLimiter
import org.example.povi.auth.email.mapper.EmailVerificationTemplateMapper
import org.example.povi.auth.email.repository.EmailVerificationTokenRepository
import org.example.povi.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender
import java.util.*

class EmailVerificationServiceTest {

    private lateinit var tokenRepository: EmailVerificationTokenRepository
    private lateinit var userRepository: UserRepository
    private lateinit var mailSender: JavaMailSender
    private lateinit var templateMapper: EmailVerificationTemplateMapper
    private lateinit var rateLimiter: EmailVerificationRateLimiter

    private lateinit var service: EmailVerificationService

    @BeforeEach
    fun setup() {
        tokenRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        mailSender = mockk(relaxed = true)
        templateMapper = mockk(relaxed = true)
        rateLimiter = mockk(relaxed = true)

        service = EmailVerificationService(
            tokenRepository, userRepository, mailSender, templateMapper, rateLimiter
        )
    }

    @Test
    fun `이메일 인증 요청 성공`() {
        val email = "user@example.com"
        val request = EmailVerificationRequestDto(email)

        every { rateLimiter.validateSendLimit(email) } just Runs
        every { tokenRepository.findByEmail(email) } returns Optional.empty()
        every { templateMapper.renderTemplate(any()) } returns "<html/>"

        // save() 타입 명시
        every { tokenRepository.save(ofType(EmailVerificationToken::class)) } answers { firstArg() }

        // 실제 MimeMessage 생성
        val session = Session.getInstance(Properties())
        val mimeMessage = MimeMessage(session)

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { mailSender.send(any<MimeMessage>()) } just Runs

        // when
        service.sendVerificationEmail(request)

        // then
        assertNotNull(mimeMessage)
        verify(exactly = 1) { mailSender.send(any<MimeMessage>()) }
    }
}