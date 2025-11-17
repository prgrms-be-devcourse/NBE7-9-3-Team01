package org.example.povi.auth.token.jwt

import org.example.povi.auth.token.dao.RefreshTokenRepository
import org.example.povi.auth.token.entity.RefreshToken
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun save(userId: String, refreshToken: String?) {
        refreshTokenRepository.save(userId, refreshToken)
    }

    fun getByEmail(userId: String?): RefreshToken {
        val token = refreshTokenRepository.findByUserId(userId)
        return RefreshToken(userId, token)
    }

    fun delete(userId: String) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}