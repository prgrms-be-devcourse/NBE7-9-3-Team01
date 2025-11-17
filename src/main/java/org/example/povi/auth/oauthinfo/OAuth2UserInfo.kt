package org.example.povi.auth.oauthinfo

/**
 * 소셜 로그인 사용자 정보를 제공하는 인터페이스
 */
interface OAuth2UserInfo {
    val providerId: String?
    val provider: String
    val email: String?
    val nickname: String?
    val attributes: Map<String, Any>
}