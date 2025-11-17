package org.example.povi.auth.oauthinfo

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * OAuth2 로그인 인증 후 반환되는 사용자 정보
 */
data class CustomOAuth2User(
    val email: String,
    val provider: String,
    val providerId: String,
    val nickname: String,
    private val attributes: Map<String, Any>
) : OAuth2User {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getName(): String {
        // user 고유 식별자 반환 (email 이 더 안정적)
        return email
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }
}