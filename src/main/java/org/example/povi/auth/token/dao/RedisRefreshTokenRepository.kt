package org.example.povi.auth.token.dao

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

/**
 * Redis를 이용한 Refresh Token 저장소 구현체
 */
@Repository
class RedisRefreshTokenRepository(
    private val redisTemplate: RedisTemplate<String, String>
) : RefreshTokenRepository {

    /**
     * Refresh Token 저장
     */
    override fun save(userId: String, refreshToken: String?) {
        if (refreshToken != null) {
            redisTemplate.opsForValue().set(
                userId,
                refreshToken,
                REFRESH_TOKEN_EXPIRATION
            )
        }
    }

    /**
     * 저장된 Refresh Token 조회
     */
    override fun findByUserId(userId: String?): String? {
        if (userId == null) return null
        return redisTemplate.opsForValue().get(userId)
    }

    /**
     * Refresh Token 삭제
     */
    override fun deleteByUserId(userId: String) {
        redisTemplate.delete(userId)
    }

    companion object {
        private val REFRESH_TOKEN_EXPIRATION: Duration = Duration.ofDays(1)
    }
}