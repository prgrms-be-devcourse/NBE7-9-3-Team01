package org.example.povi.auth.mapper

import org.example.povi.auth.enums.AuthProvider
import org.example.povi.auth.oauthinfo.CustomOAuth2User
import org.example.povi.domain.user.entity.User

object OAuthUserMapper {

    /**
     * User + OAuth 속성 → CustomOAuth2User 로 변환
     */
    fun toCustomOAuth2User(
        user: User,
        provider: AuthProvider,
        providerId: String?,
        attributes: Map<String, Any>
    ): CustomOAuth2User {

        val safeProviderId = providerId
            ?: throw IllegalArgumentException("providerId 는 null 일 수 없습니다.")

        return CustomOAuth2User(
            email = user.email,
            provider = provider.name.lowercase(),
            providerId = safeProviderId,
            nickname = user.nickname,
            attributes = attributes
        )
    }
}