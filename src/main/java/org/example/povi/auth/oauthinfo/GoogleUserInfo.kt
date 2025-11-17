package org.example.povi.auth.oauthinfo

/**
 * Google OAuth2 사용자 정보 파싱 클래스
 */
data class GoogleUserInfo(
    override val attributes: Map<String, Any>
) : OAuth2UserInfo {

    override val providerId: String?
        get() = attributes["sub"]?.toString()

    override val provider: String
        get() = "GOOGLE"

    override val email: String?
        get() = attributes["email"]?.toString()

    override val nickname: String?
        get() = attributes["name"]?.toString()
}