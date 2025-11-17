package org.example.povi.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.oauth2")
data class OAuthProperties(
    val redirectUri: String
)