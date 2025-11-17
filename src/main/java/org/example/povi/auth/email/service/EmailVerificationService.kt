package org.example.povi.auth.email.service

import org.example.povi.auth.email.dto.EmailVerificationRequestDto
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto
import org.example.povi.auth.email.entity.EmailVerificationToken
import org.example.povi.auth.email.limiter.EmailVerificationRateLimiter
import org.example.povi.auth.email.mapper.EmailVerificationTemplateMapper
import org.example.povi.auth.email.mapper.EmailVerificationTokenMapper
import org.example.povi.auth.email.repository.EmailVerificationTokenRepository
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.exception.ex.AlreadyVerifiedEmailException
import org.example.povi.global.exception.ex.ExpiredEmailTokenException
import org.example.povi.global.exception.ex.InvalidEmailTokenException
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class EmailVerificationService(
    private val tokenRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository,
    private val mailSender: JavaMailSender,
    private val templateMapper: EmailVerificationTemplateMapper,
    private val rateLimiter: EmailVerificationRateLimiter
) {

    @Transactional
    fun sendVerificationEmail(request: EmailVerificationRequestDto?) {
        val email = request?.email ?: throw IllegalArgumentException("이메일이 존재하지 않습니다.")

        rateLimiter.validateSendLimit(email)

        tokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now())

        val newToken = generateToken()
        val expiresAt = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES)

        var existing = tokenRepository.findByEmail(email).orElse(null)

        if (existing != null) {
            when {
                existing.verified -> throw AlreadyVerifiedEmailException()
                existing.isExpired -> {
                    tokenRepository.delete(existing)
                    existing = null
                }
            }
        }

        val tokenEntity = EmailVerificationTokenMapper.createOrUpdate(existing, email, newToken, expiresAt)
        tokenRepository.save(tokenEntity)

        sendEmail(email, newToken)
    }

    private fun sendEmail(toEmail: String, token: String) {
        val subject = EmailVerificationTemplateMapper.SUBJECT
        val htmlContent = templateMapper.renderTemplate(token)

        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(toEmail)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)

            mailSender.send(message)
        } catch (e: MailException) {
            throw RuntimeException("이메일 전송에 실패했습니다.", e)
        }
    }

    @Transactional
    fun verifyEmail(token: String): Boolean {
        val tokenEntity = tokenRepository.findByToken(token.trim())
            .orElseThrow { InvalidEmailTokenException() }

        if (tokenEntity.isExpired) throw ExpiredEmailTokenException()
        if (tokenEntity.verified) throw AlreadyVerifiedEmailException()

        tokenEntity.markAsVerified()
        tokenRepository.save(tokenEntity)

        userRepository.findByEmail(tokenEntity.email)
            .ifPresent { user ->
                user.verifyEmail()
                userRepository.save(user)
            }

        return true
    }

    @Transactional(readOnly = true)
    fun checkVerificationStatus(email: String): EmailVerificationStatusResponseDto {
        val isVerified = tokenRepository.findByEmail(email)
            .map { it.verified }
            .orElse(false)

        return EmailVerificationStatusResponseDto(email, isVerified)
    }

    private fun generateToken(): String = UUID.randomUUID().toString()

    companion object {
        private const val TOKEN_EXPIRATION_MINUTES = 60L
    }
}