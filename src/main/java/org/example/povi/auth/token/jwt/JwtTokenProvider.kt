package org.example.povi.auth.token.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(

    @Value("\${jwt.secret}")
    private val secretKey: String,

    @Value("\${jwt.access-expiration}")
    private val accessTokenExpireTime: Long,

    @Value("\${jwt.refresh-expiration}")
    private val refreshTokenExpireTime: Long
) {

    private lateinit var key: Key
    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    fun createAccessToken(userId: Long?, email: String): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpireTime)

        val claims = Jwts.claims().apply {
            subject = email
            this["id"] = userId
            this["email"] = email
        }

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(email: String?): String {
        val now = Date()
        val expiry = Date(now.time + refreshTokenExpireTime)

        val claims = Jwts.claims().apply {
            subject = email
        }

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String?): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: JwtException) {
            log.warn("[JWT 검증 실패] {}", e.message)
            false
        } catch (e: IllegalArgumentException) {
            log.warn("[JWT 검증 실패] {}", e.message)
            false
        }
    }

    fun getUserEmail(token: String?): String =
        getClaims(token).subject

    fun getUserId(token: String?): Long {
        val id = getClaims(token)["id"]
        return when (id) {
            is Int -> id.toLong()
            is Long -> id
            else -> throw JwtException("JWT에 저장된 사용자 ID 타입이 올바르지 않습니다.")
        }
    }

    private fun getClaims(token: String?): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}