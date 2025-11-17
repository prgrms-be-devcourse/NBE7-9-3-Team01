package org.example.povi.auth.email.limiter

import org.example.povi.global.exception.ex.RateLimitExceededException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Component
class EmailVerificationRateLimiter(
    private val redisTemplate: StringRedisTemplate
) {

    /**
     * 하루 제한(5회) 초과 여부 검증
     */
    fun validateSendLimit(email: String) {
        val key = buildKey(email)

        // count 증가 (없으면 1로 생성)
        val count = redisTemplate.opsForValue().increment(key)

        // 첫 증가면 TTL 설정
        if (count == 1L) {
            redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS)
        }

        // 최대 횟수 초과
        if (count > MAX_DAILY_LIMIT) {
            throw RateLimitExceededException("하루에 최대 5회까지만 인증 메일을 전송할 수 있습니다.")
        }
    }

    /**
     * Redis 키 생성
     * 예: email:count:user@example.com:20251114
     */
    private fun buildKey(email: String): String {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        return "email:count:$email:$today"
    }

    companion object {
        private const val MAX_DAILY_LIMIT = 5
        private const val TTL_HOURS: Long = 24
    }
}