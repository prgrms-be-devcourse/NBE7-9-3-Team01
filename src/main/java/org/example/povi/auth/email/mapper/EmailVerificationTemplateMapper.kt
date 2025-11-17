package org.example.povi.auth.email.mapper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

/**
 * 이메일 인증 메일 템플릿 매핑을 담당
 */
@Component
class EmailVerificationTemplateMapper(
    private val templateEngine: SpringTemplateEngine,
    @Value("\${app.mail.verification-link}")
    private val verificationBaseUrl: String
) {

    /**
     * HTML 템플릿 렌더링 처리
     */
    fun renderTemplate(token: String): String {
        val context = Context().apply {
            setVariable("verificationLink", verificationBaseUrl + token)
        }

        return templateEngine.process(TEMPLATE_PATH, context)
    }

    companion object {
        private const val TEMPLATE_PATH = "mail/verification"
        const val SUBJECT: String = "[POVI] 이메일 인증 요청"
    }
}