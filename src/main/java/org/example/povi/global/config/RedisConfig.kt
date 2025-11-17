package org.example.povi.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private val redisHost: String? = null

    @Value("\${spring.data.redis.port}")
    private val redisPort = 0

    /**
     * Redis 서버와 연결을 위한 ConnectionFactory (설정 기반 생성)
     */
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(
            RedisStandaloneConfiguration(redisHost, redisPort)
        )
    }

    /**
     * RedisTemplate 등록: <String></String>, String> 형식으로 직렬화 설정 포함
     */
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory

        // 문자열 직렬화 설정
        val serializer = StringRedisSerializer()
        template.keySerializer = serializer
        template.valueSerializer = serializer
        template.hashKeySerializer = serializer
        template.hashValueSerializer = serializer

        return template
    }
}
