package org.example.povi.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {

        val info = Info().apply {
            title = "데브코스 일취월장 2차프로젝트 API"
            description = "데브코스 일취월장 다이어리 서비스 프로젝트 API 문서"
            version = "1.0"
        }

        // JWT Security Scheme
        val jwtScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")

        // Kakao OAuth2 Security Scheme
        val kakaoOAuthScheme = SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .description("Kakao OAuth2 Flow")
            .flows(
                OAuthFlows().authorizationCode(
                    OAuthFlow()
                        .authorizationUrl("https://kauth.kakao.com/oauth/authorize")
                        .tokenUrl("https://kauth.kakao.com/oauth/token")
                        .scopes(
                            Scopes().apply {
                                addString("account_email", "이메일 조회 권한")
                                addString("profile_nickname", "프로필 닉네임 조회 권한")
                            }
                        )
                )
            )

        // Google OAuth2 Security Scheme
        val googleOAuthScheme = SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .description("Google OAuth2 Flow")
            .flows(
                OAuthFlows().authorizationCode(
                    OAuthFlow()
                        .authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
                        .tokenUrl("https://oauth2.googleapis.com/token")
                        .scopes(
                            Scopes().apply {
                                addString("openid", "OpenID Connect")
                                addString("email", "이메일 조회 권한")
                                addString("profile", "프로필 정보 조회 권한")
                            }
                        )
                )
            )

        // SecurityRequirement 한 번만 설정
        val securityRequirement = SecurityRequirement()
            .addList("BearerAuth")
            .addList("KakaoOAuth")
            .addList("GoogleOAuth")

        return OpenAPI()
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes("BearerAuth", jwtScheme)
                    .addSecuritySchemes("KakaoOAuth", kakaoOAuthScheme)
                    .addSecuritySchemes("GoogleOAuth", googleOAuthScheme)
            )
    }
}