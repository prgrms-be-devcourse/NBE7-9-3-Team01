package org.example.povi.auth.service

import lombok.extern.slf4j.Slf4j
import org.example.povi.auth.enums.AuthProvider
import org.example.povi.auth.mapper.OAuthUserMapper
import org.example.povi.auth.mapper.UserMapper
import org.example.povi.auth.oauthinfo.OAuth2UserInfoFactory
import org.example.povi.domain.user.entity.User
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.exception.error.ErrorCode
import org.example.povi.global.exception.ex.CustomException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

@Slf4j
@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {

        val oAuth2User = super.loadUser(userRequest)
        val attributes = oAuth2User.attributes

        // provider 예: "GOOGLE", "KAKAO"
        val registrationId = userRequest.clientRegistration.registrationId.uppercase(Locale.getDefault())
        val provider = AuthProvider.valueOf(registrationId)   // ✔ enum 확정

        // provider별 정보 파싱
        val userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes)

        val providerId = userInfo.providerId
            ?: throw OAuth2AuthenticationException("providerId 없음")

        val email = userInfo.email
            ?: throw OAuth2AuthenticationException("email 없음 (SNS에서 제공 안함)")

        // 기존 이메일 사용자와 provider 비교
        userRepository.findByEmail(email).ifPresent { existingUser: User ->
            if (existingUser.provider != provider) {
                throw CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED)
            }
        }

        // provider + providerId 로 기존 OAuth 사용자 조회
        val user = userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet {
                userRepository.save(
                    UserMapper.fromOAuth(
                        provider = provider,
                        providerId = providerId,
                        email = email,
                        attributes = attributes
                    )
                )
            }

        // SecurityContextHolder 에 넣을 사용자 객체 변환
        return OAuthUserMapper.toCustomOAuth2User(
            user = user,
            provider = provider,
            providerId = providerId,
            attributes = attributes
        )
    }
}