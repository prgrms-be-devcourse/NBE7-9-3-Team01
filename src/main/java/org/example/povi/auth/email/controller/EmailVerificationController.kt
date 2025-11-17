package org.example.povi.auth.email.controller

import jakarta.validation.Valid
import org.example.povi.auth.email.controller.docs.EmailVerificationControllerDocs
import org.example.povi.auth.email.dto.EmailVerificationRequestDto
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto
import org.example.povi.auth.email.service.EmailVerificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 이메일 인증 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/auth/email")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService
) : EmailVerificationControllerDocs {

    /**
     * 인증 메일 전송
     */
    @PostMapping("/send")
    override fun sendEmailVerification(
        @RequestBody @Valid request: EmailVerificationRequestDto
    ): ResponseEntity<Void> {

        emailVerificationService.sendVerificationEmail(request)
        return ResponseEntity.ok().build()
    }

    /**
     * 이메일 인증 토큰 검증
     */
    @GetMapping("/verify")
    override fun verifyEmail(
        @RequestParam("token") token: String
    ): ResponseEntity<Void> {

        val isVerified = emailVerificationService.verifyEmail(token)

        return if (isVerified) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * 이메일 인증 여부 조회
     */
    @GetMapping("/status")
    override fun checkEmailVerificationStatus(
        @RequestParam email: String
    ): ResponseEntity<EmailVerificationStatusResponseDto> {

        val response = emailVerificationService.checkVerificationStatus(email)
        return ResponseEntity.ok(response)
    }
}