package org.example.povi.auth.token.dao

/**
 * Refresh Token 저장/조회/삭제를 위한 인터페이스.
 * Redis 또는 다른 저장소 구현체가 이 인터페이스를 따릅니다.
 */
interface RefreshTokenRepository {

    /**
     * Refresh Token 저장
     * @param userId 사용자 ID 또는 이메일
     * @param refreshToken 저장할 토큰
     */
    fun save(userId: String, refreshToken: String?)

    /**
     * 저장된 Refresh Token 조회
     * @param userId 사용자 ID 또는 이메일
     * @return 저장된 토큰 문자열 (없으면 null)
     */
    fun findByUserId(userId: String?): String?

    /**
     * Refresh Token 삭제
     * @param userId 사용자 ID 또는 이메일
     */
    fun deleteByUserId(userId: String)
}