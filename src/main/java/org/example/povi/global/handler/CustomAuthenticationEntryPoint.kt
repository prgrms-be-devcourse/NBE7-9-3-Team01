package org.example.povi.global.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

/**
 * 인증 실패(401 Unauthorized) 시 실행되는 핸들러
 */
@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val errorDetails = mapOf(
            "code" to 401,
            "message" to "인증이 필요합니다. 토큰이 유효하지 않거나 없습니다."
        )

        response.writer.use { writer ->
            writer.write(objectMapper.writeValueAsString(errorDetails))
            writer.flush()
        }
    }
}