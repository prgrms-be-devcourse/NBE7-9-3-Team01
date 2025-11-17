package org.example.povi.auth.oauthinfo

/**
 * Kakao OAuth2 사용자 정보 파싱 클래스
 */
data class KakaoUserInfo(
    override val attributes: Map<String, Any>
) : OAuth2UserInfo {

    override val providerId: String?
        get() = attributes["id"]?.toString()

    override val provider: String
        get() = "KAKAO"

    override val email: String?
        get() {
            val account = attributes["kakao_account"] as? Map<*, *> ?: return null
            return account["email"]?.toString()
        }

    override val nickname: String?
        get() {
            val account = attributes["kakao_account"] as? Map<*, *> ?: return null
            val profile = account["profile"] as? Map<*, *> ?: return null
            return profile["nickname"]?.toString()
        }
}