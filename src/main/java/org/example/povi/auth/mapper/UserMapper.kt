package org.example.povi.auth.mapper

import org.example.povi.auth.dto.SignupRequestDto
import org.example.povi.auth.enums.AuthProvider
import org.example.povi.domain.user.entity.User
import org.example.povi.domain.user.entity.UserRole
import org.springframework.security.crypto.password.PasswordEncoder

object UserMapper {

    /**
     * 일반 회원가입 → User Entity
     */
    fun toEntity(dto: SignupRequestDto, provider: AuthProvider, encoder: PasswordEncoder): User {

        return User(
            email = dto.email,
            password = if (provider == AuthProvider.LOCAL) encoder.encode(dto.password) else "",
            nickname = dto.nickname,
            provider = provider,
            providerId = dto.providerId,
            userRole = UserRole.USER,
            isEmailVerified = false
        )
    }

    /**
     * OAuth 회원 정보 → User Entity
     */
    fun fromOAuth(
        provider: AuthProvider,
        providerId: String?,
        email: String?,
        attributes: Map<String, Any>
    ): User {

        val nickname = extractNickname(provider, attributes)

        return User(
            email = email ?: "",
            password = "",
            nickname = nickname,
            provider = provider,
            providerId = providerId,
            userRole = UserRole.USER,
            isEmailVerified = true
        )
    }

    /**
     * Provider 별 닉네임 추출
     */
    private fun extractNickname(
        provider: AuthProvider,
        attributes: Map<String, Any>
    ): String {
        return when (provider) {

            AuthProvider.KAKAO -> {
                val account = attributes["kakao_account"] as? Map<*, *> ?: return "카카오유저"
                val profile = account["profile"] as? Map<*, *> ?: return "카카오유저"
                profile["nickname"] as? String ?: "카카오유저"
            }

            AuthProvider.GOOGLE -> {
                attributes["name"] as? String ?: "구글유저"
            }

            AuthProvider.LOCAL -> "유저"
        }
    }
}