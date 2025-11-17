package org.example.povi.auth.oauthinfo

import org.example.povi.auth.enums.AuthProvider
import java.util.*

/**
 * OAuth2UserInfo 구현체를 provider별로 생성하는 Factory
 */
object OAuth2UserInfoFactory {

    /**
     * 소셜 제공자(provider)에 따라 적절한 OAuth2UserInfo 구현체 반환
     */
    fun getOAuth2UserInfo(provider: String, attributes: Map<String, Any>): OAuth2UserInfo {
        val authProvider = try {
            AuthProvider.valueOf(provider.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효하지 않은 소셜 로그인 provider 값입니다: $provider")
        }

        return when (authProvider) {
            AuthProvider.KAKAO -> KakaoUserInfo(attributes)
            AuthProvider.GOOGLE -> GoogleUserInfo(attributes)
            AuthProvider.LOCAL ->
                throw IllegalArgumentException("LOCAL provider는 OAuth2 인증을 지원하지 않습니다.")
        }
    }
}